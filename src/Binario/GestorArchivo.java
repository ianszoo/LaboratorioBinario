package binario;

import java.io.*;
import java.util.zip.CRC32;

public class GestorArchivo implements EscritorBinario, LectorBinario {

    @Override
    public void escribir(Documento documento, File archivo) throws IOException, ExtensionInvalidaException {
        validarExtension(archivo);

        byte[] cuerpoBytes = serializarCuerpo(documento);
        long checksum = calcularChecksum(cuerpoBytes);

        try (DataOutputStream out =
                     new DataOutputStream(new BufferedOutputStream(new FileOutputStream(archivo)))) {
            CabeceraArchivo cabecera = new CabeceraArchivo(FormatoEDT.VERSION_ACTUAL, checksum);
            cabecera.escribir(out);
            out.write(cuerpoBytes);
        }
    }

    @Override
    public Documento leer(File archivo)
            throws IOException, ArchivoNoEncontradoException, ArchivoCorruptoException,
            ExtensionInvalidaException, ArchivoTruncadoException {

        validarExtension(archivo);

        if (!archivo.exists() || !archivo.isFile()) {
            throw new ArchivoNoEncontradoException("No se encontró el archivo: " + archivo.getAbsolutePath());
        }

        byte[] contenidoCompleto;
        try (FileInputStream fis = new FileInputStream(archivo)) {
            contenidoCompleto = fis.readAllBytes();
        }

        if (contenidoCompleto.length < FormatoEDT.TAMANO_CABECERA) {
            throw new ArchivoCorruptoException("El archivo es demasiado pequeño para ser un .edt válido.");
        }

        DataInputStream entradaCabecera = new DataInputStream(new ByteArrayInputStream(contenidoCompleto));
        CabeceraArchivo cabecera = CabeceraArchivo.leer(entradaCabecera);

        byte[] cuerpoBytes = new byte[contenidoCompleto.length - FormatoEDT.TAMANO_CABECERA];
        System.arraycopy(contenidoCompleto, FormatoEDT.TAMANO_CABECERA, cuerpoBytes, 0, cuerpoBytes.length);

        long checksumCalculado = calcularChecksum(cuerpoBytes);
        if (checksumCalculado != cabecera.getChecksum()) {
            throw new ArchivoCorruptoException(
                    "El checksum no coincide: el archivo fue modificado o está dañado.");
        }

        return deserializarCuerpo(cuerpoBytes);
    }

 
    private byte[] serializarCuerpo(Documento documento) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream cuerpo = new DataOutputStream(buffer)) {
            escribirSeccionTexto(cuerpo, documento);
            escribirSeccionTablas(cuerpo, documento);
            cuerpo.writeByte(FormatoEDT.MARCADOR_EOF);
        }
        return buffer.toByteArray();
    }

    private void escribirSeccionTexto(DataOutputStream out, Documento documento) throws IOException {
        out.writeByte(FormatoEDT.MARCADOR_SECCION_TEXTO);
        out.writeInt(documento.getParrafos().size());
        for (Parrafo parrafo : documento.getParrafos()) {
            parrafo.escribir(out);
        }
    }

    private void escribirSeccionTablas(DataOutputStream out, Documento documento) throws IOException {
        out.writeByte(FormatoEDT.MARCADOR_SECCION_TABLAS);
        out.writeInt(documento.getTablas().size());
        for (Tablas tabla : documento.getTablas()) {
            tabla.escribir(out);
        }
    }

    private Documento deserializarCuerpo(byte[] cuerpoBytes)
            throws IOException, ArchivoCorruptoException, ArchivoTruncadoException {

        Documento documento = new Documento();
        DataInputStream cuerpo = new DataInputStream(new ByteArrayInputStream(cuerpoBytes));

        try {
            leerSeccionTexto(cuerpo, documento);
            leerSeccionTablas(cuerpo, documento);

            int marcadorFinal = cuerpo.readUnsignedByte();
            if (marcadorFinal != (FormatoEDT.MARCADOR_EOF & 0xFF)) {
                throw new ArchivoCorruptoException("No se encontró el marcador de fin de archivo esperado.");
            }
        } catch (EOFException e) {
            throw new ArchivoTruncadoException(
                    "El archivo terminó antes de lo esperado; probablemente el guardado se interrumpió a la mitad.", e);
        }

        return documento;
    }

    private void leerSeccionTexto(DataInputStream in, Documento documento)
            throws IOException, ArchivoCorruptoException {
        int marcador = in.readUnsignedByte();
        if (marcador != (FormatoEDT.MARCADOR_SECCION_TEXTO & 0xFF)) {
            throw new ArchivoCorruptoException("Marcador de sección de texto inválido o fuera de orden.");
        }
        int nParrafos = in.readInt();
        for (int i = 0; i < nParrafos; i++) {
            documento.agregarParrafo(Parrafo.leer(in));
        }
    }

    private void leerSeccionTablas(DataInputStream in, Documento documento)
            throws IOException, ArchivoCorruptoException {
        int marcador = in.readUnsignedByte();
        if (marcador != (FormatoEDT.MARCADOR_SECCION_TABLAS & 0xFF)) {
            throw new ArchivoCorruptoException("Marcador de sección de tablas inválido o fuera de orden.");
        }
        int nTablas = in.readInt();
        for (int i = 0; i < nTablas; i++) {
            documento.agregarTabla(Tablas.leer(in));
        }
    }

 
    private void validarExtension(File archivo) throws ExtensionInvalidaException {
        String nombre = archivo.getName().toLowerCase();
        if (!nombre.endsWith(FormatoEDT.EXTENSION)) {
            throw new ExtensionInvalidaException(
                    "El archivo debe tener la extensión " + FormatoEDT.EXTENSION + ": " + archivo.getName());
        }
    }

    private long calcularChecksum(byte[] datos) {
        CRC32 crc = new CRC32();
        crc.update(datos);
        return crc.getValue();
    }
}
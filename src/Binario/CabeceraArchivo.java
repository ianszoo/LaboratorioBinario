package Binario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

public class CabeceraArchivo {

    private final int version;
    private final int longitudCuerpo;
    private final long checksum;

    public CabeceraArchivo(int version, int longitudCuerpo, long checksum) {
        this.version = version;
        this.longitudCuerpo = longitudCuerpo;
        this.checksum = checksum;
    }

    public int getVersion() {
        return version;
    }

    public int getLongitudCuerpo() {
        return longitudCuerpo;
    }

    public long getChecksum() {
        return checksum;
    }

    public void escribir(DataOutputStream out) throws IOException {
        out.write(FormatoEDT.MAGIC_NUMBER);
        out.writeInt(version);
        out.writeInt(longitudCuerpo);
        out.writeLong(checksum);
    }

    public static CabeceraArchivo leer(DataInputStream in) throws IOException, ArchivoCorruptoException, ArchivoTruncadoException {
        byte[] magic = new byte[FormatoEDT.MAGIC_NUMBER.length];
        try {
            in.readFully(magic);
        } catch (EOFException e) {
            throw new ArchivoCorruptoException("El archivo es demasiado corto para contener una cabecera válida.", e);
        }

        if (!Arrays.equals(magic, FormatoEDT.MAGIC_NUMBER)) {
            throw new ArchivoCorruptoException("Firma de archivo inválida: no es un archivo .edt reconocido.");
        }

        int version;
        int longitudCuerpo;
        long checksum;
        try {
            version = in.readInt();
            longitudCuerpo = in.readInt();
            checksum = in.readLong();
        } catch (EOFException e) {
            throw new ArchivoTruncadoException("La cabecera del archivo está incompleta.", e);
        }

        if (version <= 0 || version > FormatoEDT.VERSION_ACTUAL) {
            throw new ArchivoCorruptoException("Versión de formato no soportada: " + version);
        }
        if (longitudCuerpo < 0) {
            throw new ArchivoCorruptoException("Longitud de cuerpo inválida: " + longitudCuerpo);
        }
        return new CabeceraArchivo(version, longitudCuerpo, checksum);
    }
}

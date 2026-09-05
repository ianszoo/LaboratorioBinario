package Binario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class FormatoEDT {

    private FormatoEDT() {
    }

    public static final byte[] MAGIC_NUMBER = {0x45, 0x44, 0x54, 0x31};

    public static final int VERSION_ACTUAL = 1;

    public static final String EXTENSION = ".edt";

    public static final int TAMANO_CABECERA = 20;

    public static final byte MARCADOR_SECCION_TEXTO = (byte) 0xA1;

    public static final byte MARCADOR_SECCION_TABLAS = (byte) 0xA2;

    public static final byte MARCADOR_EOF = (byte) 0xFF;

    public static final int MAX_LONGITUD_TEXTO = 64 * 1024 * 1024;

    public static final int MAX_FILAS = 1000;

    public static final int MAX_COLUMNAS = 100;

    public static final int MAX_TAMANO_FUENTE = 400;

    public static void escribirTexto(DataOutputStream out, String texto) throws IOException {
        byte[] bytes = (texto == null ? "" : texto).getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    public static String leerTexto(DataInputStream in) throws IOException, ArchivoCorruptoException {
        int longitud = in.readInt();
        if (longitud < 0 || longitud > MAX_LONGITUD_TEXTO) {
            throw new ArchivoCorruptoException("Longitud de texto inválida: " + longitud);
        }
        byte[] bytes = new byte[longitud];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}

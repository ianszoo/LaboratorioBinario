package Binario;

public final class FormatoEDT {

    private FormatoEDT() {
    }

    public static final byte[] MAGIC_NUMBER = {0x45, 0x44, 0x54, 0x31};

    public static final int VERSION_ACTUAL = 1;

    public static final String EXTENSION = ".edt";

    public static final int TAMANO_CABECERA = 16;

    public static final byte MARCADOR_SECCION_TEXTO = (byte) 0xA1;

    public static final byte MARCADOR_SECCION_TABLAS = (byte) 0xA2;

    public static final byte MARCADOR_EOF = (byte) 0xFF;
}
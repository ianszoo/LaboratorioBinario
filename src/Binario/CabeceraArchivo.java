
package Binario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

public class CabeceraArchivo {
 
    private final int version;
    private final long checksum;
 
    public CabeceraArchivo(int version, long checksum) {
        this.version = version;
        this.checksum = checksum;
    }
 
    public int getVersion() {
        return version;
    }
 
    public long getChecksum() {
        return checksum;
    }
 
    public void escribir(DataOutputStream out) throws IOException {
        out.write(FormatoEDT.MAGIC_NUMBER);
        out.writeInt(version);
        out.writeLong(checksum);
    }
 

    public static CabeceraArchivo leer(DataInputStream in) throws IOException, ArchivoCorruptoException {
        byte[] magic = new byte[FormatoEDT.MAGIC_NUMBER.length];
        try {
            in.readFully(magic);
        } catch (EOFException e) {
            throw new ArchivoCorruptoException(
                    "El archivo es demasiado corto para contener una cabecera válida.", e);
        }
 
        if (!Arrays.equals(magic, FormatoEDT.MAGIC_NUMBER)) {
            throw new ArchivoCorruptoException(
                    "Firma de archivo inválida: no es un archivo .edt reconocido.");
        }
 
        int version = in.readInt();
        if (version <= 0 || version > FormatoEDT.VERSION_ACTUAL) {
            throw new ArchivoCorruptoException(
                    "Versión de formato no soportada: " + version);
        }
 
        long checksum = in.readLong();
        return new CabeceraArchivo(version, checksum);
    }
}
 
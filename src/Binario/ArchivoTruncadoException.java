
package Binario;

public class ArchivoTruncadoException extends Exception {
 
    public ArchivoTruncadoException(String mensaje) {
        super(mensaje);
    }
 
    public ArchivoTruncadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

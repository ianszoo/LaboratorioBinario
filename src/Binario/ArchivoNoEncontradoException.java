
package Binario;

public class ArchivoNoEncontradoException extends Exception {
 
    public ArchivoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
 
    public ArchivoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
 
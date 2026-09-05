
package Binario;

/**
 *
 * @author Abril
 */
public class ArchivoCorruptoException extends Exception {
 
    public ArchivoCorruptoException(String mensaje) {
        super(mensaje);
    }
 
    public ArchivoCorruptoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}


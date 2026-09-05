
package Binario;

public class ExtensionInvalidaException extends Exception {
 
    public ExtensionInvalidaException(String mensaje) {
        super(mensaje);
    }
 
    public ExtensionInvalidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
 

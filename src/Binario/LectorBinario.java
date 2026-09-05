package Binario;

import java.io.File;
import java.io.IOException;

public interface LectorBinario {

    Documento leer(File archivo) throws IOException, ArchivoNoEncontradoException,
            ArchivoCorruptoException, ExtensionInvalidaException, ArchivoTruncadoException;
}

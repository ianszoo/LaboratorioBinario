package Binario;

import java.io.File;
import java.io.IOException;

public interface EscritorBinario {

    void escribir(Documento documento, File archivo) throws IOException, ExtensionInvalidaException;
}

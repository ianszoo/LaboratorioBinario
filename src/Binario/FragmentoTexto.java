package Binario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FragmentoTexto {

    private final String texto;
    private final FormatoTexto formato;

    public FragmentoTexto(String texto, FormatoTexto formato) {
        this.texto = texto;
        this.formato = formato;
    }

    public String getTexto() {
        return texto;
    }

    public FormatoTexto getFormato() {
        return formato;
    }

    public void escribir(DataOutputStream out) throws IOException {
        FormatoEDT.escribirTexto(out, texto);
        formato.escribir(out);
    }

    public static FragmentoTexto leer(DataInputStream in) throws IOException, ArchivoCorruptoException {
        String texto = FormatoEDT.leerTexto(in);
        FormatoTexto formato = FormatoTexto.leer(in);
        return new FragmentoTexto(texto, formato);
    }
}

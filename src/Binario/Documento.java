package Binario;

import java.util.ArrayList;
import java.util.List;

public class Documento {

    private final List<FragmentoTexto> fragmentos = new ArrayList<>();
    private final List<TablaDatos> tablas = new ArrayList<>();

    public List<FragmentoTexto> getFragmentos() {
        return fragmentos;
    }

    public List<TablaDatos> getTablas() {
        return tablas;
    }

    public void agregarFragmento(FragmentoTexto fragmento) {
        fragmentos.add(fragmento);
    }

    public void agregarTabla(TablaDatos tabla) {
        tablas.add(tabla);
    }
}

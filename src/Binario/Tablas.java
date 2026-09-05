/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Binario;

/**
 *
 * @author David Suazo Palao
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Tablas {
    private static final String PROPIEDAD_FORMATOS = "formatosCeldas";
    private static final int ANCHO_COLUMNA = 110;
    private static final int ALTO_FILA = 26;

    private final JTextPane txtPane;
    private final FormatoTexto formatoBase;
    private JTable tablaActiva;
    private Runnable alModificar;
    private Consumer<FormatoTexto> alSeleccionarCelda;

    public Tablas(JTextPane txtPane, FormatoTexto formatoBase) {
        this.txtPane = txtPane;
        this.formatoBase = formatoBase;
        txtPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tablaActiva = null;
            }
        });
    }

    public void setAlModificar(Runnable alModificar) {
        this.alModificar = alModificar;
    }

    public void setAlSeleccionarCelda(Consumer<FormatoTexto> alSeleccionarCelda) {
        this.alSeleccionarCelda = alSeleccionarCelda;
    }

    public JTable insertTable(int filas, int columnas, TablaDatos datosGuardados) {
        DefaultTableModel modeloTabla = new DefaultTableModel(filas, columnas);
        JTable tabla = new JTable(modeloTabla);
        FormatoTexto[][] formatos = new FormatoTexto[filas][columnas];

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (datosGuardados != null) {
                    modeloTabla.setValueAt(datosGuardados.getContenido(f, c), f, c);
                    formatos[f][c] = datosGuardados.getFormato(f, c).copia();
                } else {
                    formatos[f][c] = formatoBase.copia();
                }
            }
        }

        tabla.putClientProperty(PROPIEDAD_FORMATOS, formatos);
        tabla.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tabla.setRowHeight(ALTO_FILA);
        tabla.setCellSelectionEnabled(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tabla.setGridColor(new Color(160, 160, 160));
        tabla.setShowGrid(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setDefaultRenderer(Object.class, new RenderizadorCelda());
        for (int c = 0; c < columnas; c++) {
            tabla.getColumnModel().getColumn(c).setPreferredWidth(ANCHO_COLUMNA);
        }

        tabla.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tablaActiva = tabla;
                notificarSeleccion(tabla);
            }
        });
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                notificarSeleccion(tabla);
            }
        });
        tabla.getColumnModel().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                notificarSeleccion(tabla);
            }
        });
        modeloTabla.addTableModelListener(e -> notificarModificacion());

        JPanel contenedorTabla = new JPanel(new BorderLayout());
        contenedorTabla.add(tabla.getTableHeader(), BorderLayout.NORTH);
        contenedorTabla.add(tabla, BorderLayout.CENTER);
        Dimension tamano = new Dimension(ANCHO_COLUMNA * columnas, filas * ALTO_FILA + tabla.getTableHeader().getPreferredSize().height);
        contenedorTabla.setPreferredSize(tamano);
        contenedorTabla.setMaximumSize(tamano);

        txtPane.insertComponent(contenedorTabla);
        return tabla;
    }

    public void insertarDesdeDatos(TablaDatos datos) {
        int longitud = txtPane.getDocument().getLength();
        int posicion = datos.getPosicion();
        if (posicion < longitud) {
            txtPane.select(posicion, posicion + 1);
        } else {
            txtPane.setCaretPosition(longitud);
        }
        insertTable(datos.getFilas(), datos.getColumnas(), datos);
    }

    public boolean hayCeldaSeleccionada() {
        return tablaActiva != null && tablaActiva.isShowing()
                && tablaActiva.getSelectedRowCount() > 0 && tablaActiva.getSelectedColumnCount() > 0;
    }

    public void aplicarFormato(Consumer<FormatoTexto> cambio) {
        if (!hayCeldaSeleccionada()) {
            return;
        }
        JTable tabla = tablaActiva;
        FormatoTexto[][] formatos = formatosDe(tabla);
        for (int f : tabla.getSelectedRows()) {
            for (int c : tabla.getSelectedColumns()) {
                cambio.accept(formatos[f][c]);
            }
        }
        tabla.repaint();
        notificarModificacion();
    }

    public List<TablaDatos> exportTablesData() {
        List<TablaDatos> listaExportar = new ArrayList<>();
        StyledDocument doc = txtPane.getStyledDocument();
        recolectarTablas(doc.getDefaultRootElement(), listaExportar);
        return listaExportar;
    }

    public void clearTable() {
        tablaActiva = null;
    }

    private void recolectarTablas(Element elemento, List<TablaDatos> destino) {
        if (elemento.isLeaf()) {
            Component componente = StyleConstants.getComponent(elemento.getAttributes());
            JTable tabla = buscarTabla(componente);
            if (tabla != null) {
                destino.add(exportarTabla(tabla, elemento.getStartOffset()));
            }
            return;
        }
        for (int i = 0; i < elemento.getElementCount(); i++) {
            recolectarTablas(elemento.getElement(i), destino);
        }
    }

    private JTable buscarTabla(Component componente) {
        if (componente instanceof JTable) {
            return (JTable) componente;
        }
        if (componente instanceof Container) {
            for (Component hijo : ((Container) componente).getComponents()) {
                JTable tabla = buscarTabla(hijo);
                if (tabla != null) {
                    return tabla;
                }
            }
        }
        return null;
    }

    private TablaDatos exportarTabla(JTable tabla, int posicion) {
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }
        int filas = tabla.getRowCount();
        int columnas = tabla.getColumnCount();
        FormatoTexto[][] formatos = formatosDe(tabla);
        TablaDatos datosTabla = new TablaDatos(posicion, filas, columnas);
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Object contenido = tabla.getValueAt(f, c);
                datosTabla.setContenido(f, c, contenido != null ? contenido.toString() : "");
                datosTabla.setFormato(f, c, formatos[f][c].copia());
            }
        }
        return datosTabla;
    }

    private static FormatoTexto[][] formatosDe(JTable tabla) {
        return (FormatoTexto[][]) tabla.getClientProperty(PROPIEDAD_FORMATOS);
    }

    private void notificarSeleccion(JTable tabla) {
        int f = tabla.getSelectedRow();
        int c = tabla.getSelectedColumn();
        if (alSeleccionarCelda != null && f != -1 && c != -1) {
            alSeleccionarCelda.accept(formatosDe(tabla)[f][c]);
        }
    }

    private void notificarModificacion() {
        if (alModificar != null) {
            alModificar.run();
        }
    }

    private static class RenderizadorCelda extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object valor, boolean estaSeleccionado, boolean tieneFoco, int f, int c) {
            String texto = valor == null ? "" : valor.toString();
            super.getTableCellRendererComponent(tbl, texto, estaSeleccionado, tieneFoco, f, c);
            FormatoTexto formato = formatosDe(tbl)[f][c];
            setFont(formato.aFont());
            if (!estaSeleccionado) {
                setForeground(formato.getColor());
            }
            if (formato.isSubrayado() || formato.isTachado()) {
                String html = escapar(texto);
                if (formato.isSubrayado()) {
                    html = "<u>" + html + "</u>";
                }
                if (formato.isTachado()) {
                    html = "<s>" + html + "</s>";
                }
                setText("<html>" + html + "</html>");
            }
            return this;
        }

        private static String escapar(String texto) {
            return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}

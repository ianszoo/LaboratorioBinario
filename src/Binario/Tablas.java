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
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class Tablas {
    private final JTextPane txtPane;
    private final List<JTable> listaTablas = new ArrayList<>();

    public Tablas(JTextPane txtPane) {
        this.txtPane = txtPane;
    }

    public void showInsterTableDialog(Component ventanaPadre) {
        JTextField campoFilas = new JTextField("3", 5);
        JTextField campoColumnas = new JTextField("3", 5);

        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 5, 5));
        panelFormulario.add(new JLabel("Numero de Filas: "));
        panelFormulario.add(campoFilas);
        panelFormulario.add(new JLabel("Numero de Columnas: "));
        panelFormulario.add(campoColumnas);

        int botonPresionado = JOptionPane.showConfirmDialog(
            ventanaPadre, 
            panelFormulario, 
            "Insertar Nueva Tabla", 
            JOptionPane.OK_CANCEL_OPTION
        );

        if (botonPresionado == JOptionPane.OK_OPTION) {
            try {
                int cantidadFilas = Integer.parseInt(campoFilas.getText().trim());
                int cantidadColumnas = Integer.parseInt(campoColumnas.getText().trim());

                if (cantidadFilas <= 0 || cantidadColumnas <= 0) {
                    throw new NumberFormatException();
                }
                insertTable(cantidadFilas, cantidadColumnas, null);
            } catch (Exception error) {
                JOptionPane.showMessageDialog(
                    ventanaPadre, 
                    "Por favor ingrese numeros enteros validos y mayores a cero", 
                    "Error de entrada", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public void insertTable(int filas, int columnas, BinaryPersistenceManager.TableData datosGuardados) {
        DefaultTableModel modeloTabla = new DefaultTableModel(filas, columnas);
        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(26);

        int[][] matrizColores = new int[filas][columnas];

        if (datosGuardados != null) {
            for (int f = 0; f < filas; f++) {
                for (int c = 0; c < columnas; c++) {
                    tabla.setValueAt(datosGuardados.cellContents[f][c], f, c);
                    matrizColores[f][c] = datosGuardados.cellColors[f][c];
                }
            }
        }

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object valor, boolean estaSeleccionado, boolean tieneFoco, int f, int c) {
                Component componenteCelda = super.getTableCellRendererComponent(tbl, valor, estaSeleccionado, tieneFoco, f, c);
                int colorGuardado = matrizColores[f][c];

                if (colorGuardado != 0) {
                    componenteCelda.setForeground(new Color(colorGuardado));
                } else {
                    componenteCelda.setForeground(Color.BLACK);
                }
                return componenteCelda;
            }
        });

        JPopupMenu menuContextual = new JPopupMenu();
        JMenuItem opcionColor = new JMenuItem("Cambiar Color de Texto a Celda");

        opcionColor.addActionListener(evento -> {
            int filaSeleccionada = tabla.getSelectedRow();
            int columnaSeleccionada = tabla.getSelectedColumn();

            if (filaSeleccionada != -1 && columnaSeleccionada != -1) {
                Color colorElegido = JColorChooser.showDialog(tabla, "Selecciona el color para esta celda", Color.BLACK);

                if (colorElegido != null) {
                    matrizColores[filaSeleccionada][columnaSeleccionada] = colorElegido.getRGB();
                    tabla.repaint();
                }
            }
        });

        menuContextual.add(opcionColor);
        tabla.setComponentPopupMenu(menuContextual);

        JPanel contenedorTabla = new JPanel(new BorderLayout());
        contenedorTabla.add(tabla.getTableHeader(), BorderLayout.NORTH);
        contenedorTabla.add(tabla, BorderLayout.CENTER);
        contenedorTabla.setMaximumSize(new Dimension(500, filas * 28 + 30));

        listaTablas.add(tabla);
        txtPane.setCaretPosition(txtPane.getDocument().getLength());
        txtPane.insertComponent(contenedorTabla);
    }

    public List<BinaryPersistenceManager.TableData> exportTablesData() {
        List<BinaryPersistenceManager.TableData> listaExportar = new ArrayList<>();

        for (JTable tabla : listaTablas) {
            int filas = tabla.getRowCount();
            int columnas = tabla.getColumnCount();
            BinaryPersistenceManager.TableData datosTabla = new BinaryPersistenceManager.TableData(filas, columnas);

            for (int f = 0; f < filas; f++) {
                for (int c = 0; c < columnas; c++) {
                    Object contenido = tabla.getValueAt(f, c);
                    datosTabla.cellContents[f][c] = (contenido != null) ? contenido.toString() : "";
                    datosTabla.cellColors[f][c] = tabla.getForeground().getRGB();
                }
            }
            listaExportar.add(datosTabla);
        }
        return listaExportar;
    }

    public void clearTable() {
        listaTablas.clear();
    }
}
  

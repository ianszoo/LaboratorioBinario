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
    
    public Tablas (JTextPane txtPane){
        this.txtPane=txtPane;
    }
    
    public void showInsterTableDialog(Component ventanaPadre){
        JTextField campoFilas=new JTextField("3", 5);
        JTextField campoColumnas=new JTextField("3",5);
        
        JPanel panelFormulario=new JPanel (new GridLayout(2,2,5,5));
        panelFormulario.add(new JLabel("Numero de Filas: "));
        panelFormulario.add(campoFilas);
        panelFormulario.add(new JLabel("Numero de Columnas: "));
        panelFormulario.add(campoColumnas);
        
        int botonPresionado= JOptionPane.showConfirmDialog(ventanaPadre, panelFormulario, "Insertar Nueva Tabla",JOptionPane.OK_CANCEL_OPTION);
        
        if (botonPresionado==JOptionPane.OK_OPTION){
            try{
                int cantidadFilas= Integer.parseInt(campoFilas.getText().trim());
                int cantidadColumnas= Integer.parseInt(campoColumnas.getText().trim());
                
                if(cantidadFilas <=0 || cantidadColumnas <=0){
                    throw new NumberFormatException();
                    
                }
                insertTable(cantidadFilas,cantidadColumnas,null);
            } catch(Exception error){
                JOptionPane.showMessageDialog(ventanaPadre, "Por favor ingrese numeros enteros validos y mayores a cero", "Error de entrada", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    
}

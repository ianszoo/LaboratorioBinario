package Binario;

import java.io.File;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import visual.VentanaPrincipal;

public class Principal {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("sucedio un error: " + ex.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            BinarioManager manager = new BinarioManager(ventana);
            ventana.setVisible(true);
            if (args.length > 0) {
                manager.abrirArchivo(new File(args[0]));
            }
        });
    }
}

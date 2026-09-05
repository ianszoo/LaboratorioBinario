package visual;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Lanzador {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("No se pudo aplicar el look and feel del sistema: " + ex.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}

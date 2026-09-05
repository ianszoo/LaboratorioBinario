package visual;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class BarraEstado extends JPanel {

    private final JLabel etiquetaMensaje;
    private final JLabel etiquetaConteo;

    public BarraEstado() {
        super(new BorderLayout());
        setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, new Color(205, 205, 205)), new EmptyBorder(3, 10, 3, 10)));
        etiquetaMensaje = new JLabel("Listo");
        etiquetaConteo = new JLabel();
        add(etiquetaMensaje, BorderLayout.WEST);
        add(etiquetaConteo, BorderLayout.EAST);
        actualizarConteo("");
    }

    public void setMensaje(String mensaje) {
        etiquetaMensaje.setText(mensaje == null || mensaje.isEmpty() ? "Listo" : mensaje);
    }

    public void actualizarConteo(String texto) {
        int palabras = texto == null || texto.trim().isEmpty() ? 0 : texto.trim().split("\\s+").length;
        int caracteres = texto == null ? 0 : texto.length();
        etiquetaConteo.setText("Palabras: " + palabras + "    Caracteres: " + caracteres);
    }
}

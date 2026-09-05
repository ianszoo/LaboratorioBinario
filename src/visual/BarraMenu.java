package visual;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class BarraMenu extends JMenuBar {

    public BarraMenu(VentanaPrincipal ventana) {
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic(KeyEvent.VK_A);

        JMenuItem itemNuevo = crearItem("Nuevo", KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
        JMenuItem itemAbrir = crearItem("Abrir...", KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
        JMenuItem itemGuardar = crearItem("Guardar", KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
        JMenuItem itemGuardarComo = crearItem("Guardar como...", KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        JMenuItem itemSalir = new JMenuItem("Salir");

        itemNuevo.addActionListener(e -> ventana.getEditorListener().nuevoDocumento());
        itemAbrir.addActionListener(e -> ventana.getEditorListener().abrirDocumento());
        itemGuardar.addActionListener(e -> ventana.getEditorListener().guardarDocumento());
        itemGuardarComo.addActionListener(e -> ventana.getEditorListener().guardarDocumentoComo());
        itemSalir.addActionListener(e -> ventana.cerrar());

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.addSeparator();
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarComo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        add(menuArchivo);
    }

    private JMenuItem crearItem(String texto, int tecla, int modificadores) {
        JMenuItem item = new JMenuItem(texto);
        item.setAccelerator(KeyStroke.getKeyStroke(tecla, modificadores));
        return item;
    }
}

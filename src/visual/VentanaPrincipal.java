package visual;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

public class VentanaPrincipal extends JFrame {

    public static final String NOMBRE_APP = "Editor de Texto";
    public static final String EXTENSION = "edt";

    private final BarraHerramientas barraHerramientas;
    private final PanelEdicion panelEdicion;
    private final BarraEstado barraEstado;

    private EditorListener listener = new EditorListener() {
    };
    private String nombreArchivo = "Sin título";
    private boolean modificado;
    private JComponent ultimoFoco;

    public VentanaPrincipal() {
        panelEdicion = new PanelEdicion();
        barraHerramientas = new BarraHerramientas();
        barraEstado = new BarraEstado();

        setJMenuBar(new BarraMenu(this));
        setLayout(new BorderLayout());
        add(barraHerramientas, BorderLayout.NORTH);
        add(panelEdicion, BorderLayout.CENTER);
        add(barraEstado, BorderLayout.SOUTH);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner", e -> {
            Object nuevo = e.getNewValue();
            if (nuevo instanceof JComponent && SwingUtilities.isDescendingFrom((Component) nuevo, panelEdicion)) {
                ultimoFoco = (JComponent) nuevo;
            }
        });
        barraHerramientas.setAccionDevolverFoco(() -> {
            JComponent destino = ultimoFoco != null && ultimoFoco.isShowing() ? ultimoFoco : panelEdicion.getTextPane();
            destino.requestFocusInWindow();
        });

        JTextPane textPane = panelEdicion.getTextPane();
        textPane.addCaretListener(e -> actualizarBarraHerramientas());
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                documentoCambiado();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentoCambiado();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentoCambiado();
            }
        });
        atajo(textPane, KeyEvent.VK_B, barraHerramientas.getBotonNegrita());
        atajo(textPane, KeyEvent.VK_I, barraHerramientas.getBotonCursiva());
        atajo(textPane, KeyEvent.VK_U, barraHerramientas.getBotonSubrayado());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrar();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                textPane.requestFocusInWindow();
            }
        });

        actualizarTitulo();
        actualizarBarraHerramientas();
        setMinimumSize(new Dimension(700, 450));
        setSize(1000, 720);
        setLocationRelativeTo(null);
    }

    public void setEditorListener(EditorListener listener) {
        this.listener = listener == null ? new EditorListener() {
        } : listener;
        barraHerramientas.setEditorListener(this.listener);
    }

    public EditorListener getEditorListener() {
        return listener;
    }

    public JTextPane getTextPane() {
        return panelEdicion.getTextPane();
    }

    public StyledDocument getDocumento() {
        return panelEdicion.getTextPane().getStyledDocument();
    }

    public BarraHerramientas getBarraHerramientas() {
        return barraHerramientas;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo == null || nombreArchivo.isEmpty() ? "Sin título" : nombreArchivo;
        actualizarTitulo();
    }

    public boolean isModificado() {
        return modificado;
    }

    public void setModificado(boolean modificado) {
        if (this.modificado != modificado) {
            this.modificado = modificado;
            actualizarTitulo();
        }
    }

    public void setMensajeEstado(String mensaje) {
        barraEstado.setMensaje(mensaje);
    }

    public File elegirArchivoAbrir() {
        JFileChooser selector = crearSelector("Abrir documento");
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return selector.getSelectedFile();
    }

    public File elegirArchivoGuardar() {
        JFileChooser selector = crearSelector("Guardar documento");
        if (!nombreArchivo.equals("Sin título")) {
            selector.setSelectedFile(new File(nombreArchivo));
        }
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith("." + EXTENSION)) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + "." + EXTENSION);
        }
        if (archivo.exists()) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "El archivo \"" + archivo.getName() + "\" ya existe.\n¿Desea reemplazarlo?",
                    "Confirmar reemplazo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (respuesta != JOptionPane.YES_OPTION) {
                return null;
            }
        }
        return archivo;
    }

    public int confirmarGuardarCambios() {
        return JOptionPane.showConfirmDialog(this,
                "El documento \"" + nombreArchivo + "\" tiene cambios sin guardar.\n¿Desea guardarlos?",
                "Cambios sin guardar", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void cerrar() {
        if (modificado) {
            int respuesta = confirmarGuardarCambios();
            if (respuesta == JOptionPane.CANCEL_OPTION || respuesta == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (respuesta == JOptionPane.YES_OPTION) {
                listener.guardarDocumento();
                if (modificado) {
                    return;
                }
            }
        }
        dispose();
        System.exit(0);
    }

    private JFileChooser crearSelector(String titulo) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle(titulo);
        selector.setAcceptAllFileFilterUsed(false);
        selector.setFileFilter(new FileNameExtensionFilter("Documento del editor (*." + EXTENSION + ")", EXTENSION));
        return selector;
    }

    private void atajo(JTextPane textPane, int tecla, AbstractButton boton) {
        KeyStroke teclas = KeyStroke.getKeyStroke(tecla, InputEvent.CTRL_DOWN_MASK);
        textPane.getInputMap().put(teclas, "atajo" + tecla);
        textPane.getActionMap().put("atajo" + tecla, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boton.doClick();
            }
        });
    }

    private void documentoCambiado() {
        setModificado(true);
        Document doc = getTextPane().getDocument();
        try {
            barraEstado.actualizarConteo(doc.getText(0, doc.getLength()));
        } catch (Exception ex) {
            barraEstado.actualizarConteo("");
        }
    }

    private void actualizarTitulo() {
        setTitle(nombreArchivo + (modificado ? "*" : "") + " - " + NOMBRE_APP);
    }

    private void actualizarBarraHerramientas() {
        SwingUtilities.invokeLater(() -> {
            JTextPane textPane = getTextPane();
            StyledDocument doc = textPane.getStyledDocument();
            int inicio = Math.min(textPane.getSelectionStart(), textPane.getSelectionEnd());
            boolean haySeleccion = textPane.getSelectionStart() != textPane.getSelectionEnd();
            Element parrafo = doc.getParagraphElement(inicio);
            Element fragmento;
            if (haySeleccion || parrafo.getStartOffset() == inicio) {
                fragmento = doc.getCharacterElement(inicio);
            } else {
                fragmento = doc.getCharacterElement(Math.max(inicio - 1, 0));
            }
            AttributeSet atributos = fragmento.getAttributes();
            if (!haySeleccion) {
                SimpleAttributeSet entrada = new SimpleAttributeSet(textPane.getInputAttributes());
                entrada.setResolveParent(atributos);
                atributos = entrada;
            }
            barraHerramientas.actualizarDesdeAtributos(atributos);
        });
    }
}

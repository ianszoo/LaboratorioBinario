package visual;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;

public class BarraHerramientas extends JToolBar {

    private static final String[] FUENTES_COMUNES = {"Arial", "Calibri", "Times New Roman", "Georgia", "Verdana", "Tahoma", "Segoe UI", "Courier New", "SansSerif", "Serif", "Monospaced"};
    private static final Integer[] TAMANOS = {8, 9, 10, 11, 12, 14, 16, 18, 20, 24, 28, 32, 36, 48, 72};
    private static final Dimension TAMANO_BOTON = new Dimension(34, 28);

    private final JComboBox<String> comboFuente;
    private final JComboBox<Integer> comboTamano;
    private final JButton botonColor;
    private final JToggleButton botonNegrita;
    private final JToggleButton botonCursiva;
    private final JToggleButton botonSubrayado;
    private final JToggleButton botonTachado;
    private final JButton botonTabla;
    private final IconoColor iconoColor = new IconoColor();

    private EditorListener listener = new EditorListener() {
    };
    private Runnable accionDevolverFoco;
    private Color colorActual = Color.BLACK;
    private int tamanoActual = PanelEdicion.TAMANO_POR_DEFECTO;
    private boolean actualizando;

    public BarraHerramientas() {
        setFloatable(false);
        setRollover(true);
        setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(205, 205, 205)), new EmptyBorder(4, 8, 4, 8)));

        comboFuente = new JComboBox<>(fuentesDisponibles());
        comboFuente.setSelectedItem(PanelEdicion.FUENTE_POR_DEFECTO);
        comboFuente.setToolTipText("Tipo de fuente");
        fijarTamano(comboFuente, new Dimension(170, 26));
        comboFuente.addActionListener(e -> {
            if (!actualizando && comboFuente.getSelectedItem() != null) {
                listener.cambiarFuente(comboFuente.getSelectedItem().toString());
                devolverFoco();
            }
        });

        comboTamano = new JComboBox<>(TAMANOS);
        comboTamano.setEditable(true);
        comboTamano.setSelectedItem(tamanoActual);
        comboTamano.setToolTipText("Tamaño de fuente");
        fijarTamano(comboTamano, new Dimension(62, 26));
        comboTamano.addActionListener(e -> {
            if (actualizando) {
                return;
            }
            Integer tamano = leerTamano();
            if (tamano != null) {
                tamanoActual = tamano;
                listener.cambiarTamano(tamano);
            }
            mostrarTamano(tamanoActual);
            devolverFoco();
        });

        botonNegrita = crearToggle("N", "Negrita (Ctrl+B)", Font.BOLD);
        botonNegrita.addActionListener(e -> {
            listener.aplicarNegrita(botonNegrita.isSelected());
            devolverFoco();
        });
        botonCursiva = crearToggle("K", "Cursiva (Ctrl+I)", Font.ITALIC);
        botonCursiva.addActionListener(e -> {
            listener.aplicarCursiva(botonCursiva.isSelected());
            devolverFoco();
        });
        botonSubrayado = crearToggle("<html><u>S</u></html>", "Subrayado (Ctrl+U)", Font.PLAIN);
        botonSubrayado.addActionListener(e -> {
            listener.aplicarSubrayado(botonSubrayado.isSelected());
            devolverFoco();
        });
        botonTachado = crearToggle("<html><s>abc</s></html>", "Tachado", Font.PLAIN);
        botonTachado.addActionListener(e -> {
            listener.aplicarTachado(botonTachado.isSelected());
            devolverFoco();
        });

        botonColor = new JButton(iconoColor);
        botonColor.setToolTipText("Color de fuente");
        prepararBoton(botonColor);
        botonColor.addActionListener(e -> mostrarSelectorColor());

        botonTabla = new JButton("Insertar tabla");
        botonTabla.setFocusable(false);
        botonTabla.setMargin(new Insets(3, 10, 3, 10));
        botonTabla.addActionListener(e -> mostrarDialogoTabla());

        add(comboFuente);
        add(Box.createHorizontalStrut(4));
        add(comboTamano);
        add(Box.createHorizontalStrut(8));
        addSeparator();
        add(Box.createHorizontalStrut(8));
        add(botonNegrita);
        add(botonCursiva);
        add(botonSubrayado);
        add(botonTachado);
        add(Box.createHorizontalStrut(4));
        add(botonColor);
        add(Box.createHorizontalStrut(8));
        addSeparator();
        add(Box.createHorizontalStrut(8));
        add(botonTabla);
        add(Box.createHorizontalGlue());
    }

    public void setEditorListener(EditorListener listener) {
        this.listener = listener == null ? new EditorListener() {
        } : listener;
    }

    public void setAccionDevolverFoco(Runnable accion) {
        this.accionDevolverFoco = accion;
    }

    public void mostrarSelectorColor() {
        Color elegido = elegirColor(getTopLevelAncestor(), colorActual);
        if (elegido != null) {
            colorActual = elegido;
            iconoColor.color = elegido;
            botonColor.repaint();
            listener.cambiarColor(elegido);
        }
        devolverFoco();
    }

    public void mostrarDialogoTabla() {
        int[] dimension = DialogoTabla.mostrar(getTopLevelAncestor());
        if (dimension != null) {
            listener.insertarTabla(dimension[0], dimension[1]);
        }
        devolverFoco();
    }

    public void actualizarDesdeAtributos(AttributeSet atributos) {
        actualizando = true;
        try {
            botonNegrita.setSelected(StyleConstants.isBold(atributos));
            botonCursiva.setSelected(StyleConstants.isItalic(atributos));
            botonSubrayado.setSelected(StyleConstants.isUnderline(atributos));
            botonTachado.setSelected(StyleConstants.isStrikeThrough(atributos));
            seleccionarFuente(StyleConstants.getFontFamily(atributos));
            tamanoActual = StyleConstants.getFontSize(atributos);
            mostrarTamano(tamanoActual);
            colorActual = StyleConstants.getForeground(atributos);
            iconoColor.color = colorActual;
            botonColor.repaint();
        } finally {
            actualizando = false;
        }
    }

    public JToggleButton getBotonNegrita() {
        return botonNegrita;
    }

    public JToggleButton getBotonCursiva() {
        return botonCursiva;
    }

    public JToggleButton getBotonSubrayado() {
        return botonSubrayado;
    }

    private static Color elegirColor(Component padre, Color inicial) {
        JColorChooser selector = new JColorChooser(inicial);
        AbstractColorChooserPanel[] paneles = selector.getChooserPanels();
        AbstractColorChooserPanel elegido = paneles[Math.min(3, paneles.length - 1)];
        for (AbstractColorChooserPanel panel : paneles) {
            if ("RGB".equalsIgnoreCase(panel.getDisplayName())) {
                elegido = panel;
            }
        }
        selector.setChooserPanels(new AbstractColorChooserPanel[]{elegido});
        Color[] resultado = new Color[1];
        JDialog dialogo = JColorChooser.createDialog(padre, "Color de fuente", true, selector, e -> resultado[0] = selector.getColor(), null);
        dialogo.setVisible(true);
        dialogo.dispose();
        return resultado[0];
    }

    private static String[] fuentesDisponibles() {
        List<String> instaladas = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        List<String> lista = new ArrayList<>();
        for (String fuente : FUENTES_COMUNES) {
            if (instaladas.contains(fuente)) {
                lista.add(fuente);
            }
        }
        if (!lista.contains(PanelEdicion.FUENTE_POR_DEFECTO)) {
            lista.add(0, PanelEdicion.FUENTE_POR_DEFECTO);
        }
        return lista.toArray(new String[0]);
    }

    private void seleccionarFuente(String fuente) {
        boolean existe = false;
        for (int i = 0; i < comboFuente.getItemCount(); i++) {
            if (comboFuente.getItemAt(i).equals(fuente)) {
                existe = true;
            }
        }
        if (!existe) {
            comboFuente.addItem(fuente);
        }
        comboFuente.setSelectedItem(fuente);
    }

    private JToggleButton crearToggle(String texto, String tooltip, int estilo) {
        JToggleButton boton = new JToggleButton(texto);
        boton.setFont(boton.getFont().deriveFont(estilo, 13f));
        boton.setToolTipText(tooltip);
        prepararBoton(boton);
        return boton;
    }

    private void prepararBoton(AbstractButton boton) {
        boton.setFocusable(false);
        boton.setMargin(new Insets(2, 2, 2, 2));
        fijarTamano(boton, TAMANO_BOTON);
    }

    private void fijarTamano(JComponent componente, Dimension tamano) {
        componente.setPreferredSize(tamano);
        componente.setMinimumSize(tamano);
        componente.setMaximumSize(tamano);
    }

    private Integer leerTamano() {
        Object valor = comboTamano.getEditor().getItem();
        try {
            int tamano = Integer.parseInt(valor.toString().trim());
            return tamano >= 1 && tamano <= 400 ? tamano : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void mostrarTamano(int tamano) {
        boolean anterior = actualizando;
        actualizando = true;
        comboTamano.setSelectedItem(tamano);
        comboTamano.getEditor().setItem(tamano);
        actualizando = anterior;
    }

    private void devolverFoco() {
        if (accionDevolverFoco != null) {
            accionDevolverFoco.run();
        }
    }

    private static class IconoColor implements Icon {

        Color color = Color.BLACK;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(c.getFont().deriveFont(Font.BOLD, 14f));
            g2.setColor(Color.DARK_GRAY);
            int anchoA = g2.getFontMetrics().stringWidth("A");
            g2.drawString("A", x + (getIconWidth() - anchoA) / 2, y + 12);
            g2.setColor(color);
            g2.fillRect(x + 1, y + 14, getIconWidth() - 2, 4);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }
    }
}

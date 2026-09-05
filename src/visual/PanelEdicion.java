package visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class PanelEdicion extends JPanel {

    public static final String FUENTE_POR_DEFECTO = fuenteDisponible("Calibri", "Arial", "SansSerif");
    public static final int TAMANO_POR_DEFECTO = 12;

    private static final int ANCHO_HOJA = 794;
    private static final int ALTO_MINIMO_HOJA = 1123;
    private static final int MARGEN_HOJA = 72;
    private static final Color COLOR_FONDO = new Color(232, 232, 232);
    private static final Color COLOR_BORDE_HOJA = new Color(200, 200, 200);

    private final JTextPane textPane;
    private final JScrollPane scroll;

    public PanelEdicion() {
        super(new BorderLayout());

        textPane = new JTextPane();
        textPane.setBorder(new EmptyBorder(MARGEN_HOJA, MARGEN_HOJA, MARGEN_HOJA, MARGEN_HOJA));
        textPane.setBackground(Color.WHITE);
        configurarEstilosPorDefecto(textPane.getStyledDocument());

        JPanel hoja = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(ANCHO_HOJA, Math.max(d.height, ALTO_MINIMO_HOJA));
            }
        };
        hoja.setBackground(Color.WHITE);
        hoja.setBorder(new LineBorder(COLOR_BORDE_HOJA));
        hoja.add(textPane, BorderLayout.CENTER);

        FondoHoja fondo = new FondoHoja();
        fondo.add(hoja);
        fondo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                textPane.requestFocusInWindow();
            }
        });

        scroll = new JScrollPane(fondo);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    private static void configurarEstilosPorDefecto(StyledDocument documento) {
        Style base = documento.getStyle(StyleContext.DEFAULT_STYLE);
        if (base != null) {
            StyleConstants.setFontFamily(base, FUENTE_POR_DEFECTO);
            StyleConstants.setFontSize(base, TAMANO_POR_DEFECTO);
            StyleConstants.setForeground(base, Color.BLACK);
        }
    }

    private static String fuenteDisponible(String... candidatas) {
        String[] instaladas = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String candidata : candidatas) {
            for (String instalada : instaladas) {
                if (instalada.equalsIgnoreCase(candidata)) {
                    return instalada;
                }
            }
        }
        return "SansSerif";
    }

    private static class FondoHoja extends JPanel implements Scrollable {

        FondoHoja() {
            super(new FlowLayout(FlowLayout.CENTER, 0, 24));
            setBackground(COLOR_FONDO);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return orientation == SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return getParent() instanceof JViewport && getParent().getWidth() > getPreferredSize().width;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent() instanceof JViewport && getParent().getHeight() > getPreferredSize().height;
        }
    }
}

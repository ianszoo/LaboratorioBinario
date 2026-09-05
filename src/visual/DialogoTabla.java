package visual;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class DialogoTabla extends JDialog {

    private final JSpinner spinnerFilas;
    private final JSpinner spinnerColumnas;
    private boolean aceptado;

    public DialogoTabla(Window padre) {
        super(padre, "Insertar tabla", Dialog.ModalityType.APPLICATION_MODAL);

        spinnerFilas = new JSpinner(new SpinnerNumberModel(2, 1, 100, 1));
        spinnerColumnas = new JSpinner(new SpinnerNumberModel(2, 1, 30, 1));
        spinnerFilas.setPreferredSize(new Dimension(80, spinnerFilas.getPreferredSize().height));
        spinnerColumnas.setPreferredSize(new Dimension(80, spinnerColumnas.getPreferredSize().height));

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBorder(new EmptyBorder(16, 20, 8, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        campos.add(new JLabel("Número de filas:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        campos.add(spinnerFilas, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        campos.add(new JLabel("Número de columnas:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        campos.add(spinnerColumnas, gbc);

        JButton botonAceptar = new JButton("Aceptar");
        JButton botonCancelar = new JButton("Cancelar");
        botonAceptar.addActionListener(e -> {
            aceptado = true;
            dispose();
        });
        botonCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botones.setBorder(new EmptyBorder(0, 12, 6, 12));
        botones.add(botonAceptar);
        botones.add(botonCancelar);

        Container contenido = getContentPane();
        contenido.setLayout(new BorderLayout());
        contenido.add(campos, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(botonAceptar);
        getRootPane().registerKeyboardAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setResizable(false);
        pack();
        setLocationRelativeTo(padre);
    }

    public boolean fueAceptado() {
        return aceptado;
    }

    public int getFilas() {
        return (Integer) spinnerFilas.getValue();
    }

    public int getColumnas() {
        return (Integer) spinnerColumnas.getValue();
    }

    public static int[] mostrar(Component padre) {
        Window ventana = padre instanceof Window ? (Window) padre : SwingUtilities.getWindowAncestor(padre);
        DialogoTabla dialogo = new DialogoTabla(ventana);
        dialogo.setVisible(true);
        if (!dialogo.fueAceptado()) {
            return null;
        }
        return new int[]{dialogo.getFilas(), dialogo.getColumnas()};
    }
}

package Binario;

import java.awt.Color;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class FormatoTexto {

    private static final int BIT_NEGRITA = 1;
    private static final int BIT_CURSIVA = 2;
    private static final int BIT_SUBRAYADO = 4;
    private static final int BIT_TACHADO = 8;

    private String fuente;
    private int tamano;
    private Color color;
    private boolean negrita;
    private boolean cursiva;
    private boolean subrayado;
    private boolean tachado;

    public FormatoTexto(String fuente, int tamano, Color color, boolean negrita, boolean cursiva, boolean subrayado, boolean tachado) {
        this.fuente = fuente;
        this.tamano = tamano;
        this.color = color;
        this.negrita = negrita;
        this.cursiva = cursiva;
        this.subrayado = subrayado;
        this.tachado = tachado;
    }

    public static FormatoTexto porDefecto(String fuente, int tamano) {
        return new FormatoTexto(fuente, tamano, Color.BLACK, false, false, false, false);
    }

    public static FormatoTexto desdeAtributos(AttributeSet atributos) {
        return new FormatoTexto(
                StyleConstants.getFontFamily(atributos),
                StyleConstants.getFontSize(atributos),
                StyleConstants.getForeground(atributos),
                StyleConstants.isBold(atributos),
                StyleConstants.isItalic(atributos),
                StyleConstants.isUnderline(atributos),
                StyleConstants.isStrikeThrough(atributos));
    }

    public SimpleAttributeSet aAtributos() {
        SimpleAttributeSet atributos = new SimpleAttributeSet();
        StyleConstants.setFontFamily(atributos, fuente);
        StyleConstants.setFontSize(atributos, tamano);
        StyleConstants.setForeground(atributos, color);
        StyleConstants.setBold(atributos, negrita);
        StyleConstants.setItalic(atributos, cursiva);
        StyleConstants.setUnderline(atributos, subrayado);
        StyleConstants.setStrikeThrough(atributos, tachado);
        return atributos;
    }

    public Font aFont() {
        int estilo = (negrita ? Font.BOLD : 0) | (cursiva ? Font.ITALIC : 0);
        return new Font(fuente, estilo, tamano);
    }

    public FormatoTexto copia() {
        return new FormatoTexto(fuente, tamano, color, negrita, cursiva, subrayado, tachado);
    }

    public void escribir(DataOutputStream out) throws IOException {
        out.writeUTF(fuente);
        out.writeInt(tamano);
        out.writeInt(color.getRGB());
        int banderas = 0;
        if (negrita) {
            banderas |= BIT_NEGRITA;
        }
        if (cursiva) {
            banderas |= BIT_CURSIVA;
        }
        if (subrayado) {
            banderas |= BIT_SUBRAYADO;
        }
        if (tachado) {
            banderas |= BIT_TACHADO;
        }
        out.writeByte(banderas);
    }

    public static FormatoTexto leer(DataInputStream in) throws IOException, ArchivoCorruptoException {
        String fuente = in.readUTF();
        int tamano = in.readInt();
        if (tamano < 1 || tamano > FormatoEDT.MAX_TAMANO_FUENTE) {
            throw new ArchivoCorruptoException("Tamaño de fuente inválido: " + tamano);
        }
        Color color = new Color(in.readInt(), true);
        int banderas = in.readUnsignedByte();
        if ((banderas & ~(BIT_NEGRITA | BIT_CURSIVA | BIT_SUBRAYADO | BIT_TACHADO)) != 0) {
            throw new ArchivoCorruptoException("Banderas de estilo inválidas: " + banderas);
        }
        return new FormatoTexto(fuente, tamano, color,
                (banderas & BIT_NEGRITA) != 0,
                (banderas & BIT_CURSIVA) != 0,
                (banderas & BIT_SUBRAYADO) != 0,
                (banderas & BIT_TACHADO) != 0);
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isNegrita() {
        return negrita;
    }

    public void setNegrita(boolean negrita) {
        this.negrita = negrita;
    }

    public boolean isCursiva() {
        return cursiva;
    }

    public void setCursiva(boolean cursiva) {
        this.cursiva = cursiva;
    }

    public boolean isSubrayado() {
        return subrayado;
    }

    public void setSubrayado(boolean subrayado) {
        this.subrayado = subrayado;
    }

    public boolean isTachado() {
        return tachado;
    }

    public void setTachado(boolean tachado) {
        this.tachado = tachado;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FormatoTexto)) {
            return false;
        }
        FormatoTexto otro = (FormatoTexto) o;
        return fuente.equals(otro.fuente) && tamano == otro.tamano && color.equals(otro.color)
                && negrita == otro.negrita && cursiva == otro.cursiva
                && subrayado == otro.subrayado && tachado == otro.tachado;
    }

    @Override
    public int hashCode() {
        return fuente.hashCode() * 31 + tamano;
    }
}

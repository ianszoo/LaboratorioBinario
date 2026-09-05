/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Binario;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Ian Suazo Palao
 */
public class TextEditor {
    private final JTextPane txt;
    private final StyledDocument doc;

    public TextEditor(JTextPane txt, StyledDocument doc) {
        this.txt = txt;
        this.doc = txt.getStyledDocument();
    }
    
    public void applyBold(boolean enable) {
        applyAttribute(StyleConstants.Bold, enable);
    }

    public void applyItalic(boolean enable) {
        applyAttribute(StyleConstants.Italic, enable);
    }

    public void applyUnderline(boolean enable) {
        applyAttribute(StyleConstants.Underline, enable);
    }

    public void applyStrikethrough(boolean enable) {
        applyAttribute(StyleConstants.StrikeThrough, enable);
    }

    public void applyFontFamily(String family) {
        SimpleAttributeSet att=new SimpleAttributeSet();
        StyleConstants.setFontFamily(att, family);
        updateSelection(att);
    }

    public void applyFontSize(int size) {
        SimpleAttributeSet att=new SimpleAttributeSet();
        StyleConstants.setFontSize(att, size);
        updateSelection(att);
    }

    public void applyColor(Color color) {
        SimpleAttributeSet att=new SimpleAttributeSet();
        StyleConstants.setForeground(att, color);
        updateSelection(att);
    }

    private void applyAttribute(Object key, Object value) {
        SimpleAttributeSet att=new SimpleAttributeSet();
        att.addAttribute(key, value);
        updateSelection(att);
    }

    private void updateSelection(AttributeSet att) {
        int start=txt.getSelectionStart();
        int end=txt.getSelectionEnd();
        
        if (start != end){
            doc.setCharacterAttributes(start,end-start,att, false);
        } else {
            txt.setCharacterAttributes(att, false);
        }
    }
}

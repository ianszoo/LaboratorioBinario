/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Binario;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
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
    
    public List<AdminPersistencia.TextRun> extractRuns() throws BadLocationException{
        List<AdminPersistencia.TextRun> run=new ArrayList<>();
        int length=doc.getLength();
        int pos=0;
        
        while(pos<length){
            Element e=doc.getCharacterElement(pos);
            AttributeSet as=e.getAttributes();
            int fin=e.getEndOffset();
            
            if(fin>length){
                fin=length;
            }
            String txt=doc.getText(pos,fin-pos);
            String font=StyleConstants.getFontFamily(as);
            int size=StyleConstants.getFontSize(as);
            Color color=StyleConstants.getForeground(as);
            boolean bold=StyleConstants.isBold(as);
            boolean italic=StyleConstants.isItalic(as);
            boolean underline=StyleConstants.isUnderline(as);
            boolean strike=StyleConstants.isStrikeThrough(as);
            
            run.add(new AdminPersistencia.TextRun(txt,font,size,color,bold,italic,underline,strike));
            pos=fin;
        }
        return run;
    }
}

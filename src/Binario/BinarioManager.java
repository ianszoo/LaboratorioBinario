/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Binario;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import visual.EditorListener;
import visual.PanelEdicion;
import visual.VentanaPrincipal;

/**
 *
 * @author Ian Suazo Palao
 */
public class BinarioManager implements EditorListener {

    private final VentanaPrincipal ventana;
    private final TextEditor editorTexto;
    private final Tablas tablas;
    private final GestorArchivo gestor = new GestorArchivo();
    private File archivoActual;

    public BinarioManager(VentanaPrincipal ventana) {
        this.ventana = ventana;
        JTextPane textPane = ventana.getTextPane();
        editorTexto = new TextEditor(textPane, textPane.getStyledDocument());
        tablas = new Tablas(textPane, FormatoTexto.porDefecto(PanelEdicion.FUENTE_POR_DEFECTO, PanelEdicion.TAMANO_POR_DEFECTO));
        tablas.setAlModificar(() -> ventana.setModificado(true));
        tablas.setAlSeleccionarCelda(formato -> ventana.getBarraHerramientas().actualizarDesdeAtributos(formato.aAtributos()));
        ventana.setEditorListener(this);
    }

    @Override
    public void nuevoDocumento() {
        if (!confirmarDescartarCambios()) {
            return;
        }
        try {
            editorTexto.clear();
        } catch (BadLocationException ex) {
            ventana.mostrarError("No se pudo limpiar el documento: " + ex.getMessage());
            return;
        }
        tablas.clearTable();
        archivoActual = null;
        ventana.setNombreArchivo(null);
        ventana.setModificado(false);
        ventana.setMensajeEstado("Nuevo documento");
        ventana.getTextPane().requestFocusInWindow();
    }

    @Override
    public void abrirDocumento() {
        if (!confirmarDescartarCambios()) {
            return;
        }
        File archivo = ventana.elegirArchivoAbrir();
        if (archivo == null) {
            return;
        }
        abrirArchivo(archivo);
    }

    public void abrirArchivo(File archivo) {
        try {
            Documento documento = gestor.leer(archivo);
            cargarDocumento(documento);
            archivoActual = archivo;
            ventana.setNombreArchivo(archivo.getName());
            ventana.setModificado(false);
            ventana.setMensajeEstado("Abierto: " + archivo.getAbsolutePath());
            ventana.getTextPane().requestFocusInWindow();
        } catch (ArchivoNoEncontradoException ex) {
            ventana.mostrarError("Archivo no encontrado.\n" + ex.getMessage());
        } catch (ExtensionInvalidaException ex) {
            ventana.mostrarError("Extensión no válida.\n" + ex.getMessage());
        } catch (ArchivoTruncadoException ex) {
            ventana.mostrarError("El archivo está truncado o incompleto.\n" + ex.getMessage());
        } catch (ArchivoCorruptoException ex) {
            ventana.mostrarError("El archivo está corrupto.\n" + ex.getMessage());
        } catch (IOException ex) {
            ventana.mostrarError("No se pudo leer el archivo.\n" + ex.getMessage());
        } catch (BadLocationException ex) {
            ventana.mostrarError("No se pudo reconstruir el documento en pantalla.\n" + ex.getMessage());
        }
    }

    @Override
    public void guardarDocumento() {
        if (archivoActual == null) {
            guardarDocumentoComo();
        } else {
            guardarEn(archivoActual);
        }
    }

    @Override
    public void guardarDocumentoComo() {
        File archivo = ventana.elegirArchivoGuardar();
        if (archivo != null) {
            guardarEn(archivo);
        }
    }

    private void guardarEn(File archivo) {
        try {
            Documento documento = construirDocumento();
            gestor.escribir(documento, archivo);
            archivoActual = archivo;
            ventana.setNombreArchivo(archivo.getName());
            ventana.setModificado(false);
            ventana.setMensajeEstado("Guardado: " + archivo.getAbsolutePath());
        } catch (ExtensionInvalidaException ex) {
            ventana.mostrarError("Extensión no válida.\n" + ex.getMessage());
        } catch (IOException ex) {
            ventana.mostrarError("No se pudo guardar el archivo.\n" + ex.getMessage());
        } catch (BadLocationException ex) {
            ventana.mostrarError("No se pudo leer el contenido del documento.\n" + ex.getMessage());
        }
    }

    public Documento construirDocumento() throws BadLocationException {
        Documento documento = new Documento();
        for (FragmentoTexto fragmento : editorTexto.extractRuns()) {
            documento.agregarFragmento(fragmento);
        }
        for (TablaDatos tabla : tablas.exportTablesData()) {
            documento.agregarTabla(tabla);
        }
        return documento;
    }

    public void cargarDocumento(Documento documento) throws BadLocationException {
        tablas.clearTable();
        editorTexto.applyRuns(documento.getFragmentos());
        List<TablaDatos> lista = documento.getTablas();
        for (TablaDatos tabla : lista) {
            tablas.insertarDesdeDatos(tabla);
        }
        ventana.getTextPane().setCaretPosition(0);
    }

    @Override
    public void aplicarNegrita(boolean activar) {
        if (tablas.hayCeldaSeleccionada()) {
            tablas.aplicarFormato(formato -> formato.setNegrita(activar));
        } else {
            editorTexto.applyBold(activar);
        }
    }

    @Override
    public void aplicarCursiva(boolean activar) {
        if (tablas.hayCeldaSeleccionada()) {
            tablas.aplicarFormato(formato -> formato.setCursiva(activar));
        } else {
            editorTexto.applyItalic(activar);
        }
    }

    @Override
    public void aplicarSubrayado(boolean activar) {
        if (tablas.hayCeldaSeleccionada()) {
            tablas.aplicarFormato(formato -> formato.setSubrayado(activar));
        } else {
            editorTexto.applyUnderline(activar);
        }
    }

    @Override
    public void aplicarTachado(boolean activar) {
        if (tablas.hayCeldaSeleccionada()) {
            tablas.aplicarFormato(formato -> formato.setTachado(activar));
        } else {
            editorTexto.applyStrikethrough(activar);
        }
    }

    @Override
    public void cambiarFuente(String familia) {
        if (tablas.hayCeldaSeleccionada()) {
            tablas.aplicarFormato(formato -> formato.setFuente(familia));
        } else {
            editorTexto.applyFontFamily(familia);
        }
    }

    @Override
    public void cambiarTamano(int tamano) {
        if (tablas.hayCeldaSeleccionada()) {
            tablas.aplicarFormato(formato -> formato.setTamano(tamano));
        } else {
            editorTexto.applyFontSize(tamano);
        }
    }

    @Override
    public void cambiarColor(Color color) {
        if (tablas.hayCeldaSeleccionada()) {
            tablas.aplicarFormato(formato -> formato.setColor(color));
        } else {
            editorTexto.applyColor(color);
        }
    }

    @Override
    public void insertarTabla(int filas, int columnas) {
        tablas.insertTable(filas, columnas, null);
        ventana.setModificado(true);
        ventana.setMensajeEstado("Tabla de " + filas + "x" + columnas + " insertada");
    }

    private boolean confirmarDescartarCambios() {
        if (!ventana.isModificado()) {
            return true;
        }
        int respuesta = ventana.confirmarGuardarCambios();
        if (respuesta == JOptionPane.YES_OPTION) {
            guardarDocumento();
            return !ventana.isModificado();
        }
        return respuesta == JOptionPane.NO_OPTION;
    }
}

package visual;

import java.awt.Color;

public interface EditorListener {

    default void nuevoDocumento() {
    }

    default void abrirDocumento() {
    }

    default void guardarDocumento() {
    }

    default void guardarDocumentoComo() {
    }

    default void aplicarNegrita(boolean activar) {
    }

    default void aplicarCursiva(boolean activar) {
    }

    default void aplicarSubrayado(boolean activar) {
    }

    default void aplicarTachado(boolean activar) {
    }

    default void cambiarFuente(String familia) {
    }

    default void cambiarTamano(int tamano) {
    }

    default void cambiarColor(Color color) {
    }

    default void insertarTabla(int filas, int columnas) {
    }
}

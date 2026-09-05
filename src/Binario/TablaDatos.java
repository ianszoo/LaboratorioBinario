package Binario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TablaDatos {

    private final int posicion;
    private final int filas;
    private final int columnas;
    private final String[][] contenidos;
    private final FormatoTexto[][] formatos;

    public TablaDatos(int posicion, int filas, int columnas) {
        this.posicion = posicion;
        this.filas = filas;
        this.columnas = columnas;
        this.contenidos = new String[filas][columnas];
        this.formatos = new FormatoTexto[filas][columnas];
    }

    public int getPosicion() {
        return posicion;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public String getContenido(int fila, int columna) {
        String valor = contenidos[fila][columna];
        return valor == null ? "" : valor;
    }

    public void setContenido(int fila, int columna, String valor) {
        contenidos[fila][columna] = valor;
    }

    public FormatoTexto getFormato(int fila, int columna) {
        return formatos[fila][columna];
    }

    public void setFormato(int fila, int columna, FormatoTexto formato) {
        formatos[fila][columna] = formato;
    }

    public void escribir(DataOutputStream out) throws IOException {
        out.writeInt(posicion);
        out.writeInt(filas);
        out.writeInt(columnas);
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                FormatoEDT.escribirTexto(out, getContenido(f, c));
                formatos[f][c].escribir(out);
            }
        }
    }

    public static TablaDatos leer(DataInputStream in) throws IOException, ArchivoCorruptoException {
        int posicion = in.readInt();
        int filas = in.readInt();
        int columnas = in.readInt();
        if (posicion < 0) {
            throw new ArchivoCorruptoException("Posición de tabla inválida: " + posicion);
        }
        if (filas < 1 || filas > FormatoEDT.MAX_FILAS || columnas < 1 || columnas > FormatoEDT.MAX_COLUMNAS) {
            throw new ArchivoCorruptoException("Dimensiones de tabla inválidas: " + filas + "x" + columnas);
        }
        TablaDatos tabla = new TablaDatos(posicion, filas, columnas);
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                tabla.setContenido(f, c, FormatoEDT.leerTexto(in));
                tabla.setFormato(f, c, FormatoTexto.leer(in));
            }
        }
        return tabla;
    }
}

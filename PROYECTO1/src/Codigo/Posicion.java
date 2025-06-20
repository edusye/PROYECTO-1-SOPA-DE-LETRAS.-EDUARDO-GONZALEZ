/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Codigo;

/**
 * Clase que representa una posición en el tablero 4x4.
 * @author edusye
 */
public class Posicion {
    private int fila; //Declara la cantidad de filas
    private int columna; //Declara la cantidad de columnas
    
    /**
     * Constructor con fila y columna.
     * @param fila La fila (0-3)
     * @param columna La columna (0-3)
     */
    public Posicion(int fila, int columna) {
        if (fila < 0 || fila >= 4 || columna < 0 || columna >= 4) {
            throw new IllegalArgumentException("Fila y columna deben estar entre 0 y 3");
        }
        this.fila = fila;
        this.columna = columna;
    }
    
    /**
     * Constructor que crea una posición desde un índice de nodo.
     * @param indiceNodo El índice (0-15)
     */ 
    public Posicion(int indiceNodo) {
        if (indiceNodo < 0 || indiceNodo >= 16) {
            throw new IllegalArgumentException("El índice debe estar entre 0 y 15");
        }
        this.fila = indiceNodo / 4;
        this.columna = indiceNodo % 4;
    }
    
    /**
     * Convierte la posición a índice de nodo.
     * @return El índice del nodo (0-15)
     */
    public int IndiceNodo() {
        return fila * 4 + columna;
    }
    
    /**
     * Verifica si es adyacente a otra posición.
     * @param otra La otra posición
     * @return true si son adyacentes
     */
    public boolean Adyacente(Posicion otra) {
        if (otra == null) return false;
        
        int difFila = Math.abs(this.fila - otra.fila);
        int difColumna = Math.abs(this.columna - otra.columna);
        return (difFila <= 1 && difColumna <= 1) && 
               !(difFila == 0 && difColumna == 0);
    }
    
    /**
     * Verifica si la posición es válida.
     * @return true si es válida
     */
    public boolean esValida() {
        return fila >= 0 && fila < 4 && columna >= 0 && columna < 4;
    }
    
    /**
     * Getter
     * @return La fila
     */
    public int getFila() {
        return fila;
    }
    
    /**
     * Getter
     * @return La columna
     */
    public int getColumna() {
        return columna;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Posicion posicion = (Posicion) obj;
        return fila == posicion.fila && columna == posicion.columna;
    }
    
    @Override
    public int hashCode() {
        return fila * 4 + columna;
    }
    
    @Override
    public String toString() {
        return "(" + fila + "," + columna + ")";
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Codigo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Clase que representa el tablero de la sopa de letras como un grafo.
 * El tablero es de 4x4 y cada posición está conectada con sus vecinos.
 * 
 * @author estudiante
 */
public class Tablero {
    
    private static final int TAMAÑO = 4;
    private static final int TOTAL_CASILLAS = TAMAÑO * TAMAÑO;
    
    private char[][] tablero;
    private boolean[][] conexiones;
    private ArrayList<String> palabras;
    private boolean listo;
    
    /**
     * Constructor del tablero
     */
    public Tablero() {
        tablero = new char[TAMAÑO][TAMAÑO];
        conexiones = new boolean[TOTAL_CASILLAS][TOTAL_CASILLAS];
        palabras = new ArrayList<>();
        listo = false;
    }
    
    /**
     * Carga el archivo con el diccionario y el tablero
     * @param archivo ruta del archivo a cargar
     * @return true si se cargó correctamente, false en caso de error
     */
    public boolean cargarArchivo(String archivo) {
        try {
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            String linea;
            boolean leyendoPalabras = false;
            boolean leyendoTablero = false;
            ArrayList<String> palabrasTemp = new ArrayList<>();
            String letrasTablero = "";
            
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                
                if (linea.equals("dic")) {
                    leyendoPalabras = true;
                    continue;
                } else if (linea.equals("/dic")) {
                    leyendoPalabras = false;
                    continue;
                } else if (linea.equals("tab")) {
                    leyendoTablero = true;
                    continue;
                } else if (linea.equals("/tab")) {
                    leyendoTablero = false;
                    continue;
                }
                
                if (leyendoPalabras && !linea.isEmpty()) {
                    palabrasTemp.add(linea.toUpperCase());
                }
                
                if (leyendoTablero && !linea.isEmpty()) {
                    letrasTablero = letrasTablero + linea;
                }
            }
            
            lector.close();
            return procesarDatos(palabrasTemp, letrasTablero);
            
        } catch (IOException e) {
            System.out.println("Error leyendo archivo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Procesa los datos leídos del archivo
     * @param listaPalabras lista de palabras del diccionario
     * @param letras cadena con las letras del tablero separadas por comas
     * @return true si se procesaron correctamente, false en caso de error
     */
    private boolean procesarDatos(ArrayList<String> listaPalabras, String letras) {
        try {
            palabras.clear();
            for (String palabra : listaPalabras) {
                if (palabra.length() >= 3) {
                    palabras.add(palabra);
                }
            }
            
            String[] letrasArray = letras.split(",");
            
            if (letrasArray.length != 16) {
                System.out.println("Error: necesito exactamente 16 letras");
                return false;
            }
            
            int posicion = 0;
            for (int i = 0; i < TAMAÑO; i++) {
                for (int j = 0; j < TAMAÑO; j++) {
                    tablero[i][j] = letrasArray[posicion].trim().toUpperCase().charAt(0);
                    posicion++;
                }
            }
            
            crearConexiones();
            
            listo = true;
            return true;
            
        } catch (Exception e) {
            System.out.println("Error procesando datos: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Crea las conexiones entre casillas adyacentes
     */
    private void crearConexiones() {
        for (int i = 0; i < TOTAL_CASILLAS; i++) {
            for (int j = 0; j < TOTAL_CASILLAS; j++) {
                conexiones[i][j] = false;
            }
        }
        
        // Conectar casillas vecinas
        for (int fila = 0; fila < TAMAÑO; fila++) {
            for (int col = 0; col < TAMAÑO; col++) {
                int casilla = fila * TAMAÑO + col;
                
                // Revisar las 8 direcciones alrededor
                for (int df = -1; df <= 1; df++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (df == 0 && dc == 0) continue;
                        
                        int nuevaFila = fila + df;
                        int nuevaCol = col + dc;
                        
                        // Verificar que esté dentro del tablero
                        if (nuevaFila >= 0 && nuevaFila < TAMAÑO && 
                            nuevaCol >= 0 && nuevaCol < TAMAÑO) {
                            
                            int vecino = nuevaFila * TAMAÑO + nuevaCol;
                            conexiones[casilla][vecino] = true;
                            conexiones[vecino][casilla] = true;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Convierte fila y columna a número de casilla
     * @param fila número de fila (0-3)
     * @param col número de columna (0-3)
     * @return número de casilla correspondiente
     */
    public int getCasilla(int fila, int col) {
        return fila * TAMAÑO + col;
    }
    
    /**
     * Convierte número de casilla a fila y columna
     * @param casilla número de casilla (0-15)
     * @return array con la fila en posición 0 y columna en posición 1
     */
    public int[] getPosicion(int casilla) {
        int fila = casilla / TAMAÑO;
        int col = casilla % TAMAÑO;
        return new int[]{fila, col};
    }
    
    /**
     * Obtiene la letra en una posición
     * @param fila número de fila
     * @param col número de columna
     * @return letra en la posición especificada, o ' ' si está fuera de rango
     */
    public char getLetra(int fila, int col) {
        if (fila >= 0 && fila < TAMAÑO && col >= 0 && col < TAMAÑO) {
            return tablero[fila][col];
        }
        return ' ';
    }
    
    /**
     * Obtiene la letra de una casilla
     * @param casilla número de casilla
     * @return letra en la casilla especificada
     */
    public char getLetraCasilla(int casilla) {
        int[] pos = getPosicion(casilla);
        return getLetra(pos[0], pos[1]);
    }
    
    /**
     * Verifica si dos casillas están conectadas
     * @param casilla1 primera casilla
     * @param casilla2 segunda casilla
     * @return true si las casillas están conectadas, false en caso contrario
     */
    public boolean estanConectadas(int casilla1, int casilla2) {
        if (casilla1 >= 0 && casilla1 < TOTAL_CASILLAS && 
            casilla2 >= 0 && casilla2 < TOTAL_CASILLAS) {
            return conexiones[casilla1][casilla2];
        }
        return false;
    }
    
    /**
     * Obtiene las casillas vecinas de una casilla
     * @param casilla número de casilla
     * @return lista con los números de las casillas vecinas
     */
    public ArrayList<Integer> getVecinos(int casilla) {
        ArrayList<Integer> vecinos = new ArrayList<>();
        if (casilla >= 0 && casilla < TOTAL_CASILLAS) {
            for (int i = 0; i < TOTAL_CASILLAS; i++) {
                if (conexiones[casilla][i]) {
                    vecinos.add(i);
                }
            }
        }
        return vecinos;
    }
    
    /**
     * Muestra el tablero como texto
     * @return representación en cadena del tablero
     */
    public String mostrarTablero() {
        if (!listo) {
            return "Tablero no está listo";
        }
        
        String resultado = "";
        for (int i = 0; i < TAMAÑO; i++) {
            for (int j = 0; j < TAMAÑO; j++) {
                resultado = resultado + tablero[i][j];
                if (j < TAMAÑO - 1) {
                    resultado = resultado + " ";
                }
            }
            if (i < TAMAÑO - 1) {
                resultado = resultado + "\n";
            }
        }
        return resultado;
    }
    
    /**
     * Muestra las palabras del diccionario
     * @return cadena con todas las palabras separadas por comas
     */
    public String mostrarPalabras() {
        if (palabras.isEmpty()) {
            return "No hay palabras";
        }
        
        String resultado = "";
        for (int i = 0; i < palabras.size(); i++) {
            resultado = resultado + palabras.get(i);
            if (i < palabras.size() - 1) {
                resultado = resultado + ", ";
            }
        }
        return resultado;
    }
    
    /**
     * Obtiene el tamaño del tablero
     * @return tamaño del tablero (4)
     */
    public int getTamaño() {
        return TAMAÑO;
    }
    
    /**
     * Obtiene la cantidad de palabras en el diccionario
     * @return número de palabras cargadas
     */
    public int getCantidadPalabras() {
        return palabras.size();
    }
    
    /**
     * Obtiene una copia de la lista de palabras
     * @return nueva lista con las palabras del diccionario
     */
    public ArrayList<String> getPalabras() {
        return new ArrayList<>(palabras);
    }
    
    /**
     * Verifica si el tablero está listo para usar
     * @return true si el tablero fue cargado correctamente, false en caso contrario
     */
    public boolean estaListo() {
        return listo;
    }  
}

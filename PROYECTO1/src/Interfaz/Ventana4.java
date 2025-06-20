/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaz;

import Codigo.Tablero;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;

/**
 * Ventana que muestra el tablero de sopa de letras y permite la interacción del usuario.
 * @author edusye
 */
public class Ventana4 extends javax.swing.JFrame {
    private Tablero tablero; 
    private JButton[][] botonesTablero;
    private ArrayList<Integer> seleccion;
    private ArrayList<String> encontradas;
    private ArrayList<ArrayList<Integer>> caminosEncontrados;
    private boolean modoSeleccion; 
    private boolean palabraEncontradaEnBusqueda;
    private ArrayList<Integer> ultimoCaminoEncontrado; 
    private String ultimaPalabraEncontrada;
    private ArrayList<String> palabrasNoEnDiccionario;
    private String rutaArchivoDatos;
    
    /**
     * Constructor de la ventana
     * @param tablero el tablero de la sopa de letras
     */
    public Ventana4(Tablero tablero) {
        this.tablero = tablero;
        this.seleccion = new ArrayList<>();
        this.encontradas = new ArrayList<>();
        this.caminosEncontrados = new ArrayList<>();
        this.modoSeleccion = false; 
        this.palabraEncontradaEnBusqueda = false; 
        this.ultimoCaminoEncontrado = new ArrayList<>(); 
        this.ultimaPalabraEncontrada = "";
        this.palabrasNoEnDiccionario = new ArrayList<>();
        this.rutaArchivoDatos = null;
        initComponents();
        configurar();
    }
    
    /**
     * Configura los botones y elementos de la ventana
     */
    private void configurar() {
        botonesTablero = new JButton[4][4];
        botonesTablero[0][0] = BOTON1A; botonesTablero[0][1] = BOTON1B; botonesTablero[0][2] = BOTON1C; botonesTablero[0][3] = BOTON1D;
        botonesTablero[1][0] = BOTON2A; botonesTablero[1][1] = BOTON2B; botonesTablero[1][2] = BOTON2C; botonesTablero[1][3] = BOTON2D;
        botonesTablero[2][0] = BOTON3A; botonesTablero[2][1] = BOTON3B; botonesTablero[2][2] = BOTON3C; botonesTablero[2][3] = BOTON3D;
        botonesTablero[3][0] = BOTON4A; botonesTablero[3][1] = BOTON4B; botonesTablero[3][2] = BOTON4C; botonesTablero[3][3] = BOTON4D;
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                JButton boton = botonesTablero[i][j];
                boton.setText(String.valueOf(tablero.getLetra(i, j)));
                boton.setFont(new Font("Colonna MT", Font.BOLD, 18));
                boton.setBackground(Color.WHITE);
                boton.setFocusPainted(false);
                final int fila = i;
                final int col = j;
                boton.addActionListener((ActionEvent e) -> {
                    clickBoton(fila, col);
                });
            }
        }
        
        String texto = "";
        for (String palabra : tablero.getPalabras()) {
            texto += palabra.toUpperCase() + "\n";
        }
        PALABRAS.setText(texto);

        LIMPIAR.addActionListener(e -> limpiar());
        DFS.addActionListener(e -> buscarDFS());
        BFS.addActionListener(e -> buscarBFS());
        VERARBOL.setEnabled(false);
        GUARDAR.setEnabled(false);
    }
    
    /**
     * Activa el modo de selección manual de letras
     */
    private void activarModoSeleccion() {
        modoSeleccion = true;
        JOptionPane.showMessageDialog(this, "Modo de búsqueda activado. Ahora puedes seleccionar letras en el tablero.");
        
        BUSCAR.setBackground(Color.ORANGE);
        BUSCAR.setText("ACTIVO");
    }
    
    /**
     * Desactiva el modo de selección manual de letras
     */
    private void desactivarModoSeleccion() {
        modoSeleccion = false;
        BUSCAR.setBackground(new Color(255, 255, 153));
        BUSCAR.setText("BUSCAR");
        limpiar();
    }
    
    /**
     * Maneja el click en un botón del tablero
     * @param fila la fila del botón
     * @param col la columna del botón
     */
    private void clickBoton(int fila, int col) {
        if (!modoSeleccion) {
            JOptionPane.showMessageDialog(this, "Debes presionar el botón BUSCAR primero para poder seleccionar letras.");
            return;
        }
        
        int casilla = tablero.getCasilla(fila, col);
        
        if (seleccion.isEmpty()) {
            seleccion.add(casilla);
            botonesTablero[fila][col].setBackground(Color.YELLOW);
        } else if (seleccion.contains(casilla)) {
            if (seleccion.get(seleccion.size() - 1) == casilla) {
                verificarPalabra();
            }
        } else {
            int ultima = seleccion.get(seleccion.size() - 1);
            if (tablero.estanConectadas(ultima, casilla)) {
                seleccion.add(casilla);
                botonesTablero[fila][col].setBackground(Color.YELLOW);
            } else {
                JOptionPane.showMessageDialog(this, "Casillas no conectadas!");
            }
        }
    }
    
    /**
     * Verifica si la palabra seleccionada existe en el tablero
     */
    private void verificarPalabra() {
        if (seleccion.size() < 3) {
            JOptionPane.showMessageDialog(this, "Muy corta! La palabra debe tener al menos 3 letras.");
            limpiar();
            return;
        }

        String palabra = "";
        for (int casilla : seleccion) {
            palabra += tablero.getLetraCasilla(casilla);
        }
        palabra = palabra.toUpperCase();

        if (encontradas.contains(palabra)) {
            JOptionPane.showMessageDialog(this, "Ya encontraste esta palabra: " + palabra);
            limpiar();
            return;
        }

        boolean palabraDelDiccionario = esDelDiccionario(palabra);

        encontradas.add(palabra);
        caminosEncontrados.add(new ArrayList<>(seleccion));
        ultimoCaminoEncontrado = new ArrayList<>(seleccion); 
        ultimaPalabraEncontrada = palabra; 
        
        if (!palabraDelDiccionario) {
        if (!palabrasNoEnDiccionario.contains(palabra)) {
            palabrasNoEnDiccionario.add(palabra);
        }
    }

        for (int casilla : seleccion) {
            int[] pos = tablero.getPosicion(casilla);
            if (palabraDelDiccionario) {
                botonesTablero[pos[0]][pos[1]].setBackground(Color.GREEN);
            } else {
                botonesTablero[pos[0]][pos[1]].setBackground(Color.CYAN); 
            }
        }

        actualizarEncontradas();

        String mensaje;
        if (palabraDelDiccionario) {
            mensaje = "¡Encontraste una palabra del diccionario: " + palabra + "!";
            VERARBOL.setEnabled(true);
            VERARBOL.setBackground(new Color(255, 255, 153));
        } else {
            mensaje = "¡Encontraste una palabra: " + palabra + "!\n(No está en el diccionario, pero es válida)";
            VERARBOL.setEnabled(true);
            GUARDAR.setEnabled(true);
            VERARBOL.setBackground(new Color(255, 255, 153));
            GUARDAR.setBackground(new Color(255, 255, 153));
        }
        JOptionPane.showMessageDialog(this, mensaje);

        palabraEncontradaEnBusqueda = true;
        desactivarModoSeleccion();
        limpiar();
    }
    
    /**
     * Limpia la selección actual
     */
    private void limpiar() {
        for (int casilla : seleccion) {
        int[] pos = tablero.getPosicion(casilla);
        Color colorActual = botonesTablero[pos[0]][pos[1]].getBackground();
        
        if (!colorActual.equals(Color.GREEN) && !colorActual.equals(Color.CYAN)) {
            botonesTablero[pos[0]][pos[1]].setBackground(Color.WHITE);
            }
        }
        seleccion.clear();
    }
    
    
    private void guardarPalabrasNoEnDiccionario() {
        if (palabrasNoEnDiccionario.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "No hay palabras nuevas para guardar.", 
            "Información", 
            JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    if (rutaArchivoDatos == null || rutaArchivoDatos.isEmpty()) {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo donde guardar las palabras nuevas");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Archivos de texto (*.txt)";
            }
        });

        int result = fileChooser.showSaveDialog(this);
        if (result != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        rutaArchivoDatos = fileChooser.getSelectedFile().getAbsolutePath();
            if (!rutaArchivoDatos.toLowerCase().endsWith(".txt")) {
                rutaArchivoDatos += ".txt";
            }
        }

        try {
            // Leer todo el archivo como un String
            StringBuilder contenido = new StringBuilder();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(rutaArchivoDatos));
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
            reader.close();

            // Crear el texto con las nuevas palabras
            StringBuilder nuevasPalabras = new StringBuilder();
            for (String palabra : palabrasNoEnDiccionario) {
                nuevasPalabras.append(palabra.toUpperCase()).append("\n");
            }
            nuevasPalabras.append("/dic");

            // Reemplazar "/dic" con las nuevas palabras + "/dic"
            String nuevoContenido = contenido.toString().replace("/dic", nuevasPalabras.toString());

            // Escribir el archivo completo
            BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivoDatos));
            writer.write(nuevoContenido);
            writer.close();

            // Mostrar mensaje de éxito
            String mensaje = "Se guardo " + palabrasNoEnDiccionario.size() + " palabra(s) nueva(s).\n\n";
            mensaje += "Palabras agregadas:\n";
            for (String palabra : palabrasNoEnDiccionario) {
                mensaje += "• " + palabra + "\n";
            }

            JOptionPane.showMessageDialog(this, 
                mensaje, 
                "Palabras guardadas exitosamente", 
                JOptionPane.INFORMATION_MESSAGE);

            palabrasNoEnDiccionario.clear();
            GUARDAR.setEnabled(false);
            GUARDAR.setBackground(new Color(204, 204, 204));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar las palabras: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Verifica si una palabra está en el diccionario del tablero
     * @param palabra la palabra a verificar
     * @return true si la palabra está en el diccionario, false si no
     */
    private boolean esDelDiccionario(String palabra) {
        for (String p : tablero.getPalabras()) {
            if (p.toUpperCase().equals(palabra.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    

    
    /**
     * Busca todas las palabras usando DFS
     */
    private void buscarDFS() {
        long inicio = System.currentTimeMillis();
        encontradas.clear();
        caminosEncontrados.clear();
        limpiarTablero();
        
        desactivarModoSeleccion();
        
        for (String palabra : tablero.getPalabras()) {
            buscarPalabraDFS(palabra.toUpperCase());
        }

        long tiempo = System.currentTimeMillis() - inicio;
        
        if (!encontradas.isEmpty()) {
            palabraEncontradaEnBusqueda = true;
        }
        
        mostrarResultados("DFS", tiempo);
    }
    
    /**
     * Busca una palabra específica usando DFS
     * @param palabra la palabra a buscar
     */
    private void buscarPalabraDFS(String palabra) {
        for (int i = 0; i < 4 && !encontradas.contains(palabra); i++) {
            for (int j = 0; j < 4 && !encontradas.contains(palabra); j++) {
                boolean[][] visitado = new boolean[4][4];
                ArrayList<Integer> camino = new ArrayList<>();
                if (dfs(i, j, palabra, 0, visitado, camino)) {
                    encontradas.add(palabra);
                    caminosEncontrados.add(new ArrayList<>(camino));
                }
            }
        }
    }
    
    /**
     * Método recursivo para DFS
     * @param fila fila actual
     * @param col columna actual
     * @param palabra palabra que se busca
     * @param pos posición en la palabra
     * @param visitado matriz de casillas visitadas
     * @param camino camino actual
     * @return true si encuentra la palabra
     */
    private boolean dfs(int fila, int col, String palabra, int pos, boolean[][] visitado, ArrayList<Integer> camino) {
        if (fila < 0 || fila >= 4 || col < 0 || col >= 4 || visitado[fila][col]) {
            return false;
        }
        if (tablero.getLetra(fila, col) != palabra.charAt(pos)) {
            return false;
        }
        
        visitado[fila][col] = true;
        camino.add(tablero.getCasilla(fila, col));
        
        if (pos == palabra.length() - 1) {
            return true;
        }
        
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    if (dfs(fila + i, col + j, palabra, pos + 1, visitado, camino)) {
                        return true;
                    }
                }
            }
        }
        
        visitado[fila][col] = false;
        camino.remove(camino.size() - 1);
        return false;
    }
    
    /**
     * Busca todas las palabras usando BFS
     */
    private void buscarBFS() {
        long inicio = System.currentTimeMillis();
        encontradas.clear();
        caminosEncontrados.clear();
        limpiarTablero();
        
        desactivarModoSeleccion();

        for (String palabra : tablero.getPalabras()) {
            buscarPalabraBFS(palabra.toUpperCase());
        }

        long tiempo = System.currentTimeMillis() - inicio;
        
        if (!encontradas.isEmpty()) {
            palabraEncontradaEnBusqueda = true;
        }
        
        mostrarResultados("BFS", tiempo);
    }
    
    /**
     * Busca una palabra específica usando BFS
     * @param palabra la palabra a buscar
     */
    private void buscarPalabraBFS(String palabra) {
        for (int i = 0; i < 4 && !encontradas.contains(palabra); i++) {
            for (int j = 0; j < 4 && !encontradas.contains(palabra); j++) {
                if (tablero.getLetra(i, j) == palabra.charAt(0)) {
                    ArrayList<Integer> camino = bfsEncontrarPalabra(i, j, palabra);
                    if (camino != null) {
                        encontradas.add(palabra);
                        caminosEncontrados.add(camino);
                    }
                }
            }
        }
    }
    
    /**
     * Encuentra una palabra usando BFS
     * @param filaInicial fila donde empieza
     * @param colInicial columna donde empieza
     * @param palabra palabra a buscar
     * @return el camino si encuentra la palabra, null si no
     */
    private ArrayList<Integer> bfsEncontrarPalabra(int filaInicial, int colInicial, String palabra) {
        ArrayList<EstadoBFS> cola = new ArrayList<>();
        
        ArrayList<Integer> caminoInicial = new ArrayList<>();
        caminoInicial.add(tablero.getCasilla(filaInicial, colInicial));
        boolean[][] visitadoInicial = new boolean[4][4];
        visitadoInicial[filaInicial][colInicial] = true;
        
        cola.add(new EstadoBFS(filaInicial, colInicial, 1, caminoInicial, visitadoInicial));
        
        while (!cola.isEmpty()) {
            EstadoBFS actual = cola.remove(0);
            
            if (actual.indice == palabra.length()) {
                return actual.camino;
            }
            
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue;
                    
                    int nuevaFila = actual.fila + i;
                    int nuevaCol = actual.col + j;
                    
                    if (nuevaFila >= 0 && nuevaFila < 4 && nuevaCol >= 0 && nuevaCol < 4 
                        && !actual.visitado[nuevaFila][nuevaCol]) {
                        
                        if (tablero.getLetra(nuevaFila, nuevaCol) == palabra.charAt(actual.indice)) {
                            ArrayList<Integer> nuevoCamino = new ArrayList<>(actual.camino);
                            nuevoCamino.add(tablero.getCasilla(nuevaFila, nuevaCol));
                            
                            boolean[][] nuevoVisitado = new boolean[4][4];
                            for (int x = 0; x < 4; x++) {
                                for (int y = 0; y < 4; y++) {
                                    nuevoVisitado[x][y] = actual.visitado[x][y];
                                }
                            }
                            nuevoVisitado[nuevaFila][nuevaCol] = true;
                            
                            cola.add(new EstadoBFS(nuevaFila, nuevaCol, actual.indice + 1, 
                                                 nuevoCamino, nuevoVisitado));
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Clase para guardar el estado en BFS
     */
    private class EstadoBFS {
        int fila, col, indice;
        ArrayList<Integer> camino;
        boolean[][] visitado;
        
        EstadoBFS(int fila, int col, int indice, ArrayList<Integer> camino, boolean[][] visitado) {
            this.fila = fila;
            this.col = col;
            this.indice = indice;
            this.camino = camino;
            this.visitado = visitado;
        }
    }
    
    /**
     * Limpia todos los colores del tablero
     */
    private void limpiarTablero() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                botonesTablero[i][j].setBackground(Color.WHITE);
            }
        }
    }
    
    /**
     * Actualiza la lista de palabras encontradas (método original)
     */
    private void actualizarEncontradas() {
        String texto = "";
        for (String palabra : encontradas) {
            texto += palabra + "\n";
        }
        ENCONTRADAS.setText(texto);
    }
    /**
    * Muestra los resultados de la búsqueda automática (DFS o BFS)
    * @param metodo el método usado (DFS o BFS)
    * @param tiempo tiempo que tardó en ms
    */
    private void mostrarResultados(String metodo, long tiempo) {
        for (ArrayList<Integer> camino : caminosEncontrados) {
        for (int casilla : camino) {
            int[] pos = tablero.getPosicion(casilla);
            botonesTablero[pos[0]][pos[1]].setBackground(Color.GREEN);
        }
    }

        actualizarEncontradas(); 

        String mensaje = "Método: " + metodo + "\n";
        mensaje += "Encontradas: " + encontradas.size() + "/" + tablero.getPalabras().size() + "\n";
        mensaje += "Tiempo: " + tiempo + " ms";

        JOptionPane.showMessageDialog(this, mensaje);

        if (!encontradas.isEmpty()) {
            palabraEncontradaEnBusqueda = true;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        TABLERO = new javax.swing.JPanel();
        BOTON1A = new javax.swing.JButton();
        BOTON1B = new javax.swing.JButton();
        BOTON1C = new javax.swing.JButton();
        BOTON1D = new javax.swing.JButton();
        BOTON2A = new javax.swing.JButton();
        BOTON2B = new javax.swing.JButton();
        BOTON2C = new javax.swing.JButton();
        BOTON2D = new javax.swing.JButton();
        BOTON3A = new javax.swing.JButton();
        BOTON3B = new javax.swing.JButton();
        BOTON3C = new javax.swing.JButton();
        BOTON3D = new javax.swing.JButton();
        BOTON4A = new javax.swing.JButton();
        BOTON4B = new javax.swing.JButton();
        BOTON4C = new javax.swing.JButton();
        BOTON4D = new javax.swing.JButton();
        P = new javax.swing.JScrollPane();
        PALABRAS = new javax.swing.JTextArea();
        E = new javax.swing.JScrollPane();
        ENCONTRADAS = new javax.swing.JTextArea();
        ENC = new javax.swing.JLabel();
        PAL = new javax.swing.JLabel();
        LIMPIAR = new javax.swing.JButton();
        VOLVER = new javax.swing.JButton();
        BFS = new javax.swing.JButton();
        DFS = new javax.swing.JButton();
        GUARDAR = new javax.swing.JButton();
        VERARBOL = new javax.swing.JButton();
        BUSCAR = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 204, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        TABLERO.setLayout(new java.awt.GridLayout(4, 4, 1, 1));
        TABLERO.add(BOTON1A);
        TABLERO.add(BOTON1B);
        TABLERO.add(BOTON1C);
        TABLERO.add(BOTON1D);
        TABLERO.add(BOTON2A);
        TABLERO.add(BOTON2B);
        TABLERO.add(BOTON2C);
        TABLERO.add(BOTON2D);
        TABLERO.add(BOTON3A);
        TABLERO.add(BOTON3B);
        TABLERO.add(BOTON3C);
        TABLERO.add(BOTON3D);
        TABLERO.add(BOTON4A);
        TABLERO.add(BOTON4B);
        TABLERO.add(BOTON4C);
        TABLERO.add(BOTON4D);

        jPanel1.add(TABLERO, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 400, 380));

        PALABRAS.setColumns(20);
        PALABRAS.setFont(new java.awt.Font("Colonna MT", 1, 14)); // NOI18N
        PALABRAS.setRows(5);
        P.setViewportView(PALABRAS);

        jPanel1.add(P, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 60, 240, 130));

        ENCONTRADAS.setColumns(20);
        ENCONTRADAS.setFont(new java.awt.Font("Colonna MT", 1, 14)); // NOI18N
        ENCONTRADAS.setRows(5);
        E.setViewportView(ENCONTRADAS);

        jPanel1.add(E, new org.netbeans.lib.awtextra.AbsoluteConstraints(444, 240, 240, 130));

        ENC.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        ENC.setText("PALABRAS ENCONTRADAS");
        jPanel1.add(ENC, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 220, 230, -1));

        PAL.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        PAL.setText("PALABRAS ");
        jPanel1.add(PAL, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 40, 120, -1));

        LIMPIAR.setBackground(new java.awt.Color(255, 255, 153));
        LIMPIAR.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        LIMPIAR.setText("LIMPIAR");
        LIMPIAR.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(LIMPIAR, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 380, 110, 30));

        VOLVER.setBackground(new java.awt.Color(255, 102, 102));
        VOLVER.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        VOLVER.setText("VOLVER");
        VOLVER.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        VOLVER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                VOLVERMouseClicked(evt);
            }
        });
        jPanel1.add(VOLVER, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 380, 110, 30));

        BFS.setBackground(new java.awt.Color(255, 255, 153));
        BFS.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        BFS.setText("BFS");
        BFS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(BFS, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, -1, -1));

        DFS.setBackground(new java.awt.Color(255, 255, 153));
        DFS.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        DFS.setText("DFS");
        DFS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(DFS, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 450, -1, -1));

        GUARDAR.setBackground(new java.awt.Color(204, 204, 204));
        GUARDAR.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        GUARDAR.setText("GUARDAR");
        GUARDAR.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        GUARDAR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                GUARDARMouseClicked(evt);
            }
        });
        jPanel1.add(GUARDAR, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 450, -1, -1));

        VERARBOL.setBackground(new java.awt.Color(204, 204, 204));
        VERARBOL.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        VERARBOL.setText("VER ARBOL");
        VERARBOL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                VERARBOLMouseClicked(evt);
            }
        });
        jPanel1.add(VERARBOL, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 450, -1, -1));

        BUSCAR.setBackground(new java.awt.Color(255, 255, 153));
        BUSCAR.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        BUSCAR.setText("BUSCAR");
        BUSCAR.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BUSCAR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BUSCARMouseClicked(evt);
            }
        });
        jPanel1.add(BUSCAR, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 450, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );

        jPanel1.getAccessibleContext().setAccessibleDescription("panel azul");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Maneja el evento de clic del botón VOLVER.
     * Oculta la ventana actual y regresa al menú principal (Ventana1).
     * 
     * @param evt 
    */
    private void VOLVERMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_VOLVERMouseClicked
        // TODO add your handling code here:
        this.dispose();
        Ventana1 ventana1 = new Ventana1();
        ventana1.setLocationRelativeTo(null);
        ventana1.setVisible(true);
    }//GEN-LAST:event_VOLVERMouseClicked
    
    /**
     * Maneja el evento de clic del botón BUSCAR.
     * Permite habilitar la seleccion de letras para buscar una palabra.
     * Activa tambien los botones de VERARBOL y GUARDAR.
     * 
     * @param evt 
    */
    private void BUSCARMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BUSCARMouseClicked
        // TODO add your handling code here:
        activarModoSeleccion();
    }//GEN-LAST:event_BUSCARMouseClicked
    
    /**
     * Maneja el evento de clic del botón VERARBOL.
     * Oculta la ventana actual y abre Ventana5 para ver el arbol de recorrido BFS.
     * 
     * @param evt 
    */
    private void VERARBOLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_VERARBOLMouseClicked
        // TODO add your handling code here:
        if (ultimoCaminoEncontrado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay ningún camino para mostrar.");
            return;
        }

        try {
            Ventana5 ventana5 = new Ventana5(ultimoCaminoEncontrado, ultimaPalabraEncontrada, tablero, this);
            ventana5.setLocationRelativeTo(this);
            ventana5.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al mostrar el árbol BFS: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_VERARBOLMouseClicked

    private void GUARDARMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_GUARDARMouseClicked
        // TODO add your handling code here:
        guardarPalabrasNoEnDiccionario();
    }//GEN-LAST:event_GUARDARMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BFS;
    private javax.swing.JButton BOTON1A;
    private javax.swing.JButton BOTON1B;
    private javax.swing.JButton BOTON1C;
    private javax.swing.JButton BOTON1D;
    private javax.swing.JButton BOTON2A;
    private javax.swing.JButton BOTON2B;
    private javax.swing.JButton BOTON2C;
    private javax.swing.JButton BOTON2D;
    private javax.swing.JButton BOTON3A;
    private javax.swing.JButton BOTON3B;
    private javax.swing.JButton BOTON3C;
    private javax.swing.JButton BOTON3D;
    private javax.swing.JButton BOTON4A;
    private javax.swing.JButton BOTON4B;
    private javax.swing.JButton BOTON4C;
    private javax.swing.JButton BOTON4D;
    private javax.swing.JButton BUSCAR;
    private javax.swing.JButton DFS;
    private javax.swing.JScrollPane E;
    private javax.swing.JLabel ENC;
    private javax.swing.JTextArea ENCONTRADAS;
    private javax.swing.JButton GUARDAR;
    private javax.swing.JButton LIMPIAR;
    private javax.swing.JScrollPane P;
    private javax.swing.JLabel PAL;
    private javax.swing.JTextArea PALABRAS;
    private javax.swing.JPanel TABLERO;
    private javax.swing.JButton VERARBOL;
    private javax.swing.JButton VOLVER;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}

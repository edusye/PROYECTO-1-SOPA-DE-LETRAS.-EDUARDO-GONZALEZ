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

/**
 * Ventana que muestra el tablero de sopa de letras y permite la interacción del usuario.
 * @author edusye
 */
public class Ventana4 extends javax.swing.JFrame {
    private Tablero tablero;
    
    // Variables para la funcionalidad del juego
    private JButton[][] botonesTablero;
    private ArrayList<Integer> caminoSeleccionado;
    private ArrayList<String> palabrasEncontradas;
    
    /**
     * Constructor de la Ventana4.
     * @param tablero el tablero cargado con los datos
     */
    public Ventana4(Tablero tablero) {
        this.tablero = tablero;
        this.caminoSeleccionado = new ArrayList<>();
        this.palabrasEncontradas = new ArrayList<>();
        initComponents();
        inicializarJuego();
    }
    
    /**
     * Inicializa el juego después de crear los componentes.
     */
    private void inicializarJuego() {
        crearTablero();
        mostrarPalabras();
        configurarBotones();

        PALABRAS.setEditable(false);
        ENCONTRADAS.setEditable(false);
        
        // Centrar ventana
        setLocationRelativeTo(null);
        setTitle("Sopa de Letras - Encuentra las palabras");
    }
    
    /**
     * Crea el tablero con las letras.
     */
    private void crearTablero() {
        botonesTablero = new JButton[4][4];
        
        // Asignar botones
        botonesTablero[0][0] = BOTON1A;
        botonesTablero[0][1] = BOTON1B;
        botonesTablero[0][2] = BOTON1C;
        botonesTablero[0][3] = BOTON1D;
        botonesTablero[1][0] = BOTON2A;
        botonesTablero[1][1] = BOTON2B;
        botonesTablero[1][2] = BOTON2C;
        botonesTablero[1][3] = BOTON2D;
        botonesTablero[2][0] = BOTON3A;
        botonesTablero[2][1] = BOTON3B;
        botonesTablero[2][2] = BOTON3C;
        botonesTablero[2][3] = BOTON3D;
        botonesTablero[3][0] = BOTON4A;
        botonesTablero[3][1] = BOTON4B;
        botonesTablero[3][2] = BOTON4C;
        botonesTablero[3][3] = BOTON4D;
        
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
                    clickCasilla(fila, col);
                });
            }
        }
    }
    
    /**
     * Muestra las palabras a encontrar.
     */
    private void mostrarPalabras() {
        String texto = "";
        ArrayList<String> palabras = tablero.getPalabras();
        
        for (int i = 0; i < palabras.size(); i++) {
            texto += palabras.get(i).toUpperCase();
            if (i < palabras.size() - 1) {
                texto += "\n";
            }
        }
        PALABRAS.setText(texto);
    }
    
    /**
     * Configura los botones de la interfaz.
     */
    private void configurarBotones() {
        LIMPIAR.addActionListener((ActionEvent e) -> {
            limpiarTodo();
        });
        
        VOLVER.addActionListener((ActionEvent e) -> {
            regresarVentana();
        });
    }
    
    /**
     * Maneja cuando se hace click en una casilla.
     */
    private void clickCasilla(int fila, int col) {
        int casilla = tablero.getCasilla(fila, col);
        
        if (caminoSeleccionado.isEmpty()) {
            caminoSeleccionado.add(casilla);
            botonesTablero[fila][col].setBackground(Color.YELLOW);
        } else {
            if (caminoSeleccionado.contains(casilla)) {
                if (caminoSeleccionado.get(caminoSeleccionado.size() - 1) == casilla) {
                    verificarPalabra();
                }
                return;
            }
            
            int ultimaCasilla = caminoSeleccionado.get(caminoSeleccionado.size() - 1);
            if (tablero.estanConectadas(ultimaCasilla, casilla)) {
                caminoSeleccionado.add(casilla);
                botonesTablero[fila][col].setBackground(Color.YELLOW);
            } else {
                JOptionPane.showMessageDialog(this, "Las casillas deben estar conectadas!");
            }
        }
    }
    
    /**
     * Verifica si la palabra formada es correcta.
     */
    private void verificarPalabra() {
        if (caminoSeleccionado.size() < 3) {
            JOptionPane.showMessageDialog(this, "Las palabras deben tener al menos 3 letras.");
            limpiarTodo();
            return;
        }
        
        String palabra = "";
        for (int casilla : caminoSeleccionado) {
            palabra += tablero.getLetraCasilla(casilla);
        }
        
        String palabraFormada = palabra.toUpperCase();
        boolean encontrada = false;
        for (String palabraTablero : tablero.getPalabras()) {
            if (palabraTablero.toUpperCase().equals(palabraFormada)) {
                encontrada = true;
                break;
            }
        }
        
        if (encontrada) {
            if (!palabrasEncontradas.contains(palabraFormada)) {
                palabrasEncontradas.add(palabraFormada);
                marcarPalabraVerde();
                actualizarEncontradas();
                JOptionPane.showMessageDialog(this, "¡Palabra encontrada: " + palabraFormada + "!");
                
                if (palabrasEncontradas.size() == tablero.getPalabras().size()) {
                    JOptionPane.showMessageDialog(this, "¡Felicitaciones! Has encontrado todas las palabras.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Ya encontraste esta palabra: " + palabraFormada);
            }
        } else {
            JOptionPane.showMessageDialog(this, "La palabra '" + palabraFormada + "' no está en el diccionario.");
        }
        
        limpiarTodo();
    }
    
    /**
     * Marca la palabra encontrada en verde.
     */
    private void marcarPalabraVerde() {
        for (int casilla : caminoSeleccionado) {
            int[] pos = tablero.getPosicion(casilla);
            botonesTablero[pos[0]][pos[1]].setBackground(Color.GREEN);
        }
    }
    
    /**
     * Limpia la selección actual.
     */
    private void limpiarTodo() {
        for (int casilla : caminoSeleccionado) {
            int[] pos = tablero.getPosicion(casilla);
            if (!botonesTablero[pos[0]][pos[1]].getBackground().equals(Color.GREEN)) {
                botonesTablero[pos[0]][pos[1]].setBackground(Color.WHITE);
            }
        }
        caminoSeleccionado.clear();
    }
    
    /**
     * Actualiza las palabras encontradas.
     */
    private void actualizarEncontradas() {
        String texto = "";
        for (int i = 0; i < palabrasEncontradas.size(); i++) {
            texto += palabrasEncontradas.get(i);
            if (i < palabrasEncontradas.size() - 1) {
                texto += "\n";
            }
        }
        ENCONTRADAS.setText(texto);
    }
    
    /**
     * Regresa a la ventana anterior.
     */
    private void regresarVentana() {
        this.dispose();
        Ventana1 ventana1 = new Ventana1();
        ventana1.setLocationRelativeTo(null);
        ventana1.setVisible(true);
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
        jPanel3 = new javax.swing.JPanel();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        PALABRAS = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        ENCONTRADAS = new javax.swing.JTextArea();
        ENC = new javax.swing.JLabel();
        PAL = new javax.swing.JLabel();
        LIMPIAR = new javax.swing.JButton();
        VOLVER = new javax.swing.JButton();
        BFS = new javax.swing.JButton();
        DFS = new javax.swing.JButton();
        AGREGAR = new javax.swing.JButton();
        VERARBOL = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 204, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setLayout(new java.awt.GridLayout(4, 4, 4, 0));
        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, -1, -1));

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

        jPanel1.add(TABLERO, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 400, 380));

        PALABRAS.setColumns(20);
        PALABRAS.setFont(new java.awt.Font("Colonna MT", 1, 14)); // NOI18N
        PALABRAS.setRows(5);
        jScrollPane1.setViewportView(PALABRAS);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 90, -1, 130));

        ENCONTRADAS.setColumns(20);
        ENCONTRADAS.setFont(new java.awt.Font("Colonna MT", 1, 14)); // NOI18N
        ENCONTRADAS.setRows(5);
        jScrollPane2.setViewportView(ENCONTRADAS);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 260, -1, 130));

        ENC.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        ENC.setText("PALABRAS ENCONTRADAS");
        jPanel1.add(ENC, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 230, 230, -1));

        PAL.setFont(new java.awt.Font("Colonna MT", 1, 18)); // NOI18N
        PAL.setText("PALABRAS ");
        jPanel1.add(PAL, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 60, 120, -1));

        LIMPIAR.setBackground(new java.awt.Color(255, 255, 153));
        LIMPIAR.setText("LIMPIAR");
        jPanel1.add(LIMPIAR, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 400, 90, 30));

        VOLVER.setBackground(new java.awt.Color(255, 102, 102));
        VOLVER.setText("VOLVER");
        jPanel1.add(VOLVER, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 400, 90, 30));

        BFS.setText("BFS");
        jPanel1.add(BFS, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, -1, -1));

        DFS.setText("DFS");
        jPanel1.add(DFS, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 450, -1, -1));

        AGREGAR.setText("AGREGAR");
        jPanel1.add(AGREGAR, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 450, -1, -1));

        VERARBOL.setText("VER ARBOL");
        jPanel1.add(VERARBOL, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 450, -1, -1));

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

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AGREGAR;
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
    private javax.swing.JButton DFS;
    private javax.swing.JLabel ENC;
    private javax.swing.JTextArea ENCONTRADAS;
    private javax.swing.JButton LIMPIAR;
    private javax.swing.JLabel PAL;
    private javax.swing.JTextArea PALABRAS;
    private javax.swing.JPanel TABLERO;
    private javax.swing.JButton VERARBOL;
    private javax.swing.JButton VOLVER;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}

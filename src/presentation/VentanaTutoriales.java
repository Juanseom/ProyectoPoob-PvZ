package presentation;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
/**
 * Ventana que muestra los tutoriales y reglas del juego POOB vs Zombies.
 * Proporciona información sobre cómo jugar, condiciones de victoria, sistema de puntajes y modalidades de juego.
 */
public class VentanaTutoriales extends JFrame {
    private JLayeredPane layeredPane;
    private JButton botonBack;

    /**
     * Constructor de la ventana de tutoriales.
     * Inicializa y configura los elementos de la interfaz.
     */
    public VentanaTutoriales() {
        setTitle("POOB vs Zombies - Tutoriales");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        JPanel panelBack = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBack.setOpaque(false);

        prepareElements();
        prepareActions();

        setVisible(true);
    }

    /**
     * Prepara y coloca los elementos visuales en la ventana.
     * Crea paneles de información para cada sección del tutorial.
     */
    private void prepareElements(){
        ImageIcon jardinBackground = new ImageIcon("resources/jardinBackground.png");
        JLabel backgroundLabel = new JLabel(jardinBackground);
        backgroundLabel.setBounds(0, 0, 800, 600);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        JPanel panel1 = crearPanelTutorial("¿COMO JUGAR?:\n Primero selecciona el modo de juego que desees jugar, cuando entres a la partida debes de elegir las" +
                "plantas con las que va a jugar, cada planta tiene su debido costo de soles, el contador de soles va aumentando en 25 cada 10 segundos" +
                "cuando selecciones la planta que quieres plantar debes seleccionar una casilla en la matriz de 5x10 (solo puedes plantar entre la 2da y 9na columna)." +
                "Los zombies comienzan a aparecer despues de 20 segundos al iniciar la partida.", 50, 50);
        layeredPane.add(panel1, JLayeredPane.PALETTE_LAYER);

        JPanel panel2 = crearPanelTutorial("CONDICIONES DE VICTORIA:\n Si el jugador de los zombies logra hacer llegar un zombie a la casa de Dave," +
                "gana el jugador de los zombies. Si se acaba el tiempo de la partida, el ganador se define por el mayor puntaje.", 50, 160);
        layeredPane.add(panel2, JLayeredPane.PALETTE_LAYER);

        JPanel panel3 = crearPanelTutorial("SISTEMA DE PUNTAJES:\n Plantas: Cantidad restante de soles en el contador más el valor de las plantas que quedaron en el" +
                "tablero en términos de sus costos en soles. Esta cantidad se multiplica por 1.5.\n Zombies: Cantidad restante de cerebros en el contador más el valor de las zombies que quedan el" +
                "tablero en términos de sus costos en cerebros.", 50, 270);
        layeredPane.add(panel3, JLayeredPane.PALETTE_LAYER);

        JPanel panel4 = crearPanelTutorial("MODALIDADES DE JUEGO:\n Player vs Machine (PvsM): En esta modalidad el jugador solo controla las plantas y los zombies" +
                "los maneja la máquina.\n Machine vs Machine (MvsM): Tanto las plantas como los zombies los manejan las máquinas de forma estratégica.\n" +
                "Player vs Player (PvsP): En esta modalidad se tendrán dos jugadores, donde uno controla las plantas y el otro los zombies. " +
                "En esta modalidad hay un cambio en las reglas del juego inspirado en el modo Supervivencia", 50, 380);
        layeredPane.add(panel4, JLayeredPane.PALETTE_LAYER);

        botonBack = new JButton("⬅");
        botonBack.setBounds(10, 10, 50, 30);
        botonBack.setContentAreaFilled(false);
        botonBack.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.BLACK));
        botonBack.setFocusPainted(false);
        layeredPane.add(botonBack, JLayeredPane.PALETTE_LAYER);
    }

    /**
     * Crea un panel personalizado para mostrar información del tutorial.
     *
     * @param texto El texto a mostrar en el panel.
     * @param x La coordenada x del panel en la ventana.
     * @param y La coordenada y del panel en la ventana.
     * @return JPanel configurado con el texto del tutorial.
     */
    private JPanel crearPanelTutorial(String texto, int x, int y) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBounds(x, y, 700, 100);
        panel.setBackground(new Color(255, 255, 255, 200)); // Blanco semi-transparente

        JTextArea textArea = new JTextArea(texto);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 12f));

        panel.add(textArea, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    /**
     * Configura las acciones de los componentes interactivos.
     * Incluye la lógica para volver al menú principal o salir del juego.
     */
    private void prepareActions(){
        botonBack.addActionListener(e -> volverAlMenu());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Object[] opciones = {"Volver al menu", "Salir del juego"};
                int response = JOptionPane.showOptionDialog(VentanaTutoriales.this, "¿Que deseas hacer?", "Cerrar ventana",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
                if (response == 0) {
                    volverAlMenu();
                }
                if (response == 1) {
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Cierra esta ventana y vuelve al menú principal.
     */
    private void volverAlMenu(){
        dispose();
        GameGUI.getInstance().mostrar();
    }
}
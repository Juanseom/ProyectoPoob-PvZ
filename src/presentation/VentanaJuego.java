package presentation;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Ventana principal para elegir el modo  de juego  de POOB vs Zombies.
 */
public class VentanaJuego extends JFrame {
    private JLayeredPane layeredPane;
    private static VentanaJuego instance;

    private JButton botonModoDeJuego;
    private JButton botonBack;

    /**
     * Constructor de VentanaJuego.
     * Inicializa y configura los elementos de la interfaz.
     */
    public VentanaJuego() {
        instance = this;
        setTitle("POOB vs Zombies - Jugar");
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
     */
    private void prepareElements() {
        ImageIcon jardinBackground = new ImageIcon("resources/jardinBackground.png");
        JLabel backgroundLabel = new JLabel(jardinBackground);
        backgroundLabel.setBounds(0, 0, 800, 600);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        ImageIcon imagenLapida = new ImageIcon("resources/lapida.png");
        JLabel lapidaLabel = new JLabel(imagenLapida);
        lapidaLabel.setBounds(200, 10, 400, 700);
        layeredPane.add(lapidaLabel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER + 1));


        botonModoDeJuego = new JButton("Modalidad de Juego");
        botonModoDeJuego.setBounds(310, 200, 200, 100);
        botonModoDeJuego.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonModoDeJuego.setContentAreaFilled(false);
        botonModoDeJuego.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.BLACK));
        botonModoDeJuego.setFocusPainted(false);
        botonModoDeJuego.setOpaque(false);
        botonModoDeJuego.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 20f));

        layeredPane.add(botonModoDeJuego, JLayeredPane.PALETTE_LAYER);

        botonBack = new JButton("⬅");
        botonBack.setBounds(10, 10, 50, 30);
        botonBack.setContentAreaFilled(false);
        botonBack.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.BLACK));
        botonBack.setFocusPainted(false);
        layeredPane.add(botonBack, JLayeredPane.PALETTE_LAYER);

    }

    /**
     * Configura las acciones de los componentes interactivos.
     */
    private void prepareActions() {
        botonBack.addActionListener(e -> volverAlMenu());
        botonModoDeJuego.addActionListener(e -> mostrarMenuModoDeJuego());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Object[] opciones = {"Volver al menu", "Salir del juego"};
                int response = JOptionPane.showOptionDialog(VentanaJuego.this, "¿Que deseas hacer?", "Cerrar ventana",
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
     * Muestra un diálogo para seleccionar el modo de juego.
     */
    private void mostrarMenuModoDeJuego() {
        String[] opciones = {"PLAYER vs ZOMBIES", "PLAYER vs PLAYER", "MACHINE vs MACHINE"};
        String seleccion = (String) JOptionPane.showInputDialog(this,
                "Seleccione el modo de juego:", "Modo de Juego",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (seleccion != null) {
            botonModoDeJuego.setText("Modo: " + seleccion);
            if (seleccion.equals("PLAYER vs ZOMBIES")) {
                SwingUtilities.invokeLater(() -> {
                    VentanaSeleccionPlantas ventanaSeleccionPlantas = new VentanaSeleccionPlantas();
                    ventanaSeleccionPlantas.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            List<String> plantasSeleccionadas = ventanaSeleccionPlantas.getPlantasSeleccionadas();
                            new VentanaPvsM(plantasSeleccionadas);
                            setVisible(false);
                        }
                    });
                });
            }
            if (seleccion.equals("PLAYER vs PLAYER")) {
                SwingUtilities.invokeLater(() -> {
                    VentanaSeleccionPlantas ventanaSeleccionPlantas = new VentanaSeleccionPlantas();
                    ventanaSeleccionPlantas.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            List<String> plantasSeleccionadas = ventanaSeleccionPlantas.getPlantasSeleccionadas();
                            SwingUtilities.invokeLater(() -> {
                                VentanaSeleccionZombies ventanaSeleccionZombies = new VentanaSeleccionZombies();
                                ventanaSeleccionZombies.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosed(WindowEvent e) {
                                        List<String> zombiesSeleccionados = ventanaSeleccionZombies.getZombiesSeleccionados();
                                        new VentanaPvsP(plantasSeleccionadas, zombiesSeleccionados);
                                        setVisible(false);
                                    }
                                });
                            });
                        }
                    });
                });
            }
            if (seleccion.equals("MACHINE vs MACHINE")) {
                SwingUtilities.invokeLater(() -> {
                    VentanaSeleccionPlantas ventanaSeleccionPlantas = new VentanaSeleccionPlantas();
                    ventanaSeleccionPlantas.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            List<String> plantasSeleccionadas = ventanaSeleccionPlantas.getPlantasSeleccionadas();
                            SwingUtilities.invokeLater(() -> {
                                VentanaSeleccionZombies ventanaSeleccionZombies = new VentanaSeleccionZombies();
                                ventanaSeleccionZombies.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosed(WindowEvent e) {
                                        List<String> zombiesSeleccionados = ventanaSeleccionZombies.getZombiesSeleccionados();
                                        new VentanaMvsM(plantasSeleccionadas, zombiesSeleccionados);
                                        setVisible(false);
                                    }
                                });
                            });
                        }
                    });
                });
            }
        }
        botonModoDeJuego.setContentAreaFilled(false);
    }

    /**
     * Cierra esta ventana y vuelve al menú principal.
     */
    private void volverAlMenu(){
        dispose();
        GameGUI.getInstance().mostrar();
    }

    /**
     * Hace visible la ventana de juego.
     */
    public void mostrar(){
        setVisible(true);
    }

    /**
     * Obtiene la instancia única de VentanaJuego.
     * @return La instancia de VentanaJuego.
     */
    public static VentanaJuego getInstance(){
        return instance;
    }
}
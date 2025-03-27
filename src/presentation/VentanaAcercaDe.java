package presentation;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana que muestra la información "Acerca de" del juego POOB vs Zombies.
 */
public class VentanaAcercaDe extends JFrame {
    private JLayeredPane layeredPane;
    private JButton botonBack;

    /**
     * Constructor de la ventana "Acerca de".
     * Inicializa y configura los elementos de la interfaz.
     */
    public VentanaAcercaDe() {
        setTitle("POOB vs Zombies - Acerca de");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        JPanel panelBack = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBack.setOpaque(false);
        layeredPane.add(panelBack, BorderLayout.NORTH);

        prepareElements();
        prepareActions();

        setVisible(true);
    }

    /**
     * Prepara y coloca los elementos visuales en la ventana.
     */
    private void prepareElements(){
        ImageIcon jardinBackground = new ImageIcon("resources/jardinBackground.png");
        JLabel backgroundLabel = new JLabel(jardinBackground);
        backgroundLabel.setBounds(0, 0, 800, 600);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        ImageIcon imagenCartel = new ImageIcon("resources/imagenCartel.png");
        JLabel cartelLabel = new JLabel(imagenCartel);
        cartelLabel.setBounds(200, 10, 400, 507);
        layeredPane.add(cartelLabel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER + 1));

        botonBack = new JButton("⬅");
        botonBack.setBounds(10, 10, 50, 30);
        botonBack.setContentAreaFilled(false);
        botonBack.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.BLACK));
        botonBack.setFocusPainted(false);
        layeredPane.add(botonBack, JLayeredPane.PALETTE_LAYER);

        JPanel panelCreditos = crearPanelCreditos();
        panelCreditos.setBounds(200,45,400,400);
        layeredPane.add(panelCreditos, JLayeredPane.POPUP_LAYER);
    }

    /**
     * Configura las acciones de los componentes interactivos.
     */
    private void prepareActions(){
        botonBack.addActionListener(e -> volverAlMenu());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Object[] opciones = {"Volver al menu", "Salir del juego"};
                int response = JOptionPane.showOptionDialog(VentanaAcercaDe.this, "¿Que deseas hacer?", "Cerrar ventana",
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
     * Crea y devuelve un panel con la información de créditos.
     * @return JPanel con la información de créditos del juego.
     */
    private JPanel crearPanelCreditos(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel tituloLabel = new JLabel("POOB vs Zombies");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloLabel.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 30f));

        JLabel versionLabel = new JLabel("Versión 1.0");
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 25f));

        JLabel creadoresLabel = new JLabel("Creado por:");
        creadoresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        creadoresLabel.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 25f));

        JLabel creador1Label = new JLabel("- Juan Sebastian Ortega");
        creador1Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        creador1Label.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 20f));

        JLabel creador2Label = new JLabel("- Juan Miguel Rojas");
        creador2Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        creador2Label.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 20f));

        JLabel fechaLabel = new JLabel("Fecha:  2024");
        fechaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fechaLabel.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 20f));

        JLabel universidadLabel = new JLabel("Escuela Colombiana de Ingeniería Julio Garavito");
        universidadLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        universidadLabel.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 20f));

        panel.add(tituloLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(versionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(creadoresLabel);
        panel.add(creador1Label);
        panel.add(creador2Label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(fechaLabel);
        panel.add(universidadLabel);

        return panel;
    }


    /**
     * Cierra esta ventana y vuelve al menú principal.
     */
    private void volverAlMenu(){
        dispose();
        GameGUI.getInstance().mostrar();
    }
}
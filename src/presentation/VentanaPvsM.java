package presentation;

import domain.*;

import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Ventana que representa el modo de juego Player vs Machine (PvsM) en POOB vs Zombies.
 * Esta clase maneja la interfaz gráfica para el modo de juego donde un jugador controla las plantas
 * contra los zombies controlados por la máquina.
 */
public class VentanaPvsM extends JFrame implements Serializable {
    private GamePvsM gamePvsM;

    private JLayeredPane layeredPane;
    private JPanel tableroPanel;
    private JLabel contadorSolesLabel;
    private Clip gameClip;

    private String plantaSeleccionada;
    private Cursor cursorGirasol, cursorNuez, cursorMina, cursorECIPlant, cursorLanzaGuisantes,cursorEvolve,cursorPala;
    private List<String> plantasSeleccionadas;

    //Timers
    private transient Timer movimientoZombieTimer;
    private transient Timer timerActualizacion;
    private transient Timer timerSoles;
    private transient Timer dañoZombieTimer;
    private transient Timer oleadasTimer;
    private transient Timer zombieTimerProgresi;
    private transient Timer mensajeTimer;
    private transient Timer zombieTimer;
    private transient Timer ataqueTimer;


    private static final int TIEMPO_INICIAL_ZOMBIES = 20000; // 20 segundos
    private static final int TIEMPO_INICIAL_GENERACION_ZOMBIE = 10000; // 10 segundos
    private static final int TIEMPO_MINIMO_GENERACION_ZOMBIE = 2000; // 2 segundos
    private static final int DECREMENTO_TIEMPO = 500; // Reducción de 0.5 segundos en cada generación
    private int tiempoActualGeneracionZombie;


    private JLabel[] podadoraLabels;
    private List<Timer> zombieTimers = new ArrayList<>();

    private Map<JButton, Timer> evolveTimers;
    private Map<JButton, Integer> evolveStages;



    /**
     * Constructor de la ventana PvsM.
     * Inicializa la ventana, configura los elementos de la interfaz y prepara las acciones.
     *
     * @param plantasSeleccionadas Lista de plantas seleccionadas por el jugador.
     */
    public VentanaPvsM(List<String> plantasSeleccionadas) {
        this.plantasSeleccionadas = plantasSeleccionadas;
        setTitle("POOB vs Zombies - Player vs Zombies");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        JPanel panelBack = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBack.setOpaque(false);

        prepareElements();
        prepareActions();
        inicializarPodadoras();
        evolveTimers = new HashMap<>();
        evolveStages = new HashMap<>();

        GameGUI.getInstance().stopMusic();

        playMusic("resources/peleaTheme.wav");

        gamePvsM = new GamePvsM();
        iniciarTimerActualizacion();
        iniciarTimerSoles();
        iniciarOleadasZombies();
        inicializarLanzanguisantes();
        setVisible(true);
    }

    /**
     * Prepara y coloca los elementos visuales en la ventana.
     * Incluye el fondo del juego, el inventario de plantas, y otros elementos de la interfaz.
     */
    private void prepareElements() {
        crearMenuBar();
        prepareTablero();
        prepareCursores();
        prepareInventario();

        ImageIcon imagenBackground = new ImageIcon("resources/patioPvsZ.png");
        JLabel backgroundLabel = new JLabel(imagenBackground);
        backgroundLabel.setBounds(0, 0, 800, 600);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        JButton botonPausa;

        botonPausa = new JButton("Pausar");
        botonPausa.setBounds(600, 10, 105, 35);
        botonPausa.setBackground(Color.RED);
        botonPausa.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.BLACK));
        botonPausa.setFocusPainted(false);
        botonPausa.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 20f));
        botonPausa.addActionListener(e -> mostrarLapida());
        layeredPane.add(botonPausa, JLayeredPane.PALETTE_LAYER);

    }

    private void mostrarLapida() {
        detenerTimers(); // Pausa el juego

        // Crear el panel principal de la lápida
        JPanel lapidaPanel = new JPanel();
        lapidaPanel.setLayout(new BoxLayout(lapidaPanel, BoxLayout.Y_AXIS));
        lapidaPanel.setBackground(new Color(60, 60, 60));
        lapidaPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));

        JLabel titulo = new JLabel("Juego en Pausa");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(GameGUI.fuenteZombi.deriveFont(Font.BOLD, 24f));
        titulo.setForeground(Color.WHITE);

        JButton reanudarButton = new JButton("Reanudar Partida");
        JButton guardarButton = new JButton("Guardar Partida");
        JButton cargarButton = new JButton("Cargar Partida");
        JButton terminarButton = new JButton("Terminar Partida");
        JButton volverModosButton = new JButton("Volver a Modos");
        JButton volverMenuButton = new JButton("Volver al Menú");

        // Crear el diálogo de la lápida
        JDialog dialogoLapida = new JDialog(this, "Juego en Pausa", true);

        // Configuración de botones
        configurarBotonLapida(reanudarButton, e -> {
            iniciarTimers(); // Reanuda el juego
            dialogoLapida.dispose(); // Cierra la ventana de la lápida
        });
        configurarBotonLapida(guardarButton, e -> optionSaveAction());
        configurarBotonLapida(cargarButton, e -> optionImportAction());
        configurarBotonLapida(terminarButton, e -> terminarPartida(e));
        configurarBotonLapida(volverModosButton, e -> volverAModos());
        configurarBotonLapida(volverMenuButton, e -> volverAlMenu());

        // Agregar componentes al panel
        lapidaPanel.add(Box.createVerticalStrut(10));
        lapidaPanel.add(titulo);
        lapidaPanel.add(Box.createVerticalStrut(15));
        lapidaPanel.add(reanudarButton);
        lapidaPanel.add(guardarButton);
        lapidaPanel.add(cargarButton);
        lapidaPanel.add(terminarButton);
        lapidaPanel.add(volverModosButton);
        lapidaPanel.add(volverMenuButton);

        // Mostrar el diálogo
        dialogoLapida.getContentPane().add(lapidaPanel);
        dialogoLapida.setSize(300, 400);
        dialogoLapida.setLocationRelativeTo(this);
        dialogoLapida.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogoLapida.setVisible(true);
    }


    private void configurarBotonLapida(JButton boton, ActionListener accion) {
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(200, 30));
        boton.setFocusPainted(false);
        boton.setBackground(Color.DARK_GRAY);
        boton.setForeground(Color.WHITE);
        boton.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 16f));
        boton.addActionListener(accion);
    }


    /**
     * Crea y configura la barra de menú del juego.
     * Incluye opciones para pausar, guardar, cargar y terminar la partida.
     */
    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuJuego = new JMenu("Juego");

        JMenuItem itemPausar = new JMenuItem("Pausar");
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        JMenuItem itemCargar = new JMenuItem("Cargar");
        JMenuItem itemTerminar = new JMenuItem("Terminar");

        itemPausar.addActionListener(this::pausarJuego);
        itemGuardar.addActionListener(e -> optionSaveAction());
        itemCargar.addActionListener(e -> optionImportAction());
        itemCargar.addActionListener(e -> optionImportAction());

        menuJuego.add(itemPausar);
        menuJuego.add(itemGuardar);
        menuJuego.add(itemCargar);
        menuJuego.add(itemTerminar);

        menuBar.add(menuJuego);
        setJMenuBar(menuBar);
    }

    private void prepareCursores() {
        ImageIcon girasolIcon = new ImageIcon("resources/girasol.png");
        cursorGirasol = Toolkit.getDefaultToolkit().createCustomCursor(
                girasolIcon.getImage(), new Point(0, 0), "cursorGirasol");

        ImageIcon nuezIcon = new ImageIcon("resources/nuez.png");
        cursorNuez = Toolkit.getDefaultToolkit().createCustomCursor(
                nuezIcon.getImage(), new Point(0, 0), "cursorNuez");

        ImageIcon lanzaguisantesIcon = new ImageIcon("resources/lanzaguisantes.png");
        cursorLanzaGuisantes = Toolkit.getDefaultToolkit().createCustomCursor(
                lanzaguisantesIcon.getImage(), new Point(0, 0), "cursorLanzaGuisantes");

        ImageIcon minaIcon = new ImageIcon("resources/mina.png");
        cursorMina = Toolkit.getDefaultToolkit().createCustomCursor(
                minaIcon.getImage(), new Point(0, 0), "cursorMina");

        ImageIcon eciplantIcon = new ImageIcon("resources/ECIPlant.png");
        cursorECIPlant = Toolkit.getDefaultToolkit().createCustomCursor(
                eciplantIcon.getImage(), new Point(0, 0), "cursorECIPlant");

        ImageIcon palaIcon = new ImageIcon("resources/pala.png");
        cursorPala = Toolkit.getDefaultToolkit().createCustomCursor(
                palaIcon.getImage(), new Point(0, 0), "cursorPala");

        ImageIcon evolveIcon = new ImageIcon("resources/evolve.png");
        cursorEvolve = Toolkit.getDefaultToolkit().createCustomCursor(
                evolveIcon.getImage(), new Point(0, 0), "cursorEvolve");
    }


    /**
     * Prepara el tablero de juego.
     *
     * @return Panel que representa el tablero de juego.
     */
    private JPanel prepareTablero() {
        tableroPanel = new JPanel();
        tableroPanel.setLayout(new GridLayout(5, 10));
        tableroPanel.setOpaque(false);
        tableroPanel.setBounds(12, 55, 750, 480);
        for (int i = 0; i < 5 * 10; i++) {
            JButton cell = new JButton();
            cell.setOpaque(false);
            cell.setContentAreaFilled(false);
            cell.setBorderPainted(false);
            cell.setFocusPainted(false);
            cell.addActionListener(e -> plantarEnCelda((JButton) e.getSource()));
            tableroPanel.add(cell);
        }
        layeredPane.add(tableroPanel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER + 1));
        return tableroPanel;
    }

    /**
     * Prepara el inventario de plantas.
     */
    private void prepareInventario() {
        prepareCartasInvertario();

        ImageIcon imagenInventario = new ImageIcon("resources/plantasInventario.png");
        JLabel inventarioLabel = new JLabel(imagenInventario);
        inventarioLabel.setBounds(30, -160, 500, 400);
        layeredPane.add(inventarioLabel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER + 2));

        ImageIcon imagenPala = new ImageIcon("resources/palaInventario.png");
        JButton botonPala = new JButton(imagenPala);
        botonPala.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonPala.setBounds(500, 0, 50, 50);
        botonPala.addActionListener(e -> seleccionarPala());
        layeredPane.add(botonPala, JLayeredPane.PALETTE_LAYER);

        contadorSolesLabel = new JLabel("50");
        contadorSolesLabel.setBounds(85, 55, 60, 30);
        contadorSolesLabel.setForeground(Color.black);
        contadorSolesLabel.setFont(GameGUI.fuenteZombi.deriveFont(Font.BOLD, 20f));

        layeredPane.add(contadorSolesLabel, JLayeredPane.PALETTE_LAYER);
    }

    /**
     * Prepara las imagenes y la posicion de todas las cartas para seleccionar a las plantas
     */
    private void prepareCartasInvertario() {
        if (plantasSeleccionadas.contains("Girasol")) {
            ImageIcon cartaGirasol = new ImageIcon("resources/cartaGirasol.png");
            JButton botonGirasol = new JButton(cartaGirasol);
            botonGirasol.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonGirasol.setBounds(140, 7, 49, 69);
            botonGirasol.addActionListener(e -> seleccionarPlanta("Girasol"));
            layeredPane.add(botonGirasol, JLayeredPane.PALETTE_LAYER);
        }

        if (plantasSeleccionadas.contains("LanzaGuisantes")) {
            ImageIcon cartaLanzaguisantes = new ImageIcon("resources/cartaLanzaguisantes.png");
            JButton botonLanzaguisantes = new JButton(cartaLanzaguisantes);
            botonLanzaguisantes.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonLanzaguisantes.setBounds(200, 7, 49, 69);
            botonLanzaguisantes.addActionListener(e -> seleccionarPlanta("LanzaGuisantes"));
            layeredPane.add(botonLanzaguisantes, JLayeredPane.PALETTE_LAYER);
        }

        if (plantasSeleccionadas.contains("Nuez")) {
            ImageIcon cartaNuez = new ImageIcon("resources/cartaNuez.png");
            JButton botonNuez = new JButton(cartaNuez);
            botonNuez.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonNuez.setBounds(259, 7, 49, 69);
            botonNuez.addActionListener(e -> seleccionarPlanta("Nuez"));
            layeredPane.add(botonNuez, JLayeredPane.PALETTE_LAYER);
        }

        if (plantasSeleccionadas.contains("Mina")) {
            ImageIcon cartaMina = new ImageIcon("resources/cartaMina.png");
            JButton botonMina = new JButton(cartaMina);
            botonMina.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonMina.setBounds(318, 7, 49, 69);
            botonMina.addActionListener(e -> seleccionarPlanta("Mina"));
            layeredPane.add(botonMina, JLayeredPane.PALETTE_LAYER);
        }

        if (plantasSeleccionadas.contains("ECIPlant")) {
            ImageIcon cartaEciPlant = new ImageIcon("resources/cartaEciPlant.png");
            JButton botonEciPlant = new JButton(cartaEciPlant);
            botonEciPlant.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonEciPlant.setBounds(377, 7, 49, 69);
            botonEciPlant.addActionListener(e -> seleccionarPlanta("ECIPlant"));
            layeredPane.add(botonEciPlant, JLayeredPane.PALETTE_LAYER);
        }

        if (plantasSeleccionadas.contains("Evolve")) {
            ImageIcon cartaEvolve = new ImageIcon("resources/cartaEvolve.png");
            JButton botonEvolve = new JButton(cartaEvolve);
            botonEvolve.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonEvolve.setBounds(435, 7, 49, 69);
            botonEvolve.addActionListener(e -> seleccionarPlanta("Evolve"));
            layeredPane.add(botonEvolve, JLayeredPane.PALETTE_LAYER);
        }
    }

    /**
     * Inicializa las podadoras en el juego.
     */
    private void inicializarPodadoras() {
        podadoraLabels = new JLabel[5];
        for (int fila = 0; fila < 5; fila++) {
            JLabel podadoraLabel = new JLabel();
            podadoraLabel.setBounds(12, 55 + fila * 96, 75, 96);
            layeredPane.add(podadoraLabel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER + 2));
            podadoraLabels[fila] = podadoraLabel;
        }
    }

    /**
     * Actualiza el estado visual de las podadoras.
     */
    public void actualizarPodadoras() {
        for (int fila = 0; fila < 5; fila++) {
            if (!gamePvsM.tienePodadora(fila)) {
                ImageIcon peligroIcon = new ImageIcon("resources/warning.png");
                podadoraLabels[fila].setIcon(peligroIcon);
            } else {
                podadoraLabels[fila].setIcon(null);
            }

        }
    }

    /**
     * Inicia un temporizador para actualizar periódicamente el estado del juego.
     */
    private void iniciarTimerActualizacion() {
        timerActualizacion = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarPodadoras();
                verificarGameOver();
            }
        });
        timerActualizacion.start();
    }

    /**
     * Inicia un temporizador para generar soles periódicamente.
     */
    private void iniciarTimerSoles() {
        timerSoles = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePvsM.actualizarEstadoJuego();
                actualizarContadorSoles();
            }
        });
        timerSoles.start();
    }

    private void inicializarLanzanguisantes() {
        dañoZombieTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atacarZombies(); // Aplica daño a los zombies
            }
        });
        dañoZombieTimer.setRepeats(true);
        dañoZombieTimer.start(); // Inicia el temporizador
    }

    /**
     * Actualiza el contador de soles en la interfaz gráfica.
     */
    private void actualizarContadorSoles() {
        SwingUtilities.invokeLater(() -> {
            contadorSolesLabel.setText(String.valueOf(gamePvsM.getContadorSoles()));
        });
    }

    /**
     * Inicia las oleadas de zombies en el juego.
     */
    private void iniciarOleadasZombies() {
        oleadasTimer = new Timer(1000, new ActionListener() {
            private int tiempoTranscurrido = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                tiempoTranscurrido += 1;

                // Primera oleada a los 20 segundos
                if (tiempoTranscurrido == 20) {
                    mostrarMensajeEmergente("¡Primera oleada de zombies!", 2000);
                    generarZombiesProgresivamente(4, 2); // Generar 4 zombies, cada 2 segundos
                }

                // Mensaje de segunda oleada a los 50 segundos
                if (tiempoTranscurrido == 50) {
                    mostrarMensajeEmergente("¡Se aproxima la segunda oleada!", 2000);
                }

                // Segunda oleada a los 60 segundos
                if (tiempoTranscurrido == 55) {
                    generarZombiesProgresivamente(8, 1); // Generar 10 zombies, cada 2 segundos
                }

                // Mensaje de tercera oleada a los 90 segundos
                if (tiempoTranscurrido == 90) {
                    mostrarMensajeEmergente("¡Prepárate para la última oleada!", 2000);
                }

                // Tercera oleada a los 100 segundos
                if (tiempoTranscurrido == 95) {
                    generarZombiesProgresivamente(12, 1); // Generar 15 zombies, cada 1 segundo
                }

                // Detener el timer después de la tercera oleada
                if (tiempoTranscurrido >= 150) {
                    ((Timer) e.getSource()).stop();
                    verificarFinDelJuego();
                }
            }
        });

        oleadasTimer.start();
    }

    /**
     * Genera zombies de forma progresiva en intervalos de tiempo.
     *
     * @param cantidad  Número de zombies a generar.
     * @param intervalo Intervalo en segundos entre cada zombie.
     */
    private void generarZombiesProgresivamente(int cantidad, int intervalo) {
        zombieTimerProgresi = new Timer(intervalo * 1000, new ActionListener() {
            private int zombiesGenerados = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (zombiesGenerados < cantidad) {
                    // Generar un zombie en una fila aleatoria
                    int fila = new Random().nextInt(5); // Fila aleatoria
                    int columna = 9; // Última columna

                    if (gamePvsM.sePuedeGenerarZombie(fila, columna)) {
                        String tipoZombie = seleccionarTipoZombieAleatorio();
                        JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + columna);

                        gamePvsM.crearZombie(fila, columna, tipoZombie);

                        switch (tipoZombie) {
                            case "ZombieNormal":
                                celda.setIcon(new ImageIcon("resources/zombieNormal.gif"));
                                break;
                            case "ZombieCono":
                                celda.setIcon(new ImageIcon("resources/zombieCono.gif"));
                                break;
                            case "ZombieBalde":
                                celda.setIcon(new ImageIcon("resources/zombieBalde.gif"));
                                break;
                        }

                        celda.revalidate();
                        celda.repaint();
                        moverZombie(fila, columna, tipoZombie);
                        zombiesGenerados++;
                    }
                } else {
                    ((Timer) e.getSource()).stop(); // Detener el temporizador cuando se generen todos los zombies
                }
            }
        });

        zombieTimerProgresi.start();
    }


    /**
     * Verifica si el jugador ha ganado después de la última oleada.
     */
    private void verificarFinDelJuego() {
        if (!gamePvsM.isGameOver()) {
            JOptionPane.showMessageDialog(this, "¡Has ganado el juego!", "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
            volverAlMenu();
        }
    }


    /**
     * Configura las acciones de los componentes interactivos.
     * Incluye la lógica para volver al menú o a la selección de modos.
     */
    private void prepareActions() {
        // Acción para iniciar el juego
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopMusic();
                System.exit(0);
            }
        });

        // Acción para manejar clics en el tablero (plantar plantas)
        tableroPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component clicked = tableroPanel.getComponentAt(e.getPoint());
                if (clicked instanceof JButton) {
                    JButton celda = (JButton) clicked;
                    plantarEnCelda(celda);
                }
            }
        });
    }



    /**
     * Selecciona una planta para plantar.
     *
     * @param planta Nombre de la planta seleccionada.
     */
    private void seleccionarPlanta(String planta) {
        plantaSeleccionada = planta;
        if ("Girasol".equals(planta)) {
            setCursor(cursorGirasol);
        } else if ("LanzaGuisantes".equals(planta)) {
            setCursor(cursorLanzaGuisantes);
        } else if ("Nuez".equals(planta)) {
            setCursor(cursorNuez);
        } else if ("Mina".equals(planta)) {
            setCursor(cursorMina);
        } else if ("ECIPlant".equals(planta)) {
            setCursor(cursorECIPlant);

        }else if ("Evolve".equals(planta)) {
            setCursor(cursorEvolve);
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Selecciona la pala para quitar plantas.
     */
    private void seleccionarPala() {
        plantaSeleccionada = "Pala";
        setCursor(cursorPala);
    }

    /**
     * Maneja la acción de plantar en una celda del tablero.
     *
     * @param celda Botón que representa la celda del tablero.
     */
    private void plantarEnCelda(JButton celda) {
        if (plantaSeleccionada != null) {
            // Obtener el índice del botón dentro del panel
            int indice = tableroPanel.getComponentZOrder(celda);

            // Calcular fila y columna basados en el índice
            int fila = indice / 10; // div
            int columna = indice % 10; // mod

            if (gamePvsM.hayZombieEnCelda(fila, columna)) {
                mostrarMensajeEmergente("No puedes plantar aqui", 1000);
                return;
            }

            int solesPrevios = gamePvsM.getContadorSoles();
            if (columna >= 1 && columna <= 8) {
                if ("Girasol".equals(plantaSeleccionada)) {
                    if (gamePvsM.hayRecursosSuficientes("Girasol")) {
                        celda.setIcon(new ImageIcon("resources/girasol.gif"));
                        gamePvsM.plantarPlanta(fila, columna, "Girasol");
                        int solesActuales = gamePvsM.getContadorSoles();
                        if (solesPrevios != solesActuales) {
                            actualizarContadorSoles();
                        }
                    } else {
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }
                if ("LanzaGuisantes".equals(plantaSeleccionada)) {
                    if (gamePvsM.hayRecursosSuficientes("LanzaGuisantes")) {
                        celda.setIcon(new ImageIcon("resources/lanzaguisantes.gif"));
                        gamePvsM.plantarPlanta(fila, columna, "LanzaGuisantes");
                        int solesActuales = gamePvsM.getContadorSoles();
                        if (solesPrevios != solesActuales) {
                            actualizarContadorSoles();
                        }

                    } else {
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }
                if ("Nuez".equals(plantaSeleccionada)) {
                    if (gamePvsM.hayRecursosSuficientes("Nuez")) {
                        celda.setIcon(new ImageIcon("resources/nuez.gif"));
                        gamePvsM.plantarPlanta(fila, columna, "Nuez");
                        int solesActuales = gamePvsM.getContadorSoles();
                        if (solesPrevios != solesActuales) {
                            actualizarContadorSoles();
                        }
                    } else {
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }
                if ("Mina".equals(plantaSeleccionada)) {
                    if (gamePvsM.hayRecursosSuficientes("Mina")) {
                        celda.setIcon(new ImageIcon("resources/mina.gif"));
                        gamePvsM.plantarPlanta(fila, columna, "Mina");
                        int solesActuales = gamePvsM.getContadorSoles();
                        if (solesPrevios != solesActuales) {
                            actualizarContadorSoles();
                        }
                    } else {
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }
                if ("ECIPlant".equals(plantaSeleccionada)) {
                    if (gamePvsM.hayRecursosSuficientes("ECIPlant")) {
                        celda.setIcon(new ImageIcon("resources/ECIPlant.png"));
                        gamePvsM.plantarPlanta(fila, columna, "ECIPlant");
                        int solesActuales = gamePvsM.getContadorSoles();
                        if (solesPrevios != solesActuales) {
                            actualizarContadorSoles();
                        }
                    } else {
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }

                if ("Evolve".equals(plantaSeleccionada)) {
                    if (gamePvsM.hayRecursosSuficientes("Evolve")) {
                        celda.setIcon(new ImageIcon("resources/evolve0.gif"));
                        gamePvsM.plantarPlanta(fila, columna, "Evolve");
                        int solesActuales = gamePvsM.getContadorSoles();
                        evolveStages.put(celda, 0);
                        Timer evolveTimer = new Timer(15000, e -> evolucionarPlanta(celda));
                        evolveTimer.setRepeats(false);
                        evolveTimer.start();
                        evolveTimers.put(celda, evolveTimer);

                        if (solesPrevios != solesActuales) {
                            actualizarContadorSoles();
                        }
                    } else {
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }
                if (plantaSeleccionada.equals("Pala")) {
                    quitarPlanta(fila, columna);
                }
            } else {
                mostrarMensajeEmergente("Aqui no se puede plantar", 1000);
            }
            plantaSeleccionada = null;
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Quita una planta de una celda específica.
     *
     * @param fila    Fila de la celda.
     * @param columna Columna de la celda.
     */
    private void quitarPlanta(int fila, int columna) {
        Entidad entidad = gamePvsM.getEntidadEn(fila, columna);
        if (entidad instanceof Planta) {
            Planta planta = (Planta) entidad;

            planta.recibirDaño(planta.getVida());//Le restamos la vida por el total de vida para matarla

            if (planta instanceof Girasol) {
                ((Girasol) planta).detenerGeneracionDeSoles();
            }
            if (planta instanceof ECIPlant) {
                ((ECIPlant) planta).detenerGeneracionDeSoles();
            }
            gamePvsM.quitarPlanta(fila, columna);
            JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + columna);
            celda.setIcon(null);
        } else {
            mostrarMensajeEmergente("No hay planta para quitar", 1000);
        }
    }


    private void atacarZombies() {
        for (int fila = 0; fila < 5; fila++) { // Recorremos todas las filas
            for (int columna = 0; columna < 10; columna++) { // Recorremos las columnas
                if (gamePvsM.hayLanzaGuisantesEn(fila, columna)) { // Verificamos si hay LanzaGuisantes
                    Entidad entidad = gamePvsM.getEntidadEn(fila, columna);
                    if (entidad instanceof Zombie) { // Si encontramos un zombie
                        Zombie zombie = (Zombie) entidad;
                        zombie.recibirDaño(20); // Aplica daño de 20 puntos
                        System.out.println(zombie.getNombre() + " recibió 20 de daño. Vida restante: " + zombie.getVida());

                        if (zombie.getVida() <= 0) { // Si el zombie muere
                            gamePvsM.quitarZombie(fila, columna);// Lo elimina del tablero y del modelo
                            JButton celdaZombie = (JButton) tableroPanel.getComponent(fila * 10 + columna + 1); // Accede a la celda del tablero
                            celdaZombie.setIcon(null); // Borra el icono del zombie del tablero
                            System.out.println("Zombie eliminado en fila " + fila + ", columna " + columna);
                        }
                    }
                }
            }
        }
    }

    /**
     * Inicia el temporizador para la generación de zombies.
     * Muestra un mensaje de advertencia y luego comienza la generación continua de zombies.
     */
    private void iniciarTimerZombie() {
        mensajeTimer = new Timer(TIEMPO_INICIAL_ZOMBIES, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarMensajeEmergente("¡Ya vienen los zombies!", 2000);
                ((Timer) e.getSource()).stop();
                iniciarGeneracionContinuaZombies();
            }
        });
        mensajeTimer.setRepeats(false);
        mensajeTimer.start();
    }

    /**
     * Inicia la generación continua de zombies.
     * Configura un temporizador que genera zombies periódicamente,
     * reduciendo gradualmente el intervalo entre generaciones.
     */
    private void iniciarGeneracionContinuaZombies() {
        tiempoActualGeneracionZombie = TIEMPO_INICIAL_GENERACION_ZOMBIE;
        zombieTimer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarZombie();
                //Vamos reduciendo el tiempo para el proximo zombie hasta llegar al minimo
                tiempoActualGeneracionZombie = Math.max(tiempoActualGeneracionZombie - DECREMENTO_TIEMPO, TIEMPO_MINIMO_GENERACION_ZOMBIE);
                ((Timer) e.getSource()).setDelay(tiempoActualGeneracionZombie);
            }
        });
        zombieTimer.start();
    }


    /**
     * Genera un zombie en una posición aleatoria del tablero.
     * Selecciona un tipo de zombie aleatorio y lo coloca en la última columna de una fila aleatoria.
     */
    private void generarZombie() {
        int fila = new Random().nextInt(5); // Genera un número aleatorio entre 0 y 4
        int columna = 9; // Última columna

        if (gamePvsM.sePuedeGenerarZombie(fila, columna)) {
            String tipoZombie = seleccionarTipoZombieAleatorio();
            JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + columna);

            gamePvsM.crearZombie(fila, columna, tipoZombie);

            switch (tipoZombie) {
                case "ZombieNormal":
                    celda.setIcon(new ImageIcon("resources/zombieNormal.gif"));
                    break;
                case "ZombieCono":
                    celda.setIcon(new ImageIcon("resources/zombieCono.gif"));
                    break;
                case "ZombieBalde":
                    celda.setIcon(new ImageIcon("resources/zombieBalde.gif"));
                    break;
            }

            celda.revalidate();
            celda.repaint();
            moverZombie(fila, columna, tipoZombie);
        }
    }

    /**
     * Selecciona aleatoriamente un tipo de zombie para generar.
     *
     * @return String con el tipo de zombie seleccionado ("ZombieNormal", "ZombieCono", o "ZombieBalde").
     */
    private String seleccionarTipoZombieAleatorio() {
        Random random = new Random();
        int seleccion = random.nextInt(100); //número entre 0 y 99

        if (seleccion < 60) {
            return "ZombieNormal";
        } else if (seleccion < 92) {
            return "ZombieCono";
        } else {
            return "ZombieBalde";
        }
    }

    /**
     * Inicia el movimiento de un zombie en el tablero.
     * Mueve el zombie de derecha a izquierda, manejando colisiones con plantas y podadoras.
     *
     * @param fila       La fila en la que se mueve el zombie.
     * @param columna    La columna inicial del zombie.
     * @param tipoZombie El tipo de zombie que se está moviendo.
     */
    private void moverZombie(int fila, int columna, String tipoZombie) {
        movimientoZombieTimer = new Timer(5000, new ActionListener() { // Mueve cada 2 segundos
            int columnaActual = columna;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (columnaActual > 0) {
                    if (columnaActual == 1 && gamePvsM.tienePodadora(fila)) {
                        // Activar la podadora
                        gamePvsM.activarPodadora(fila);
                        actualizarPodadoras();
                        limpiarFila(fila);
                        ((Timer) e.getSource()).stop();
                        return;
                    }
                    JButton celdaActual = (JButton) tableroPanel.getComponent(fila * 10 + columnaActual);
                    celdaActual.setIcon(null);
                    columnaActual--;
                    JButton nuevaCelda = (JButton) tableroPanel.getComponent(fila * 10 + columnaActual);
                    nuevaCelda.setIcon(new ImageIcon("resources/" + tipoZombie.toLowerCase() + ".gif"));
                    gamePvsM.moverZombie(fila);
                }
                // Verificar si hay una mina activada en la casilla adyacente
                if (gamePvsM.hayMinaActivadaEnSiguienteColumna(fila, columnaActual - 1)) {
                    eliminarZombieYMina(fila, columnaActual - 1);
                    ((Timer) e.getSource()).stop();
                    return;
                }

                // Verificar si hay una planta en la siguiente columna
                if (gamePvsM.hayPlantaEnSiguienteColumna(fila, columnaActual - 1)) {
                    atacarPlanta(fila, columnaActual - 1, tipoZombie);
                    ((Timer) e.getSource()).stop();
                    return;
                }

                // Verificamos si el zombie ha llegado a la primera columna
                if (columnaActual == 0) {
                    if (!gamePvsM.tienePodadora(fila)) {
                        gamePvsM.setGameOver(true);
                        verificarGameOver();
                    }
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        movimientoZombieTimer.start();
        zombieTimers.add(movimientoZombieTimer);
    }

    /**
     * Elimina un zombie y una mina la cual exploto.
     *
     * @param fila    La fila donde ocurre la explosión.
     * @param columna La columna donde ocurre la explosión.
     */
    private void eliminarZombieYMina(int fila, int columna) {
        gamePvsM.eliminarZombieYMina(fila, columna);
        JButton celdaZombie = (JButton) tableroPanel.getComponent(fila * 10 + (columna + 1));
        JButton celdaMina = (JButton) tableroPanel.getComponent(fila * 10 + columna);
        celdaZombie.setIcon(null);
        celdaMina.setIcon(null);
    }

    /**
     * Maneja el ataque de un zombie a una planta.
     * El zombie ataca repetidamente a la planta hasta que esta sea destruida o el zombie sea eliminado.
     *
     * @param fila       La fila donde ocurre el ataque.
     * @param columna    La columna donde ocurre el ataque.
     * @param tipoZombie El tipo de zombie que está atacando.
     */
    private void atacarPlanta(int fila, int columna, String tipoZombie) {
        ataqueTimer = new Timer(500, new ActionListener() { // Ataca cada 0.5 segundos
            @Override
            public void actionPerformed(ActionEvent e) {
                Entidad entidad = gamePvsM.getEntidadEn(fila, columna);
                if (entidad instanceof Planta) {
                    Planta planta = (Planta) entidad;
                    planta.recibirDaño(100);
                    System.out.println(planta.getNombre() + " recibió daño 100 de daño y le queda: " + planta.getVida() + " de vida");
                    if (planta.getVida() <= 0) {
                        // La planta ha sido eliminada
                        gamePvsM.quitarPlanta(fila, columna);
                        JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + columna);
                        celda.setIcon(null);
                        ((Timer) e.getSource()).stop();
                        // Mover el zombie a la posición de la planta eliminada
                        JButton celdaZombie = (JButton) tableroPanel.getComponent(fila * 10 + (columna + 1));
                        celdaZombie.setIcon(null);
                        celda.setIcon(new ImageIcon("resources/" + tipoZombie.toLowerCase() + ".gif"));
                        gamePvsM.moverZombie(fila);
                        // Continuar el movimiento del zombie desde la nueva posición
                        moverZombie(fila, columna, tipoZombie);
                    }
                } else {
                    // No hay planta, detener el ataque y reanudar el movimiento
                    ((Timer) e.getSource()).stop();
                    moverZombie(fila, columna, tipoZombie);
                }
            }
        });
        ataqueTimer.start();
    }

    /**
     * Limpia todas las entidades (plantas y zombies) de una fila específica.
     * Se utiliza cuando una podadora se activa en esa fila.
     *
     * @param fila La fila que se va a limpiar.
     */
    private void limpiarFila(int fila) {
        for (int col = 0; col < 10; col++) {
            JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + col);
            celda.setIcon(null);
        }
    }

    /**
     * Pausa el juego actual.
     *
     * @param e El evento de acción que desencadena la pausa.
     */
    private void pausarJuego(ActionEvent e) {
        detenerTimers();
        stopMusic();
    }

    private void optionSaveAction() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de partida (.bin)", "bin");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".bin")) {
                    file = new File(file.getAbsolutePath() + ".bin");
                }
                gamePvsM.exportar(file);
                JOptionPane.showMessageDialog(this, "Partida guardada exitosamente en: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la partida: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void optionImportAction() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto", "txt");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                gamePvsM.importar(file); // Carga la partida en el modelo
                actualizarEstadoDesdeModelo(); // Actualiza la UI con el modelo cargado
                reanudarEstadoCargado(); // Reactiva funciones específicas de plantas y zombies
                JOptionPane.showMessageDialog(this, "Partida cargada exitosamente desde: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la partida: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reanudarEstadoCargado() {
        // Reiniciar temporizadores para plantas como Girasol
        for (int fila = 0; fila < 5; fila++) {
            for (int columna = 0; columna < 10; columna++) {
                Entidad entidad = gamePvsM.getEntidadEn(fila, columna);
                if (entidad instanceof Girasol) {
                    reanudarGeneracionDeSoles(); // Método que debes implementar en Girasol
                } else if (entidad instanceof Zombie) {
                    moverZombie(fila, columna, entidad.getClass().getSimpleName()); // Reactiva el movimiento de zombies
                }
            }
        }
    }

    public void reanudarGeneracionDeSoles() {
        if (timerSoles != null) timerSoles.stop();
        if (timerSoles != null) timerSoles.start();
    }

    /**
     * Termina la partida actual y vuelve al menú principal.
     *
     * @param e El evento de acción que desencadena la terminación.
     */
    private void terminarPartida(ActionEvent e) {
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que quieres terminar la partida?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            finalizarPartida();
            volverAlMenu();
        }
    }


    /**
     * Reproduce la música de fondo del juego.
     *
     * @param filepath La ruta del archivo de audio a reproducir.
     */
    private void playMusic(String filepath) {
        try {
            File audioFile = new File(filepath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            gameClip = AudioSystem.getClip();
            gameClip.open(audioStream);
            gameClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al reproducir música: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Detiene la reproducción de la música actual.
     */
    private void stopMusic() {
        if (gameClip != null && gameClip.isRunning()) {
            gameClip.stop();
            gameClip.close();
        }
    }

    /**
     * Muestra un mensaje emergente temporal en la pantalla.
     *
     * @param mensaje  Mensaje a mostrar.
     * @param duracion Duración en milisegundos que se mostrará el mensaje.
     */
    public static void mostrarMensajeEmergente(String mensaje, int duracion) {
        JWindow ventana = new JWindow();
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0, 150)); // Fondo semitransparente
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        JLabel etiqueta = new JLabel(mensaje);
        etiqueta.setForeground(Color.WHITE); // Color del texto
        etiqueta.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 20f));
        panel.add(etiqueta);

        ventana.getContentPane().add(panel);
        ventana.setSize(300, 40);
        ventana.setLocationRelativeTo(null); // Centrar en la pantalla

        ventana.setBackground(new Color(0, 0, 0, 0)); // Fondo transparente
        ventana.setOpacity(0.9f);

        ventana.setVisible(true);

        new Timer(duracion, e -> ventana.dispose()).start(); //ocultar la ventana después de la duración especificada
    }

    /**
     * Cierra esta ventana y vuelve al menú principal.
     */
    private void volverAlMenu() {
        finalizarPartida();
        dispose();
        GameGUI.getInstance().mostrar();
        GameGUI.getInstance().playMusic("resources/menuTheme.wav");
    }

    /**
     * Cierra esta ventana y vuelve a la selección de modos de juego.
     */
    private void volverAModos() {
        finalizarPartida();
        dispose();
        GameGUI.getInstance().playMusic("resources/menuTheme.wav");
        VentanaJuego.getInstance().mostrar();
    }

    private void detenerTimers() {
        if (timerActualizacion != null) timerActualizacion.stop();
        for (Timer zombieTimer : zombieTimers) {
            zombieTimer.stop();
        }
        if (timerSoles != null) timerSoles.stop();
        if (dañoZombieTimer != null) dañoZombieTimer.stop();
        if (oleadasTimer != null) oleadasTimer.stop();
        if (zombieTimerProgresi != null) zombieTimerProgresi.stop();
        if (zombieTimer != null) zombieTimer.stop();
        if (mensajeTimer != null) mensajeTimer.stop();
        if (ataqueTimer != null) ataqueTimer.stop();
    }

    private void iniciarTimers() {
        if (timerActualizacion != null) timerActualizacion.start();
        for (Timer zombieTimer : zombieTimers) {
            zombieTimer.start();
        }
        if (timerSoles != null) timerSoles.start();
        if (dañoZombieTimer != null) dañoZombieTimer.start();
        if (oleadasTimer != null) oleadasTimer.start();
        if (zombieTimerProgresi != null) zombieTimerProgresi.start();
        if (zombieTimer != null) zombieTimer.start();
        if (mensajeTimer != null) mensajeTimer.start();
        if (ataqueTimer != null) ataqueTimer.start();
    }

    /**
     * Actualiza el estado de la interfaz gráfica según el modelo del juego.
     */
    private void actualizarEstadoDesdeModelo() {
        for (int fila = 0; fila < 5; fila++) {
            for (int columna = 0; columna < 10; columna++) {
                JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + columna);
                celda.setIcon(null); // Limpia la celda
                Entidad entidad = gamePvsM.getEntidadEn(fila, columna);

                if (entidad != null) {
                    if (entidad instanceof Planta) {
                        Planta planta = (Planta) entidad;
                        switch (planta.getNombre()) {
                            case "Girasol":
                                celda.setIcon(new ImageIcon("resources/girasol.gif"));
                                break;
                            case "LanzaGuisantes":
                                celda.setIcon(new ImageIcon("resources/lanzaguisantes.gif"));
                                break;
                            case "Nuez":
                                celda.setIcon(new ImageIcon("resources/nuez.gif"));
                                break;
                            case "Mina":
                                celda.setIcon(new ImageIcon("resources/mina.gif"));
                                break;
                            case "ECIPlant":
                                celda.setIcon(new ImageIcon("resources/ECIPlant.gif"));
                                break;
                            case "Evolve":
                                celda.setIcon(new ImageIcon("resources/evolve.gif"));
                                break;
                            case "Podadora":
                        }
                    } else if (entidad instanceof Zombie) {
                        Zombie zombie = (Zombie) entidad;
                        switch (zombie.getNombre()) {
                            case "ZombieNormal":
                                celda.setIcon(new ImageIcon("resources/zombieNormal.gif"));
                                break;
                            case "ZombieCono":
                                celda.setIcon(new ImageIcon("resources/zombieCono.gif"));
                                break;
                            case "ZombieBalde":
                                celda.setIcon(new ImageIcon("resources/zombieBalde.gif"));
                                break;
                        }
                    }
                }
            }
        }
    }

    private void evolucionarPlanta(JButton celda) {
        int etapaActual = evolveStages.get(celda);
        if (etapaActual < 2) {
            etapaActual++;
            evolveStages.put(celda, etapaActual);

            ImageIcon nuevoIcon = new ImageIcon("resources/evolve" + etapaActual + ".gif");
            celda.setIcon(nuevoIcon);

            if (etapaActual < 2) {
                Timer nuevoTimer = new Timer(15000, e -> evolucionarPlanta(celda));
                nuevoTimer.setRepeats(false);
                nuevoTimer.start();
                evolveTimers.put(celda, nuevoTimer);
            }
        }
    }



    /**
     * Finaliza correctamente la partida, deteniendo todos los temporizadores y liberando recursos.
     */
    private void finalizarPartida() {
        detenerTimers(); // Detiene todos los temporizadores
        stopMusic(); // Detiene la música de fondo
        gamePvsM = null; // Libera el modelo de juego
        System.gc(); // Solicita al recolector de basura liberar memoria
    }

    /**
     * Verifica si el juego ha terminado.
     */
    private void verificarGameOver() {
        if (gamePvsM.isGameOver()) {
            timerActualizacion.stop();
            JOptionPane.showMessageDialog(this, "¡Game Over! Los zombies han llegado a tu casa.", "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
            volverAlMenu();
        }
    }

}
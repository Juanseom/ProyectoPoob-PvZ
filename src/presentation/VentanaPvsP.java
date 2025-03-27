package presentation;

import domain.*;

import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Ventana que representa el modo de juego Player vs Machine (PvsM) en POOB vs Zombies.
 * Esta clase maneja la interfaz gráfica para el modo de juego donde un jugador controla las plantas
 * contra los zombies controlados por la máquina.
 */
public class VentanaPvsP extends JFrame {
    private GamePvsP gamePvsP;

    private JLayeredPane layeredPane;
    private JPanel tableroPanel;
    private JLabel contadorSolesLabel;
    private JLabel contadorCerebrosLabel;
    private Timer timerActualizacion;
    private Clip gameClip;

    private String plantaSeleccionada;
    private String zombieSeleccionado;
    private Cursor cursorNuez, cursorMina, cursorLanzaGuisantes, cursorPala;
    private Cursor cursorZombieNormal, cursorZombieCono, cursorZombieBalde, cursorBrainstein, cursorECIZombie;
    private List<String> plantasSeleccionadas;
    private List<String> zombiesSeleccionados;

    private JLabel[] podadoraLabels;


    /**
     * Constructor de la ventana PvsM.
     * Inicializa la ventana, configura los elementos de la interfaz y prepara las acciones.
     */

    public VentanaPvsP(List<String> plantasSeleccionadas, List<String> zombiesSeleccionados) {
        this.plantasSeleccionadas = plantasSeleccionadas;
        this.zombiesSeleccionados = zombiesSeleccionados;

        setTitle("POOB vs Zombies - Player vs Player");
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

        GameGUI.getInstance().stopMusic();

        playMusic("resources/peleaTheme.wav");

        gamePvsP = new GamePvsP();
        iniciarTimerActualizacion();
        iniciarMensajeInicio();
        iniciarTimerCerebros(); // Genera cerebros después de 20 segundos
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


    }

    private void prepareCursores() {
        ImageIcon nuezIcon = new ImageIcon("resources/nuez.png");
        cursorNuez = Toolkit.getDefaultToolkit().createCustomCursor(
                nuezIcon.getImage(), new Point(0, 0), "cursorNuez");

        ImageIcon lanzaguisantesIcon = new ImageIcon("resources/lanzaguisantes.png");
        cursorLanzaGuisantes = Toolkit.getDefaultToolkit().createCustomCursor(
                lanzaguisantesIcon.getImage(), new Point(0, 0), "cursorLanzaGuisantes");

        ImageIcon minaIcon = new ImageIcon("resources/mina.png");
        cursorMina = Toolkit.getDefaultToolkit().createCustomCursor(
                minaIcon.getImage(), new Point(0, 0), "cursorMina");

        ImageIcon palaIcon = new ImageIcon("resources/pala.png");
        cursorPala = Toolkit.getDefaultToolkit().createCustomCursor(
                palaIcon.getImage(), new Point(0, 0), "cursorPala");

        ImageIcon zombieNormalIcon = new ImageIcon("resources/zombieNormal.png");
        cursorZombieNormal = Toolkit.getDefaultToolkit().createCustomCursor(
                zombieNormalIcon.getImage(), new Point(0, 0), "cursorZombieNormal");

        ImageIcon zombieConoIcon = new ImageIcon("resources/zombieCono.png");
        cursorZombieCono = Toolkit.getDefaultToolkit().createCustomCursor(
                zombieConoIcon.getImage(), new Point(0, 0), "cursorZombieCono");

        ImageIcon zombieBaldeIcon = new ImageIcon("resources/zombieBalde.png");
        cursorZombieBalde = Toolkit.getDefaultToolkit().createCustomCursor(
                zombieBaldeIcon.getImage(), new Point(0, 0), "cursorZombieBalde");

        ImageIcon brainsteinIcon = new ImageIcon("resources/brainstein.png");
        cursorBrainstein = Toolkit.getDefaultToolkit().createCustomCursor(
                brainsteinIcon.getImage(), new Point(0, 0), "cursorBrainstein");

        ImageIcon eciZombieIcon = new ImageIcon("resources/ECIZombie.png");
        cursorECIZombie = Toolkit.getDefaultToolkit().createCustomCursor(
                eciZombieIcon.getImage(), new Point(0, 0), "cursorECIZombie");
    }

    /**
     * Prepara el tablero de juego.
     *
     * @return Panel que representa el tablero de juego.
     */
    private JPanel prepareTablero() {
        tableroPanel = new JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                //Dibuja la linea roja
                g.setColor(Color.RED);
                int x = (int) (getWidth() * 0.705);
                g.drawLine(x, 0, x, getHeight());
            }
        };
        tableroPanel.setLayout(new GridLayout(5, 10));
        tableroPanel.setOpaque(false);
        tableroPanel.setBounds(12, 55, 750, 480);

        for (int i = 0; i < 5 * 10 ; i++) {
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
        prepareCartasInventario();

        ImageIcon imagenInventarioPlantas = new ImageIcon("resources/plantasInventarioPvP.png");
        JLabel inventarioPlantasLabel = new JLabel(imagenInventarioPlantas);
        inventarioPlantasLabel.setBounds(10, 0, 360, 68);
        layeredPane.add(inventarioPlantasLabel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER + 2));

        ImageIcon imagenInventarioZombies = new ImageIcon("resources/zombiesInventario.png");
        JLabel inventarioZombiesLabel = new JLabel(imagenInventarioZombies);
        inventarioZombiesLabel.setBounds(420, 0, 360, 68);
        layeredPane.add(inventarioZombiesLabel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER + 2));

        ImageIcon imagenPala = new ImageIcon("resources/palaInventario.png");
        JButton botonPala = new JButton(imagenPala);
        botonPala.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonPala.setBounds(370,0,50,50);
        botonPala.addActionListener(e -> seleccionarPala());
        layeredPane.add(botonPala, JLayeredPane.PALETTE_LAYER);

        contadorSolesLabel = new JLabel("999");
        contadorSolesLabel.setBounds(25, 43, 60, 30);
        contadorSolesLabel.setForeground(Color.black);
        contadorSolesLabel.setFont(GameGUI.fuenteZombi.deriveFont(Font.BOLD, 20f));
        layeredPane.add(contadorSolesLabel, JLayeredPane.PALETTE_LAYER);

        contadorCerebrosLabel = new JLabel("50");
        contadorCerebrosLabel.setBounds(740, 43, 60, 30);
        contadorCerebrosLabel.setForeground(Color.black);
        contadorCerebrosLabel.setFont(GameGUI.fuenteZombi.deriveFont(Font.BOLD, 20f));
        iniciarTimerActualizacion();
        layeredPane.add(contadorCerebrosLabel, JLayeredPane.PALETTE_LAYER);
    }

    /**
     * Prepara las imagenes y la posicion de todas las cartas para seleccionar a las plantas y a los zombies
     */
    private void prepareCartasInventario() {
        if (plantasSeleccionadas.contains("LanzaGuisantes")) {
            ImageIcon cartaLanzaguisantes = new ImageIcon("resources/cartaLanzaguisantes.png");
            JButton botonLanzaguisantes = new JButton(cartaLanzaguisantes);
            botonLanzaguisantes.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonLanzaguisantes.setBounds(80, 5, 49, 60);
            botonLanzaguisantes.addActionListener(e -> seleccionarPlanta("LanzaGuisantes"));
            layeredPane.add(botonLanzaguisantes, JLayeredPane.PALETTE_LAYER);
        }

        if (plantasSeleccionadas.contains("Nuez")) {
            ImageIcon cartaNuez = new ImageIcon("resources/cartaNuez.png");
            JButton botonNuez = new JButton(cartaNuez);
            botonNuez.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonNuez.setBounds(140, 5, 49, 60);
            botonNuez.addActionListener(e -> seleccionarPlanta("Nuez"));
            layeredPane.add(botonNuez, JLayeredPane.PALETTE_LAYER);
        }

        if (plantasSeleccionadas.contains("Mina")) {
            ImageIcon cartaMina = new ImageIcon("resources/cartaMina.png");
            JButton botonMina = new JButton(cartaMina);
            botonMina.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonMina.setBounds(200, 5, 49, 60);
            botonMina.addActionListener(e -> seleccionarPlanta("Mina"));
            layeredPane.add(botonMina, JLayeredPane.PALETTE_LAYER);
        }

        if (zombiesSeleccionados.contains("ZombieNormal")) {
            ImageIcon cartaZombieNormal = new ImageIcon("resources/cartaZombieNormal.png");
            JButton botonZombieNormal = new JButton(cartaZombieNormal);
            botonZombieNormal.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonZombieNormal.setBounds(430, 5, 49, 60);
            botonZombieNormal.addActionListener(e -> seleccionarZombie("ZombieNormal"));
            layeredPane.add(botonZombieNormal, JLayeredPane.PALETTE_LAYER);
        }

        if (zombiesSeleccionados.contains("ZombieCono")) {
            ImageIcon cartaZombieCono = new ImageIcon("resources/cartaZombieCono.png");
            JButton botonZombieCono = new JButton(cartaZombieCono);
            botonZombieCono.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonZombieCono.setBounds(540, 5, 49, 60);
            botonZombieCono.addActionListener(e -> seleccionarZombie("ZombieCono"));
            layeredPane.add(botonZombieCono, JLayeredPane.PALETTE_LAYER);
        }

        if (zombiesSeleccionados.contains("ZombieBalde")) {
            ImageIcon cartaZombieBalde = new ImageIcon("resources/cartaZombieBalde.png");
            JButton botonZombieBalde = new JButton(cartaZombieBalde);
            botonZombieBalde.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonZombieBalde.setBounds(485, 5, 49, 60);
            botonZombieBalde.addActionListener(e -> seleccionarZombie("ZombieBalde"));
            layeredPane.add(botonZombieBalde, JLayeredPane.PALETTE_LAYER);
        }

        if (zombiesSeleccionados.contains("Brainstein")) {
            ImageIcon cartaBrainstein = new ImageIcon("resources/cartaBrainstein.png");
            JButton botonBrainstein = new JButton(cartaBrainstein);
            botonBrainstein.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonBrainstein.setBounds(595, 5, 49, 60);
            botonBrainstein.addActionListener(e -> seleccionarZombie("Brainstein"));
            layeredPane.add(botonBrainstein, JLayeredPane.PALETTE_LAYER);
        }

        if (zombiesSeleccionados.contains("ECIZombie")) {
            ImageIcon cartaECIZombie = new ImageIcon("resources/cartaECIZombie.png");
            JButton botonECIZombie = new JButton(cartaECIZombie);
            botonECIZombie.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonECIZombie.setBounds(650, 5, 49, 60);
            botonECIZombie.addActionListener(e -> seleccionarZombie("ECIZombie"));
            layeredPane.add(botonECIZombie, JLayeredPane.PALETTE_LAYER);
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
            if (!gamePvsP.tienePodadora(fila)) {
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
     * Muestra un mensaje inicial al jugador después de 3 segundos.
     */
    private void iniciarMensajeInicio() {
        Timer mensajeInicio = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarMensajeEmergente("¡Jugador de plantas, crea tu estrategia!", 2000);
                ((Timer) e.getSource()).stop(); // Detener el temporizador
            }
        });
        mensajeInicio.setRepeats(false); // Solo se ejecuta una vez
        mensajeInicio.start();
    }


    /**
     * Inicia un temporizador para comenzar la generación de cerebros a los 20 segundos.
     */
    private void iniciarTimerCerebros() {
        Timer inicioCerebros = new Timer(20000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarMensajeEmergente("¡Cerebros disponibles para los zombies!", 2000);
                iniciarGeneracionCerebros();
                ((Timer) e.getSource()).stop(); // Detener el temporizador
            }
        });
        inicioCerebros.setRepeats(false);
        inicioCerebros.start();
    }

    /**
     * Inicia la generación periódica de cerebros.
     */
    private void iniciarGeneracionCerebros() {
        Timer timerCerebros = new Timer(10000, new ActionListener() { // Cada 10 segundos
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePvsP.actualizarEstadoJuego(); // Actualizar el modelo de juego
                actualizarContadorCerebros(); // Actualizar la interfaz gráfica
            }
        });
        timerCerebros.start();
    }


    /**
     * Actualiza el contador de soles en la interfaz gráfica.
     */
    private void actualizarContadorCerebros() {
        SwingUtilities.invokeLater(() -> {
            contadorCerebrosLabel.setText(String.valueOf(gamePvsP.getContadorCerebros()));
        });
    }

    /**
     * Configura las acciones de los componentes interactivos.
     * Incluye la lógica para volver al menú o a la selección de modos.
     */
    private void prepareActions() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Object[] opciones = {"Volver al menu", "Salir del juego"};
                int response = JOptionPane.showOptionDialog(VentanaPvsP.this, "¿Que deseas hacer?", "Cerrar ventana",
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
     * Crea y configura la barra de menú del juego.
     * Incluye opciones para pausar, guardar, cargar y terminar la partida.
     */
    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu volver = new JMenu("Volver");
        JMenu menuJuego = new JMenu("Juego");

        JMenuItem itemVolver = new JMenuItem("Volver");
        itemVolver.addActionListener(e -> menuVolver());
        volver.add(itemVolver);

        JMenuItem itemPausar = new JMenuItem("Pausar");
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        JMenuItem itemCargar = new JMenuItem("Cargar");
        JMenuItem itemTerminar = new JMenuItem("Terminar");

        itemPausar.addActionListener(this::pausarJuego);
        itemGuardar.addActionListener(this::guardarPartida);
        itemCargar.addActionListener(this::cargarPartida);
        itemTerminar.addActionListener(this::terminarPartida);

        menuJuego.add(itemPausar);
        menuJuego.add(itemGuardar);
        menuJuego.add(itemCargar);
        menuJuego.add(itemTerminar);

        menuBar.add(volver);
        menuBar.add(menuJuego);
        setJMenuBar(menuBar);
    }

    private void menuVolver() {
        Object[] opciones = {"Volver al menu", "Volver a los modos"};
        int response = JOptionPane.showOptionDialog(VentanaPvsP.this, "¿Que deseas hacer?", "Cerrar ventana",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (response == 0) {
            volverAlMenu();
        }
        if (response == 1) {
            volverAModos();
        }
    }

    /**
     * Selecciona una planta para plantar.
     * @param planta Nombre de la planta seleccionada.
     */
    private void seleccionarPlanta(String planta) {
        plantaSeleccionada = planta;
        if ("LanzaGuisantes".equals(planta)) {
            setCursor(cursorLanzaGuisantes);
        }else if ("Nuez".equals(planta)) {
            setCursor(cursorNuez);
        }else if ("Mina".equals(planta)) {
            setCursor(cursorMina);
        }else{
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Selecciona la pala para quitar plantas.
     */
    private void seleccionarPala(){
        plantaSeleccionada = "Pala";
        setCursor(cursorPala);
    }

    /**
     * Selecciona un zombie para colocar.
     * @param zombie Nombre del zombie seleccionado.
     */
    private void seleccionarZombie(String zombie) {
        zombieSeleccionado = zombie;
        if ("ZombieNormal".equals(zombie)) {
            setCursor(cursorZombieNormal);
        }else if ("ZombieCono".equals(zombie)) {
            setCursor(cursorZombieCono);
        }else if ("ZombieBalde".equals(zombie)) {
            setCursor(cursorZombieBalde);
        }else if ("Brainstein".equals(zombie)) {
            setCursor(cursorBrainstein);
        }else if ("ECIZombie".equals(zombie)) {
            setCursor(cursorECIZombie);
        }else{
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Maneja la acción de plantar en una celda del tablero.
     * @param celda Botón que representa la celda del tablero.
     */
    private void plantarEnCelda(JButton celda) {
        // Obtener el índice del botón dentro del panel
        int indice = tableroPanel.getComponentZOrder(celda);

        // Calcular fila y columna basados en el índice
        int fila = indice / 10; // div
        int columna = indice % 10; // mod

        int cerebrosPrevios = gamePvsP.getContadorCerebros();
        if (plantaSeleccionada != null) {
            if(gamePvsP.hayZombieEnCelda(fila,columna)){
                mostrarMensajeEmergente("No puedes plantar aqui", 1000);
                return;
            }
            if(columna >= 1 && columna <= 6){
                if ("LanzaGuisantes".equals(plantaSeleccionada)) {
                    celda.setIcon(new ImageIcon("resources/lanzaguisantes.gif"));
                    gamePvsP.plantarPlanta(fila, columna, "LanzaGuisantes");
                }
                if ("Nuez".equals(plantaSeleccionada)) {
                    celda.setIcon(new ImageIcon("resources/nuez.gif"));
                    gamePvsP.plantarPlanta(fila, columna, "Nuez");
                }
                if ("Mina".equals(plantaSeleccionada)) {
                    celda.setIcon(new ImageIcon("resources/mina.gif"));
                    gamePvsP.plantarPlanta(fila, columna, "Mina");
                }
                if(plantaSeleccionada.equals("Pala")){
                    quitarPlanta(fila, columna);
                }
            }else{
                mostrarMensajeEmergente("Aqui no se puede plantar", 1000);
            }
            plantaSeleccionada = null;
            setCursor(Cursor.getDefaultCursor());
        }
        if(zombieSeleccionado != null){

            if(columna >= 7 && columna <= 9){
                if ("ZombieNormal".equals(zombieSeleccionado)) {
                    if(gamePvsP.hayRecursosSuficientes("ZombieNormal")) {
                        celda.setIcon(new ImageIcon("resources/zombieNormal.gif"));
                        gamePvsP.crearZombie(fila, columna, "ZombieNormal");
                        int cerebrosActuales = gamePvsP.getContadorCerebros();
                        if (cerebrosPrevios != cerebrosActuales) {
                            actualizarContadorCerebros();
                        }
                        moverZombie(fila, columna, "ZombieNormal");
                    }else{
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }
                if ("ZombieCono".equals(zombieSeleccionado)) {
                    if(gamePvsP.hayRecursosSuficientes("ZombieCono")) {
                        celda.setIcon(new ImageIcon("resources/zombieCono.gif"));
                        gamePvsP.crearZombie(fila, columna, "ZombieCono");
                        int cerebrosActuales = gamePvsP.getContadorCerebros();
                        if (cerebrosPrevios != cerebrosActuales) {
                            actualizarContadorCerebros();
                        }
                        moverZombie(fila, columna, "ZombieCono");

                    }else{
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }
                if ("ZombieBalde".equals(zombieSeleccionado)) {
                    if(gamePvsP.hayRecursosSuficientes("ZombieBalde")) {
                        celda.setIcon(new ImageIcon("resources/zombieBalde.gif"));
                        gamePvsP.crearZombie(fila, columna, "ZombieBalde");
                        int cerebrosActuales = gamePvsP.getContadorCerebros();
                        if (cerebrosPrevios != cerebrosActuales) {
                            actualizarContadorCerebros();
                        }
                        moverZombie(fila, columna, "ZombieBalde");

                    }else{
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }
                if ("Brainstein".equals(zombieSeleccionado)) {
                    if(gamePvsP.hayRecursosSuficientes("Brainstein")) {
                        celda.setIcon(new ImageIcon("resources/brainstein.gif"));
                        gamePvsP.crearZombie(fila, columna, "Brainstein");
                        int cerebrosActuales = gamePvsP.getContadorCerebros();
                        if (cerebrosPrevios != cerebrosActuales) {
                            actualizarContadorCerebros();
                        }

                    }else{
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }
                if ("ECIZombie".equals(zombieSeleccionado)) {
                    if(gamePvsP.hayRecursosSuficientes("ECIZombie")) {
                        celda.setIcon(new ImageIcon("resources/ECIZombie.png"));
                        gamePvsP.crearZombie(fila, columna, "ECIZombie");
                        int cerebrosActuales = gamePvsP.getContadorCerebros();
                        if (cerebrosPrevios != cerebrosActuales) {
                            actualizarContadorCerebros();
                        }
                    }else{
                        mostrarMensajeEmergente("No tienes recursos suficientes", 1000);
                    }
                }
            }else{
                mostrarMensajeEmergente("Aqui no se puede plantar", 1000);
            }
            zombieSeleccionado = null;
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Quita una planta de una celda específica.
     * @param fila Fila de la celda.
     * @param columna Columna de la celda.
     */
    private void quitarPlanta(int fila, int columna) {
        Entidad entidad = gamePvsP.getEntidadEn(fila, columna);
        if(entidad instanceof Planta) {
            Planta planta = (Planta) entidad;

            planta.recibirDaño(planta.getVida());//Le restamos la vida por el total de vida para matarla

            gamePvsP.quitarPlanta(fila, columna);
            JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + columna);
            celda.setIcon(null);
        }else{
            mostrarMensajeEmergente("No hay planta para quitar", 1000);
        }
    }

    private void moverZombie(int fila, int columna, String tipoZombie) {
        Timer movimientoTimer = new Timer(2000, new ActionListener() {
            int columnaActual = columna;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(columnaActual > 0) {
                    if(columnaActual == 1 && gamePvsP.tienePodadora(fila)){
                        gamePvsP.activarPodadora(fila);
                        actualizarPodadoras();
                        limpiarFila(fila);
                        ((Timer)e.getSource()).stop();
                        return;
                    }
                    JButton celdaActual = (JButton) tableroPanel.getComponent(fila * 10 + columnaActual);
                    celdaActual.setIcon(null);
                    columnaActual--;
                    JButton nuevaCelda = (JButton) tableroPanel.getComponent(fila * 10 + columnaActual);
                    nuevaCelda.setIcon(new ImageIcon("resources/" + tipoZombie.toLowerCase() + ".gif"));
                    gamePvsP.moverZombie(fila);
                }
                // Verificar si hay una mina activada en la casilla adyacente
                if (gamePvsP.hayMinaActivadaEnSiguienteColumna(fila, columnaActual - 1)) {
                    eliminarZombieYMina(fila, columnaActual - 1);
                    ((Timer)e.getSource()).stop();
                    return;
                }

                // Verificar si hay una planta en la siguiente columna
                if (gamePvsP.hayPlantaEnSiguienteColumna(fila, columnaActual - 1)) {
                    atacarPlanta(fila, columnaActual - 1, tipoZombie);
                    ((Timer)e.getSource()).stop();
                    return;
                }

                if(columnaActual == 0) {
                    if(!gamePvsP.tienePodadora(fila)){
                        gamePvsP.setGameOver(true);
                        verificarGameOver();
                        ((Timer)e.getSource()).stop();
                    }
                }
            }
        });
        movimientoTimer.start();
    }

    /**
     * Elimina un zombie y una mina la cual exploto.
     * @param fila La fila donde ocurre la explosión.
     * @param columna La columna donde ocurre la explosión.
     */
    private void eliminarZombieYMina(int fila, int columna) {
        gamePvsP.eliminarZombieYMina(fila, columna);
        JButton celdaZombie = (JButton) tableroPanel.getComponent(fila * 10 + (columna + 1));
        JButton celdaMina = (JButton) tableroPanel.getComponent(fila * 10 + columna);
        celdaZombie.setIcon(null);
        celdaMina.setIcon(null);
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
            gamePvsP.limpiarFila(fila);
        }
    }

    /**
     * Maneja el ataque de un zombie a una planta.
     * El zombie ataca repetidamente a la planta hasta que esta sea destruida o el zombie sea eliminado.
     *
     * @param fila La fila donde ocurre el ataque.
     * @param columna La columna donde ocurre el ataque.
     * @param tipoZombie El tipo de zombie que está atacando.
     */
    private void atacarPlanta(int fila, int columna, String tipoZombie) {
        Timer ataqueTimer = new Timer(500, new ActionListener() { // Ataca cada 0.5 segundos
            @Override
            public void actionPerformed(ActionEvent e) {
                Entidad entidad = gamePvsP.getEntidadEn(fila, columna);
                if (entidad instanceof Planta) {
                    Planta planta = (Planta) entidad;
                    planta.recibirDaño(100);
                    System.out.println(planta.getNombre() + " recibió daño 100 de daño y le queda: " + planta.getVida() + " de vida");
                    if (planta.getVida() <= 0) {
                        // La planta ha sido eliminada
                        gamePvsP.quitarPlanta(fila, columna);
                        JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + columna);
                        celda.setIcon(null);
                        ((Timer)e.getSource()).stop();
                        // Mover el zombie a la posición de la planta eliminada
                        JButton celdaZombie = (JButton) tableroPanel.getComponent(fila * 10 + (columna + 1));
                        celdaZombie.setIcon(null);
                        celda.setIcon(new ImageIcon("resources/" + tipoZombie.toLowerCase() + ".gif"));
                        gamePvsP.moverZombie(fila);
                        // Continuar el movimiento del zombie desde la nueva posición
                        moverZombie(fila, columna, tipoZombie);
                    }
                } else {
                    // No hay planta, detener el ataque y reanudar el movimiento
                    ((Timer)e.getSource()).stop();
                    moverZombie(fila, columna, tipoZombie);
                }
            }
        });
        ataqueTimer.start();
    }

    /**
     * Pausa el juego actual.
     * @param e El evento de acción que desencadena la pausa.
     */
    private void pausarJuego(ActionEvent e) {
        // Implementar lógica de pausa
        JOptionPane.showMessageDialog(this, "Juego pausado");
    }

    /**
     * Guarda el estado actual de la partida.
     * @param e El evento de acción que desencadena el guardado.
     */
    private void guardarPartida(ActionEvent e) {
        // Implementar lógica de guardado
        JOptionPane.showMessageDialog(this, "Partida guardada");
    }

    /**
     * Carga una partida previamente guardada.
     * @param e El evento de acción que desencadena la carga.
     */
    private void cargarPartida(ActionEvent e) {
        // Implementar lógica de carga
        JOptionPane.showMessageDialog(this, "Partida cargada");
    }

    /**
     * Termina la partida actual y vuelve al menú principal.
     * @param e El evento de acción que desencadena la terminación.
     */
    private void terminarPartida(ActionEvent e) {
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que quieres terminar la partida?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            volverAlMenu();
        }
    }

    /**
     * Reproduce la música de fondo del juego.
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
     * @param mensaje Mensaje a mostrar.
     * @param duracion Duración en milisegundos que se mostrará el mensaje.
     */
    public static void mostrarMensajeEmergente(String mensaje, int duracion){
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
        stopMusic();
        dispose();
        GameGUI.getInstance().mostrar();
        GameGUI.getInstance().playMusic("resources/menuTheme.wav");
    }

    /**
     * Cierra esta ventana y vuelve a la selección de modos de juego.
     */
    private void volverAModos(){
        stopMusic();
        dispose();
        GameGUI.getInstance().playMusic("resources/menuTheme.wav");
        VentanaJuego.getInstance().mostrar();
    }


    /**
     * Calcula los puntajes finales para ambos jugadores.
     * @return Un array con dos elementos: [puntajePlantas, puntajeZombies]
     */
    private int[] calcularPuntajes() {
        int puntajePlantas = 0;
        int puntajeZombies = 0;

        // Calcular puntaje de las plantas
        for (int fila = 0; fila < 5; fila++) {
            for (int columna = 0; columna < 10; columna++) {
                Entidad entidad = gamePvsP.getEntidadEn(fila, columna);
                if (entidad instanceof Planta) {
                    puntajePlantas += ((Planta) entidad).getCosto();
                }
            }
        }
        puntajePlantas = (int) (puntajePlantas * 1.5);

        // Calcular puntaje de los zombies
        puntajeZombies = gamePvsP.getContadorCerebros();
        for (int fila = 0; fila < 5; fila++) {
            for (int columna = 0; columna < 10; columna++) {
                Entidad entidad = gamePvsP.getEntidadEn(fila, columna);
                if (entidad instanceof Zombie) {
                    puntajeZombies += ((Zombie) entidad).getCosto();
                }
            }
        }

        return new int[]{puntajePlantas, puntajeZombies};
    }

    /**
     * Verifica si el juego ha terminado.
     */
    private void verificarGameOver() {
        if (gamePvsP.isGameOver()) {
            timerActualizacion.stop();

            int[] puntajes = calcularPuntajes();
            int puntajePlantas = puntajes[0];
            int puntajeZombies = puntajes[1];

            String mensaje = String.format(
                    "¡Game Over!\n\n" + "Puntaje Plantas: %d\n" + "Puntaje Zombies: %d\n\n" + "%s",
                    puntajePlantas,
                    puntajeZombies,
                    puntajePlantas > puntajeZombies ? "¡Las Plantas ganan!" :
                            puntajeZombies > puntajePlantas ? "¡Los Zombies ganan!" : "¡Empate!"
            );

            JOptionPane.showMessageDialog(this, mensaje, "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
            volverAlMenu();
        }
    }
}
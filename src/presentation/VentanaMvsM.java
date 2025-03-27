package presentation;

import domain.*;

import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.List;

/**
 * Ventana que representa el modo de juego Machine vs Machine (MvsM) en POOB vs Zombies.
 * Esta clase maneja la interfaz gráfica para el modo de juego donde una maquina controla las plantas
 * contra los zombies controlados por otra máquina.
 */
public class VentanaMvsM extends JFrame {
    private GameMvsM gameMvsM;

    private JLayeredPane layeredPane;
    private JPanel tableroPanel;
    private JButton botonVolver;
    private Timer timerActualizacion;
    private Clip gameClip;

    private List<String> plantasSeleccionadas;
    private List<String> zombiesSeleccionados;
    private int tipoPlantaActual = 0; // Índice para alternar entre tipos de plantas
    private int tipoZombieActual = 0; // Índice para alternar entre tipos de zombies


    private static final int TIEMPO_INICIAL_ZOMBIES = 20000; // 20 segundos
    private static final int TIEMPO_INICIAL_GENERACION_ZOMBIE = 5000; // 10 segundos
    private static final int TIEMPO_MINIMO_GENERACION_ZOMBIE = 1000; // 1 segundo
    private static final int DECREMENTO_TIEMPO = 200; // Reducción de 0.2 segundos en cada generación
    private int tiempoActualGeneracionZombie;

    private static final int TIEMPO_PLANTAR_PLANTAS = 5000;
    private Random random = new Random();

    private JLabel[] podadoraLabels;


    /**
     * Constructor de la ventana MvsM.
     * Inicializa la ventana, configura los elementos de la interfaz y prepara las acciones.
     */
    public VentanaMvsM(List<String> plantasSeleccionadas, List<String> zombiesSeleccionados) {
        this.plantasSeleccionadas = plantasSeleccionadas;
        this.zombiesSeleccionados = zombiesSeleccionados;

        setTitle("POOB vs Zombies - Machine vs Machine");
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

        gameMvsM = new GameMvsM();
        iniciarTimerActualizacion();
        iniciarTimerZombie();
        iniciarTimerPlantas();
        setVisible(true);
    }

    /**
     * Prepara y coloca los elementos visuales en la ventana.
     * Incluye el fondo del juego, el inventario de plantas, y otros elementos de la interfaz.
     */
    private void prepareElements() {
        crearMenuBar();
        prepareTablero();

        ImageIcon imagenBackground = new ImageIcon("resources/patioPvsZ.png");
        JLabel backgroundLabel = new JLabel(imagenBackground);
        backgroundLabel.setBounds(0,0,800,600);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        botonVolver = new JButton("Volver");
        botonVolver.setBounds(10, 10, 50, 30);
        botonVolver.setBackground(Color.red);
        botonVolver.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.BLACK));
        botonVolver.setFocusPainted(false);
        botonVolver.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 15f));
        layeredPane.add(botonVolver, JLayeredPane.PALETTE_LAYER);
    }


    /**
     * Prepara el tablero de juego.
     *
     * @return Panel que representa el tablero de juego.
     */
    private JPanel prepareTablero(){
        tableroPanel = new JPanel();
        tableroPanel.setLayout(new GridLayout(5, 10));
        tableroPanel.setOpaque(false);
        tableroPanel.setBounds(12, 55, 750, 480);
        for(int i = 0; i < 5 * 10; i++) {
            JButton cell = new JButton();
            cell.setOpaque(false);
            cell.setContentAreaFilled(false);
            cell.setBorderPainted(false);
            cell.setFocusPainted(false);
            tableroPanel.add(cell);
        }
        layeredPane.add(tableroPanel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER + 1));
        return tableroPanel;
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
            if(!gameMvsM.tienePodadora(fila)){
                ImageIcon peligroIcon = new ImageIcon("resources/warning.png");
                podadoraLabels[fila].setIcon(peligroIcon);
            }else{
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
     * Configura las acciones de los componentes interactivos.
     * Incluye la lógica para volver al menú o a la selección de modos.
     */
    private void prepareActions() {
        botonVolver.addActionListener(e -> {
            Object[] opciones = {"Volver al menu", "Volver a los modos"};
            int response = JOptionPane.showOptionDialog(VentanaMvsM.this, "¿Que deseas hacer?", "Cerrar ventana",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (response == 0) {
                volverAlMenu();
            }
            if (response == 1) {
                volverAModos();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Object[] opciones = {"Volver al menu", "Salir del juego"};
                int response = JOptionPane.showOptionDialog(VentanaMvsM.this, "¿Que deseas hacer?", "Cerrar ventana",
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
        JMenu menuJuego = new JMenu("Juego");

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

        menuBar.add(menuJuego);
        setJMenuBar(menuBar);
    }

    /**
     * Inicia un temporizador para plantar automáticamente plantas estratégicamente en el tablero.
     */
    private void iniciarTimerPlantas() {
        Timer timerPlantas = new Timer(TIEMPO_PLANTAR_PLANTAS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                plantarAutomaticamente();
            }
        });
        timerPlantas.start();
    }

    /**
     * Planta automáticamente las plantas en el tablero de forma estratégica.
     * Reglas:
     * - LanzaGuisantes en las primeras columnas.
     * - Nuez en las últimas columnas.
     * - Mina en las columnas intermedias.
     * Planta automáticamente una planta de manera intercalada en una fila aleatoria.
     */
    private void plantarAutomaticamente() {
        if (plantasSeleccionadas.isEmpty()) {
            return; // No hay plantas seleccionadas para plantar
        }
        Timer timerPlantar = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Escoge una fila aleatoria
                int fila = random.nextInt(5);
                // Determina la columna según el tipo de planta actual
                int columna;

                String planta =  plantasSeleccionadas.get(tipoPlantaActual);
                tipoPlantaActual = (tipoPlantaActual + 1) % plantasSeleccionadas.size();

                switch (planta) {
                    case "LanzaGuisantes":
                        columna = random.nextBoolean() ? 1 : 2; // Columna 1 o 2
                        break;
                    case "Mina":
                        columna = random.nextBoolean() ? 3 : 4; // Columna 3 o 4
                        break;
                    case "Nuez":
                        columna = random.nextBoolean() ? 5 : 6; // Columna 5 o 6
                        break;
                    default:
                        return; // Caso no válido (seguridad adicional)
                }

                // Planta la planta en la celda seleccionada solo si esta vacia
                if (!gameMvsM.hayPlantaEnCelda(fila, columna) && !gameMvsM.hayZombieEnCelda(fila, columna)) {
                    plantarEnCeldaAuto(fila, columna, planta);
                }
            }
        });
        timerPlantar.start(); // Inicia el temporizador
    }

    /**
     * Planta una planta específica en la celda si es posible.
     * @param fila La fila donde se quiere plantar.
     * @param columna La columna donde se quiere plantar.
     * @param planta El nombre de la planta a plantar.
     */
    private void plantarEnCeldaAuto(int fila, int columna, String planta) {
        // Obtener la celda correspondiente
        JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + columna);

        // Planta la planta si la celda está vacía y en una columna válida
        if (columna >= 0 && columna <= 9) {
            if (planta.equals("LanzaGuisantes")){
                celda.setIcon(new ImageIcon("resources/lanzaguisantes.gif"));
                gameMvsM.crearEntidad(fila, columna, "LanzaGuisantes");
            }
            if (planta.equals("Nuez")){
                celda.setIcon(new ImageIcon("resources/nuez.gif"));
                gameMvsM.crearEntidad(fila, columna, "Nuez");
            }
            if (planta.equals("Mina")){
                celda.setIcon(new ImageIcon("resources/mina.gif"));
                gameMvsM.crearEntidad(fila, columna, "Mina");
            }

            // Aseguramos que los cambios visuales se reflejen en el tablero
            celda.revalidate();
            celda.repaint();
        }
    }

    /**
     * Inicia el temporizador para la generación de zombies.
     * Muestra un mensaje de advertencia y luego comienza la generación continua de zombies.
     */
    private void iniciarTimerZombie() {
        Timer mensajeTimer = new Timer(TIEMPO_INICIAL_ZOMBIES, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarMensajeEmergente("¡Maquina de zombies ya puede atacar!", 2000);
                ((Timer)e.getSource()).stop();
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
    private void iniciarGeneracionContinuaZombies(){
        tiempoActualGeneracionZombie = TIEMPO_INICIAL_GENERACION_ZOMBIE;
        Timer zombieTimer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarZombie();
                //Vamos reduciendo el tiempo para el proximo zombie hasta llegar al minimo
                tiempoActualGeneracionZombie = Math.max(tiempoActualGeneracionZombie - DECREMENTO_TIEMPO, TIEMPO_MINIMO_GENERACION_ZOMBIE);
                ((Timer) e.getSource()).setDelay(tiempoActualGeneracionZombie);
            }
        });
        zombieTimer.setInitialDelay(0);
        zombieTimer.start();
    }


    /**
     * Genera un zombie en una posición aleatoria del tablero.
     * Selecciona un tipo de zombie aleatorio y lo coloca en la última columna de una fila aleatoria.
     */
    private void generarZombie() {
        if (zombiesSeleccionados.isEmpty()) {
            return;
        }

        int fila =  new Random().nextInt(5); // Genera un número aleatorio entre 0 y 4
        int columna = 9; // Última columna

        if (gameMvsM.sePuedeGenerarZombie(fila, columna)) {
            String tipoZombie = zombiesSeleccionados.get(tipoZombieActual);
            tipoZombieActual = (tipoZombieActual + 1) % zombiesSeleccionados.size();
            JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + columna);

            gameMvsM.crearEntidad(fila, columna, tipoZombie);

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
     * Inicia el movimiento de un zombie en el tablero.
     * Mueve el zombie de derecha a izquierda, manejando colisiones con plantas y podadoras.
     *
     * @param fila La fila en la que se mueve el zombie.
     * @param columna La columna inicial del zombie.
     * @param tipoZombie El tipo de zombie que se está moviendo.
     */
    private void moverZombie(int fila, int columna, String tipoZombie) {
        Timer movimientoTimer = new Timer(2000, new ActionListener() { // Mueve cada 2 segundos
            int columnaActual = columna;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (columnaActual > 0) {
                    if (columnaActual == 1 && gameMvsM.tienePodadora(fila)) {
                        // Activar la podadora
                        gameMvsM.activarPodadora(fila);
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
                    gameMvsM.moverZombie(fila);
                }

                // Verificar si hay una mina activada en la casilla adyacente
                if (gameMvsM.hayMinaActivadaEnSiguienteColumna(fila, columnaActual - 1)) {
                    eliminarZombieYMina(fila, columnaActual - 1);
                    ((Timer)e.getSource()).stop();
                    return;
                }

                // Verificar si hay una planta en la siguiente columna
                if (gameMvsM.hayPlantaEnSiguienteColumna(fila, columnaActual - 1)) {
                    atacarPlanta(fila, columnaActual - 1, tipoZombie);
                    ((Timer)e.getSource()).stop();
                    return;
                }

                // Verificamos si el zombie ha llegado a la primera columna
                if (columnaActual == 0) {
                    if (!gameMvsM.tienePodadora(fila)) {
                        gameMvsM.setGameOver(true);
                        verificarGameOver();
                    }
                    ((Timer)e.getSource()).stop();
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
        gameMvsM.eliminarZombieYMina(fila, columna);
        JButton celdaZombie = (JButton) tableroPanel.getComponent(fila * 10 + (columna + 1));
        JButton celdaMina = (JButton) tableroPanel.getComponent(fila * 10 + columna);
        celdaZombie.setIcon(null);
        celdaMina.setIcon(null);
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
                Entidad entidad = gameMvsM.getEntidadEn(fila, columna);
                if (entidad instanceof Planta) {
                    Planta planta = (Planta) entidad;
                    planta.recibirDaño(100);
                    System.out.println(planta.getNombre() + " recibió daño 100 de daño y le queda: " + planta.getVida() + " de vida");
                    if (planta.getVida() <= 0) {
                        // La planta ha sido eliminada
                        gameMvsM.quitarEntidad(fila, columna);
                        JButton celda = (JButton) tableroPanel.getComponent(fila * 10 + columna);
                        celda.setIcon(null);
                        ((Timer)e.getSource()).stop();
                        // Mover el zombie a la posición de la planta eliminada
                        JButton celdaZombie = (JButton) tableroPanel.getComponent(fila * 10 + (columna + 1));
                        celdaZombie.setIcon(null);
                        celda.setIcon(new ImageIcon("resources/" + tipoZombie.toLowerCase() + ".gif"));
                        gameMvsM.moverZombie(fila);
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
            volverAModos();
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
     * Verifica si el juego ha terminado.
     */
    private void verificarGameOver() {
        if (gameMvsM.isGameOver()) {
            timerActualizacion.stop();
            JOptionPane.showMessageDialog(this, "¡Maquina de Zombies ha Ganado!", "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
            volverAlMenu();
        }
    }
}
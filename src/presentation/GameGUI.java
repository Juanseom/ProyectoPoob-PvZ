package presentation;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.border.BevelBorder;

/**
 * Clase principal de la GUI para el juego POOB vs Zombies.
 * Gestiona el menú principal y la navegación a otras pantallas.
 */
public class GameGUI extends JFrame {

    private JLayeredPane layeredPane;
    public static Font fuenteZombi;
    private static GameGUI instance;
    private Canvas canvas;
    private Clip menuClip;

    private JButton botonJugar;
    private JButton botonTutorial;
    private JButton botonAcercaDe;
    private JButton botonSalir;

    /**
     * Constructor de GameGUI.
     * Inicializa la ventana principal y sus componentes.
     */
    public GameGUI() {
        instance = this;
        setTitle("POOB vs Zombies");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        playMusic("resources/menuTheme.wav");

        layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        prepareElements();
        prepareActions();
        setVisible(true);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(800,600));
        canvas.setMaximumSize(new Dimension(800,600));
        canvas.setMinimumSize(new Dimension(800,600));
        add(canvas);
    }

    /**
     * Obtiene la instancia única de GameGUI.
     * @return La instancia de GameGUI.
     */

    public static GameGUI getInstance(){
        return instance;
    }

    /**
     * Prepara los elementos visuales de la GUI.
     */
    private void prepareElements(){
        ImageIcon imagenBackground = new ImageIcon("resources/backgroundMenu.png");
        JLabel backgroundLabel = new JLabel(imagenBackground);
        backgroundLabel.setBounds(0,0,800,600);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        ImageIcon imagenPvZ = new ImageIcon("resources/PvZMenu.png");
        JLabel pvzLabel = new JLabel(imagenPvZ);
        pvzLabel.setBounds(240,0,311,300);
        layeredPane.add(pvzLabel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER + 1));

        JPanel botonesPanel = new JPanel();
        botonesPanel.setLayout(new BoxLayout(botonesPanel, BoxLayout.Y_AXIS));
        botonesPanel.setOpaque(false);
        botonesPanel.setBounds(530,215,180,300);

        botonJugar = new JButton("Jugar");
        configuracionBotonMenu(botonJugar);
        botonTutorial = new JButton("Tutorial");
        configuracionBotonMenu(botonTutorial);
        botonAcercaDe = new JButton("Acerca de");
        configuracionBotonMenu(botonAcercaDe);
        botonSalir = new JButton("Salir");
        configuracionBotonMenu(botonSalir);

        botonesPanel.add(Box.createVerticalGlue());
        botonesPanel.add(botonJugar);
        botonesPanel.add(Box.createRigidArea(new Dimension(0, 7)));
        botonesPanel.add(botonTutorial);
        botonesPanel.add(Box.createRigidArea(new Dimension(0, 7)));
        botonesPanel.add(botonAcercaDe);
        botonesPanel.add(Box.createRigidArea(new Dimension(0, 7)));
        botonesPanel.add(botonSalir);
        botonesPanel.add(Box.createVerticalGlue());

        layeredPane.add(botonesPanel, JLayeredPane.PALETTE_LAYER);
    }

    /**
     * Configura las acciones de los botones.
     */
    private void prepareActions(){
        botonJugar.addActionListener(e -> abrirVentanaJuego());
        botonTutorial.addActionListener(e ->  abrirVentanaTutotial());
        botonAcercaDe.addActionListener(e -> abrirVentanaAcercaDe());
        botonSalir.addActionListener(e -> salirJuegoBoton());
    }

    /**
     * Configura la apariencia y propiedades de un botón del menú.
     * @param boton El JButton que se va a configurar para el menú.
     */
    private void configuracionBotonMenu(JButton boton){
        fuenteZombi = cargarFuente();

        boton.setPreferredSize(new Dimension(150,40));
        boton.setMaximumSize(new Dimension(150, 40));
        boton.setMinimumSize(new Dimension(150, 40));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setContentAreaFilled(false);
        boton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.BLACK));
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setFont(fuenteZombi.deriveFont(Font.PLAIN, 30f));
    }

    /**
     * Carga la fuente personalizada del juego.
     * @return La fuente cargada o una fuente por defecto.
     */
    private Font cargarFuente(){
        try{
            InputStream is = getClass().getResourceAsStream("/resources/fuenteZombi.otf");
            if (is == null) {
                is = getClass().getClassLoader().getResourceAsStream("resources/fuenteZombi.otf");
            }
            if (is == null) {
                File file = new File("resources/fuenteZombi.otf");
                if (file.exists()) {
                    is = new FileInputStream(file);
                }
            }
            if (is == null) {
                throw new IOException("No se pudo encontrar el archivo de fuente");
            }
            fuenteZombi = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(fuenteZombi);
            return fuenteZombi;
        }catch (IOException | FontFormatException e){
            e.printStackTrace(); // para mas informacion del error
            System.out.println("Error cargando la fuente: " + e.getMessage());
            return new Font("Arial", Font.BOLD, 12); // Retorna una fuente por defecto si falla la carga
        }
    }


    /**
     * Abre la ventana del boton Jugar.
     */
    private void abrirVentanaJuego() {
        setVisible(false);
        SwingUtilities.invokeLater(() -> new VentanaJuego());
    }

    /**
     * Abre la ventana de tutoriales.
     */
    private void abrirVentanaTutotial(){
        setVisible(false);
        SwingUtilities.invokeLater(() -> new VentanaTutoriales());
    }

    /**
     *
     * Abre la ventana "Acerca de".
     */
    private void abrirVentanaAcercaDe(){
        setVisible(false);
        SwingUtilities.invokeLater(() -> new VentanaAcercaDe());
    }


    /**
     * Muestra un diálogo de confirmación para salir del juego.
     * Si el usuario confirma, la aplicación se cerrará.
     */
    private void salirJuegoBoton() {
        int respuesta = JOptionPane.showConfirmDialog(GameGUI.this,
                "¿Quieres salir del juego?", "Confirmar cierre",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Reproduce la música de fondo para el juego.
     * Este método carga un archivo de audio, lo abre como un clip y comienza a reproducirlo en un bucle continuo.
     * @param filepath La ruta al archivo de audio que se reproducirá.
     */
    public void playMusic(String filepath){
        try{
            File menuAudioFile = new File(filepath);
            AudioInputStream menuAudioStream = AudioSystem.getAudioInputStream(menuAudioFile);

            menuClip = AudioSystem.getClip();
            menuClip.open(menuAudioStream);
            menuClip.loop(Clip.LOOP_CONTINUOUSLY);//Para ponerla en bucle
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Error al reproducir musica" + e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
        }
    }
     /**
     * Pausa la música de fondo reproducida.
     */
    public void stopMusic() {
        if (menuClip != null && menuClip.isRunning()) {
            menuClip.stop();
            menuClip.close();
        }
    }

    /**
     * Hace visible la ventana principal.
     */
    public void mostrar(){
        setVisible(true);
    }

    /**
     * Método principal para iniciar la aplicación.
     */
    public static void main(String[] args) {
        new GameGUI();
    }
}

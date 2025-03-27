package presentation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana para la selección de plantas en el juego.
 */
public class VentanaSeleccionZombies extends JFrame {
    private List<String> zombiesSeleccionados;

    /**
     * Constructor de la ventana de selección de zombies.
     * Inicializa la interfaz gráfica y los componentes para seleccionar zombies.
     */
    public VentanaSeleccionZombies() {
        zombiesSeleccionados = new ArrayList<>();
        setTitle("Seleccionar Zombies (Da click en la imagen para dejar seleccionado el Zombie)");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBackground(new Color(130, 32, 200));

        // Crear los JCheckBox con imágenes alternadas
        JCheckBox zombieNormalCheckBox = crearCheckBoxConImagen(
                "Zombie Normal",
                "resources/cartaZombieNormalSeleccionado.png",
                "resources/cartaZombieNormal.png"

        );
        configurarFuente(zombieNormalCheckBox);

        JCheckBox zombieConoCheckBox = crearCheckBoxConImagen(
                "Zombie Cono",
                "resources/cartaZombieConoSeleccionado.png",
                "resources/cartaZombieCono.png"
        );
        configurarFuente(zombieConoCheckBox);

        JCheckBox zombieBaldeCheckBox = crearCheckBoxConImagen(
                "Zombie Balde",
                "resources/cartaZombieBaldeSeleccionado.png",
                "resources/cartaZombieBalde.png"
        );
        configurarFuente(zombieBaldeCheckBox);

        JCheckBox brainsteinCheckBox = crearCheckBoxConImagen(
                "Brainstein (Solo para PvP)",
                "resources/cartaBrainsteinSeleccionado.png",
                "resources/cartaBrainstein.png"
        );
        configurarFuente(brainsteinCheckBox);

        JCheckBox eciZombieCheckBox = crearCheckBoxConImagen(
                "ECI Zombie",
                "resources/cartaEciZombieSeleccionado.png",
                "resources/cartaEciZombie.png"
        );
        configurarFuente(eciZombieCheckBox);

        // Añadir los JCheckBox al panel
        panel.add(zombieNormalCheckBox);
        panel.add(zombieConoCheckBox);
        panel.add(zombieBaldeCheckBox);
        panel.add(brainsteinCheckBox);
        panel.add(eciZombieCheckBox);

        // Botón para confirmar selección
        JButton confirmarButton = new JButton("Confirmar");
        confirmarButton.setBackground(new Color(100, 30, 230));
        confirmarButton.setForeground(Color.WHITE);
        confirmarButton.setFocusPainted(false);
        confirmarButton.setFont(GameGUI.fuenteZombi.deriveFont(Font.BOLD, 20f));
        confirmarButton.addActionListener(e -> {
            zombiesSeleccionados.clear();
            if (zombieNormalCheckBox.isSelected()) zombiesSeleccionados.add("ZombieNormal");
            if (zombieConoCheckBox.isSelected()) zombiesSeleccionados.add("ZombieCono");
            if (zombieBaldeCheckBox.isSelected()) zombiesSeleccionados.add("ZombieBalde");
            if (brainsteinCheckBox.isSelected()) zombiesSeleccionados.add("Brainstein");
            if (eciZombieCheckBox.isSelected()) zombiesSeleccionados.add("ECIZombie");
            if (zombiesSeleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Debes seleccionar al menos un zombie para continuar.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                dispose();
            }
        });

        panel.add(confirmarButton);
        add(panel);

        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(100, 20, 200), 15));
        setVisible(true);
    }


    /**
     * Método para crear un JCheckBox con diferentes imágenes para los estados seleccionado y no seleccionado.
     *
     * @param texto El texto a mostrar.
     * @param rutaImagenNormal La ruta de la imagen para el estado no seleccionado.
     * @param rutaImagenSeleccionada La ruta de la imagen para el estado seleccionado.
     * @return Un JCheckBox con imágenes para ambos estados.
     */
    private JCheckBox crearCheckBoxConImagen(String texto, String rutaImagenNormal, String rutaImagenSeleccionada) {
        // Crear íconos para ambos estados
        ImageIcon iconNormal = new ImageIcon(new ImageIcon(rutaImagenNormal).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        ImageIcon iconSeleccionado = new ImageIcon(new ImageIcon(rutaImagenSeleccionada).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        JCheckBox checkBox = new JCheckBox(texto, iconNormal);
        checkBox.setHorizontalTextPosition(SwingConstants.RIGHT); // Texto a la derecha de la imagen
        checkBox.setIconTextGap(10); // Espacio entre la imagen y el texto
        checkBox.setOpaque(false); // Fondo transparente
        checkBox.setSelectedIcon(iconSeleccionado); // Imagen para el estado seleccionado

        return checkBox;
    }


    /**
     * Configura la fuente y el color del texto de un JCheckBox.
     *
     * @param componente El JCheckBox a configurar.
     */
    private void configurarFuente(JCheckBox componente) {
        componente.setFont(GameGUI.fuenteZombi.deriveFont(Font.PLAIN, 18f));
        componente.setForeground(Color.WHITE);
    }

    /**
     * Obtiene la lista de plantas seleccionadas por el usuario.
     *
     * @return Lista de nombres de las plantas seleccionadas.
     */
    public List<String> getZombiesSeleccionados() {
        return zombiesSeleccionados;
    }
}

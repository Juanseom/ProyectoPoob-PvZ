package presentation;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana para la selección de plantas en el juego.
 */
public class VentanaSeleccionPlantas extends JFrame implements Serializable {
    private List<String> plantasSeleccionadas;

    /**
     * Constructor de la ventana de selección de plantas.
     * Inicializa la interfaz gráfica y los componentes para seleccionar plantas.
     */
    /**
     * Constructor de la ventana de selección de plantas.
     * Inicializa la interfaz gráfica y los componentes para seleccionar plantas.
     */
    public VentanaSeleccionPlantas() {
        plantasSeleccionadas = new ArrayList<>();
        setTitle("Seleccionar Plantas (Da click en la imagen para dejar seleccionada la planta)");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBackground(new Color(140, 70, 0));

        // Crear los JCheckBox con imágenes alternadas
        JCheckBox girasolCheckBox = crearCheckBoxConImagen(
                "Girasol (Solo para PvZ)",
                "resources/cartaGirasolSeleccionado.png",
                "resources/cartaGirasol.png"
        );
        configurarFuente(girasolCheckBox);

        JCheckBox lanzaGuisantesCheckBox = crearCheckBoxConImagen(
                "LanzaGuisantes",
                "resources/cartaLanzaGuisantesSeleccionado.png",
                "resources/cartaLanzaGuisantes.png"
        );
        configurarFuente(lanzaGuisantesCheckBox);

        JCheckBox nuezCheckBox = crearCheckBoxConImagen(
                "Nuez",
                "resources/cartaNuezSeleccionado.png",
                "resources/cartaNuez.png"
        );
        configurarFuente(nuezCheckBox);

        JCheckBox minaCheckBox = crearCheckBoxConImagen(
                "Mina",
                "resources/cartaMinaSeleccionado.png",
                "resources/cartaMina.png"
        );
        configurarFuente(minaCheckBox);

        JCheckBox eciPlantCheckBox = crearCheckBoxConImagen(
                "ECIPlant (Solo para PvZ)",
                "resources/cartaEciPlantSeleccionado.png",
                "resources/cartaEciPlant.png"
        );
        configurarFuente(eciPlantCheckBox);

        JCheckBox evolveCheckBox = crearCheckBoxConImagen(
                "Evolve",
                "resources/cartaEvolveSeleccionado.png",
                "resources/cartaEvolve.png"
        );
        configurarFuente(evolveCheckBox);


        // Añadir los JCheckBox al panel
        panel.add(girasolCheckBox);
        panel.add(lanzaGuisantesCheckBox);
        panel.add(nuezCheckBox);
        panel.add(minaCheckBox);
        panel.add(eciPlantCheckBox);
        panel.add(evolveCheckBox);

        // Botón para confirmar selección
        JButton confirmarButton = new JButton("Confirmar");
        confirmarButton.setBackground(new Color(120, 60, 0));
        confirmarButton.setForeground(Color.WHITE);
        confirmarButton.setFocusPainted(false);
        confirmarButton.setFont(GameGUI.fuenteZombi.deriveFont(Font.BOLD, 20f));
        confirmarButton.addActionListener(e -> {
            plantasSeleccionadas.clear();
            if (girasolCheckBox.isSelected()) plantasSeleccionadas.add("Girasol");
            if (lanzaGuisantesCheckBox.isSelected()) plantasSeleccionadas.add("LanzaGuisantes");
            if (nuezCheckBox.isSelected()) plantasSeleccionadas.add("Nuez");
            if (minaCheckBox.isSelected()) plantasSeleccionadas.add("Mina");
            if (eciPlantCheckBox.isSelected()) plantasSeleccionadas.add("ECIPlant");
            if (evolveCheckBox.isSelected()) plantasSeleccionadas.add("Evolve");
            if (plantasSeleccionadas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Debes seleccionar al menos una planta para continuar.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                dispose();
            }
        });

        panel.add(confirmarButton);
        add(panel);

        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(110, 55, 0), 15)); // Borde naranja
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
    public List<String> getPlantasSeleccionadas() {
        return plantasSeleccionadas;
    }
}

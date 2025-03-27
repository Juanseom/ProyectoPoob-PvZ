package domain;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.Timer;

/**
 * Clase que representa una planta tipo Mina en el juego.
 * Esta planta se activa después de un tiempo determinado y puede explotar para eliminar zombis.
 * Extiende la clase Planta.
 */
public class Mina extends Planta implements Serializable {
    private boolean activada;//atributo para revisar si la mina ya esta activada para explotar
    private boolean active; // marca si esta viva
    private transient Timer timerMina;
    private static final int TIEMPO_PARA_ACTIVAR = 14000;

    /**
     * Constructor de la clase Mina.
     * Inicializa una nueva Mina con 100 puntos de vida, 25 de costo y nombre "Nuez".
     * Inicia el temporizador para la activación de la mina.
     */
    public Mina() {
        super(100, 25, "Nuez", true);
        this.activada = false;
        iniciarTimerMina();
    }

    /**
     * Inicia el temporizador para la activación de la mina.
     * La mina se activará después de TIEMPO_PARA_ACTIVAR milisegundos.
     */
    private void iniciarTimerMina() {
        timerMina = new Timer(TIEMPO_PARA_ACTIVAR, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activada = true;
                timerMina.stop();
            }
        });
        timerMina.setRepeats(false);
        timerMina.start();
    }

    /**
     * Intenta hacer explotar la mina contra un zombie.
     * Si la mina está activada, elimina al zombie y se desactiva a sí misma.
     * @param zombie El zombie contra el que explota la mina.
     * @return true si la mina explotó, false en caso contrario.
     */
    public boolean explotar(Zombie zombie) {
        if (activada) {
            zombie.recibirDaño(zombie.getVida()); // Elimina al zombie completamente
            this.recibirDaño(this.getVida()); // Desactiva la mina para evitar que explote más de una vez
            return true; // Indica que la mina explotó
        }
        return false;
    }

    /**
     * Maneja el daño recibido por la mina.
     * Si la vida llega a 0 o menos, detiene el temporizador de activación.
     * @param daño La cantidad de daño recibido.
     */
    @Override
    public void recibirDaño(int daño) {
        super.recibirDaño(daño);
        if (getVida() <= 0) {
            if (timerMina != null) {
                timerMina.stop();
            }
        }
    }

    /**
     * Verifica si la mina está activada y lista para explotar.
     * @return true si la mina está activada, false en caso contrario.
     */
    public boolean estaActivada() {
        return activada;
    }

    /**
     * Verifica si la mina está activa (viva) en el juego.
     * @return true si la mina está activa, false en caso contrario.
     */
    public boolean isActiva() {
        return active;
    }

    /**
     * Obtiene la fila en la que se encuentra la Mina.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getFila() {
        return 0;
    }

    /**
     * Obtiene la columna en la que se encuentra la Mina.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getColumna() {
        return 0;
    }

    @Override
    public int getCosto() {
        return 25;
    }
}

package domain;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase que representa una planta Girasol en el juego.
 * Esta planta genera soles periódicamente, que son utilizados como recurso en el juego.
 * Extiende la clase Planta.
 */
public class Girasol extends Planta implements Serializable {
    private static final int INTERVALO_GENERACION = 20000;
    private transient Timer timer;
    private static final int SOLES_GENERADOS = 25;
    private boolean active;
    private GamePvsM juego;

    /**
     * Constructor de la clase Girasol.
     * Inicializa un nuevo Girasol con 300 puntos de vida, 50 de costo y nombre "Girasol".
     * También inicia la generación de soles.
     * @param juego Referencia al juego PvsM en el que se encuentra el girasol.
     */
    public Girasol(GamePvsM juego) {
        super(300, 50, "Girasol", true);
        this.juego = juego;
        iniciarGeneracionDeSoles();
    }

    /**
     * Maneja el daño recibido por el girasol.
     * Si la vida llega a 0 o menos, detiene la generación de soles y desactiva el girasol.
     * @param daño La cantidad de daño recibido.
     */
    @Override
    public void recibirDaño(int daño) {
        super.recibirDaño(daño);
        if (getVida() <= 0) {
            detenerGeneracionDeSoles();
            active = false;
        }
    }

    /**
     * Detiene la generación de soles cancelando el timer.
     */
    public void detenerGeneracionDeSoles() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Inicia el proceso de generación periódica de soles.
     * Los soles se generan cada INTERVALO_GENERACION milisegundos, con un retraso inicial de 10 segundos.
     */
    private void iniciarGeneracionDeSoles() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                juego.incrementarSoles(SOLES_GENERADOS);
            }
        }, 10000, INTERVALO_GENERACION);
    }

    /**
     * Obtiene la fila en la que se encuentra el Girasol.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getFila() {
        return 0;
    }

    /**
     * Obtiene la columna en la que se encuentra el Girasol.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getColumna() {
        return 0;
    }

    @Override
    public int getCosto() {
        return 50;
    }

}
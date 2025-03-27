package domain;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase que representa una planta especial llamada ECIPlant en el juego.
 * Esta planta tiene la capacidad de generar soles periódicamente.
 * Extiende la clase Planta.
 */
public class ECIPlant extends Planta implements Serializable {
    private static final int INTERVALO_GENERACION = 20000;
    private transient Timer timer;
    private static final int SOLES_GENERADOS = 50;
    private boolean active;
    private GamePvsM juego;

    /**
     * Constructor de la clase ECIPlant.
     * Inicializa una nueva ECIPlant con 150 puntos de vida, 75 de costo y nombre "ECIPlant".
     * También inicia la generación de soles.
     * @param juego Referencia al juego PvsM en el que se encuentra la planta.
     */
    public ECIPlant(GamePvsM juego) {
        super(150, 75, "ECIPlant", true);
        this.juego = juego;
        iniciarGeneracionDeSoles();
    }

    /**
     * Maneja el daño recibido por la planta.
     * Si la vida llega a 0 o menos, detiene la generación de soles y desactiva la planta.
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
     * Inicia la generación periódica de soles.
     * Programa un timer para generar soles cada INTERVALO_GENERACION milisegundos,
     * con un retraso inicial de 10 segundos.
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
     * Obtiene la fila en la que se encuentra la ECIPlant.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getFila() {
        return 0;
    }

    /**
     * Obtiene la columna en la que se encuentra la ECIPlant.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getColumna() {
        return 0;
    }

    /**
     * Verifica si la ECIPlant está activa.
     * @return Siempre devuelve true, lo que podría no reflejar el estado real de la planta.
     */
    public boolean isActiva() {
        return true;
    }

    @Override
    public int getCosto() {
        return 75;
    }
}
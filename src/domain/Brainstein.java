package domain;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase que representa al zombie Brainstein, un tipo especial de zombie que puede generar cerebros.
 * Extiende la clase Zombie.
 */
public class Brainstein extends Zombie {
    private static final int INTERVALO_GENERACION = 20000;
    private Timer timer;
    private static final int CEREBROS_GENERADOS = 25;
    private boolean active;
    private GamePvsP juego;

    /**
     * Constructor de la clase Brainstein.
     * Inicializa un nuevo Brainstein con valores predeterminados.
     */
    public Brainstein(GamePvsP juego) {
        super(300,50, "Brainstein", true);
        this.juego = juego;
        iniciarGeneracionDeCerebros();
    }

    /**
     * Recibe daño el Brainstein.
     * @param daño La cantidad de daño a recibir.
     */
    @Override
    public void recibirDaño(int daño) {
        super.recibirDaño(daño);
        if (getVida() <= 0) {
            detenerGeneracionDeCerebros();
            active = false;
        }
    }

    /**
     * Detiene la generación de cerebros cancelando el timer.
     */
    public void detenerGeneracionDeCerebros() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Inicia el proceso de generación periódica de soles.
     * Los soles se generan cada INTERVALO_GENERACION milisegundos, con un retraso inicial de 10 segundos.
     */
    private void iniciarGeneracionDeCerebros() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                juego.incrementarCerebros(CEREBROS_GENERADOS);
            }
        }, 10000, INTERVALO_GENERACION);
    }

    /**
     * Obtiene la fila en la que se encuentra el Brainstein.
     * @return Siempre devuelve 0.
     */
    @Override
    public int getFila() {
        return 0;
    }

    /**
     * Obtiene la columna en la que se encuentra el Brainstein.
     * @return Siempre devuelve 0.
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



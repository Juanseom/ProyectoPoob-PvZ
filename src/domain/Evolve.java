package domain;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * La clase {@code Evolve} representa una planta que evoluciona
 */
public class Evolve extends Planta implements Serializable {
    private int etapa;
    private Timer timer;
    private Timer ataqueTimer;

    /**
     * Constructor de la clase {@code Evolve}.
     * Inicializa la planta con sus atributos predeterminados (vida, costo, nombre y estado).
     * También inicia el temporizador para evolucionar y el temporizador para atacar en la segunda etapa.
     */
    public Evolve() {
        super(200, 500, "Evolve", true);
        this.etapa = 0;
        this.timer = new Timer();
        this.ataqueTimer = new Timer();
        iniciarEvolucion();
    }

    /**
     * Maneja el timer para evolucionar en la primera etapa.
     */
    private void iniciarEvolucion() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                evolucionarASegundaEtapa();
            }
        }, 15000); // 15 segundos para la primera etapa
    }

    /**
     * Maneja el timer para evolucionar en la segunda etapa.
     */
    private void evolucionarASegundaEtapa() {
        etapa = 2;
        iniciarAtaqueSegundaEtapa();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                evolucionarATerceraEtapa();
            }
        }, 20000); // 20 segundos para la segunda etapa
    }

    /**
     * Maneja el timer para evolucionar en la tercera etapa.
     */
    private void evolucionarATerceraEtapa() {
        etapa = 3;
        ataqueTimer.cancel();
        iniciarAtaqueTerceraEtapa();
    }

    /**
     * Maneja el timer para atacar en la segunda etapa.
     */
    private void iniciarAtaqueSegundaEtapa() {
        ataqueTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                atacar();
            }
        }, 0, 3000); // Ataca cada 3 segundos
    }

    /**
     * Maneja el timer para atacar en la tercera etapa.
     */
    private void iniciarAtaqueTerceraEtapa() {
        ataqueTimer = new Timer();
        ataqueTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                atacar();
            }
        }, 0, 1000); // Ataca cada segundo
    }

    /**
     * Ataca en la planta según la etapa en la que se encuentra.
     */
    public void atacar() {
        if (etapa == 1) {
            // No hace nada en la primera etapa
            return;
        }
        System.out.println("Evolve ataca en etapa " + etapa);
    }

    /**
     * Devuelve la etapa en la que se encuentra la planta.
     * @return etapa en la que se encuentra la planta
     */
    public int getEtapa(){
        return etapa;
    }


    @Override
    public int getCosto() {
        return 200;
    }

    @Override
    public int getFila() {
        return 0;
    }

    @Override
    public int getColumna() {
        return 0;
    }
}
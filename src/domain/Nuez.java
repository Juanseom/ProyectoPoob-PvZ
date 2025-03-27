package domain;

import java.io.Serializable;

/**
 * Clase que representa una planta Nuez en el juego.
 * Esta planta tiene una gran cantidad de vida y sirve principalmente como barrera defensiva.
 * Extiende la clase Planta.
 */
public class Nuez extends Planta implements Serializable {
    private Boolean active;

    /**
     * Constructor de la clase Nuez.
     * Inicializa una nueva Nuez con 4000 puntos de vida, 50 de costo y nombre "Nuez".
     */
    public Nuez() {
        super(4000,50 , "Nuez", true);
    }

    /**
     * Maneja el daño recibido por la nuez.
     * Si la vida llega a 0 o menos, desactiva la nuez.
     * @param daño La cantidad de daño recibido.
     */
    @Override
    public void recibirDaño(int daño) {
        super.recibirDaño(daño);
        if (getVida() <= 0) {
            active = false;
        }
    }

    /**
     * Obtiene la fila en la que se encuentra la Nuez.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getFila() {
        return 0;
    }

    /**
     * Obtiene la columna en la que se encuentra la Nuez.
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

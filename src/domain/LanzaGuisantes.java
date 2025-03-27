package domain;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * La clase {@code LanzaGuisantes} representa una planta que dispara Guisantes
 * automáticamente a intervalos regulares. Extiende la clase {@code Planta}.
 */
public class LanzaGuisantes extends Planta implements Serializable {
    private int fila;
    private int columna;

    /**
     * Constructor de la clase {@code LanzaGuisantes}.
     * Inicializa la planta con sus atributos predeterminados (vida, costo, nombre y estado).
     * También inicia el temporizador para disparar Guisantes automáticamente.
     */
    public LanzaGuisantes() {
        super(300, 100, "LanzaGuisantes", true);
    }

    /**
     * Recibe daño y reduce la vida de la planta si está activa.
     *
     * @param daño La cantidad de daño recibido.
     */
    @Override
    public void recibirDaño(int daño) {
        if (activa) {
            super.recibirDaño(daño);
        }
    }

    /**
     * Dispara Guisantes
     * @param zombie Zombie objetivo del ataque.
     */
    public void atacar(Zombie zombie) {
        zombie.recibirDaño(20);
        System.out.println("LanzaGuisantes en [" + fila + "," + columna + "] ataca a " + zombie.getNombre() + " causando 20 de daño." + "Al " + zombie.getNombre() + " le quedan " + zombie.getVida() + " puntos de vida.");
    }

    /**
     * Establece la fila en la que está ubicada la planta.
     * @param fila en la que está ubicada la planta.
     */
    public void setFila(int fila) {
        this.fila = fila;
    }

    /**
     * Establece la columna en la que está ubicada la planta.
     * @param columna en la que está ubicada la planta.
     */
    public void setColumna(int columna) {
        this.columna = columna;
    }

    /**
     * Devuelve la fila en la que está ubicada la planta.
     *
     * @return La fila de la planta.
     */
    @Override
    public int getFila() {
        return fila;
    }

    /**
     * Devuelve la columna en la que está ubicada la planta.
     *
     * @return La columna de la planta.
     */
    @Override
    public int getColumna() {
        return columna;
    }

    @Override
    public int getCosto() {
        return 100;
    }
}
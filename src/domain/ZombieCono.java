package domain;

import java.io.Serializable;

/**
 * Clase que representa un Zombie con un cono en el juego.
 * Este tipo de zombie tiene una resistencia intermedia debido a su protección con el cono.
 * Extiende la clase Zombie.
 */
public class ZombieCono extends Zombie implements Serializable {
    private boolean activo = true;
    private int fila;
    private int columna;

    /**
     * Constructor de la clase ZombieCono.
     * Inicializa un nuevo ZombieCono con 380 puntos de vida, 150 de costo y nombre "ZombieCono".
     */
    public ZombieCono() {
        super(380, 150,"ZombieCono", true);
        System.out.println("Se ha creado un zombie Cono.");
    }

    /**
     * Verifica si el zombie está activo.
     * @return true si el zombie está activo, false en caso contrario.
     */
    public boolean estaActivo() {
        return activo;
    }

    /**
     * Realiza un ataque a una planta si tanto el zombie como la planta están activos.
     * @param planta La planta objetivo del ataque.
     */
    public void atacar(Planta planta) {
        if (this.estaActivo() && planta.estaActiva()) {
            int daño = 100;
            planta.recibirDaño(daño);
            System.out.println(this.getNombre() + " ha atacado a " + planta.getNombre() + " causando " + daño + " de daño.");
        }
    }

    /**
     * Maneja el daño recibido por el zombie.
     * Solo recibe daño si está activo.
     * @param daño La cantidad de daño recibido.
     */
    @Override
    public void recibirDaño(int daño) {
        if (activo) {
            super.recibirDaño(daño);
        }
    }

    /**
     * Obtiene la fila en la que se encuentra el ZombieCono.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getFila() {
        return fila;
    }

    /**
     * Obtiene la columna en la que se encuentra el ZombieCono.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    @Override
    public int getCosto() {
        return 150;
    }
}

package domain;

import java.io.Serializable;

/**
 * Clase que representa un Zombie normal en el juego.
 * Este tipo de zombie es el más básico.
 * Extiende la clase Zombie.
 */
public class ZombieNormal extends Zombie implements Serializable {
    private boolean activo = true;
    private int fila;
    private int columna;

    /**
     * Constructor de la clase ZombieNormal.
     * Inicializa un nuevo ZombieNormal con 100 puntos de vida, 100 de coste y nombre "ZombieNormal".
     */
    public ZombieNormal() {
        super(100,100,"ZombieNormal",true);
        this.fila = 0;
        this.columna = 0;
        System.out.println("Se ha creado un zombie normal.");
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
     * Obtiene la fila en la que se encuentra el ZombieNormal.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getFila() {
        return fila;
    }

    /**
     * Obtiene la columna en la que se encuentra el ZombieNormal.
     * @return Siempre devuelve 0, lo que podría indicar una posición fija o no implementada.
     */
    @Override
    public int getColumna() {
        return columna;
    }

    /**
     * Establece la columna en la que se encuentra el ZombieNormal.
     * @param columna La nueva columna del zombie.
     */
    public void setColumna(int columna) {
        this.columna = columna;
    }

    @Override
    public int getCosto() {
        return 100;
    }
}

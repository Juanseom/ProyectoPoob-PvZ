package domain;

import java.io.Serializable;

/**
 * La clase {@code ECIZombi} representa un zombi que dispara Guisantes
 * automáticamente a intervalos regulares. Extiende la clase {@code Zombi}.
 */
public class ECIZombi extends Zombie implements Serializable {
    private int fila;
    private int columna;

    /**
     * Constructor de la clase {@code ECIZombie}.
     * Inicializa el zombi con sus atributos predeterminados (vida, costo, nombre y estado).
     * También inicia el temporizador para disparar Guisantes automáticamente.
     */
    public ECIZombi() {
        super(300, 100, "ECIZombi", true);
    }

    /**
     * Recibe daño y reduce la vida del zombi si está activa.
     *
     * @param daño La cantidad de daño recibido.
     */
    @Override
    public void recibirDaño(int daño) {
        super.recibirDaño(daño);
    }

    public void atacar(Planta planta) {
        planta.recibirDaño(20);
        System.out.println("ECIZombi en [" + fila + "," + columna + "] ataca a " + planta.getNombre() + " causando 20 de daño." + "Al " + planta.getNombre() + " le quedan " + planta.getVida() + " puntos de vida.");
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    /**
     * Devuelve la fila en la que está ubicada el zombi.
     *
     * @return La fila del zombi.
     */
    @Override
    public int getFila() {
        return fila;
    }

    /**
     * Devuelve la columna en la que está ubicada el zombi.
     *
     * @return La columna del zombi.
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
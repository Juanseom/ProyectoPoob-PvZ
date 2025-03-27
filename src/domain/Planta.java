package domain;

/**
 * Clase abstracta que representa una planta en el juego.
 * Implementa la interfaz Entidad y define las características básicas de todas las plantas.
 */
public abstract class Planta implements Entidad{
    private int costo;
    public static boolean activa;
    private String nombre;
    private int vida;

    /**
     * Constructor para crear una nueva planta.
     *
     * @param vida La cantidad inicial de vida de la planta.
     * @param costo El costo en soles para plantar esta planta.
     * @param nombre El nombre de la planta.
     * @param activa Si la planta está inicialmente activa.
     */
    public Planta (int vida, int costo, String nombre, boolean activa){
        this.vida = vida;
        this.costo = costo;
        this.nombre = nombre;
        this.activa = true;

    }

    /**
     * Método para que la planta reciba daño.
     * Reduce la vida de la planta y la desactiva si su vida llega a cero o menos.
     *
     * @param daño La cantidad de daño que recibe la planta.
     */
    public void recibirDaño(int daño) {
        vida -= daño;
        if (vida <= 0) {
            activa = false;
        }
    }

    // Getters y setters

    /**
     * Obtiene la vida actual de la planta.
     * @return La cantidad de vida actual de la planta.
     */
    public int getVida() {
        return vida;
    }

    /**
     * Obtiene el nombre de la planta.
     * @return El nombre de la planta.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Verifica si la planta está activa.
     * @return true si la planta está activa, false en caso contrario.
     */
    public boolean estaActiva() {
        return activa;
    }

    /**
     * Obtiene el costo de plantar la planta.
     * @return El costo de plantar la planta.
     */
    public abstract int getCosto();
}
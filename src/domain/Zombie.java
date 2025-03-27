package domain;

/**
 * Clase abstracta que representa un zombie en el juego.
 * Implementa la interfaz Entidad y define las características básicas de todos los zombies.
 */
public abstract class Zombie implements Entidad{
    private int costo;
    private boolean activo;
    private String nombre;
    private int vida;
    private int daño;
    private int fila;
    private int columna;


    /**
     * Constructor para crear un nuevo zombie.
     *
     * @param vida La cantidad inicial de vida del zombie.
     * @param costo El costo asociado al zombie (si aplica).
     * @param nombre El nombre del zombie.
     * @param activo Si el zombie está inicialmente activo.
     */
    public Zombie (int vida, int costo, String nombre,boolean activo){
        this.vida = vida;
        this.costo = costo;
        this.nombre = nombre;
        this.activo = true;
        this.daño = daño;
    }

    /**
     * Verifica si el zombie está activo.
     * @return true si el zombie está activo, false en caso contrario.
     */
    public boolean estaActivo() {
        return activo;
    }

    /**
     * Método para que el zombie reciba daño.
     * Reduce la vida del zombie y lo desactiva si su vida llega a cero o menos.
     * @param daño La cantidad de daño que recibe el zombie.
     */
    public void recibirDaño(int daño) {
        vida -= daño;
        if (vida <= 0) {
            vida = 0;
            setActivo(false);
        }
    }

    /**
     * cambia el estado del zombie (activo o no).
     * @param activo estado del zombie
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Realiza un ataque del zombie a una planta.
     * @param planta La planta que será atacada.
     */
    public void atacar(Planta planta) {
        if (this.estaActivo() && planta.estaActiva()) {
            planta.recibirDaño(daño);
            System.out.println(this.getNombre() + " ha atacado a " + planta.getNombre() + " causando " + daño + " de daño.");
        }
    }

    // Getters y setters

    /**
     * Obtiene la vida actual del zombie.
     * @return La cantidad de vida actual del zombie.
     */
    public int getVida() {
        return vida;
    }

    /**
     * Obtiene el nombre del zombie.
     * @return El nombre del zombie.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el daño que puede infligir el zombie.
     * @return La cantidad de daño que puede infligir el zombie.
     */
    public int getDaño(){
        return daño;
    }

    @Override
    public int getFila() {
        return fila;
    }

    @Override
    public int getColumna() {
        return columna;
    }

    /**
     * Establece la fila en la que se encuentra el zombie.
     * @param fila La nueva fila del zombie.
     */
    public void setFila(int fila) {
        this.fila = fila;
    }

    /**
     * Establece la columna en la que se encuentra el zombie.
     * @param columna La nueva columna del zombie.
     */
    public void setColumna(int columna) {
        this.columna = columna;
    }

    /**
     * Obtiene el costo asociado al zombie (si aplica).
     * @return el costo asociado al zombie (si aplica).
     */
    public abstract int getCosto();

}
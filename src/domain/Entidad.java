package domain;

/**
 * La interfaz Entidad representa cualquier objeto que puede existir en el tablero del juego.
 * Esto incluye plantas, zombies, y cualquier otro elemento que tenga una posición en el tablero.
 */
public interface Entidad {

    /**
     * Obtiene la fila en la que se encuentra la entidad en el tablero.
     * @return int que representa el índice de la fila (0-based) donde está ubicada la entidad.
     */
    int getFila();

    /**
     * Obtiene la columna en la que se encuentra la entidad en el tablero.
     * @return int que representa el índice de la columna (0-based) donde está ubicada la entidad.
     */
    int getColumna();

    /**
     * Obtiene la vida actual de la entidad.
     * @return int que representa la cantidad de vida actual de la entidad.
     */
    int getVida();
}
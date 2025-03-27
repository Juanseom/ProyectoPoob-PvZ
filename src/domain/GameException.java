package domain;

/**
 * Clase que representa una excepción específica del juego.
 * Esta clase extiende la clase Exception para manejar errores o situaciones excepcionales
 * que pueden ocurrir durante la ejecución del juego.
 */
public class GameException extends Exception  {

    /**
     * Constructor de la clase GameException.
     * @param message El mensaje de error que describe la excepción.
     */
    public GameException(String message){
        super(message);
    }
}

package domain;

/**
 * Clase que representa el juego Plantas vs Zombies, en este caso, controlado por maquinaas.
 * Maneja la lógica del juego, incluyendo el tablero, las entidades, y las mecánicas del juego.
 */
public class GameMvsM{

    private static final int FILAS = 5;
    private static final int COLUMNAS = 10;
    private static GameMvsM instance;

    private Entidad[][] tablero;
    private boolean[] podadoras;
    private boolean juegoTerminado;

    /**
     * Constructor de la clase GameMvsM.
     * Inicializa el tablero, las listas de zombies, las podadoras y establece la cantidad inicial de soles.
     */
    public GameMvsM() {
        tablero = new Entidad[FILAS][COLUMNAS];

        podadoras = new boolean[FILAS];
        for(int i = 0; i < FILAS; i++) {
            podadoras[i] = true; //Inicializar las podadoras en true
        }

        juegoTerminado = false;
    }

    /**
     * Actualiza el estado del juego.
     * Incrementa los soles, verifica la presencia de zombies en la primera columna,
     * activa las podadoras si es necesario y actualiza el estado de las minas.
     */
    public void actualizarEstadoJuego() {

        // Verificar zombies en la primera columna
        for (int fila = 0; fila < FILAS; fila++) {
            if (tablero[fila][0] instanceof Zombie) {
                if (podadoras[fila]) {
                    activarPodadora(fila);
                } else {
                    juegoTerminado = true;
                    return; // Terminar el juego si un zombie llega a la primera columna sin podadora
                }
            }
        }
        estadoMinas();
    }

    /**
     * Maneja el estado de las minas en el tablero.
     * Verifica las interacciones entre zombies y minas, activando explosiones o ataques según corresponda.
     */
    private void estadoMinas(){
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                if (tablero[fila][columna] instanceof Zombie) {
                    Zombie zombie = (Zombie) tablero[fila][columna];

                    // Verificar si hay una mina en la columna anterior
                    if (columna > 0 && tablero[fila][columna - 1] instanceof Mina) {
                        Mina mina = (Mina) tablero[fila][columna - 1];
                        if (mina.estaActivada() && mina.explotar(zombie)) {
                            // La mina explotó y eliminó al zombie
                            tablero[fila][columna] = null; // Eliminar zombie
                            tablero[fila][columna - 1] = null; // Eliminar mina
                        }
                    }
                }
            }
        }
    }


    /**
     * Crea una nueva entidad en el tablero si la posición está vacía.
     * @param fila La fila donde se creara la entidad.
     * @param columna La columna donde se creara la entidad.
     * @param tipoEntidad El tipo de entidad a crear.
     */
    public void crearEntidad(int fila, int columna, String tipoEntidad) {
        if (getEntidadEn(fila, columna) == null) {
            tablero[fila][columna] = crearEntidad(tipoEntidad);
        }
    }

    /**
     * Quita una entidad del tablero en la posición especificada.
     * @param fila La fila de la entidad a quitar.
     * @param columna La columna de la entidad a quitar.
     */
    public void quitarEntidad(int fila, int columna) {
        if (esPosicionValida(fila, columna)) {
            tablero[fila][columna] = null;
        }
    }

    /**
     * Verifica si hay una planta en la columna siguiente a la posición especificada.
     * @param fila La fila a verificar.
     * @param columna La columna a partir de la cual se verifica la siguiente.
     * @return true si hay una planta en la siguiente columna, false en caso contrario.
     */
    public boolean hayPlantaEnSiguienteColumna(int fila, int columna) {
        if (columna < 0) {
            return false; // No hay columna siguiente si estamos en la columna 0
        }
        Entidad entidad = getEntidadEn(fila, columna);
        return entidad instanceof Planta;
    }


    /**
     * Verifica si hay una mina activada en la columna siguiente a la posición dada.
     *
     * @param fila La fila a verificar.
     * @param columna La columna actual del zombie.
     * @return true si hay una mina activada en la siguiente columna, false en caso contrario.
     */
    public boolean hayMinaActivadaEnSiguienteColumna(int fila, int columna) {
        if (columna >= 0 && columna < COLUMNAS - 1) {
            Entidad entidad = tablero[fila][columna];
            if (entidad instanceof Mina) {
                Mina mina = (Mina) entidad;
                return mina.estaActivada();
            }
        }
        return false;
    }

    /**
     * Elimina tanto el zombie como la mina en la posición especificada.
     *
     * @param fila La fila de la posición.
     * @param columna La columna de la posición.
     */
    public void eliminarZombieYMina(int fila, int columna) {
        // Eliminar la mina
        if (columna >= 0 && columna < COLUMNAS) {
            tablero[fila][columna] = null;
        }

        // Eliminar el zombie (asumiendo que está en la columna siguiente)
        if (columna + 1 < COLUMNAS) {
            tablero[fila][columna + 1] = null;
        }
    }


    /**
     * Verifica si hay un zombie en la celda especificada.
     * @param fila La fila de la celda a verificar.
     * @param columna La columna de la celda a verificar.
     * @return true si hay un zombie en la celda, false en caso contrario.
     */
    public boolean hayZombieEnCelda(int fila, int columna) {
        return tablero[fila][columna] instanceof Zombie;
    }

    /**
     * Verifica si hay una planta en la celda especificada.
     * @param fila La fila de la celda a verificar.
     * @param columna La columna de la celda a verificar.
     * @return true si hay una planta en la celda, false en caso contrario.
     */
    public boolean hayPlantaEnCelda(int fila, int columna) {
        return tablero[fila][columna] instanceof Planta;
    }

    /**
     * Crea una nueva entidad del tipo especificado.
     * @param tipoEntidad El tipo de entidad a crear.
     * @return La nueva entidad creada.
     * @throws IllegalArgumentException si el tipo de entidad no es válido.
     */
    private Entidad crearEntidad(String tipoEntidad) {
        switch (tipoEntidad) {
            case "LanzaGuisantes":
                return new LanzaGuisantes();
            case "Nuez":
                return new Nuez();
            case "Mina":
                return new Mina();
            case "ZombieNormal":
                return new ZombieNormal();
            case "ZombieCono":
                return new ZombieCono();
            case "ZombieBalde":
                return new ZombieBalde();
            default:
                throw new IllegalArgumentException("Tipo de entidad no válido: " + tipoEntidad);
        }
    }

    /**
     * Verifica si se puede generar un zombie en la posición especificada.
     * @param fila La fila donde se intenta generar el zombie.
     * @param columna La columna donde se intenta generar el zombie.
     * @return true si se puede generar un zombie en la posición, false en caso contrario.
     */
    public boolean sePuedeGenerarZombie(int fila, int columna) {
        if (fila >= 0 && fila < FILAS && columna == COLUMNAS - 1 && tablero[fila][columna] == null) {
            return true;
        }
        return false;
    }


    /**
     * Mueve los zombies en una fila específica hacia la izquierda si es posible.
     * @param fila La fila en la que se moverán los zombies.
     */
    public void moverZombie(int fila) {
        for (int col = 0; col < COLUMNAS - 1; col++) {
            if (tablero[fila][col] instanceof Zombie) {
                Zombie zombie = (Zombie) tablero[fila][col];
                int columnaActual = zombie.getColumna();

                if (columnaActual > 0) {
                    if (tablero[fila][columnaActual - 1] == null) {
                        tablero[fila][columnaActual] = null;
                        tablero[fila][columnaActual - 1] = zombie;
                        zombie.setColumna(columnaActual - 1);
                    }
                }
            }
        }
    }

    /**
     * Obtiene la entidad en una posición específica del tablero.
     * @param fila La fila de la posición.
     * @param columna La columna de la posición.
     * @return La entidad en la posición especificada, o null si no hay ninguna o la posición es inválida.
     */
    public Entidad getEntidadEn(int fila, int columna) {
        if (esPosicionValida(fila, columna)) {
            return tablero[fila][columna];
        }
        return null;
    }

    /**
     * Verifica si una posición dada es válida dentro del tablero.
     * @param fila La fila a verificar.
     * @param columna La columna a verificar.
     * @return true si la posición es válida, false en caso contrario.
     */
    private boolean esPosicionValida(int fila, int columna) {
        return fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS;
    }

    /**
     * Genera una representación en cadena del estado actual del tablero.
     * @return Una cadena que representa el estado del tablero.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (tablero[i][j] == null) {
                    sb.append("[ ]");
                } else if (tablero[i][j] instanceof Planta) {
                    sb.append("[P]");
                } else if (tablero[i][j] instanceof Zombie) {
                    sb.append("[Z]");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Activa la podadora en una fila específica, eliminando todos los zombies en esa fila.
     * @param fila La fila en la que se activará la podadora.
     */
    public void activarPodadora(int fila) {
        podadoras[fila] = false;
        for (int columna = 0; columna < COLUMNAS; columna++) {
            if (tablero[fila][columna] instanceof Zombie) {
                tablero[fila][columna] = null;
            }
        }
    }

    /**
     * Verifica si hay una podadora disponible en una fila específica.
     * @param fila La fila a verificar.
     * @return true si hay una podadora disponible en la fila, false en caso contrario.
     */
    public boolean tienePodadora(int fila) {
        return podadoras[fila];
    }

    /**
     * Obtiene la instancia única de la clase GameMvsM (patrón Singleton).
     * @return La instancia única de GamePvsM.
     */
    public static GameMvsM getInstance() {
        return instance;
    }

    /**
     * Establece el estado de finalización del juego.
     * @param gameOver true si el juego ha terminado, false en caso contrario.
     */
    public void setGameOver(boolean gameOver) {
        this.juegoTerminado = gameOver;
    }

    /**
     * Verifica si el juego ha terminado.
     * @return true si el juego ha terminado, false en caso contrario.
     */
    public boolean isGameOver() {
        return juegoTerminado;
    }

}
package domain;

public class GamePvsP{

    private static final int FILAS = 5;
    private static final int COLUMNAS = 10;
    private static GamePvsP instance;

    private Entidad[][] tablero;
    private boolean[] podadoras;

    private int contadorTiempo = 0;
    private int contadorCerebros;
    private boolean juegoTerminado;


    public GamePvsP() {
        tablero = new Entidad[FILAS][COLUMNAS];
        contadorCerebros = 50; // Cantidad inicial de cerebros


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
        if(contadorTiempo % 10 == 0) {
            contadorCerebros += 50;
        }

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
        for (int fila = 0; fila < FILAS; fila++) {
            moverZombie(fila);
        }
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
     * Incrementa la cantidad de cerebros del jugador.
     * @param cantidad La cantidad de cerebros a incrementar.
     */
    public void incrementarCerebros(int cantidad) {
        contadorCerebros += cantidad;
    }

    /**
     * Planta una nueva planta en el tablero si hay recursos suficientes y la posición está vacía.
     * @param fila La fila donde se plantará la planta.
     * @param columna La columna donde se plantará la planta.
     * @param tipoPlanta El tipo de planta a plantar.
     */
    public void plantarPlanta(int fila, int columna, String tipoPlanta) {
        if (getEntidadEn(fila, columna) == null) {
            tablero[fila][columna] = crearEntidad(tipoPlanta);
        }
    }

    /**
     * Quita una planta del tablero en la posición especificada.
     * @param fila La fila de la planta a quitar.
     * @param columna La columna de la planta a quitar.
     */
    public void quitarPlanta(int fila, int columna) {
        if (esPosicionValida(fila, columna)) {
            tablero[fila][columna] = null;
        }
    }

    /**
     * Crea un nuevo zombie en la posición especificada del tablero.
     * @param fila La fila donde se creará el zombie.
     * @param columna La columna donde se creará el zombie.
     * @param tipoZombie El tipo de zombie a crear.
     */
    public void crearZombie(int fila, int columna, String tipoZombie) {
        if (getEntidadEn(fila, columna) == null && hayRecursosSuficientes(tipoZombie)) {
            int costoZombie = getCosto(tipoZombie);
            tablero[fila][columna] = crearEntidad(tipoZombie);
            contadorCerebros -= costoZombie;
        }
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
            case "Brainstein":
                return new Brainstein(this);
            case "ECIZombie":
                return new ECIZombi();
            default:
                throw new IllegalArgumentException("Tipo de entidad no válido: " + tipoEntidad);
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

    public void eliminarZombie(int fila, int columna) {
        if (tablero[fila][columna] instanceof Zombie) {
            tablero[fila][columna] = null;
        }
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
     * Obtiene la cantidad actual de cerebros del jugador.
     * @return El número actual de cerebros.
     */
    public int getContadorCerebros(){
        return contadorCerebros;
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
     * Verifica si hay suficientes recursos (cerebros) para colocar un tipo específico de entidad
     *
     * @param tipoEntidad El tipo de entidad que se desea colocar en el tablero, representado como una cadena.
     * @return true si hay suficientes recursos para colocar el tipo de entidad especificado, false en caso contrario.
     */
    public boolean hayRecursosSuficientes(String tipoEntidad) {
        return contadorCerebros >= getCosto(tipoEntidad);
    }

    /**
     * Obtiene el costo de una entidad específica.
     * @param tipoEntidad El tipo de entidad cuyo costo se quiere conocer.
     * @return El costo de la entidad especificada.
     * @throws IllegalArgumentException si el tipo de entidad no es reconocido.
     */
    private int getCosto(String tipoEntidad) {
        switch (tipoEntidad) {
            case "ZombieNormal":
                return 100;
            case "ZombieCono":
                return 150;
            case "ZombieBalde":
                return 200;
            case "Brainstein":
                return 50;
            case "ECIZombie":
                return 250;
            default:
                throw new IllegalArgumentException("Tipo no reconocido");
        }
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

    public void limpiarFila(int fila) {
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
     * Obtiene la instancia única de la clase GamePvsM (patrón Singleton).
     * @return La instancia única de GamePvsM.
     */
    public static GamePvsP getInstance() {
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
package domain;

import java.io.*;
import java.util.*;

/**
 * Clase que representa el juego Plantas vs Zombies.
 * Maneja la lógica del juego, incluyendo el tablero, las entidades, y las mecánicas del juego.
 * Implementa la interfaz Runnable para permitir la ejecución del juego en un hilo separado.
 */
public class GamePvsM implements Serializable{

    private static final int FILAS = 5;
    private static final int COLUMNAS = 10;
    private static GamePvsM instance;

    private Entidad[][] tablero;
    private List<List<Zombie>> zombiesPorFila;
    private boolean[] podadoras;

    private int contadorTiempo = 0;
    private int contadorSoles;
    private boolean juegoTerminado;


    /**
     * Constructor de la clase GamePvsM.
     * Inicializa el tablero, las listas de zombies, las podadoras y establece la cantidad inicial de soles.
     */
    public GamePvsM() {
        tablero = new Entidad[FILAS][COLUMNAS];
        zombiesPorFila = new ArrayList<>(FILAS);
        contadorSoles = 500; // Cantidad inicial de soles

        for (int i = 0; i < FILAS; i++) {
            zombiesPorFila.add(new ArrayList<>());
        }

        podadoras = new boolean[FILAS];
        for (int i = 0; i < FILAS; i++) {
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
        if (contadorTiempo % 10 == 0) {
            contadorSoles += 25;
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
    }

    /**
     * Maneja el estado de las minas en el tablero.
     * Verifica las interacciones entre zombies y minas, activando explosiones o ataques según corresponda.
     */
    private void estadoMinas() {
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
     * Incrementa la cantidad de soles del jugador.
     *
     * @param cantidad La cantidad de soles a incrementar.
     */
    public void incrementarSoles(int cantidad) {
        contadorSoles += cantidad;
    }


    /**
     * Planta una nueva planta en el tablero si hay recursos suficientes y la posición está vacía.
     *
     * @param fila       La fila donde se plantará la planta.
     * @param columna    La columna donde se plantará la planta.
     * @param tipoPlanta El tipo de planta a plantar.
     */
    public void plantarPlanta(int fila, int columna, String tipoPlanta) {
        if (getEntidadEn(fila, columna) == null && hayRecursosSuficientes(tipoPlanta)) {
            int costoPlanta = getCosto(tipoPlanta);
            tablero[fila][columna] = crearEntidad(tipoPlanta);
            contadorSoles -= costoPlanta;
        }
    }

    /**
     * Quita una planta del tablero en la posición especificada.
     *
     * @param fila    La fila de la planta a quitar.
     * @param columna La columna de la planta a quitar.
     */
    public void quitarPlanta(int fila, int columna) {
        if (esPosicionValida(fila, columna)) {
            tablero[fila][columna] = null;
        }
    }

    /**
     * Crea un nuevo zombie en la posición especificada del tablero.
     *
     * @param fila       La fila donde se creará el zombie.
     * @param columna    La columna donde se creará el zombie.
     * @param tipoZombie El tipo de zombie a crear.
     */
    public void crearZombie(int fila, int columna, String tipoZombie) {
        Zombie nuevoZombie = (Zombie) crearEntidad(tipoZombie);
        tablero[fila][columna] = nuevoZombie;
        zombiesPorFila.get(fila).add(nuevoZombie);
    }

    public void quitarZombie(int fila, int columna) {
        Zombie zombie = (Zombie) getEntidadEn(fila, columna);
        tablero[fila][columna] = null;
        zombiesPorFila.get(fila).remove(zombie);
    }

    /**
     * Verifica si hay una planta en la columna siguiente a la posición especificada.
     *
     * @param fila    La fila a verificar.
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
     * Verifica si hay un LanzaGuisantes en la posición especificada.
     * @param fila La fila a verificar.
     * @param columna La columna a verificar.
     * @return true si hay un LanzaGuisantes en la posición, false en caso contrario.
     */
    public boolean hayLanzaGuisantesEn(int fila, int columna) {
        return tablero[fila][columna] instanceof LanzaGuisantes;
    }

    private void atacarConLanzaGuisantes(int fila, int columna) {
        for (int col = columna + 1; col < COLUMNAS; col++) {
            if (tablero[fila][col] instanceof Zombie) {
                Zombie zombie = (Zombie) tablero[fila][col];
                zombie.recibirDaño(20);
                if (zombie.getVida() <= 0) {
                    System.out.println("Zombie en [" + fila + "," + col + "] ha sido eliminado.");
                    tablero[fila][col] = null;
                    zombiesPorFila.get(fila).remove(zombie);
                }
                break; // Solo ataca al primer zombie en la fila
            }
        }
    }

    /**
     * Verifica si hay una mina activada en la columna siguiente a la posición dada.
     *
     * @param fila    La fila a verificar.
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
     * @param fila    La fila de la posición.
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
     * Crea una nueva entidad del tipo especificado.
     * @param tipoEntidad El tipo de entidad a crear.
     * @return La nueva entidad creada.
     * @throws IllegalArgumentException si el tipo de entidad no es válido.
     */
    private Entidad crearEntidad(String tipoEntidad) {
        switch (tipoEntidad) {
            case "Girasol":
                return new Girasol(this);
            case "LanzaGuisantes":
                return new LanzaGuisantes();
            case "Nuez":
                return new Nuez();
            case "Mina":
                return new Mina();
            case "ECIPlant":
                return new ECIPlant(this);
            case "ZombieNormal":
                return new ZombieNormal();
            case "ZombieCono":
                return new ZombieCono();
            case "ZombieBalde":
                return new ZombieBalde();
            case "Evolve":
                return new Evolve();
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
        List<Zombie> zombiesFila = zombiesPorFila.get(fila);
        for (int i = 0; i < zombiesFila.size(); i++) {
            Zombie zombie = zombiesFila.get(i);
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

    /**
     * Obtiene la cantidad actual de soles del jugador.
     * @return El número actual de soles.
     */
    public int getContadorSoles(){
        return contadorSoles;
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
     * Verifica si hay suficientes recursos (soles o cerebros) para colocar un tipo específico de entidad
     *
     * @param tipoEntidad El tipo de entidad que se desea colocar en el tablero, representado como una cadena.
     * @return true si hay suficientes recursos para colocar el tipo de entidad especificado, false en caso contrario.
     */
    public boolean hayRecursosSuficientes(String tipoEntidad) {
        return contadorSoles >= getCosto(tipoEntidad);
    }


    /**
     * Obtiene el costo de una entidad específica.
     * @param tipoEntidad El tipo de entidad cuyo costo se quiere conocer.
     * @return El costo de la entidad especificada.
     * @throws IllegalArgumentException si el tipo de entidad no es reconocido.
     */
    private int getCosto(String tipoEntidad) {
        switch (tipoEntidad) {
            case "Girasol":
                return 50;
            case "LanzaGuisantes":
                return 100;
            case "Nuez":
                return 50;
            case "Mina":
                return 25;
            case "ECIPlant":
                return 75;
            case "Evolve":
                return 200;
            default:
                throw new IllegalArgumentException("Tipo no reconocido");
        }
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
        zombiesPorFila.get(fila).clear();
    }

    /**
     * Verifica si hay una podadora disponible en una fila específica.
     * @param fila La fila a verificar.
     * @return true si hay una podadora disponible en la fila, false en caso contrario.
     */
    public boolean tienePodadora(int fila) {
        return podadoras[fila];
    }


//    }

    public void importar(File file) throws Exception {
        limpiarTablero(); // Limpia el tablero antes de cargar
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] partes = line.split(" ");
                if (partes.length != 3) {
                    throw new IOException("Formato incorrecto en la línea: " + line);
                }

                String tipo = partes[0];
                int fila = Integer.parseInt(partes[1]);
                int columna = Integer.parseInt(partes[2]);

                // Crear y colocar la entidad en el tablero
                Entidad entidad = null;
                switch (tipo) {
                    case "Girasol":
                        entidad = new Girasol(this);
                        break;
                    case "LanzaGuisantes":
                        entidad = new LanzaGuisantes();
                        break;
                    case "Nuez":
                        entidad = new Nuez();
                        break;
                    case "Mina":
                        entidad = new Mina();
                        break;
                    case "ECIPlant":
                        entidad = new ECIPlant(this);
                        break;
                    case "Evolve":
                        entidad = new Evolve();
                        break;
                    case "ZombieNormal":
                        entidad = new ZombieNormal();
                        break;
                    case "ZombieCono":
                        entidad = new ZombieCono();
                        break;
                    case "ZombieBalde":
                        entidad = new ZombieBalde();
                        break;
                    default:
                        throw new IllegalArgumentException("Tipo de entidad desconocido: " + tipo);
                }

                agregarEntidad(entidad, fila, columna);
            }
        }
    }


    public void exportar(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            for (int fila = 0; fila < FILAS; fila++) {
                for (int columna = 0; columna < COLUMNAS; columna++) {
                    Entidad entidad = getEntidadEn(fila, columna);
                    if (entidad != null) {
                        String tipo = entidad.getClass().getSimpleName(); // Obtiene el tipo exacto
                        writer.write(tipo + " " + fila + " " + columna + "\n");
                    }
                }
            }
        }
    }

    public void removerEntidad(int fila, int columna) {
        if (esPosicionValida(fila, columna)) {
            tablero[fila][columna] = null;
        }
    }

    public void agregarEntidad(Entidad entidad, int fila, int columna) {
        if (esPosicionValida(fila, columna)) {
            tablero[fila][columna] = entidad;
        }
    }

    public  void limpiarTablero() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                removerEntidad(fila, columna);
            }
        }
    }

    /**
     * Obtiene la instancia única de la clase GamePvsM (patrón Singleton).
     * @return La instancia única de GamePvsM.
     */
    public static GamePvsM getInstance() {
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
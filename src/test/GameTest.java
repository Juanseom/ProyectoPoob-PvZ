package test;

import domain.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 *Clase que maneja las pruebas unitarias del dominio
 */
public class GameTest {

    private GamePvsM gamePvsM;
    private GameMvsM gameMvsM;
    private GamePvsP gamePvsP;

    @Before
    public void setUp() {
        gamePvsM = new GamePvsM();
        gameMvsM = new GameMvsM();
        gamePvsP = new GamePvsP();
    }

    @Test
    public void testPlantarPlantaPvsM() {
        gamePvsM.plantarPlanta(2, 3, "Girasol");
        Entidad entidad = gamePvsM.getEntidadEn(2, 3);
        assertTrue(entidad instanceof Planta);
        assertEquals("Girasol", entidad.getClass().getSimpleName());
    }

    @Test
    public void testQuitarPlantaPvsM() {
        gamePvsM.plantarPlanta(2, 3, "Girasol");
        gamePvsM.quitarPlanta(2, 3);
        assertNull(gamePvsM.getEntidadEn(2, 3));
    }

    @Test
    public void testHayRecursosSuficientesPvsM() {
        assertTrue(gamePvsM.hayRecursosSuficientes("Girasol"));
        for (int i = 0; i < 10; i++) {
            gamePvsM.plantarPlanta(0, i, "Girasol");
        }
        assertFalse(gamePvsM.hayRecursosSuficientes("Girasol"));
    }

    @Test
    public void testCrearZombiePvsM() {
        gamePvsM.crearZombie(4, 9, "ZombieNormal");
        Entidad entidad = gamePvsM.getEntidadEn(4, 9);
        assertTrue(entidad instanceof Zombie);
    }

    @Test
    public void testNoMoverZombiePvsM() {
        // Arrange
        gamePvsM.crearZombie(2, 0, "ZombieNormal");
        Zombie zombie = (Zombie) gamePvsM.getEntidadEn(2, 0);

        // Act
        gamePvsM.moverZombie(2);

        // Assert
        assertEquals("El zombie debería permanecer en la columna 0", 0, zombie.getColumna());
        assertSame("El zombie en (2,0) debería ser el mismo objeto que creamos", zombie, gamePvsM.getEntidadEn(2, 0));
        assertTrue("Debería haber un zombie en (2,0)", gamePvsM.getEntidadEn(2, 0) instanceof Zombie);

        // Verificar que el zombie no se ha movido a otra posición
        for (int col = 1; col < 10; col++) {
            assertNull("No debería haber un zombie en la columna " + col, gamePvsM.getEntidadEn(2, col));
        }
    }

    @Test
    public void testActivarPodadoraPvsM() {
        gamePvsM.crearZombie(3, 0, "ZombieNormal");
        gamePvsM.activarPodadora(3);
        assertNull(gamePvsM.getEntidadEn(3, 0));
        assertFalse(gamePvsM.tienePodadora(3));
    }

    @Test
    public void testIncrementarSolesPvsM() {
        int solesPrevios = gamePvsM.getContadorSoles();
        gamePvsM.incrementarSoles(50);
        assertEquals(solesPrevios + 50, gamePvsM.getContadorSoles());
    }

    @Test
    public void testGameOverPvsM() {
        assertFalse(gamePvsM.isGameOver());
        gamePvsM.setGameOver(true);
        assertTrue(gamePvsM.isGameOver());
    }

    @Test
    public void testActualizarEstadoJuegoPvsM() {
        int solesPrevios = gamePvsM.getContadorSoles();
        gamePvsM.actualizarEstadoJuego();
        assertTrue("El contador de soles debería aumentar después de actualizar el estado del juego",
                gamePvsM.getContadorSoles() > solesPrevios);
    }

    @Test
    public void testPlantarPlantaSinRecursosSuficientesPvsM() {
        // Asegurarse de que no hay suficientes soles
        gamePvsM.incrementarSoles(-gamePvsM.getContadorSoles());
        gamePvsM.incrementarSoles(50); // Asegurar que hay exactamente 50 soles

        gamePvsM.plantarPlanta(0, 0, "LanzaGuisantes");
        assertNull("No debería haberse plantado una planta sin recursos suficientes",
                gamePvsM.getEntidadEn(0, 0));
    }


    // Test: Plantar planta con recursos suficientes
    @Test
    public void testPlantarPlantaConRecursosPvsM() {
        gamePvsM.plantarPlanta(2, 3, "Girasol");
        assertNotNull(gamePvsM.getEntidadEn(2, 3));
        assertTrue(gamePvsM.getEntidadEn(2, 3) instanceof Girasol);
    }

    // Test: Exportar e importar partida
    @Test
    public void testExportarEImportarPartidaPvsM() throws Exception {
        // Preparar estado inicial
        gamePvsM.plantarPlanta(2, 3, "Girasol");
        gamePvsM.crearZombie(1, 9, "ZombieNormal");

        // Exportar
        File file = new File("testGameState.txt");
        gamePvsM.exportar(file);

        // Limpiar el juego y cargar el estado desde el archivo
        gamePvsM.limpiarTablero();
        gamePvsM.importar(file);

        // Verificar que el estado se haya restaurado correctamente
        assertTrue(gamePvsM.getEntidadEn(2, 3) instanceof Girasol);
        assertTrue(gamePvsM.getEntidadEn(1, 9) instanceof ZombieNormal);

        // Eliminar el archivo después de la prueba
        file.delete();
    }

    @Test
    public void testZombieSeDetieneConPlantaPvsM() {
        gamePvsM.plantarPlanta(2, 5, "Nuez");
        gamePvsM.crearZombie(2, 7, "ZombieNormal");

        gamePvsM.moverZombie(2);
        gamePvsM.moverZombie(2);
        assertNotNull(gamePvsM.getEntidadEn(2, 5)); // La planta sigue en su lugar
        assertNotNull(gamePvsM.getEntidadEn(2, 7)); // Zombie se mantiene en su posición original
    }

    @Test
    public void testGeneracionAutomaticaDeZombiesMvsM() {
        assertTrue(gameMvsM.sePuedeGenerarZombie(2, 9)); // Última columna libre
        gameMvsM.crearEntidad(2, 9, "ZombieNormal");
        assertNotNull(gameMvsM.getEntidadEn(2, 9));
    }

//    @Test
//    public void testFinDelJuegoMvsM() {
//        gameMvsM.crearEntidad(0, 1, "ZombieNormal");
//        gameMvsM.actualizarEstadoJuego();
//        // Asegurarse de que no hay podadora en la fila 0
//        gameMvsM.activarPodadora(0);
//        gameMvsM.moverZombie(0);
//        gameMvsM.actualizarEstadoJuego();
//
//        gameMvsM.moverZombie(0);
//        gameMvsM.actualizarEstadoJuego();
//
//        assertTrue("El juego debería terminar cuando un zombie llega a la primera columna", gameMvsM.isGameOver());
//    }

    @Test
    public void testCrearZombiePvP() {
        gamePvsP.incrementarCerebros(100); // Assuming 100 is enough to create a zombie

        gamePvsP.crearZombie(1, 9, "ZombieNormal");

        Entidad entidad = gamePvsP.getEntidadEn(1, 9);
        assertNotNull("Debería haberse creado un zombie en (1,9)", entidad);
        assertTrue("La entidad creada debería ser un ZombieNormal", entidad instanceof ZombieNormal);
    }

//    @Test
//    public void testMovimientoZombiePvP() {
//        gamePvsP.crearZombie(0, 1, "ZombieNormal");
//        gamePvsP.moverZombie(0);
//
//        assertTrue(gamePvsP.hayZombieEnCelda(0, 1));
//        assertFalse(gamePvsP.hayZombieEnCelda(0, 0));
//    }

    @Test
    public void testCrearZombieSinRecursosPvP() {
        gamePvsP.incrementarCerebros(-50); // Dejar los cerebros en 0
        gamePvsP.crearZombie(0, 9, "ZombieNormal");
        assertNull(gamePvsP.getEntidadEn(0, 9)); // Zombie no debería haberse creado
    }

    @Test
    public void testCrearEntidadMvsM() {
        gameMvsM.crearEntidad(0, 0, "LanzaGuisantes");
        assertTrue(gameMvsM.hayPlantaEnCelda(0, 0));
    }

    @Test
    public void testQuitarEntidadMvsM() {
        gameMvsM.crearEntidad(0, 0, "LanzaGuisantes");
        gameMvsM.quitarEntidad(0, 0);
        assertFalse(gameMvsM.hayPlantaEnCelda(0, 0));
    }

    @Test
    public void testHayPlantaEnSiguienteColumnaMvsM() {
        gameMvsM.crearEntidad(0, 1, "LanzaGuisantes");
        assertTrue(gameMvsM.hayPlantaEnSiguienteColumna(0, 1));
    }

//    @Test
//    public void testHayMinaActivadaEnSiguienteColumnaMvsM() {
//        gameMvsM.crearEntidad(0, 1, "Mina");
//        // Asumiendo que las minas se activan automáticamente o hay un método para activarlas
//        assertTrue(gameMvsM.hayMinaActivadaEnSiguienteColumna(0, 2));
//    }

    @Test
    public void testEliminarZombieYMinaMvsM() {
        gameMvsM.crearEntidad(0, 0, "Mina");
        gameMvsM.crearEntidad(0, 1, "ZombieNormal");
        gameMvsM.eliminarZombieYMina(0, 0);
        assertFalse(gameMvsM.hayPlantaEnCelda(0, 0));
        assertFalse(gameMvsM.hayZombieEnCelda(0, 1));
    }

    @Test
    public void testHayZombieEnCeldaMvsM() {
        gameMvsM.crearEntidad(0, 0, "ZombieNormal");
        assertTrue(gameMvsM.hayZombieEnCelda(0, 0));
    }

    @Test
    public void testSePuedeGenerarZombieMvsM() {
        assertTrue(gameMvsM.sePuedeGenerarZombie(0, 9)); // Última columna
        gameMvsM.crearEntidad(0, 9, "ZombieNormal");
        assertFalse(gameMvsM.sePuedeGenerarZombie(0, 9)); // Ya ocupada
    }

    @Test
    public void testMoverZombieMvsM() {
        gameMvsM.crearEntidad(0, 1, "ZombieNormal");
        gameMvsM.moverZombie(0);

        assertTrue(gameMvsM.hayZombieEnCelda(0, 1));
        assertFalse(gameMvsM.hayZombieEnCelda(0, 0));
    }

//    @Test
//    public void testEstadoMinasMvsM() {
//        gameMvsM.crearEntidad(0, 1, "Mina");
//        gameMvsM.crearEntidad(0, 2, "ZombieNormal");
//        gameMvsM.actualizarEstadoJuego();
//        // Asumiendo que la mina explota y elimina al zombie
//        assertFalse(gameMvsM.hayPlantaEnCelda(0, 1));
//        assertFalse(gameMvsM.hayZombieEnCelda(0, 2));
//    }

    @Test
    public void testRecibirDañoReduceVidaPeroMantieneActivo() {
        ZombieNormal zombie = new ZombieNormal();
        int vidaInicial = zombie.getVida();
        int daño = vidaInicial / 2;

        zombie.recibirDaño(daño);

        assertEquals(vidaInicial - daño, zombie.getVida());
        assertTrue(zombie.estaActivo());
    }

    @Test
    public void testRecibirDañoIgualAVidaDesactivaPlanta() {
        Planta planta = new Nuez();
        int vidaInicial = planta.getVida();

        planta.recibirDaño(vidaInicial);

        assertEquals(0, planta.getVida());
        assertFalse(planta.estaActiva());
    }
}
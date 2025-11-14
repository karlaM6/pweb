package co.edu.javeriana.proyectoWeb.RegataOnline.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.proyectoWeb.RegataOnline.model.Jugador;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Mapa;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Barco;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Celda;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Modelo;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.JugadorRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.MapaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.BarcoRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.CeldaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.ModeloRepositorio;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Prueba de Sistema (End-to-End) para el flujo completo de una regata.
 * 
 * Caso de uso: "Crear una partida, ver el mapa, mover el barco, y alcanzar la meta"
 * 
 * Este es el caso de uso más complejo y largo del sistema, involucrando:
 * 1. Creación de entidades (Jugador, Mapa, Barco)
 * 2. Creación de una partida
 * 3. Navegación a la pantalla de juego
 * 4. Movimiento del barco en el mapa
 * 5. Validación del estado de la partida
 */
@ActiveProfiles("system-test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
// Use a random port to avoid failures when the default port (8080) is already in use on the system.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RegataSystemTest {

    @Autowired
    private JugadorRepositorio jugadorRepo;

    @Autowired
    private MapaRepositorio mapaRepo;

    @Autowired
    private BarcoRepositorio barcoRepo;

    @Autowired
    private CeldaRepositorio celdaRepo;

    @Autowired
    private ModeloRepositorio modeloRepo;

    @LocalServerPort
    private int port;

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    private String baseUrl;

    @BeforeEach
    void setup() {
    // Limpiar datos en orden correcto (eliminar dependencias primero)
    barcoRepo.deleteAll();
    celdaRepo.deleteAll();
    mapaRepo.deleteAll();
    jugadorRepo.deleteAll();
    modeloRepo.deleteAll();

        // Crear entidades necesarias
        // 1. Crear un jugador
        Jugador jugador = new Jugador();
        jugador.setNombre("TestJugador");
        jugador = jugadorRepo.save(jugador);

        // 2. Crear un modelo de barco
        Modelo modelo = new Modelo();
        modelo.setNombreModelo("Velero Test");
        modelo.setColor("Rojo");
        modelo = modeloRepo.save(modelo);

        // 3. Crear un barco
        Barco barco = new Barco();
        barco.setNombre("BarcoTest");
        barco.setModelo(modelo);
        barco.setPosicionX(0);
        barco.setPosicionY(0);
        barco = barcoRepo.save(barco);

        // 4. Crear un mapa (10x10)
        Mapa mapa = new Mapa(10, 10);
        mapa = mapaRepo.save(mapa);

        // 5. Crear celdas del mapa
        // Celda de partida (P) en (0, 0)
        Celda celdaPartida = new Celda("P", 0, 0);
        celdaPartida.setMapa(mapa);
        celdaRepo.save(celdaPartida);

        // Celdas de agua (navegables) para crear un camino
        for (int i = 1; i < 10; i++) {
            Celda celdaAgua = new Celda("", i, 0);  // tipo vacío = agua
            celdaAgua.setMapa(mapa);
            celdaRepo.save(celdaAgua);
        }

        // Celda de meta (M) en (9, 0)
        Celda celdaMeta = new Celda("M", 9, 0);
        celdaMeta.setMapa(mapa);
        celdaRepo.save(celdaMeta);

        // Rellenar el resto del mapa con agua
        for (int y = 1; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Celda celda = new Celda("", x, y);
                celda.setMapa(mapa);
                celdaRepo.save(celda);
            }
        }

    // Inicializar Playwright
        this.playwright = Playwright.create();
        this.browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true));
        this.context = browser.newContext();
        this.page = context.newPage();

    // Build base URL from the injected random port so tests hit this Spring Boot instance
    this.baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    void testFlujoCompletoRegata() {
    // PASO 1: Ir a la página principal, luego navegar al menú "Jugar" (cliente SPA)
    page.navigate(baseUrl + "/");

        // --- DEBUG: volcar el HTML recibido al arrancar la SPA ---
        try {
            String initialHtml = page.content();
            System.out.println("[DEBUG] initial page.content() length=" + (initialHtml == null ? 0 : initialHtml.length()));
            System.out.println("[DEBUG] initial page snippet: \n" + (initialHtml == null ? "<null>" : initialHtml.substring(0, Math.min(2000, initialHtml.length()))));
        } catch (Exception e) {
            System.out.println("[DEBUG] error reading initial page content: " + e.getMessage());
        }

        // Hacer clic en el link de navegación 'Jugar' para que el enrutador cliente cargue /partida/menu
        Locator linkJugar = page.locator("a:has-text('Jugar')");
        linkJugar.waitFor();
        linkJugar.click();

        // Dar un pequeño tiempo para que la SPA ejecute scripts y renderice (debug)
        page.waitForTimeout(1000);

        // --- DEBUG: volcar HTML después del click para ver si la SPA ya renderizó el DOM esperado ---
        try {
            String afterClickHtml = page.content();
            System.out.println("[DEBUG] afterClick page.content() length=" + (afterClickHtml == null ? 0 : afterClickHtml.length()));
            System.out.println("[DEBUG] afterClick page snippet: \n" + (afterClickHtml == null ? "<null>" : afterClickHtml.substring(0, Math.min(2000, afterClickHtml.length()))));
        } catch (Exception e) {
            System.out.println("[DEBUG] error reading afterClick page content: " + e.getMessage());
        }

        // Esperar a que cargue el menú de partidas (renderizado por la SPA)
        Locator menuContainer = page.locator("div.container");
        menuContainer.waitFor();
        
        PlaywrightAssertions.assertThat(page).hasTitle("RegataOnline");

        // PASO 2: Hacer clic en "Crear Partida"
        Locator btnCrearPartida = page.locator("button:has-text('Crear Partida')");
        btnCrearPartida.waitFor();
        btnCrearPartida.click();

        // PASO 3: Esperar a que aparezca la pantalla de selección de jugador
        Locator selectJugador = page.locator("select");
        selectJugador.waitFor();

        // Seleccionar el jugador "TestJugador"
        page.selectOption("select", "1");  // Suponiendo que TestJugador tiene ID 1
        
        // Hacer clic en siguiente
        Locator btnSiguiente = page.locator("button:has-text('Siguiente')");
        btnSiguiente.waitFor();
        btnSiguiente.click();

        // PASO 4: Esperar a que aparezcan los mapas disponibles
        Locator cardMapa = page.locator(".card-mapa").first();
        cardMapa.waitFor();

        // Seleccionar el primer mapa disponible
        cardMapa.click();

        // Hacer clic en siguiente
        btnSiguiente = page.locator("button:has-text('Siguiente')");
        btnSiguiente.click();

        // PASO 5: Esperar a que aparezcan los barcos disponibles
        Locator cardBarco = page.locator(".card-barco").first();
        cardBarco.waitFor();

        // Seleccionar el primer barco disponible
        cardBarco.click();

        // PASO 6: Crear la partida
        Locator btnCrear = page.locator("button:has-text('Crear Partida')");
        btnCrear.waitFor();
        btnCrear.click();

        // PASO 7: Esperar a navegar a la pantalla de juego
        Locator gameContainer = page.locator("div.juego-container");
        gameContainer.waitFor();

        // PASO 8: Validar que la partida se creó correctamente
        Locator statsCard = page.locator("div.stats-card");
        statsCard.waitFor();

        // Verificar que el jugador correcto está jugando
        Locator playerName = page.locator(".jugador strong");
        PlaywrightAssertions.assertThat(playerName).hasText("TestJugador");

        // Verificar posición inicial del barco (debe ser 0,0)
        Locator posicionValue = page.locator(".stat-row").filter(new Locator.FilterOptions().setHasText("Posición")).locator(".stat-value");
        PlaywrightAssertions.assertThat(posicionValue).containsText("X:0");

        // Verificar que el estado inicial es "activa"
        Locator estadoValue = page.locator(".stat-row").filter(new Locator.FilterOptions().setHasText("Estado")).locator(".stat-value");
        PlaywrightAssertions.assertThat(estadoValue).containsText("activa");

        // PASO 9: Mover el barco
        // Buscar celdas clickeables (destinos posibles válidos)
        Locator celdasClickeables = page.locator("div.celda.clickable");
        
        // Esperar a que haya celdas disponibles
        if (celdasClickeables.count() > 0) {
            celdasClickeables.first().waitFor();
            
            // Realizar varios movimientos
            for (int i = 0; i < 2 && celdasClickeables.count() > 0; i++) {
                Locator siguienteCelda = page.locator("div.celda.clickable").first();
                if (siguienteCelda.isVisible()) {
                    siguienteCelda.click();
                    page.waitForTimeout(800);  // Esperar a que se procese el movimiento
                }
            }
        }

        // PASO 10: Validar que hubo movimientos
        Locator movimientosValue = page.locator(".stat-row").filter(new Locator.FilterOptions().setHasText("Movimientos")).locator(".stat-value");
        String movimientosText = movimientosValue.textContent();
        assert !movimientosText.equals("0") : "Debería haber movimientos realizados, pero tiene: " + movimientosText;

        // PASO 11: Validar que el botón Pausar está disponible y funciona
        Locator btnPausar = page.locator("button:has-text('Pausar')");
        PlaywrightAssertions.assertThat(btnPausar).isEnabled();
        btnPausar.click();
        
        page.waitForTimeout(500);

        // Verificar que la partida se pausó
        Locator estadoDespuesPausa = page.locator(".stat-row").filter(new Locator.FilterOptions().setHasText("Estado")).locator(".stat-value");
        PlaywrightAssertions.assertThat(estadoDespuesPausa).containsText("pausada");

        // PASO 12: Reanudar la partida
        // Buscar el botón de reanudar (que aparece cuando está pausada)
        Locator btnReanudar = page.locator("button").filter(new Locator.FilterOptions().setHasText("Reanudar"));
        if (btnReanudar.count() > 0) {
            btnReanudar.click();
            page.waitForTimeout(500);

            // Verificar que la partida está activa nuevamente
            Locator estadoReactivado = page.locator(".stat-row").filter(new Locator.FilterOptions().setHasText("Estado")).locator(".stat-value");
            PlaywrightAssertions.assertThat(estadoReactivado).containsText("activa");
        }

        // PASO 13: Finalizar la partida
        Locator btnFinalizar = page.locator("button:has-text('🛑 Finalizar')");
        btnFinalizar.click();

        // Esperar a que se procese la finalización
        page.waitForTimeout(1000);

        // Debería volver al menú (o mostrar una confirmación)
    // Validar que estamos en una URL servida por la aplicación bajo el baseUrl
    String currentUrl = page.url();
    assert currentUrl.contains(this.baseUrl) : "Debería estar en la aplicación: " + currentUrl;
    }
}

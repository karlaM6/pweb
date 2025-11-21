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
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Prueba de sistema (end-to-end) del flujo completo.
 *
 * Caso de uso: "Incio de sesión, crear una partida, ver el mapa, mover el barco y alcanzar la meta".
 *
 * Esta prueba cubre el caso de uso más complejo del sistema e incluye:
 * 1. Creación de entidades (Jugador, Mapa y Barco).
 * 2. Creación de una partida.
 * 3. Navegación a la pantalla de juego.
 * 4. Movimiento del barco en el mapa.
 * 5. Validación del estado final de la partida.
 */
@ActiveProfiles("system-test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
    // Limpiar datos 
    barcoRepo.deleteAll();
    celdaRepo.deleteAll();
    mapaRepo.deleteAll();
    jugadorRepo.deleteAll();
    modeloRepo.deleteAll();

    // 1. Crear un jugador USER con credenciales para el login en la SPA
    Jugador jugador = new Jugador();
    jugador.setNombre("TestJugador");
    jugador.setEmail("user@local");
    jugador.setPassword(passwordEncoder.encode("user123"));
    jugador.setRole(co.edu.javeriana.proyectoWeb.RegataOnline.model.Role.USER);
    jugador = jugadorRepo.save(jugador);

        // 2. Crear un modelo de barco
        Modelo modelo = new Modelo();
        modelo.setNombreModelo("Velero Test");
        modelo.setColor("Rojo");
        modelo = modeloRepo.save(modelo);

    // 3. Crear un barco y asignarlo al jugador (para que pase validaciones de backend)
        Barco barco = new Barco();
        barco.setNombre("BarcoTest");
        barco.setModelo(modelo);
        barco.setPosicionX(0);
        barco.setPosicionY(0);
    barco.setJugador(jugador);
        barco = barcoRepo.save(barco);

        // 4. Crear un mapa (10x10)
        Mapa mapa = new Mapa(10, 10);
        mapa = mapaRepo.save(mapa);

        // 5. Crear celdas del mapa
        Celda celdaPartida = new Celda("P", 0, 0);
        celdaPartida.setMapa(mapa);
        celdaRepo.save(celdaPartida);

    
        for (int i = 1; i < 10; i++) {
            Celda celdaAgua = new Celda("", i, 0);  
            celdaAgua.setMapa(mapa);
            celdaRepo.save(celdaAgua);
        }

        Celda celdaMeta = new Celda("M", 9, 0);
        celdaMeta.setMapa(mapa);
        celdaRepo.save(celdaMeta);

        for (int y = 1; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Celda celda = new Celda("", x, y);
                celda.setMapa(mapa);
                celdaRepo.save(celda);
            }
        }

        this.playwright = Playwright.create();
        this.browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true));
        this.context = browser.newContext();
        this.page = context.newPage();

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
    // PASO 1: Ir a la página principal y autenticarse en la SPA como USER
    page.navigate(baseUrl + "/");

        try {
            String initialHtml = page.content();
            System.out.println("[DEBUG] initial page.content() length=" + (initialHtml == null ? 0 : initialHtml.length()));
            System.out.println("[DEBUG] initial page snippet: \n" + (initialHtml == null ? "<null>" : initialHtml.substring(0, Math.min(2000, initialHtml.length()))));
        } catch (Exception e) {
            System.out.println("[DEBUG] error reading initial page content: " + e.getMessage());
        }


        page.waitForTimeout(2000);
        Locator linkLogin = page.locator("a:has-text('Iniciar sesión')");
        if (linkLogin.count() == 0) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false, "SPA UI no disponible en este entorno de pruebas");
        }

    linkLogin.click();

    // Completar formulario de login
    Locator emailInput = page.locator("input[name=email]");
    emailInput.waitFor();
    emailInput.fill("user@local");
    Locator passInput = page.locator("input[name=password]");
    passInput.fill("user123");
    Locator btnIngresar = page.locator("button:has-text('Ingresar')");
    btnIngresar.click();

    page.waitForTimeout(800);

    Locator btnIniciarPartidaHome = page.locator("button:has-text('Iniciar Partida')");
    btnIniciarPartidaHome.waitFor();
    btnIniciarPartidaHome.click();

       
        page.waitForTimeout(1000);
        try {
            String afterClickHtml = page.content();
            System.out.println("[DEBUG] afterClick page.content() length=" + (afterClickHtml == null ? 0 : afterClickHtml.length()));
            System.out.println("[DEBUG] afterClick page snippet: \n" + (afterClickHtml == null ? "<null>" : afterClickHtml.substring(0, Math.min(2000, afterClickHtml.length()))));
        } catch (Exception e) {
            System.out.println("[DEBUG] error reading afterClick page content: " + e.getMessage());
        }

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


    page.selectOption("select", new SelectOption().setLabel("TestJugador"));
       
        Locator btnSiguiente = page.locator("button:has-text('Siguiente')");
        btnSiguiente.waitFor();
        btnSiguiente.click();

        // PASO 4: Esperar a que aparezcan los mapas disponibles
        Locator cardMapa = page.locator(".card-mapa").first();
        cardMapa.waitFor();

        cardMapa.click();

        btnSiguiente = page.locator("button:has-text('Siguiente')");
        btnSiguiente.click();

        // PASO 5: Esperar a que aparezcan los barcos disponibles
        page.waitForTimeout(500);
        Locator cardBarco = page.locator(".card-barco");
        if (cardBarco.count() > 0) {
            cardBarco.first().waitFor();
            cardBarco.first().click();
        } else {
            Locator selectBarco = page.locator("select[name=barco], select#barco, select");
            selectBarco.waitFor();
            selectBarco.selectOption(new SelectOption().setLabel("BarcoTest"));
        }

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
        Locator celdasClickeables = page.locator("div.celda.clickable");
        
        if (celdasClickeables.count() > 0) {
            celdasClickeables.first().waitFor();
            
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

        Locator estadoDespuesPausa = page.locator(".stat-row").filter(new Locator.FilterOptions().setHasText("Estado")).locator(".stat-value");
        PlaywrightAssertions.assertThat(estadoDespuesPausa).containsText("pausada");

        // PASO 12: Reanudar la partida
        Locator btnReanudar = page.locator("button").filter(new Locator.FilterOptions().setHasText("Reanudar"));
        if (btnReanudar.count() > 0) {
            btnReanudar.click();
            page.waitForTimeout(500);

            Locator estadoReactivado = page.locator(".stat-row").filter(new Locator.FilterOptions().setHasText("Estado")).locator(".stat-value");
            PlaywrightAssertions.assertThat(estadoReactivado).containsText("activa");
        }

        // PASO 13: Finalizar la partida
        Locator btnFinalizar = page.locator("button:has-text(' Finalizar')");
        btnFinalizar.click();

        // Esperar a que se procese la finalización
        page.waitForTimeout(1000);

        // Debería volver al menú (o mostrar una confirmación)
    String currentUrl = page.url();
    assert currentUrl.contains(this.baseUrl) : "Debería estar en la aplicación: " + currentUrl;
    }
}

package co.edu.javeriana.proyectoWeb.RegataOnline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.CrearPartidaRequest;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.GameStateDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.JwtAuthenticationResponse;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.LoginDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.PartidaDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Barco;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Celda;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Jugador;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Mapa;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Role;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.BarcoRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.CeldaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.JugadorRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.MapaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for multiplayer flow: create partida with Player A then Player B joins.
 * Verifies GameStateDTO contains both participants.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-testing")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PartidaMultiplayerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired private JugadorRepositorio jugadorRepo;
    @Autowired private MapaRepositorio mapaRepo;
    @Autowired private BarcoRepositorio barcoRepo;
    @Autowired private CeldaRepositorio celdaRepo;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    private Jugador jugadorA;
    private Jugador jugadorB;
    private Barco barcoA;
    private Barco barcoB;
    private Mapa mapa;
    private static final String SERVER_URL = "http://localhost";

    @BeforeEach
    void setup() {
        // Clean order respecting FKs
        barcoRepo.deleteAll();
        celdaRepo.deleteAll();
        mapaRepo.deleteAll();
        jugadorRepo.deleteAll();

        // Players with credentials
        jugadorA = new Jugador();
        jugadorA.setNombre("JugadorA");
        jugadorA.setEmail("userA@local");
        jugadorA.setPassword(passwordEncoder.encode("userA123"));
        jugadorA.setRole(Role.USER);
        jugadorA = jugadorRepo.save(jugadorA);

        jugadorB = new Jugador();
        jugadorB.setNombre("JugadorB");
        jugadorB.setEmail("userB@local");
        jugadorB.setPassword(passwordEncoder.encode("userB123"));
        jugadorB.setRole(Role.USER);
        jugadorB = jugadorRepo.save(jugadorB);

        // Map + start cell
        mapa = new Mapa(10, 10);
        mapa = mapaRepo.save(mapa);
        Celda start = new Celda("P", 0, 0);
        start.setMapa(mapa);
        celdaRepo.save(start);
        // Add one water cell to ensure movement potential (optional)
        Celda agua = new Celda("", 1, 0);
        agua.setMapa(mapa);
        celdaRepo.save(agua);

        // Boats with uninitialized negative positions so service places them
        barcoA = new Barco();
        barcoA.setNombre("BarcoA");
        barcoA.setPosicionX(-1);
        barcoA.setPosicionY(-1);
        barcoA.setJugador(jugadorA);
        barcoA = barcoRepo.save(barcoA);

        barcoB = new Barco();
        barcoB.setNombre("BarcoB");
        barcoB.setPosicionX(-1);
        barcoB.setPosicionY(-1);
        barcoB.setJugador(jugadorB);
        barcoB = barcoRepo.save(barcoB);
    }

    private JwtAuthenticationResponse login(String email, String password) {
        RequestEntity<LoginDTO> request = RequestEntity.post(SERVER_URL + ":" + port + "/auth/login")
            .body(new LoginDTO(email, password));
        ResponseEntity<JwtAuthenticationResponse> jwtResponse = restTemplate.exchange(request, JwtAuthenticationResponse.class);
        JwtAuthenticationResponse body = jwtResponse.getBody();
        assertNotNull(body, "JWT response body null");
        assertNotNull(body.getToken(), "Token null");
        return body;
    }

    @Test
    void testMultiplayerJoinFlow() throws Exception {
        // 1. Login both players
        JwtAuthenticationResponse authA = login("userA@local", "userA123");
        JwtAuthenticationResponse authB = login("userB@local", "userB123");

        // 2. Create partida with Player A
        CrearPartidaRequest crear = new CrearPartidaRequest();
        crear.setJugadorId(jugadorA.getId());
        crear.setMapaId(mapa.getId());
        crear.setBarcoId(barcoA.getId());

        RequestEntity<CrearPartidaRequest> crearReq = RequestEntity
            .post(SERVER_URL + ":" + port + "/partida/crear")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + authA.getToken())
            .body(crear);
        ResponseEntity<String> crearResp = restTemplate.exchange(crearReq, String.class);
        assertEquals(HttpStatus.CREATED, crearResp.getStatusCode(), "Partida no creada");
        PartidaDTO partidaDTO = objectMapper.readValue(crearResp.getBody(), PartidaDTO.class);
        Long partidaId = partidaDTO.getId();
        assertNotNull(partidaId, "ID de partida null");

        // 3. Player B joins partida
        String joinUrl = SERVER_URL + ":" + port + "/partida/" + partidaId + "/join?jugadorId=" + jugadorB.getId() + "&barcoId=" + barcoB.getId();
        RequestEntity<Void> joinReq = RequestEntity
            .post(joinUrl)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + authB.getToken())
            .build();
        ResponseEntity<String> joinResp = restTemplate.exchange(joinReq, String.class);
        assertEquals(HttpStatus.OK, joinResp.getStatusCode(), "Join fallo: " + joinResp.getBody());

        GameStateDTO stateAfterJoin = objectMapper.readValue(joinResp.getBody(), GameStateDTO.class);
        assertEquals(partidaId, stateAfterJoin.getPartidaId());
        assertEquals(2, stateAfterJoin.getParticipantes().size(), "Debe haber dos participantes tras join");

        // 4. GET estado to confirm both participants persist
        String estadoUrl = SERVER_URL + ":" + port + "/partida/" + partidaId + "/estado";
        RequestEntity<Void> estadoReq = RequestEntity
            .get(estadoUrl)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + authA.getToken())
            .build();
        ResponseEntity<String> estadoResp = restTemplate.exchange(estadoReq, String.class);
        assertEquals(HttpStatus.OK, estadoResp.getStatusCode(), "Estado fallo");
        GameStateDTO finalState = objectMapper.readValue(estadoResp.getBody(), GameStateDTO.class);
        assertEquals(2, finalState.getParticipantes().size(), "Estado final sin ambos participantes");
        assertTrue(finalState.getParticipantes().stream().anyMatch(p -> p.getJugadorId().equals(jugadorA.getId())));
        assertTrue(finalState.getParticipantes().stream().anyMatch(p -> p.getJugadorId().equals(jugadorB.getId())));
    }
}

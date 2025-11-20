package co.edu.javeriana.proyectoWeb.RegataOnline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.CrearPartidaRequest;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.JwtAuthenticationResponse;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.LoginDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.GameStateDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Barco;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-testing")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PartidaFinalizacionIntegrationTest {

    private static final String SERVER_URL = "http://localhost";

    @Autowired private TestRestTemplate restTemplate;
    @LocalServerPort private int port;
    @Autowired private JugadorRepositorio jugadorRepo;
    @Autowired private MapaRepositorio mapaRepo;
    @Autowired private BarcoRepositorio barcoRepo;
    @Autowired private CeldaRepositorio celdaRepo;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    private Jugador jugadorA, jugadorB;
    private Barco barcoA, barcoB;
    private Mapa mapa;

    @BeforeEach
    void setup() {
        barcoRepo.deleteAll(); celdaRepo.deleteAll(); mapaRepo.deleteAll(); jugadorRepo.deleteAll();

        jugadorA = new Jugador(); jugadorA.setNombre("JA"); jugadorA.setEmail("ja@local"); jugadorA.setPassword(passwordEncoder.encode("ja123")); jugadorA.setRole(Role.USER); jugadorA = jugadorRepo.save(jugadorA);
        jugadorB = new Jugador(); jugadorB.setNombre("JB"); jugadorB.setEmail("jb@local"); jugadorB.setPassword(passwordEncoder.encode("jb123")); jugadorB.setRole(Role.USER); jugadorB = jugadorRepo.save(jugadorB);

        mapa = new Mapa(10,10); mapa = mapaRepo.save(mapa);
        co.edu.javeriana.proyectoWeb.RegataOnline.model.Celda s = new co.edu.javeriana.proyectoWeb.RegataOnline.model.Celda("P",0,0); s.setMapa(mapa); celdaRepo.save(s);

        barcoA = new Barco(); barcoA.setNombre("BA"); barcoA.setJugador(jugadorA); barcoA = barcoRepo.save(barcoA);
        barcoB = new Barco(); barcoB.setNombre("BB"); barcoB.setJugador(jugadorB); barcoB = barcoRepo.save(barcoB);
    }

    private JwtAuthenticationResponse login(String email, String password) {
        RequestEntity<LoginDTO> request = RequestEntity.post(SERVER_URL + ":" + port + "/auth/login").body(new LoginDTO(email, password));
        ResponseEntity<JwtAuthenticationResponse> resp = restTemplate.exchange(request, JwtAuthenticationResponse.class);
        JwtAuthenticationResponse body = resp.getBody(); assertNotNull(body); return body;
    }

    @Test
    void testFinalizar_PropagaEstadoATodosLosParticipantes() throws Exception {
        // A crea partida
        JwtAuthenticationResponse authA = login("ja@local", "ja123");
        CrearPartidaRequest crearA = new CrearPartidaRequest(); crearA.setJugadorId(jugadorA.getId()); crearA.setMapaId(mapa.getId()); crearA.setBarcoId(barcoA.getId());
        RequestEntity<CrearPartidaRequest> crearReqA = RequestEntity.post(SERVER_URL + ":" + port + "/partida/crear").header(HttpHeaders.AUTHORIZATION, "Bearer " + authA.getToken()).body(crearA);
        ResponseEntity<String> crearRespA = restTemplate.exchange(crearReqA, String.class);
        assertEquals(HttpStatus.CREATED, crearRespA.getStatusCode());
        co.edu.javeriana.proyectoWeb.RegataOnline.dto.PartidaDTO partida = objectMapper.readValue(crearRespA.getBody(), co.edu.javeriana.proyectoWeb.RegataOnline.dto.PartidaDTO.class);

        // B se une
        JwtAuthenticationResponse authB = login("jb@local", "jb123");
        String joinUrl = SERVER_URL + ":" + port + "/partida/" + partida.getId() + "/join?jugadorId=" + jugadorB.getId() + "&barcoId=" + barcoB.getId();
        RequestEntity<Void> joinReq = RequestEntity.post(joinUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + authB.getToken()).build();
        ResponseEntity<String> joinResp = restTemplate.exchange(joinReq, String.class);
        assertEquals(HttpStatus.OK, joinResp.getStatusCode());

        // A finaliza la partida
        String finalizarUrl = SERVER_URL + ":" + port + "/partida/" + partida.getId() + "/finalizar";
        RequestEntity<Void> finReq = RequestEntity.put(finalizarUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + authA.getToken()).build();
        ResponseEntity<String> finResp = restTemplate.exchange(finReq, String.class);
        assertEquals(HttpStatus.OK, finResp.getStatusCode());

        // B consulta estado y debe ver 'terminada'
        String estadoUrl = SERVER_URL + ":" + port + "/partida/" + partida.getId() + "/estado";
        RequestEntity<Void> estadoReq = RequestEntity.get(estadoUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + authB.getToken()).build();
        ResponseEntity<String> estadoResp = restTemplate.exchange(estadoReq, String.class);
        assertEquals(HttpStatus.OK, estadoResp.getStatusCode());
        GameStateDTO state = objectMapper.readValue(estadoResp.getBody(), GameStateDTO.class);
        assertEquals("terminada", state.getEstadoPartida());
    }
}

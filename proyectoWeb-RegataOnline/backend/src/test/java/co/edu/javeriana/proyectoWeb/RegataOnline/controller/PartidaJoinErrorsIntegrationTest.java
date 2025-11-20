package co.edu.javeriana.proyectoWeb.RegataOnline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.CrearPartidaRequest;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.JwtAuthenticationResponse;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.LoginDTO;
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
public class PartidaJoinErrorsIntegrationTest {

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
        barcoRepo.deleteAll();
        celdaRepo.deleteAll();
        mapaRepo.deleteAll();
        jugadorRepo.deleteAll();

        jugadorA = new Jugador(); jugadorA.setNombre("A"); jugadorA.setEmail("a@local"); jugadorA.setPassword(passwordEncoder.encode("a123")); jugadorA.setRole(Role.USER); jugadorA = jugadorRepo.save(jugadorA);
        jugadorB = new Jugador(); jugadorB.setNombre("B"); jugadorB.setEmail("b@local"); jugadorB.setPassword(passwordEncoder.encode("b123")); jugadorB.setRole(Role.USER); jugadorB = jugadorRepo.save(jugadorB);

        mapa = new Mapa(10,10); mapa = mapaRepo.save(mapa);
        // start cell
        co.edu.javeriana.proyectoWeb.RegataOnline.model.Celda start = new co.edu.javeriana.proyectoWeb.RegataOnline.model.Celda("P",0,0);
        start.setMapa(mapa); celdaRepo.save(start);

        barcoA = new Barco(); barcoA.setNombre("BarcoA"); barcoA.setJugador(jugadorA); barcoA = barcoRepo.save(barcoA);
        barcoB = new Barco(); barcoB.setNombre("BarcoB"); barcoB.setJugador(jugadorB); barcoB = barcoRepo.save(barcoB);
    }

    private JwtAuthenticationResponse login(String email, String password) {
        RequestEntity<LoginDTO> request = RequestEntity.post(SERVER_URL + ":" + port + "/auth/login").body(new LoginDTO(email, password));
        ResponseEntity<JwtAuthenticationResponse> resp = restTemplate.exchange(request, JwtAuthenticationResponse.class);
        JwtAuthenticationResponse body = resp.getBody(); assertNotNull(body); return body;
    }

    @Test
    void testJoin_BarcoNoPertenece_DebeRetornar400ConCodigoBARCO_INVALIDO() throws Exception {
        // jugadorA crea partida con su barcoA
        JwtAuthenticationResponse aAuth = login("a@local", "a123");
        CrearPartidaRequest crear = new CrearPartidaRequest(); crear.setJugadorId(jugadorA.getId()); crear.setMapaId(mapa.getId()); crear.setBarcoId(barcoA.getId());
        RequestEntity<CrearPartidaRequest> crearReq = RequestEntity.post(SERVER_URL + ":" + port + "/partida/crear").header(HttpHeaders.AUTHORIZATION, "Bearer " + aAuth.getToken()).body(crear);
        ResponseEntity<String> crearResp = restTemplate.exchange(crearReq, String.class);
        assertEquals(HttpStatus.CREATED, crearResp.getStatusCode());
        co.edu.javeriana.proyectoWeb.RegataOnline.dto.PartidaDTO partida = objectMapper.readValue(crearResp.getBody(), co.edu.javeriana.proyectoWeb.RegataOnline.dto.PartidaDTO.class);

        // jugadorB intenta unirse usando barcoA (pertenece a A) => BARCO_INVALIDO
        JwtAuthenticationResponse bAuth = login("b@local", "b123");
        String joinUrl = SERVER_URL + ":" + port + "/partida/" + partida.getId() + "/join?jugadorId=" + jugadorB.getId() + "&barcoId=" + barcoA.getId();
        RequestEntity<Void> joinReq = RequestEntity.post(joinUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + bAuth.getToken()).build();
        ResponseEntity<String> joinResp = restTemplate.exchange(joinReq, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, joinResp.getStatusCode());
        assertNotNull(joinResp.getBody());
        assertTrue(joinResp.getBody().contains("BARCO_INVALIDO"));
    }

    @Test
    void testJoin_BarcoEnUso_DebeRetornar409ConCodigoBARCO_EN_USO() throws Exception {
        // jugadorB creates a partida with barcoB (so it's in use)
        JwtAuthenticationResponse bAuth = login("b@local", "b123");
        CrearPartidaRequest crearB = new CrearPartidaRequest(); crearB.setJugadorId(jugadorB.getId()); crearB.setMapaId(mapa.getId()); crearB.setBarcoId(barcoB.getId());
        RequestEntity<CrearPartidaRequest> crearReqB = RequestEntity.post(SERVER_URL + ":" + port + "/partida/crear").header(HttpHeaders.AUTHORIZATION, "Bearer " + bAuth.getToken()).body(crearB);
        ResponseEntity<String> crearRespB = restTemplate.exchange(crearReqB, String.class);
        assertEquals(HttpStatus.CREATED, crearRespB.getStatusCode());

        // Now jugadorA creates another partida
        JwtAuthenticationResponse aAuth = login("a@local", "a123");
        CrearPartidaRequest crearA = new CrearPartidaRequest(); crearA.setJugadorId(jugadorA.getId()); crearA.setMapaId(mapa.getId()); crearA.setBarcoId(barcoA.getId());
        RequestEntity<CrearPartidaRequest> crearReqA = RequestEntity.post(SERVER_URL + ":" + port + "/partida/crear").header(HttpHeaders.AUTHORIZATION, "Bearer " + aAuth.getToken()).body(crearA);
        ResponseEntity<String> crearRespA = restTemplate.exchange(crearReqA, String.class);
        assertEquals(HttpStatus.CREATED, crearRespA.getStatusCode());
        co.edu.javeriana.proyectoWeb.RegataOnline.dto.PartidaDTO partidaA = objectMapper.readValue(crearRespA.getBody(), co.edu.javeriana.proyectoWeb.RegataOnline.dto.PartidaDTO.class);

        // jugadorB tries to join partidaA using barcoB which is already in use by B's own partida => should be 409
        String joinUrl = SERVER_URL + ":" + port + "/partida/" + partidaA.getId() + "/join?jugadorId=" + jugadorB.getId() + "&barcoId=" + barcoB.getId();
        RequestEntity<Void> joinReq = RequestEntity.post(joinUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + bAuth.getToken()).build();
        ResponseEntity<String> joinResp = restTemplate.exchange(joinReq, String.class);

        assertEquals(HttpStatus.CONFLICT, joinResp.getStatusCode());
        assertNotNull(joinResp.getBody());
        assertTrue(joinResp.getBody().contains("BARCO_EN_USO"));
    }
}

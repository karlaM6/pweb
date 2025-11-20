package co.edu.javeriana.proyectoWeb.RegataOnline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.CrearPartidaRequest;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-testing")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MovimientoControladorIntegrationTest {

    private static final String SERVER_URL = "http://localhost";

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

    private Jugador jugador;
    private Mapa mapa;
    private Barco barco;

    @BeforeEach
    void setup() {
        barcoRepo.deleteAll();
        celdaRepo.deleteAll();
        mapaRepo.deleteAll();
        jugadorRepo.deleteAll();

        jugador = new Jugador();
        jugador.setNombre("MoverUser");
        jugador.setEmail("mover@local");
        jugador.setPassword(passwordEncoder.encode("mover123"));
        jugador.setRole(Role.USER);
        jugador = jugadorRepo.save(jugador);

        mapa = new Mapa(10, 10);
        mapa = mapaRepo.save(mapa);
        // start cell and a goal far away
        Celda start = new Celda("P", 0, 0); start.setMapa(mapa); celdaRepo.save(start);
        Celda meta = new Celda("M", 9, 9); meta.setMapa(mapa); celdaRepo.save(meta);

        barco = new Barco();
        barco.setNombre("MoverBarco");
        barco.setJugador(jugador);
        barco = barcoRepo.save(barco);
    }

    private JwtAuthenticationResponse login(String email, String password) {
        RequestEntity<LoginDTO> request = RequestEntity.post(SERVER_URL + ":" + port + "/auth/login")
            .body(new LoginDTO(email, password));
        ResponseEntity<JwtAuthenticationResponse> jwtResponse = restTemplate.exchange(request, JwtAuthenticationResponse.class);
        JwtAuthenticationResponse body = jwtResponse.getBody();
        assertNotNull(body);
        assertNotNull(body.getToken());
        return body;
    }

    @Test
    void testMoverBarco_HappyPath() throws Exception {
        JwtAuthenticationResponse auth = login("mover@local", "mover123");

        // Crear partida
        co.edu.javeriana.proyectoWeb.RegataOnline.dto.CrearPartidaRequest crear = new CrearPartidaRequest();
        crear.setJugadorId(jugador.getId());
        crear.setMapaId(mapa.getId());
        crear.setBarcoId(barco.getId());

        RequestEntity<CrearPartidaRequest> crearReq = RequestEntity.post(SERVER_URL + ":" + port + "/partida/crear")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getToken()).body(crear);
        ResponseEntity<String> crearResp = restTemplate.exchange(crearReq, String.class);
        assertEquals(HttpStatus.CREATED, crearResp.getStatusCode());
        PartidaDTO partida = objectMapper.readValue(crearResp.getBody(), PartidaDTO.class);
        Long partidaId = partida.getId();
        assertNotNull(partidaId);

        // Ejecutar movimiento sencillo: aceleración 0,1 (mover en Y)
        String moverUrl = SERVER_URL + ":" + port + "/partida/" + partidaId + "/mover?barcoId=" + barco.getId() + "&aceleracionX=0&aceleracionY=1";
        RequestEntity<Void> moverReq = RequestEntity.put(moverUrl)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getToken()).build();
        ResponseEntity<String> moverResp = restTemplate.exchange(moverReq, String.class);

        assertEquals(HttpStatus.OK, moverResp.getStatusCode(), "Mover debería devolver 200: " + moverResp.getBody());
        assertNotNull(moverResp.getBody());
        PartidaDTO after = objectMapper.readValue(moverResp.getBody(), PartidaDTO.class);
        assertTrue(after.getMovimientos() >= 1, "Movimientos debe incrementarse");
    }

    @Test
    void testMoverBarco_FueraDelMapa_DebeRetornar400() throws Exception {
        JwtAuthenticationResponse auth = login("mover@local", "mover123");

        CrearPartidaRequest crear = new CrearPartidaRequest();
        crear.setJugadorId(jugador.getId());
        crear.setMapaId(mapa.getId());
        crear.setBarcoId(barco.getId());

        RequestEntity<CrearPartidaRequest> crearReq = RequestEntity.post(SERVER_URL + ":" + port + "/partida/crear")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getToken()).body(crear);
        ResponseEntity<String> crearResp = restTemplate.exchange(crearReq, String.class);
        assertEquals(HttpStatus.CREATED, crearResp.getStatusCode());
        PartidaDTO partida = objectMapper.readValue(crearResp.getBody(), PartidaDTO.class);

        Long partidaId = partida.getId();

        // Intentar moverse fuera del mapa: aceleración negativa desde borde 0 -> produce error
        String moverUrl = SERVER_URL + ":" + port + "/partida/" + partidaId + "/mover?barcoId=" + barco.getId() + "&aceleracionX=-1&aceleracionY=0";
        RequestEntity<Void> moverReq = RequestEntity.put(moverUrl)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getToken()).build();
        ResponseEntity<String> moverResp = restTemplate.exchange(moverReq, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, moverResp.getStatusCode(), "Mover fuera del mapa debe retornar 400");
        assertNotNull(moverResp.getBody());
        assertTrue(moverResp.getBody().contains("No puedes moverte ahí") || moverResp.getBody().toLowerCase().contains("mapa"));
    }
}

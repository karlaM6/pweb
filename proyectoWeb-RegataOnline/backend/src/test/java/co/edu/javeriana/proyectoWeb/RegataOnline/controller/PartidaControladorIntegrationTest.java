package co.edu.javeriana.proyectoWeb.RegataOnline.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.JwtAuthenticationResponse;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.LoginDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Role;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import co.edu.javeriana.proyectoWeb.RegataOnline.repository.JugadorRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.MapaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.BarcoRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.CeldaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.CrearPartidaRequest;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.PartidaDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Jugador;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Mapa;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Barco;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Celda;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-testing")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PartidaControladorIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private JugadorRepositorio jugadorRepo;

    @Autowired
    private MapaRepositorio mapaRepo;

    @Autowired
    private BarcoRepositorio barcoRepo;
        @Autowired
        private CeldaRepositorio celdaRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Jugador jugador;
    private Mapa mapa;
    private Barco barco;
    private static String SERVER_URL = "http://localhost";


    @BeforeEach
    void setup() {
    
 
           barcoRepo.deleteAll();
           celdaRepo.deleteAll();
           jugadorRepo.deleteAll();
           mapaRepo.deleteAll();

        
    // create test users
    jugadorRepo.deleteAll();
    Jugador admin = new Jugador();
    admin.setNombre("Admin");
    admin.setEmail("admin@local");
    admin.setPassword(passwordEncoder.encode("admin123"));
    admin.setRole(Role.ADMIN);
    jugadorRepo.save(admin);

    Jugador user = new Jugador();
    user.setNombre("User");
    user.setEmail("user@local");
    user.setPassword(passwordEncoder.encode("user123"));
    user.setRole(Role.USER);
    jugadorRepo.save(user);

    jugador = new Jugador();
    jugador.setNombre("JugadorTest");
    jugador = jugadorRepo.save(jugador);

       
        mapa = new Mapa(10, 10);
        mapa = mapaRepo.save(mapa);

      
        Celda celdaPartida = new Celda("P", 0, 0);
        celdaPartida.setMapa(mapa);
        celdaRepo.save(celdaPartida);

        barco = new Barco();
        barco.setNombre("BarcoTest");
        barco = barcoRepo.save(barco);
    }

    private JwtAuthenticationResponse login(String email, String password) {
    RequestEntity<LoginDTO> request = RequestEntity.post(SERVER_URL + ":" + port + "/auth/login")
        .body(new LoginDTO(email, password));
    ResponseEntity<JwtAuthenticationResponse> jwtResponse = restTemplate.exchange(request,
        JwtAuthenticationResponse.class);
    JwtAuthenticationResponse body = jwtResponse.getBody();
    assertNotNull(body);
    return body;
    }

    @Test
    void testCrearPartida_DebeRetornar201() throws Exception {
        CrearPartidaRequest request = new CrearPartidaRequest();
        request.setJugadorId(jugador.getId());
        request.setMapaId(mapa.getId());
        request.setBarcoId(barco.getId());

    JwtAuthenticationResponse user = login("user@local", "user123");
    RequestEntity<CrearPartidaRequest> req = RequestEntity.post(SERVER_URL + ":" + port + "/partida/crear")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken()).body(request);
    ResponseEntity<String> response = restTemplate.exchange(req, String.class);

        System.out.println("Response Body: " + response.getBody());
        System.out.println("Response Status: " + response.getStatusCode());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        PartidaDTO partida = objectMapper.readValue(response.getBody(), PartidaDTO.class);
        assertEquals(jugador.getId(), partida.getJugadorId());
    }

    @Test
    void testObtenerPartidaActiva_CuandoNoExiste_DebeRetornar404() {
    JwtAuthenticationResponse user = login("user@local", "user123");
    RequestEntity<Void> req = RequestEntity.get(SERVER_URL + ":" + port + "/partida/activa/" + jugador.getId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken()).build();
    ResponseEntity<?> response = restTemplate.exchange(req, Object.class);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

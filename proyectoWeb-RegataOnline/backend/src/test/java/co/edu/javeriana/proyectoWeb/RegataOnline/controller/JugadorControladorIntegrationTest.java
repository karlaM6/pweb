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
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.BarcoRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.PartidaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.JugadorDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Jugador;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-testing")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class JugadorControladorIntegrationTest {
    private static String SERVER_URL = "http://localhost";

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private JugadorRepositorio jugadorRepo;

    @Autowired
    private BarcoRepositorio barcoRepo;

    @Autowired
    private PartidaRepositorio partidaRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Jugador jugador;

    @BeforeEach
    void setup() {
        partidaRepo.deleteAll();
        barcoRepo.deleteAll();
        jugadorRepo.deleteAll();

        // create test users
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
    void testListarJugadores_DebeRetornar200() {
    JwtAuthenticationResponse user = login("user@local", "user123");
    RequestEntity<Void> request = RequestEntity.get(SERVER_URL + ":" + port + "/jugador/list")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken()).build();
    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("JugadorTest"));
    }

    @Test
    void testBuscarJugadorPorId_DebeRetornar200() throws Exception {
    JwtAuthenticationResponse user = login("user@local", "user123");
    RequestEntity<Void> request = RequestEntity.get(SERVER_URL + ":" + port + "/jugador/" + jugador.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken()).build();
    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JugadorDTO jugadorDTO = objectMapper.readValue(response.getBody(), JugadorDTO.class);
        assertEquals(jugador.getId(), jugadorDTO.getId());
        assertEquals("JugadorTest", jugadorDTO.getNombre());
    }

    @Test
    void testBuscarJugadorPorIdInexistente_DebeRetornarEmpty() throws Exception {
    JwtAuthenticationResponse user = login("user@local", "user123");
    RequestEntity<Void> request = RequestEntity.get(SERVER_URL + ":" + port + "/jugador/9999")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken()).build();
    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCrearJugador_DebeRetornar200() throws Exception {
        JugadorDTO nuevoJugador = new JugadorDTO();
        nuevoJugador.setNombre("NuevoJugador");

    JwtAuthenticationResponse admin = login("admin@local", "admin123");
    RequestEntity<JugadorDTO> request = RequestEntity.post(SERVER_URL + ":" + port + "/jugador")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin.getToken()).body(nuevoJugador);
    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        List<Jugador> jugadores = jugadorRepo.findAll();
        assertTrue(jugadores.stream().anyMatch(j -> j.getNombre().equals("NuevoJugador")));
    }

    @Test
    void testActualizarJugador_DebeRetornar200() throws Exception {
        JugadorDTO jugadorActualizado = new JugadorDTO();
        jugadorActualizado.setId(jugador.getId());
        jugadorActualizado.setNombre("JugadorActualizado");

    JwtAuthenticationResponse admin = login("admin@local", "admin123");
    RequestEntity<JugadorDTO> request = RequestEntity.put(SERVER_URL + ":" + port + "/jugador")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin.getToken()).body(jugadorActualizado);
    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Optional<Jugador> jugadorOptional = jugadorRepo.findById(jugador.getId());
        assertTrue(jugadorOptional.isPresent());
        assertEquals("JugadorActualizado", jugadorOptional.get().getNombre());
    }

    @Test
    void testBorrarJugador_DebeRetornar200() {
    JwtAuthenticationResponse admin = login("admin@local", "admin123");
    RequestEntity<Void> request = RequestEntity.delete(SERVER_URL + ":" + port + "/jugador/" + jugador.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin.getToken()).build();
    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Optional<Jugador> jugadorOptional = jugadorRepo.findById(jugador.getId());
        assertFalse(jugadorOptional.isPresent());
    }
}

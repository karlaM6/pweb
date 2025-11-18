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
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Jugador;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.JugadorRepositorio;
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

import co.edu.javeriana.proyectoWeb.RegataOnline.repository.MapaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.CeldaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.BarcoRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.PartidaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.MapaDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.CrearMapaRequest;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Mapa;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Celda;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-testing")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MapaControladorIntegrationTest {
    private static String SERVER_URL = "http://localhost";


    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private MapaRepositorio mapaRepo;

    @Autowired
    private CeldaRepositorio celdaRepo;

    @Autowired
    private BarcoRepositorio barcoRepo;

    @Autowired
    private PartidaRepositorio partidaRepo;

    @Autowired
    private JugadorRepositorio jugadorRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Mapa mapa;

    @BeforeEach
    void setup() {
        partidaRepo.deleteAll();
        barcoRepo.deleteAll();
        celdaRepo.deleteAll();
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

        mapa = new Mapa(10, 10);
        mapa = mapaRepo.save(mapa);

        Celda celdaPartida = new Celda("P", 0, 0);
        celdaPartida.setMapa(mapa);
        celdaRepo.save(celdaPartida);

        Celda celdaMeta = new Celda("M", 9, 9);
        celdaMeta.setMapa(mapa);
        celdaRepo.save(celdaMeta);
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
    void testListarMapas_DebeRetornar200() {
    JwtAuthenticationResponse user = login("user@local", "user123");
    RequestEntity<Void> request = RequestEntity.get(SERVER_URL + ":" + port + "/mapa/list")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken()).build();
    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("10"));
    }

    @Test
    void testObtenerMapaPorId_DebeRetornar200() throws Exception {
    JwtAuthenticationResponse user = login("user@local", "user123");
    RequestEntity<Void> request = RequestEntity.get(SERVER_URL + ":" + port + "/mapa/" + mapa.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken()).build();
    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        MapaDTO mapaDTO = objectMapper.readValue(response.getBody(), MapaDTO.class);
        assertEquals(mapa.getId(), mapaDTO.getId());
        assertEquals(10, mapaDTO.getFilas());
        assertEquals(10, mapaDTO.getColumnas());
    }

    @Test
    void testObtenerMapaPorIdInexistente_DebeRetornar404() {
    JwtAuthenticationResponse user = login("user@local", "user123");
    RequestEntity<Void> request = RequestEntity.get(SERVER_URL + ":" + port + "/mapa/9999")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken()).build();
    ResponseEntity<?> response = restTemplate.exchange(request, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCrearMapa_DebeRetornar201() throws Exception {
        CrearMapaRequest request = new CrearMapaRequest();
        request.setFilas(15);
        request.setColumnas(15);

    JwtAuthenticationResponse admin = login("admin@local", "admin123");
    RequestEntity<CrearMapaRequest> req = RequestEntity.post(SERVER_URL + ":" + port + "/mapa/crear")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin.getToken()).body(request);
    ResponseEntity<String> response = restTemplate.exchange(req, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        MapaDTO mapaDTO = objectMapper.readValue(response.getBody(), MapaDTO.class);
        assertEquals(15, mapaDTO.getFilas());
        assertEquals(15, mapaDTO.getColumnas());
    }

    @Test
    void testBorrarMapa_DebeRetornar200() {
    JwtAuthenticationResponse admin = login("admin@local", "admin123");
    RequestEntity<Void> req = RequestEntity.delete(SERVER_URL + ":" + port + "/mapa/" + mapa.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin.getToken()).build();
    ResponseEntity<String> response = restTemplate.exchange(req, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Long mapaId = mapa.getId();
        assertNotNull(mapaId);
        Optional<Mapa> mapaOptional = mapaRepo.findById(mapaId);
        assertFalse(mapaOptional.isPresent());
    }
}

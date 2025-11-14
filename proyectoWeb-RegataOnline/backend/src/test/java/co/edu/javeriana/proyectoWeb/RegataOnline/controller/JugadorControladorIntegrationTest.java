package co.edu.javeriana.proyectoWeb.RegataOnline.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    private Jugador jugador;

    @BeforeEach
    void setup() {
        partidaRepo.deleteAll();
        barcoRepo.deleteAll();
        jugadorRepo.deleteAll();

        jugador = new Jugador();
        jugador.setNombre("JugadorTest");
        jugador = jugadorRepo.save(jugador);
    }

    @Test
    void testListarJugadores_DebeRetornar200() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                SERVER_URL + ":" + port + "/jugador/list",
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("JugadorTest"));
    }

    @Test
    void testBuscarJugadorPorId_DebeRetornar200() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                SERVER_URL + ":" + port + "/jugador/" + jugador.getId(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JugadorDTO jugadorDTO = objectMapper.readValue(response.getBody(), JugadorDTO.class);
        assertEquals(jugador.getId(), jugadorDTO.getId());
        assertEquals("JugadorTest", jugadorDTO.getNombre());
    }

    @Test
    void testBuscarJugadorPorIdInexistente_DebeRetornarEmpty() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                SERVER_URL + ":" + port + "/jugador/9999",
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCrearJugador_DebeRetornar200() throws Exception {
        JugadorDTO nuevoJugador = new JugadorDTO();
        nuevoJugador.setNombre("NuevoJugador");

        ResponseEntity<String> response = restTemplate.postForEntity(
                SERVER_URL + ":" + port + "/jugador",
                nuevoJugador,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        List<Jugador> jugadores = jugadorRepo.findAll();
        assertTrue(jugadores.stream().anyMatch(j -> j.getNombre().equals("NuevoJugador")));
    }

    @Test
    void testActualizarJugador_DebeRetornar200() throws Exception {
        JugadorDTO jugadorActualizado = new JugadorDTO();
        jugadorActualizado.setId(jugador.getId());
        jugadorActualizado.setNombre("JugadorActualizado");

        ResponseEntity<String> response = restTemplate.exchange(
                SERVER_URL + ":" + port + "/jugador",
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(jugadorActualizado),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Optional<Jugador> jugadorOptional = jugadorRepo.findById(jugador.getId());
        assertTrue(jugadorOptional.isPresent());
        assertEquals("JugadorActualizado", jugadorOptional.get().getNombre());
    }

    @Test
    void testBorrarJugador_DebeRetornar200() {
        ResponseEntity<String> response = restTemplate.exchange(
                SERVER_URL + ":" + port + "/jugador/" + jugador.getId(),
                org.springframework.http.HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Optional<Jugador> jugadorOptional = jugadorRepo.findById(jugador.getId());
        assertFalse(jugadorOptional.isPresent());
    }
}

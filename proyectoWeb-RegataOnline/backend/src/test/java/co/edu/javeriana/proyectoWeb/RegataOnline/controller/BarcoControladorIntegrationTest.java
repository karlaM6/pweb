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

import co.edu.javeriana.proyectoWeb.RegataOnline.repository.BarcoRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.ModeloRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.PartidaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.BarcoDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Barco;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Modelo;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-testing")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BarcoControladorIntegrationTest {

    private static String SERVER_URL = "http://localhost";
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private BarcoRepositorio barcoRepo;

    @Autowired
    private ModeloRepositorio modeloRepo;

    @Autowired
    private PartidaRepositorio partidaRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private Barco barco;
    private Modelo modelo;

    @BeforeEach
    void setup() {
        partidaRepo.deleteAll();
        barcoRepo.deleteAll();
        modeloRepo.deleteAll();

        modelo = new Modelo();
        modelo.setNombreModelo("ModeloTest");
        modelo.setColor("Rojo");
        modelo = modeloRepo.save(modelo);

        barco = new Barco();
        barco.setNombre("BarcoTest");
        barco.setModelo(modelo);
        barco.setPosicionX(0);
        barco.setPosicionY(0);
        barco = barcoRepo.save(barco);
    }

    @Test
    void testListarBarcos_DebeRetornar200() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                SERVER_URL + ":" + port + "/barco/list",
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("BarcoTest"));
    }

    @Test
    void testBuscarBarcoPorId_DebeRetornar200() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                SERVER_URL + ":" + port + "/barco/" + barco.getId(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        BarcoDTO barcoDTO = objectMapper.readValue(response.getBody(), BarcoDTO.class);
        assertEquals(barco.getId(), barcoDTO.getId());
        assertEquals("BarcoTest", barcoDTO.getNombre());
    }

    @Test
    void testBuscarBarcoPorIdInexistente_DebeRetornarError() {
        ResponseEntity<?> response = restTemplate.getForEntity(
                SERVER_URL + ":" + port + "/barco/9999",
                Object.class);

        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testBuscarBarcosPorNombre_DebeRetornar200() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                SERVER_URL + ":" + port + "/barco/search?searchText=BarcoTest",
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("BarcoTest"));
    }

    @Test
    void testBuscarBarcosPorNombreVacio_DebeRetornarError() {
        ResponseEntity<?> response = restTemplate.getForEntity(
                SERVER_URL + ":" + port + "/barco/search?searchText=",
                Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCrearBarco_DebeRetornar200() throws Exception {
        BarcoDTO nuevoBarco = new BarcoDTO();
        nuevoBarco.setNombre("NuevoBarco");
        nuevoBarco.setPosicionX(5);
        nuevoBarco.setPosicionY(5);

        ResponseEntity<String> response = restTemplate.postForEntity(
                SERVER_URL + ":" + port + "/barco",
                nuevoBarco,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        List<Barco> barcos = barcoRepo.findAll();
        assertTrue(barcos.stream().anyMatch(b -> b.getNombre().equals("NuevoBarco")));
    }

    @Test
    void testActualizarBarco_DebeRetornar200() throws Exception {
        BarcoDTO barcoActualizado = new BarcoDTO();
        barcoActualizado.setId(barco.getId());
        barcoActualizado.setNombre("BarcoActualizado");
        barcoActualizado.setPosicionX(10);
        barcoActualizado.setPosicionY(10);

        ResponseEntity<String> response = restTemplate.exchange(
                SERVER_URL + ":" + port + "/barco",
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(barcoActualizado),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Optional<Barco> barcoOptional = barcoRepo.findById(barco.getId());
        assertTrue(barcoOptional.isPresent());
        assertEquals("BarcoActualizado", barcoOptional.get().getNombre());
    }

    @Test
    void testBorrarBarco_DebeRetornar200() {
        ResponseEntity<String> response = restTemplate.exchange(
                SERVER_URL + ":" + port + "/barco/" + barco.getId(),
                org.springframework.http.HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Optional<Barco> barcoOptional = barcoRepo.findById(barco.getId());
        assertFalse(barcoOptional.isPresent());
    }
}

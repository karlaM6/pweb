package co.edu.javeriana.proyectoWeb.RegataOnline.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.annotation.Secured;

import co.edu.javeriana.proyectoWeb.RegataOnline.dto.CrearPartidaRequest;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.PartidaDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.services.PartidaServicio;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.GameStateDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/partida")
@Secured({ Role.Code.USER })
@Tag(name = "Partida", description = "Endpoints para gestionar las partidas del juego")
public class PartidaControlador {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PartidaServicio partidaServicio;

    @PostMapping("/crear")
    @Operation(summary = "Crear nueva partida", description = "Crea una nueva partida con jugador, mapa y barco")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partida creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o jugador ya tiene partida activa")
    })
    public ResponseEntity<?> crearPartida(
        @Parameter(description = "Datos para crear la partida", required = true)
        @RequestBody CrearPartidaRequest request) {
        try {
            log.info("Solicitud para crear partida: jugadorId={}, mapaId={}, barcoId={}", 
                request.getJugadorId(), request.getMapaId(), request.getBarcoId());
            
            PartidaDTO partida = partidaServicio.crearPartida(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(partida);
        } catch (Exception e) {
            log.error("Error al crear partida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/activa/{jugadorId}")
    @Operation(summary = "Obtener partida activa", description = "Busca la partida activa o pausada de un jugador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partida encontrada"),
        @ApiResponse(responseCode = "404", description = "No hay partida activa")
    })
    public ResponseEntity<PartidaDTO> obtenerPartidaActiva(
        @Parameter(description = "ID del jugador", example = "1", required = true)
        @PathVariable Long jugadorId) {
        return partidaServicio.buscarPartidaActiva(jugadorId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener partida por ID", description = "Obtiene los detalles de una partida específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partida encontrada"),
        @ApiResponse(responseCode = "404", description = "Partida no encontrada")
    })
    public ResponseEntity<PartidaDTO> obtenerPartida(
        @Parameter(description = "ID de la partida", example = "1", required = true)
        @PathVariable Long id) {
        return partidaServicio.obtenerPartida(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/jugador/{jugadorId}")
    @Operation(summary = "Listar partidas del jugador", description = "Obtiene todas las partidas de un jugador")
    @ApiResponse(responseCode = "200", description = "Lista de partidas obtenida exitosamente")
    public List<PartidaDTO> listarPartidasJugador(
        @Parameter(description = "ID del jugador", example = "1", required = true)
        @PathVariable Long jugadorId) {
        return partidaServicio.listarPartidasJugador(jugadorId);
    }

    @PutMapping("/{id}/pausar")
    @Operation(summary = "Pausar partida", description = "Pausa una partida activa")
    @ApiResponse(responseCode = "200", description = "Partida pausada exitosamente")
    public ResponseEntity<PartidaDTO> pausarPartida(
        @Parameter(description = "ID de la partida", example = "1", required = true)
        @PathVariable Long id) {
        try {
            PartidaDTO partida = partidaServicio.pausarPartida(id);
            return ResponseEntity.ok(partida);
        } catch (Exception e) {
            log.error("Error al pausar partida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar partida", description = "Marca una partida como terminada")
    @ApiResponse(responseCode = "200", description = "Partida finalizada exitosamente")
    public ResponseEntity<PartidaDTO> finalizarPartida(
        @Parameter(description = "ID de la partida", example = "1", required = true)
        @PathVariable Long id) {
        try {
            PartidaDTO partida = partidaServicio.finalizarPartida(id);
            return ResponseEntity.ok(partida);
        } catch (Exception e) {
            log.error("Error al finalizar partida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/reanudar")
    @Operation(summary = "Reanudar partida", description = "Cambia una partida pausada a estado activa")
    @ApiResponse(responseCode = "200", description = "Partida reanudada exitosamente")
    public ResponseEntity<?> reanudarPartida(
        @Parameter(description = "ID de la partida", example = "1", required = true)
        @PathVariable Long id) {
        try {
            PartidaDTO partida = partidaServicio.reanudarPartida(id);
            return ResponseEntity.ok(partida);
        } catch (Exception e) {
            log.error("Error al reanudar partida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/mover")
    @Operation(summary = "Mover barco", description = "Mueve el barco aplicando aceleración (-1, 0, o +1) a cada componente de velocidad")
    @ApiResponse(responseCode = "200", description = "Barco movido exitosamente")
    public ResponseEntity<?> moverBarco(
        @Parameter(description = "ID de la partida", example = "1", required = true)
        @PathVariable Long id,
        @Parameter(description = "ID del barco a mover (omitido para modo single player)")
        @RequestParam(required = false) Long barcoId,
        @Parameter(description = "Aceleración en X (-1, 0, o +1)", required = true)
        @RequestParam Integer aceleracionX,
        @Parameter(description = "Aceleración en Y (-1, 0, o +1)", required = true)
        @RequestParam Integer aceleracionY) {
        try {
            PartidaDTO partida = partidaServicio.moverBarco(id, aceleracionX, aceleracionY, barcoId);
            return ResponseEntity.ok(partida);
        } catch (Exception e) {
            log.error("Error al mover barco: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "Unirse a partida", description = "Jugador con su barco se une a una partida activa")
    @ApiResponse(responseCode = "200", description = "Jugador unido exitosamente")
    public ResponseEntity<?> unirAPartida(
        @Parameter(description = "ID de la partida", required = true) @PathVariable Long id,
        @Parameter(description = "ID del jugador", required = true) @RequestParam Long jugadorId,
        @Parameter(description = "ID del barco", required = true) @RequestParam Long barcoId) {
        try {
            GameStateDTO state = partidaServicio.unirAPartida(id, jugadorId, barcoId);
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("El barco no pertenece")) {
                log.warn("Join rechazado - barco no pertenece al jugador: {}", msg);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new co.edu.javeriana.proyectoWeb.RegataOnline.dto.ErrorDTO("BARCO_INVALIDO: " + msg));
            }
            if (msg != null && msg.contains("ya está en uso")) {
                log.warn("Join rechazado - barco en uso: {}", msg);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new co.edu.javeriana.proyectoWeb.RegataOnline.dto.ErrorDTO("BARCO_EN_USO: " + msg));
            }
            log.error("Error al unir jugador a partida: {}", msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new co.edu.javeriana.proyectoWeb.RegataOnline.dto.ErrorDTO(msg != null ? msg : "Error desconocido al unirse"));
        }
    }

    @GetMapping("/{id}/estado")
    @Operation(summary = "Estado de partida", description = "Obtiene snapshot con todos los participantes y sus barcos")
    @ApiResponse(responseCode = "200", description = "Estado obtenido")
    public ResponseEntity<?> estadoPartida(@PathVariable Long id) {
        try {
            GameStateDTO state = partidaServicio.obtenerEstadoPartida(id);
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            String msg = e.getMessage();
            // Diferenciar logging según sea un 404 esperado (polling antes de crear/join) u otro conflicto real
            if (msg != null && msg.toLowerCase().contains("no encontrada")) {
                // Ruido esperado por el cliente cuando aún no existe la partida: bajar nivel a DEBUG
                log.debug("Partida no encontrada (esperado durante polling): {}", msg);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            } else {
                log.error("Error al obtener estado de partida (conflicto): {}", msg);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
            }
        }
    }
}

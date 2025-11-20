package co.edu.javeriana.proyectoWeb.RegataOnline.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.proyectoWeb.RegataOnline.dto.CrearPartidaRequest;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.PartidaDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.GameStateDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.mapper.PartidaMapper;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Barco;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Celda;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Jugador;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Mapa;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Partida;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.PartidaParticipante;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.BarcoRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.JugadorRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.MapaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.PartidaRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.PartidaParticipanteRepositorio;

@Service
public class PartidaServicio {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PartidaRepositorio partidaRepositorio;

    @Autowired
    private JugadorRepositorio jugadorRepositorio;

    @Autowired
    private MapaRepositorio mapaRepositorio;

    @Autowired
    private BarcoRepositorio barcoRepositorio;

    @Autowired
    private PartidaParticipanteRepositorio participanteRepositorio;

    
     //Crea una nueva partida
     
    @Transactional
    public PartidaDTO crearPartida(CrearPartidaRequest request) {
        log.info("Creando nueva partida para jugador {}", request.getJugadorId());

        // Validar que no exista una partida activa para este jugador
        Jugador jugador = jugadorRepositorio.findById(request.getJugadorId())
            .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + request.getJugadorId()));

        Optional<Partida> partidaExistente = partidaRepositorio.findByJugadorAndEstadoIn(
            jugador, 
            Arrays.asList("activa", "pausada")
        );

        if (partidaExistente.isPresent()) {
            throw new RuntimeException("El jugador ya tiene una partida activa. Debe finalizarla antes de crear una nueva.");
        }

        // Validar mapa
        Mapa mapa = mapaRepositorio.findById(request.getMapaId())
            .orElseThrow(() -> new RuntimeException("Mapa no encontrado con ID: " + request.getMapaId()));

        // Validar barco
        Barco barco = barcoRepositorio.findById(request.getBarcoId())
            .orElseThrow(() -> new RuntimeException("Barco no encontrado con ID: " + request.getBarcoId()));

        // Asegurar que el mismo barco no esté siendo usado en otra partida activa/pausada
        partidaRepositorio.findByBarcoAndEstadoIn(barco, Arrays.asList("activa", "pausada"))
            .ifPresent(p -> { throw new RuntimeException("El barco seleccionado ya está en uso en otra partida."); });

        // Validar que el barco pertenece al jugador
        if (barco.getJugador() != null && !barco.getJugador().getId().equals(jugador.getId())) {
            throw new RuntimeException("El barco no pertenece a este jugador");
        }

        // Buscar la celda de partida (tipo 'P') en el mapa
        Celda celdaPartida = mapa.getCeldas().stream()
            .filter(c -> "P".equals(c.getTipo()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("El mapa no tiene una celda de partida (tipo 'P')"));

        // Colocar el barco en la posición de partida con velocidad (0,0)
        barco.setPosicionX(celdaPartida.getPosicionX());
        barco.setPosicionY(celdaPartida.getPosicionY());
        barco.setVelocidadX(0);
        barco.setVelocidadY(0);
        barcoRepositorio.save(barco);

        log.info("Barco colocado en posición de partida: ({}, {})", 
            celdaPartida.getPosicionX(), celdaPartida.getPosicionY());

        // Crear partida
    Partida partida = new Partida(jugador, mapa, barco);
    partida = partidaRepositorio.save(partida);

    // Crear primer participante (host)
    PartidaParticipante participante = new PartidaParticipante(partida, jugador, barco);
    participanteRepositorio.save(participante);
    partida.agregarParticipante(participante);

        log.info("Partida creada exitosamente con ID: {}", partida.getId());
        return PartidaMapper.toDTO(partida);
    }

    /**
     * Busca una partida activa del jugador
     */
    public Optional<PartidaDTO> buscarPartidaActiva(Long jugadorId) {
        log.info("Buscando partida activa para jugador {}", jugadorId);

        Jugador jugador = jugadorRepositorio.findById(jugadorId)
            .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + jugadorId));

        return partidaRepositorio.findByJugadorAndEstadoIn(jugador, Arrays.asList("activa", "pausada"))
            .map(PartidaMapper::toDTO);
    }

    /**
     * Obtiene una partida por ID
     */
    public Optional<PartidaDTO> obtenerPartida(Long id) {
        return partidaRepositorio.findById(id)
            .map(PartidaMapper::toDTO);
    }

    /**
     * Obtiene snapshot del estado multijugador de la partida.
     */
    public GameStateDTO obtenerEstadoPartida(Long partidaId) {
        Partida partida = partidaRepositorio.findById(partidaId)
            .orElseThrow(() -> new RuntimeException("Partida no encontrada con ID: " + partidaId));

        GameStateDTO state = new GameStateDTO(partida.getId(), partida.getEstado(), partida.getHaLlegadoMeta());
        List<PartidaParticipante> participantes = participanteRepositorio.findByPartida(partida);
        for (PartidaParticipante p : participantes) {
            Barco b = p.getBarco();
            state.addParticipante(new GameStateDTO.ParticipantState(
                p.getJugador().getId(),
                p.getJugador().getEmail(),
                b.getId(),
                b.getNombre(),
                b.getPosicionX(),
                b.getPosicionY(),
                b.getVelocidadX(),
                b.getVelocidadY(),
                p.getEstado()
            ));
        }
        return state;
    }

    /**
     * Un jugador se une a una partida existente con un barco propio.
     */
    @Transactional
    public GameStateDTO unirAPartida(Long partidaId, Long jugadorId, Long barcoId) {
        Partida partida = partidaRepositorio.findById(partidaId)
            .orElseThrow(() -> new RuntimeException("Partida no encontrada con ID: " + partidaId));
        if (!"activa".equals(partida.getEstado())) {
            throw new RuntimeException("Solo se puede unir a partidas activas");
        }
        Jugador jugador = jugadorRepositorio.findById(jugadorId)
            .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + jugadorId));
        Barco barco = barcoRepositorio.findById(barcoId)
            .orElseThrow(() -> new RuntimeException("Barco no encontrado con ID: " + barcoId));

        // Validar que el barco pertenece al jugador
        if (barco.getJugador() != null && !barco.getJugador().getId().equals(jugador.getId())) {
            throw new RuntimeException("El barco no pertenece a este jugador");
        }

        // Validar que el barco no esté ya en otra partida activa/pausada
        if (participanteRepositorio.existsByBarcoAndPartida_EstadoIn(barco, Arrays.asList("activa", "pausada"))) {
            throw new RuntimeException("El barco ya está en uso en otra partida activa/pausada");
        }
        // También revisar campo legado de Partida (por si matches single player existentes)
        partidaRepositorio.findByBarcoAndEstadoIn(barco, Arrays.asList("activa", "pausada"))
            .ifPresent(p -> { throw new RuntimeException("El barco ya está en uso en otra partida"); });

        // Colocar barco en celda de partida si aún no tiene posición (usamos valores negativos como 'no inicializado')
        if (barco.getPosicionX() < 0 || barco.getPosicionY() < 0) {
            Celda celdaPartida = partida.getMapa().getCeldas().stream()
                .filter(c -> "P".equals(c.getTipo()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El mapa no tiene una celda de partida (tipo 'P')"));
            barco.setPosicionX(celdaPartida.getPosicionX());
            barco.setPosicionY(celdaPartida.getPosicionY());
            barco.setVelocidadX(0);
            barco.setVelocidadY(0);
            barcoRepositorio.save(barco);
        }

        PartidaParticipante nuevo = new PartidaParticipante(partida, jugador, barco);
        participanteRepositorio.save(nuevo);
        partida.agregarParticipante(nuevo);

        return obtenerEstadoPartida(partida.getId());
    }

    /**
     * Lista todas las partidas de un jugador
     */
    public List<PartidaDTO> listarPartidasJugador(Long jugadorId) {
        Jugador jugador = jugadorRepositorio.findById(jugadorId)
            .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + jugadorId));

        return partidaRepositorio.findByJugador(jugador).stream()
            .map(PartidaMapper::toDTO)
            .toList();
    }

    /**
     * Pausa una partida
     */
    @Transactional
    public PartidaDTO pausarPartida(Long id) {
        log.info("Pausando partida {}", id);

        Partida partida = partidaRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Partida no encontrada con ID: " + id));

        partida.setEstado("pausada");
        partida = partidaRepositorio.save(partida);

        return PartidaMapper.toDTO(partida);
    }

    /**
     * Finaliza una partida
     */
    @Transactional
    public PartidaDTO finalizarPartida(Long id) {
        log.info("Finalizando partida {}", id);

        Partida partida = partidaRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Partida no encontrada con ID: " + id));

        partida.setEstado("terminada");
        partida = partidaRepositorio.save(partida);

        return PartidaMapper.toDTO(partida);
    }

    /**
     * Reanuda una partida pausada, dejándola en estado 'activa'
     */
    @Transactional
    public PartidaDTO reanudarPartida(Long id) {
        log.info("Reanudando partida {}", id);

        Partida partida = partidaRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Partida no encontrada con ID: " + id));

        if ("terminada".equals(partida.getEstado())) {
            throw new RuntimeException("No se puede reanudar una partida terminada");
        }

        // Si ya está activa, simplemente devolver
        if (!"activa".equals(partida.getEstado())) {
            partida.setEstado("activa");
            partida = partidaRepositorio.save(partida);
        }

        return PartidaMapper.toDTO(partida);
    }

    /**
     * Mueve el barco aplicando cambios de aceleración a la velocidad
     * @param partidaId ID de la partida
     * @param aceleracionX Cambio en velocidadX (-1, 0, o +1)
     * @param aceleracionY Cambio en velocidadY (-1, 0, o +1)
     */
    @Transactional
    public PartidaDTO moverBarco(Long partidaId, Integer aceleracionX, Integer aceleracionY, Long barcoId) {
        log.info("Moviendo barco en partida {} con aceleración ({}, {})", partidaId, aceleracionX, aceleracionY);

        // Validar aceleraciones
        if (aceleracionX < -1 || aceleracionX > 1 || aceleracionY < -1 || aceleracionY > 1) {
            throw new RuntimeException("La aceleración solo puede ser -1, 0, o +1");
        }

        Partida partida = partidaRepositorio.findById(partidaId)
            .orElseThrow(() -> new RuntimeException("Partida no encontrada con ID: " + partidaId));

        if (!partida.getEstado().equals("activa")) {
            throw new RuntimeException("La partida no está activa");
        }

        Barco barco;
        if (barcoId != null) {
            // Buscar participante específico
            barco = participanteRepositorio.findByPartida(partida).stream()
                .map(PartidaParticipante::getBarco)
                .filter(b -> b.getId().equals(barcoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El barco indicado no está en esta partida"));
        } else {
            // Modo legado single-player
            barco = partida.getBarco();
        }
        Mapa mapa = partida.getMapa();

        log.info("=== ESTADO ANTES DEL MOVIMIENTO ===");
        log.info("Mapa: {}x{} con {} celdas", mapa.getFilas(), mapa.getColumnas(), 
            mapa.getCeldas() != null ? mapa.getCeldas().size() : 0);
        log.info("Posición actual del barco: ({}, {})", barco.getPosicionX(), barco.getPosicionY());
        log.info("Velocidad actual del barco: ({}, {})", barco.getVelocidadX(), barco.getVelocidadY());
        log.info("Aceleración recibida: ({}, {})", aceleracionX, aceleracionY);

        // 1. Actualizar velocidad con la aceleración
        int nuevaVelocidadX = barco.getVelocidadX() + aceleracionX;
        int nuevaVelocidadY = barco.getVelocidadY() + aceleracionY;

        log.info("Nueva velocidad calculada: ({}, {})", nuevaVelocidadX, nuevaVelocidadY);

        // 2. Calcular nueva posición: posición_actual + velocidad_nueva
        int nuevaPosicionX = barco.getPosicionX() + nuevaVelocidadX;
        int nuevaPosicionY = barco.getPosicionY() + nuevaVelocidadY;

        log.info("Nueva posición calculada: ({}, {})", nuevaPosicionX, nuevaPosicionY);

        // 3. Validar que la nueva posición esté dentro del mapa
        if (nuevaPosicionX < 0 || nuevaPosicionX >= mapa.getColumnas() || 
            nuevaPosicionY < 0 || nuevaPosicionY >= mapa.getFilas()) {
            log.warn("Movimiento inválido: fuera del mapa");
            throw new RuntimeException(" No puedes moverte ahí. El movimiento sale del mapa. Nueva posición: (" + 
                nuevaPosicionX + ", " + nuevaPosicionY + ")");
        }

        // 4. Validar trayectoria: revisar todas las celdas intermedias
        List<int[]> trayectoria = calcularTrayectoria(
            barco.getPosicionX(), barco.getPosicionY(),
            nuevaPosicionX, nuevaPosicionY
        );

        for (int[] punto : trayectoria) {
            int px = punto[0];
            int py = punto[1];

            // Verificar si está fuera del mapa
            if (px < 0 || px >= mapa.getColumnas() || py < 0 || py >= mapa.getFilas()) {
                log.warn("Movimiento inválido: Trayectoria sale del mapa en ({}, {})", px, py);
                throw new RuntimeException("No puedes moverte ahí. La trayectoria cruza el límite del mapa en (" + px + ", " + py + ")");
            }

            // Verificar si atraviesa una pared
            Celda celda = mapa.getCeldas().stream()
                .filter(c -> c.getPosicionX() == px && c.getPosicionY() == py)
                .findFirst()
                .orElse(null);

            if (celda != null && "x".equals(celda.getTipo())) {
                log.warn("Movimiento inválido: Colisionó con pared en ({}, {})", px, py);
                throw new RuntimeException("💥 ¡Chocaste con una pared en (" + px + ", " + py + ")! Elige otra dirección.");
            }
        }

        // 5. Obtener celda de destino final
        Celda celdaDestino = mapa.getCeldas().stream()
            .filter(c -> c.getPosicionX() == nuevaPosicionX && c.getPosicionY() == nuevaPosicionY)
            .findFirst()
            .orElse(null);

        // 6. Verificar colisión con otros barcos (evitar misma celda final)
        List<Barco> otrosBarcos = participanteRepositorio.findByPartida(partida).stream()
            .map(PartidaParticipante::getBarco)
            .filter(b -> !b.getId().equals(barco.getId()))
            .toList();
        for (Barco ob : otrosBarcos) {
            if (ob.getPosicionX() == nuevaPosicionX && ob.getPosicionY() == nuevaPosicionY) {
                throw new RuntimeException("Colisión con otro barco en la celda destino (" + nuevaPosicionX + ", " + nuevaPosicionY + ")");
            }
        }

        // 7. Actualizar velocidad y posición del barco
        barco.setVelocidadX(nuevaVelocidadX);
        barco.setVelocidadY(nuevaVelocidadY);
        barco.setPosicionX(nuevaPosicionX);
        barco.setPosicionY(nuevaPosicionY);
        barcoRepositorio.save(barco);

        // 8. Verificar si llegó a la meta
        if (celdaDestino != null && "M".equals(celdaDestino.getTipo())) {
            partida.setHaLlegadoMeta(true);
            partida.setEstado("terminada");
            log.info("¡Barco llegó a la meta!");
        }

        // 9. Incrementar contador de movimientos (global de la partida)
        partida.setMovimientos(partida.getMovimientos() + 1);
        partida = partidaRepositorio.save(partida);

        log.info("Barco movido exitosamente. Velocidad: ({}, {}), Posición: ({}, {}), Movimientos: {}", 
            nuevaVelocidadX, nuevaVelocidadY, nuevaPosicionX, nuevaPosicionY, partida.getMovimientos());
        
        return PartidaMapper.toDTO(partida);
    }

    /**
     * Calcula la trayectoria del barco usando algoritmo de Bresenham
     * para verificar todas las celdas intermedias entre origen y destino
     */
    private List<int[]> calcularTrayectoria(int x0, int y0, int x1, int y1) {
        List<int[]> trayectoria = new java.util.ArrayList<>();
        
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        
        int x = x0;
        int y = y0;
        
        while (true) {
            // No incluir la posición inicial (ya está validada)
            if (!(x == x0 && y == y0)) {
                trayectoria.add(new int[]{x, y});
            }
            
            if (x == x1 && y == y1) {
                break;
            }
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        
        log.info("Trayectoria calculada: {} celdas intermedias", trayectoria.size());
        return trayectoria;
    }
}

package edu.tuuni.regata.service;

import edu.tuuni.regata.domain.*;
import edu.tuuni.regata.repo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameSessionService {
    private final GameSessionRepository sessionRepo;
    private final PlayerSessionRepository playerSessionRepo;
    private final GameMapService mapService;
    private final PlayerService playerService;
    private final BoatService boatService;

    public GameSessionService(GameSessionRepository sessionRepo, PlayerSessionRepository playerSessionRepo,
                             GameMapService mapService, PlayerService playerService, BoatService boatService) {
        this.sessionRepo = sessionRepo;
        this.playerSessionRepo = playerSessionRepo;
        this.mapService = mapService;
        this.playerService = playerService;
        this.boatService = boatService;
    }

    public List<GameSession> getActiveSessions() {
        return sessionRepo.findByActiveTrue();
    }

    public GameSession findById(Long id) {
        return sessionRepo.findById(id).orElse(null);
    }

    public GameSession createSession(String name, Long mapId) {
        GameSession session = new GameSession();
        session.setName(name);
        session.setGameMap(mapService.findById(mapId));
        session.setStartTime(LocalDateTime.now());
        session.setActive(true);
        return sessionRepo.save(session);
    }

    public void addPlayerToSession(Long sessionId, Long playerId, Long boatId) {
        GameSession session = findById(sessionId);
        Player player = playerService.findById(playerId);
        Boat boat = boatService.findById(boatId);
        
        if (session == null || player == null || boat == null) {
            throw new RuntimeException("Sesión, jugador o barco no encontrado");
        }

        // Reset boat position to start
        boat.setX(0);
        boat.setY(0);
        boat.setVx(0);
        boat.setVy(0);
        boatService.save(boat);

        PlayerSession playerSession = new PlayerSession();
        playerSession.setGameSession(session);
        playerSession.setPlayer(player);
        playerSession.setBoat(boat);
        playerSession.setFinished(false);
        
        playerSessionRepo.save(playerSession);
    }

    public void movePlayerBoat(Long sessionId, Long playerId, String direction) {
        GameSession session = findById(sessionId);
        if (session == null || !session.isActive()) {
            throw new RuntimeException("Sesión no válida");
        }

        // Find player's boat in this session
        PlayerSession playerSession = session.getPlayerSessions().stream()
            .filter(ps -> ps.getPlayer().getId().equals(playerId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Jugador no encontrado en la sesión"));

        Boat boat = playerSession.getBoat();
        GameMap map = session.getGameMap();
        
        int newX = boat.getX();
        int newY = boat.getY();
        
        // Calculate new position based on direction
        switch (direction.toLowerCase()) {
            case "up": newY = Math.max(0, boat.getY() - 1); break;
            case "down": newY = Math.min(map.getHeight() - 1, boat.getY() + 1); break;
            case "left": newX = Math.max(0, boat.getX() - 1); break;
            case "right": newX = Math.min(map.getWidth() - 1, boat.getX() + 1); break;
        }
        
        // Check collision with obstacles
        if (isValidPosition(map, newX, newY)) {
            boat.setX(newX);
            boat.setY(newY);
            
            // Check if reached finish line
            if (isFinishPosition(map, newX, newY) && !playerSession.isFinished()) {
                playerSession.setFinished(true);
                int finishPosition = (int) session.getPlayerSessions().stream()
                    .filter(PlayerSession::isFinished).count();
                playerSession.setFinishPosition(finishPosition);
                playerSessionRepo.save(playerSession);
            }
            
            boatService.save(boat);
        } else {
            throw new RuntimeException("Movimiento no válido - hay un obstáculo");
        }
    }
    
    private boolean isValidPosition(GameMap map, int x, int y) {
        String[] rows = map.getGrid().split("\n");
        if (y >= 0 && y < rows.length && x >= 0 && x < rows[y].length()) {
            char cell = rows[y].charAt(x);
            return cell != 'X'; // X = wall/obstacle
        }
        return false;
    }
    
    private boolean isFinishPosition(GameMap map, int x, int y) {
        String[] rows = map.getGrid().split("\n");
        if (y >= 0 && y < rows.length && x >= 0 && x < rows[y].length()) {
            char cell = rows[y].charAt(x);
            return cell == 'M'; // M = meta/finish
        }
        return false;
    }
}
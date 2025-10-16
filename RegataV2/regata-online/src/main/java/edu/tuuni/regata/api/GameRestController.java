package edu.tuuni.regata.api;

import edu.tuuni.regata.domain.*;
import edu.tuuni.regata.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameRestController {
    private final PlayerService playerService;
    private final BoatService boatService;
    private final GameMapService mapService;

    public GameRestController(PlayerService playerService, BoatService boatService, GameMapService mapService) {
        this.playerService = playerService;
        this.boatService = boatService;
        this.mapService = mapService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startGame(@RequestBody Map<String, Long> request) {
        Long playerId = request.get("playerId");
        Long boatId = request.get("boatId");
        Long mapId = request.get("mapId");

        Player player = playerService.findById(playerId);
        Boat boat = boatService.findById(boatId);
        GameMap gameMap = mapService.findById(mapId);

        if (player == null || boat == null || gameMap == null) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("player", player);
        response.put("boat", boat);
        response.put("gameMap", gameMap);
        response.put("status", "started");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/move")
    public ResponseEntity<Map<String, Object>> moveBoat(@RequestBody Map<String, Object> request) {
        Long boatId = Long.valueOf(request.get("boatId").toString());
        String direction = (String) request.get("direction");
        Integer currentX = (Integer) request.get("x");
        Integer currentY = (Integer) request.get("y");

        Boat boat = boatService.findById(boatId);
        if (boat == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("boat", boat);
        response.put("x", currentX);
        response.put("y", currentY);
        response.put("direction", direction);
        response.put("status", "moved");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/state")
    public ResponseEntity<Map<String, Object>> getGameState(
            @RequestParam Long playerId,
            @RequestParam Long boatId,
            @RequestParam Long mapId) {
        
        Player player = playerService.findById(playerId);
        Boat boat = boatService.findById(boatId);
        GameMap gameMap = mapService.findById(mapId);

        if (player == null || boat == null || gameMap == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("player", player);
        response.put("boat", boat);
        response.put("gameMap", gameMap);

        return ResponseEntity.ok(response);
    }
}

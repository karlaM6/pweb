package edu.tuuni.regata.api;

import edu.tuuni.regata.domain.Player;
import edu.tuuni.regata.domain.Role;
import edu.tuuni.regata.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthRestController {
    private final PlayerService playerService;

    public AuthRestController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        
        // Find player by username
        Player player = playerService.findAll().stream()
            .filter(p -> p.getUsername().equals(username))
            .findFirst()
            .orElse(null);

        if (player == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no encontrado"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", player.getId());
        response.put("username", player.getUsername());
        response.put("displayName", player.getDisplayName());
        response.put("role", player.getRole().name());
        response.put("authenticated", true);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        Player player = playerService.findById(userId);
        if (player == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", player.getId());
        response.put("username", player.getUsername());
        response.put("displayName", player.getDisplayName());
        response.put("role", player.getRole().name());
        response.put("authenticated", true);

        return ResponseEntity.ok(response);
    }
}

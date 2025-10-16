package edu.tuuni.regata.api;

import edu.tuuni.regata.domain.GameMap;
import edu.tuuni.regata.service.GameMapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maps")
@CrossOrigin(origins = "*")
public class GameMapRestController {
    private final GameMapService mapService;

    public GameMapRestController(GameMapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping
    public List<GameMap> getAllMaps() {
        return mapService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameMap> getMap(@PathVariable Long id) {
        GameMap map = mapService.findById(id);
        return map != null ? ResponseEntity.ok(map) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public GameMap createMap(@RequestBody GameMap map) {
        return mapService.save(map);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameMap> updateMap(@PathVariable Long id, @RequestBody GameMap map) {
        GameMap existing = mapService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        map.setId(id);
        return ResponseEntity.ok(mapService.save(map));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMap(@PathVariable Long id) {
        mapService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

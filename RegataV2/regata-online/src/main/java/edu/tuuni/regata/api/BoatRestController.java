package edu.tuuni.regata.api;

import edu.tuuni.regata.domain.Boat;
import edu.tuuni.regata.service.BoatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boats")
@CrossOrigin(origins = "*")
public class BoatRestController {
    private final BoatService boatService;

    public BoatRestController(BoatService boatService) {
        this.boatService = boatService;
    }

    @GetMapping
    public List<Boat> getAllBoats() {
        return boatService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boat> getBoat(@PathVariable Long id) {
        Boat boat = boatService.findById(id);
        return boat != null ? ResponseEntity.ok(boat) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Boat createBoat(@RequestBody Boat boat) {
        return boatService.save(boat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boat> updateBoat(@PathVariable Long id, @RequestBody Boat boat) {
        Boat existing = boatService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        boat.setId(id);
        return ResponseEntity.ok(boatService.save(boat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoat(@PathVariable Long id) {
        boatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

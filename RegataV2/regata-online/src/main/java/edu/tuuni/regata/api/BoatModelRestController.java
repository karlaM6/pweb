package edu.tuuni.regata.api;

import edu.tuuni.regata.domain.BoatModel;
import edu.tuuni.regata.service.BoatModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@CrossOrigin(origins = "*")
public class BoatModelRestController {
    private final BoatModelService modelService;

    public BoatModelRestController(BoatModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping
    public List<BoatModel> getAllModels() {
        return modelService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoatModel> getModel(@PathVariable Long id) {
        BoatModel model = modelService.findById(id);
        return model != null ? ResponseEntity.ok(model) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public BoatModel createModel(@RequestBody BoatModel model) {
        return modelService.save(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoatModel> updateModel(@PathVariable Long id, @RequestBody BoatModel model) {
        BoatModel existing = modelService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        model.setId(id);
        return ResponseEntity.ok(modelService.save(model));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        modelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package edu.tuuni.regata.web;


import edu.tuuni.regata.domain.GameMap;
import edu.tuuni.regata.service.GameMapService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/maps")
public class GameMapController {
    private final GameMapService service;

    public GameMapController(GameMapService service) {
        this.service = service;
    }

    @GetMapping 
    String list(Model model) { 
        model.addAttribute("maps", service.findAll()); 
        return "maps/list"; 
    }

    @GetMapping("/new") 
    String createForm(Model model) { 
        model.addAttribute("gameMap", new GameMap()); 
        return "maps/form"; 
    }

    @PostMapping 
    String create(@Valid @ModelAttribute GameMap gameMap, BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "maps/form"; 
        service.save(gameMap); 
        ra.addFlashAttribute("msg", "Mapa guardado"); 
        return "redirect:/maps"; 
    }

    @GetMapping("/{id}")
    String show(@PathVariable Long id, Model model) {
        model.addAttribute("gameMap", service.findById(id));
        return "maps/show";
    }

    @GetMapping("/{id}/edit") 
    String editForm(@PathVariable Long id, Model model) { 
        model.addAttribute("gameMap", service.findById(id)); 
        return "maps/form"; 
    }

    @PostMapping("/{id}") 
    String update(@PathVariable Long id, @Valid @ModelAttribute GameMap gameMap, BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "maps/form"; 
        gameMap.setId(id); 
        service.save(gameMap); 
        ra.addFlashAttribute("msg", "Mapa actualizado"); 
        return "redirect:/maps"; 
    }

    @PostMapping("/{id}/delete") 
    String delete(@PathVariable Long id, RedirectAttributes ra) { 
        service.delete(id); 
        ra.addFlashAttribute("msg", "Mapa eliminado");
        return "redirect:/maps"; 
    }
}
package edu.tuuni.regata.web;


import edu.tuuni.regata.domain.Boat;
import edu.tuuni.regata.repo.BoatModelRepository;
import edu.tuuni.regata.repo.PlayerRepository;
import edu.tuuni.regata.service.BoatService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/boats")
public class BoatController {
    private final BoatService service;
    private final PlayerRepository players;
    private final BoatModelRepository models;


    public BoatController(BoatService service, PlayerRepository players, BoatModelRepository models){
        this.service = service; this.players = players; this.models = models;
    }


    @GetMapping 
    String list(Model model){ 
        model.addAttribute("boats", service.findAll()); 
        return "boats/list"; 
    }


    @GetMapping("/new") 
    String createForm(Model model){
        model.addAttribute("boat", new Boat());
        model.addAttribute("players", players.findAll());
        model.addAttribute("models", models.findAll());
        return "boats/form";
    }


    @PostMapping 
    String create(@Valid @ModelAttribute Boat boat, BindingResult br, Model model, RedirectAttributes ra){
        if (br.hasErrors()){
            model.addAttribute("players", players.findAll());
            model.addAttribute("models", models.findAll());
            return "boats/form";
        }
        service.save(boat);
        ra.addFlashAttribute("msg", "Barco guardado");
        return "redirect:/boats";
    }


    @GetMapping("/{id}")
    String show(@PathVariable Long id, Model model){
        model.addAttribute("boat", service.findById(id));
        return "boats/show";
    }

    @GetMapping("/{id}/edit") 
    String editForm(@PathVariable Long id, Model model){
        model.addAttribute("boat", service.findById(id));
        model.addAttribute("players", players.findAll());
        model.addAttribute("models", models.findAll());
        return "boats/form";
    }


    @PostMapping("/{id}") 
    String update(@PathVariable Long id, @Valid @ModelAttribute Boat boat, BindingResult br, Model model, RedirectAttributes ra){
        if (br.hasErrors()){
            model.addAttribute("players", players.findAll());
            model.addAttribute("models", models.findAll());
            return "boats/form";
        }
        boat.setId(id);
        service.save(boat);
        ra.addFlashAttribute("msg", "Barco actualizado");
        return "redirect:/boats";
    }


    @PostMapping("/{id}/delete") 
    String delete(@PathVariable Long id, RedirectAttributes ra){ 
        service.delete(id); 
        ra.addFlashAttribute("msg", "Barco eliminado");
        return "redirect:/boats"; 
    }
}
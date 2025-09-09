package edu.tuuni.regata.web;


import edu.tuuni.regata.domain.Player;
import edu.tuuni.regata.repo.BoatRepository;
import edu.tuuni.regata.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService service;
    private final BoatRepository boats;
    public PlayerController(PlayerService service, BoatRepository boats) {
        this.service = service; this.boats = boats; }


    @GetMapping String list(Model model){ model.addAttribute("players", service.findAll()); return "players/list"; }
    @GetMapping("/new") String createForm(Model model){ model.addAttribute("player", new Player()); return "players/form"; }
    @PostMapping String create(@Valid @ModelAttribute Player player, BindingResult br, RedirectAttributes ra){
        if (br.hasErrors()) return "players/form"; service.save(player); ra.addFlashAttribute("msg","Jugador guardado"); return "redirect:/players"; }


    @GetMapping("/{id}") String show(@PathVariable Long id, Model model){ model.addAttribute("player", service.findById(id)); return "players/show"; }
    @GetMapping("/{id}/edit") String editForm(@PathVariable Long id, Model model){ model.addAttribute("player", service.findById(id)); return "players/form"; }
    @PostMapping("/{id}") String update(@PathVariable Long id, @Valid @ModelAttribute Player player, BindingResult br, RedirectAttributes ra){
        if (br.hasErrors()) return "players/form"; player.setId(id); service.save(player); ra.addFlashAttribute("msg","Jugador actualizado"); return "redirect:/players"; }
    @PostMapping("/{id}/delete") String delete(@PathVariable Long id, RedirectAttributes ra){
        long inUse = boats.countByOwner_Id(id);
        if (inUse>0){ ra.addFlashAttribute("error","No se puede eliminar: jugador con "+inUse+" barco(s)."); return "redirect:/players"; }
        service.delete(id); ra.addFlashAttribute("msg","Jugador eliminado"); return "redirect:/players"; }
}
package edu.tuuni.regata.web;


import edu.tuuni.regata.domain.BoatModel;
import edu.tuuni.regata.repo.BoatRepository;
import edu.tuuni.regata.service.BoatModelService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/models")
public class BoatModelController {
    private final BoatModelService service; private final BoatRepository boats;
    public BoatModelController(BoatModelService service, BoatRepository boats){ this.service=service; this.boats=boats; }


    @GetMapping String list(Model model){ model.addAttribute("models", service.findAll()); return "models/list"; }
    @GetMapping("/new") String createForm(Model model){ model.addAttribute("modelDto", new BoatModel()); return "models/form"; }
    @PostMapping String create(@Valid @ModelAttribute("modelDto") BoatModel m, BindingResult br, RedirectAttributes ra){ if(br.hasErrors()) return "models/form"; service.save(m); ra.addFlashAttribute("msg","Modelo guardado"); return "redirect:/models"; }


    @GetMapping("/{id}") String show(@PathVariable Long id, Model model){ model.addAttribute("modelDto", service.findById(id)); return "models/show"; }
    @GetMapping("/{id}/edit") String editForm(@PathVariable Long id, Model model){ model.addAttribute("modelDto", service.findById(id)); return "models/form"; }
    @PostMapping("/{id}") String update(@PathVariable Long id, @Valid @ModelAttribute("modelDto") BoatModel m, BindingResult br, RedirectAttributes ra){ if(br.hasErrors()) return "models/form"; m.setId(id); service.save(m); ra.addFlashAttribute("msg","Modelo actualizado"); return "redirect:/models"; }
    @PostMapping("/{id}/delete") String delete(@PathVariable Long id, RedirectAttributes ra){
        long inUse = boats.countByModel_Id(id);
        if (inUse>0){ ra.addFlashAttribute("error","No se puede eliminar: modelo en uso por "+inUse+" barco(s)."); return "redirect:/models"; }
        service.delete(id); ra.addFlashAttribute("msg","Modelo eliminado"); return "redirect:/models"; }
}
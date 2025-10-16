package edu.tuuni.regata.web;

import edu.tuuni.regata.domain.*;
import edu.tuuni.regata.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/game")
public class GameController {
    private final PlayerService playerService;
    private final BoatService boatService;
    private final GameMapService mapService;

    public GameController(PlayerService playerService, BoatService boatService, GameMapService mapService) {
        this.playerService = playerService;
        this.boatService = boatService;
        this.mapService = mapService;
    }

    @GetMapping
    public String showGame(Model model) {
        model.addAttribute("players", playerService.findAll());
        model.addAttribute("boats", boatService.findAll());
        model.addAttribute("maps", mapService.findAll());
        
        // Add the demo map for the game
        if (!mapService.findAll().isEmpty()) {
            model.addAttribute("gameMap", mapService.findAll().get(0));
        }
        return "game/play";
    }

    @PostMapping("/start")
    public String startRace(@RequestParam Long playerId, @RequestParam Long boatId, @RequestParam Long mapId) {
        // Redirect to the actual game board with parameters
        return "redirect:/game/board?playerId=" + playerId + "&boatId=" + boatId + "&mapId=" + mapId;
    }

    @GetMapping("/board")
    public String showBoard(@RequestParam Long playerId, @RequestParam Long boatId, @RequestParam Long mapId, Model model) {
        // Load the selected entities
        Player player = playerService.findById(playerId);
        Boat boat = boatService.findById(boatId);
        GameMap gameMap = mapService.findById(mapId);
        
        model.addAttribute("player", player);
        model.addAttribute("boat", boat);
        model.addAttribute("gameMap", gameMap);
        
        return "game/board";
    }
}
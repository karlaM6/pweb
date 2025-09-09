package edu.tuuni.regata.service;


import edu.tuuni.regata.domain.Player;
import edu.tuuni.regata.repo.PlayerRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class PlayerService {
    private final PlayerRepository repo;
    public PlayerService(PlayerRepository repo) { this.repo = repo; }
    public List<Player> findAll() { return repo.findAll(); }
    public Player findById(Long id) { return repo.findById(id).orElseThrow(); }
    public Player save(Player p) { return repo.save(p); }
    public void delete(Long id) { repo.deleteById(id); }
}
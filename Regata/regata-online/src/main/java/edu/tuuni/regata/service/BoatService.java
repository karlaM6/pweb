package edu.tuuni.regata.service;


import edu.tuuni.regata.domain.Boat;
import edu.tuuni.regata.repo.BoatRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class BoatService {
    private final BoatRepository repo;
    public BoatService(BoatRepository repo) { this.repo = repo; }
    public List<Boat> findAll() { return repo.findAll(); }
    public Boat findById(Long id) { return repo.findById(id).orElseThrow(); }
    public Boat save(Boat b) { return repo.save(b); }
    public void delete(Long id) { repo.deleteById(id); }
}
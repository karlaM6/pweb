package edu.tuuni.regata.service;


import edu.tuuni.regata.domain.BoatModel;
import edu.tuuni.regata.repo.BoatModelRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class BoatModelService {
    private final BoatModelRepository repo;
    public BoatModelService(BoatModelRepository repo) { this.repo = repo; }
    public List<BoatModel> findAll() { return repo.findAll(); }
    public BoatModel findById(Long id) { return repo.findById(id).orElseThrow(); }
    public BoatModel save(BoatModel m) { return repo.save(m); }
    public void delete(Long id) { repo.deleteById(id); }
}
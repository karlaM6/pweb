package edu.tuuni.regata.service;


import edu.tuuni.regata.domain.GameMap;
import edu.tuuni.regata.repo.GameMapRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GameMapService {
    private final GameMapRepository repository;

    public GameMapService(GameMapRepository repository) {
        this.repository = repository;
    }

    public List<GameMap> findAll() {
        return repository.findAll();
    }

    public GameMap findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public GameMap save(GameMap gameMap) {
        return repository.save(gameMap);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
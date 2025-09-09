package edu.tuuni.regata.repo;


import edu.tuuni.regata.domain.GameMap;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GameMapRepository extends JpaRepository<GameMap, Long> { }
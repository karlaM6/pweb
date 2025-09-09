package edu.tuuni.regata.repo;


import edu.tuuni.regata.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlayerRepository extends JpaRepository<Player, Long> { }
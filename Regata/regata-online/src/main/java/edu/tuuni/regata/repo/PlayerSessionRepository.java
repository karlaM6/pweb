package edu.tuuni.regata.repo;

import edu.tuuni.regata.domain.PlayerSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerSessionRepository extends JpaRepository<PlayerSession, Long> {
}
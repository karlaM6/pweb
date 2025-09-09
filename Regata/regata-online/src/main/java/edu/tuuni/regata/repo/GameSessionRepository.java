package edu.tuuni.regata.repo;

import edu.tuuni.regata.domain.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByActiveTrue();
}
package edu.tuuni.regata.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GameSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private LocalDateTime startTime;
    private boolean active;
    
    @ManyToOne
    private GameMap gameMap;
    
    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    private List<PlayerSession> playerSessions = new ArrayList<>();
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public GameMap getGameMap() { return gameMap; }
    public void setGameMap(GameMap gameMap) { this.gameMap = gameMap; }
    public List<PlayerSession> getPlayerSessions() { return playerSessions; }
    public void setPlayerSessions(List<PlayerSession> playerSessions) { this.playerSessions = playerSessions; }
}
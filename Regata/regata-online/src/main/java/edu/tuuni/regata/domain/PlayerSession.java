package edu.tuuni.regata.domain;

import jakarta.persistence.*;

@Entity
public class PlayerSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private GameSession gameSession;
    
    @ManyToOne
    private Player player;
    
    @ManyToOne
    private Boat boat;
    
    private boolean finished;
    private int finishPosition;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GameSession getGameSession() { return gameSession; }
    public void setGameSession(GameSession gameSession) { this.gameSession = gameSession; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public Boat getBoat() { return boat; }
    public void setBoat(Boat boat) { this.boat = boat; }
    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }
    public int getFinishPosition() { return finishPosition; }
    public void setFinishPosition(int finishPosition) { this.finishPosition = finishPosition; }
}
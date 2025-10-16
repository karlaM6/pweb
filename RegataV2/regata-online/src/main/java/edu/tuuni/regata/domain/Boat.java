package edu.tuuni.regata.domain;


import jakarta.persistence.*;


@Entity
public class Boat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false)
    private BoatModel model;


    @ManyToOne(optional = false)
    private Player owner;


    private int vx; // velocidad x
    private int vy; // velocidad y
    private int x; // posición x
    private int y; // posición y


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BoatModel getModel() { return model; }
    public void setModel(BoatModel model) { this.model = model; }
    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }
    public int getVx() { return vx; }
    public void setVx(int vx) { this.vx = vx; }
    public int getVy() { return vy; }
    public void setVy(int vy) { this.vy = vy; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}
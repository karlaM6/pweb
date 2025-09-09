package edu.tuuni.regata.domain;


import jakarta.persistence.*;


@Entity
public class GameMap {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private int width;
    private int height;


    @Lob
    private String grid; // líneas separadas por \n con ' ' agua, 'X' pared, 'P' partida, 'M' meta


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public String getGrid() { return grid; }
    public void setGrid(String grid) { this.grid = grid; }
}
package edu.tuuni.regata.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


@Entity
public class BoatModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank
    @Column(unique = true)
    private String name;


    @NotBlank
    private String colorHex; // p.ej. "#FF0000"


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
}
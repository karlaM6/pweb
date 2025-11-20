package co.edu.javeriana.proyectoWeb.RegataOnline.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa un participante dentro de una partida multijugador.
 * Mantiene la relación Jugador-Barco-Partida. La posición y velocidad
 * siguen guardándose en el Barco para reutilizar lógica existente.
 */
@Entity
@Table(name = "partida_participante")
public class PartidaParticipante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    @ManyToOne
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;

    @ManyToOne
    @JoinColumn(name = "barco_id", nullable = false)
    private Barco barco;

    // Estado particular del participante dentro de la partida (ej: activo, eliminado)
    private String estado = "activo";

    public PartidaParticipante() {}

    public PartidaParticipante(Partida partida, Jugador jugador, Barco barco) {
        this.partida = partida;
        this.jugador = jugador;
        this.barco = barco;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Partida getPartida() { return partida; }
    public void setPartida(Partida partida) { this.partida = partida; }

    public Jugador getJugador() { return jugador; }
    public void setJugador(Jugador jugador) { this.jugador = jugador; }

    public Barco getBarco() { return barco; }
    public void setBarco(Barco barco) { this.barco = barco; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

package co.edu.javeriana.proyectoWeb.RegataOnline.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PartidaDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("jugadorId")
    private Long jugadorId;

    @JsonProperty("jugadorNombre")
    private String jugadorNombre;

    @JsonProperty("mapaId")
    private Long mapaId;

    @JsonProperty("mapaFilas")
    private Integer mapaFilas;

    @JsonProperty("mapaColumnas")
    private Integer mapaColumnas;

    @JsonProperty("barcoId")
    private Long barcoId;

    @JsonProperty("barcoNombre")
    private String barcoNombre;

    @JsonProperty("barcoPosicionX")
    private Integer barcoPosicionX;

    @JsonProperty("barcoPosicionY")
    private Integer barcoPosicionY;

    @JsonProperty("barcoVelocidadX")
    private Integer barcoVelocidadX;

    @JsonProperty("barcoVelocidadY")
    private Integer barcoVelocidadY;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("fechaInicio")
    private LocalDateTime fechaInicio;

    @JsonProperty("fechaUltimaJugada")
    private LocalDateTime fechaUltimaJugada;

    @JsonProperty("movimientos")
    private Integer movimientos;

    @JsonProperty("haLlegadoMeta")
    private Boolean haLlegadoMeta;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getJugadorNombre() {
        return jugadorNombre;
    }

    public void setJugadorNombre(String jugadorNombre) {
        this.jugadorNombre = jugadorNombre;
    }

    public Long getMapaId() {
        return mapaId;
    }

    public void setMapaId(Long mapaId) {
        this.mapaId = mapaId;
    }

    public Integer getMapaFilas() {
        return mapaFilas;
    }

    public void setMapaFilas(Integer mapaFilas) {
        this.mapaFilas = mapaFilas;
    }

    public Integer getMapaColumnas() {
        return mapaColumnas;
    }

    public void setMapaColumnas(Integer mapaColumnas) {
        this.mapaColumnas = mapaColumnas;
    }

    public Long getBarcoId() {
        return barcoId;
    }

    public void setBarcoId(Long barcoId) {
        this.barcoId = barcoId;
    }

    public String getBarcoNombre() {
        return barcoNombre;
    }

    public void setBarcoNombre(String barcoNombre) {
        this.barcoNombre = barcoNombre;
    }

    public Integer getBarcoPosicionX() {
        return barcoPosicionX;
    }

    public void setBarcoPosicionX(Integer barcoPosicionX) {
        this.barcoPosicionX = barcoPosicionX;
    }

    public Integer getBarcoPosicionY() {
        return barcoPosicionY;
    }

    public void setBarcoPosicionY(Integer barcoPosicionY) {
        this.barcoPosicionY = barcoPosicionY;
    }

    public Integer getBarcoVelocidadX() {
        return barcoVelocidadX;
    }

    public void setBarcoVelocidadX(Integer barcoVelocidadX) {
        this.barcoVelocidadX = barcoVelocidadX;
    }

    public Integer getBarcoVelocidadY() {
        return barcoVelocidadY;
    }

    public void setBarcoVelocidadY(Integer barcoVelocidadY) {
        this.barcoVelocidadY = barcoVelocidadY;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaUltimaJugada() {
        return fechaUltimaJugada;
    }

    public void setFechaUltimaJugada(LocalDateTime fechaUltimaJugada) {
        this.fechaUltimaJugada = fechaUltimaJugada;
    }

    public Integer getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(Integer movimientos) {
        this.movimientos = movimientos;
    }

    public Boolean getHaLlegadoMeta() {
        return haLlegadoMeta;
    }

    public void setHaLlegadoMeta(Boolean haLlegadoMeta) {
        this.haLlegadoMeta = haLlegadoMeta;
    }
}

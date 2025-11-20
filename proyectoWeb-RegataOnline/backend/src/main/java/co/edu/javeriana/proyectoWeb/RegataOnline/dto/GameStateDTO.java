package co.edu.javeriana.proyectoWeb.RegataOnline.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Snapshot del estado de una partida multijugador.
 */
public class GameStateDTO {

    private Long partidaId;
    private String estadoPartida;
    private Boolean haLlegadoMeta;
    private List<ParticipantState> participantes = new ArrayList<>();

    public GameStateDTO() {}

    public GameStateDTO(Long partidaId, String estadoPartida, Boolean haLlegadoMeta) {
        this.partidaId = partidaId;
        this.estadoPartida = estadoPartida;
        this.haLlegadoMeta = haLlegadoMeta;
    }

    public Long getPartidaId() { return partidaId; }
    public void setPartidaId(Long partidaId) { this.partidaId = partidaId; }
    public String getEstadoPartida() { return estadoPartida; }
    public void setEstadoPartida(String estadoPartida) { this.estadoPartida = estadoPartida; }
    public Boolean getHaLlegadoMeta() { return haLlegadoMeta; }
    public void setHaLlegadoMeta(Boolean haLlegadoMeta) { this.haLlegadoMeta = haLlegadoMeta; }
    public List<ParticipantState> getParticipantes() { return participantes; }
    public void setParticipantes(List<ParticipantState> participantes) { this.participantes = participantes; }

    public void addParticipante(ParticipantState ps) { this.participantes.add(ps); }

    public static class ParticipantState {
        private Long jugadorId;
        private String jugadorEmail;
        private Long barcoId;
        private String barcoNombre;
        private int posicionX;
        private int posicionY;
        private int velocidadX;
        private int velocidadY;
        private String estado;

        public ParticipantState() {}

        public ParticipantState(Long jugadorId, String jugadorEmail, Long barcoId, String barcoNombre,
                                 int posicionX, int posicionY, int velocidadX, int velocidadY, String estado) {
            this.jugadorId = jugadorId;
            this.jugadorEmail = jugadorEmail;
            this.barcoId = barcoId;
            this.barcoNombre = barcoNombre;
            this.posicionX = posicionX;
            this.posicionY = posicionY;
            this.velocidadX = velocidadX;
            this.velocidadY = velocidadY;
            this.estado = estado;
        }

        public Long getJugadorId() { return jugadorId; }
        public void setJugadorId(Long jugadorId) { this.jugadorId = jugadorId; }
        public String getJugadorEmail() { return jugadorEmail; }
        public void setJugadorEmail(String jugadorEmail) { this.jugadorEmail = jugadorEmail; }
        public Long getBarcoId() { return barcoId; }
        public void setBarcoId(Long barcoId) { this.barcoId = barcoId; }
        public String getBarcoNombre() { return barcoNombre; }
        public void setBarcoNombre(String barcoNombre) { this.barcoNombre = barcoNombre; }
        public int getPosicionX() { return posicionX; }
        public void setPosicionX(int posicionX) { this.posicionX = posicionX; }
        public int getPosicionY() { return posicionY; }
        public void setPosicionY(int posicionY) { this.posicionY = posicionY; }
        public int getVelocidadX() { return velocidadX; }
        public void setVelocidadX(int velocidadX) { this.velocidadX = velocidadX; }
        public int getVelocidadY() { return velocidadY; }
        public void setVelocidadY(int velocidadY) { this.velocidadY = velocidadY; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }
}

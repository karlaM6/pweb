package co.edu.javeriana.proyectoWeb.RegataOnline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.proyectoWeb.RegataOnline.model.Barco;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Partida;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.PartidaParticipante;

@Repository
public interface PartidaParticipanteRepositorio extends JpaRepository<PartidaParticipante, Long> {

    List<PartidaParticipante> findByPartida(Partida partida);

    Optional<PartidaParticipante> findByPartidaAndBarco(Partida partida, Barco barco);

    // Para validar exclusividad de barco en partidas activas/pausadas
    boolean existsByBarcoAndPartida_EstadoIn(Barco barco, List<String> estados);
}

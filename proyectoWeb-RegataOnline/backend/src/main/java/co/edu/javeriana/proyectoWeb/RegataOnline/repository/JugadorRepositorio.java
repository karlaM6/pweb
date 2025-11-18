package co.edu.javeriana.proyectoWeb.RegataOnline.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.proyectoWeb.RegataOnline.model.Jugador;

@Repository
public interface JugadorRepositorio extends JpaRepository<Jugador, Long> {

    // Custom query to support the original method name used in the service layer.
    @Query("select j from Jugador j where lower(j.nombre) like lower(concat('%', :nombre, '%'))")
    List<Jugador> buscarJugadoresPorNombre(@Param("nombre") String nombre);

    Optional<Jugador> findByEmail(String email);
    
}

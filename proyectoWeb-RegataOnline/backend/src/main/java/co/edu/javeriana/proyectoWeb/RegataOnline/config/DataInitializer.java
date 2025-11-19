package co.edu.javeriana.proyectoWeb.RegataOnline.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.proyectoWeb.RegataOnline.model.Jugador;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Barco;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Modelo;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Role;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.JugadorRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.BarcoRepositorio;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.ModeloRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Simple data initializer for development. Creates an ADMIN and a USER if they do not exist.
 * This runs in all profiles (including default). Remove or protect in production.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private JugadorRepositorio jugadorRepositorio;

    @Autowired
    private BarcoRepositorio barcoRepositorio;

    @Autowired
    private ModeloRepositorio modeloRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // create admin if not present
        String adminEmail = "admin@local";
        Jugador admin = jugadorRepositorio.findByEmail(adminEmail).orElse(null);
        if (admin == null) {
            admin = new Jugador();
            admin.setNombre("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin = jugadorRepositorio.save(admin);
            System.out.println("Created default admin: " + adminEmail + "/admin123");
        }
        ensureDefaultBarco(admin);

        String userEmail = "user@local";
        Jugador user = jugadorRepositorio.findByEmail(userEmail).orElse(null);
        if (user == null) {
            user = new Jugador();
            user.setNombre("User");
            user.setEmail(userEmail);
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(Role.USER);
            user = jugadorRepositorio.save(user);
            System.out.println("Created default user: " + userEmail + "/user123");
        }
        ensureDefaultBarco(user);
    }

    /**
     * Crea un barco inicial para el jugador si no tiene ninguno.
     * Evita bloquear el flujo de creación de partidas para usuarios nuevos.
     */
    private void ensureDefaultBarco(Jugador jugador) {
        if (jugador == null) return;
        // La colección puede venir null si nunca se inicializó.
        if (jugador.getBarcos() != null && !jugador.getBarcos().isEmpty()) {
            return; // Ya tiene barcos
        }
        // Buscar / crear modelo por defecto
        Modelo defaultModel = modeloRepositorio.findByNombreModelo("Default").orElseGet(() -> {
            Modelo m = new Modelo("Default", "Azul");
            return modeloRepositorio.save(m);
        });
        Barco barco = new Barco("Barco Inicial", 0, 0, 0, 0);
        barco.setJugador(jugador);
        barco.setModelo(defaultModel);
        barcoRepositorio.save(barco);
        // No es estrictamente necesario volver a guardar jugador, pero asegura relación bidireccional si luego se accede a la lista.
        // Recargar jugador para forzar sincronización si fuese necesario.
        System.out.println("Asignado barco inicial a jugador: " + jugador.getEmail());
    }
}

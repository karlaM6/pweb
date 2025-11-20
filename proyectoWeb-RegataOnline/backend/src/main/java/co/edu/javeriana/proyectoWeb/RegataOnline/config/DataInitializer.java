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

        // Segundo usuario para pruebas de multijugador/polling
        String user2Email = "user2@local";
        Jugador user2 = jugadorRepositorio.findByEmail(user2Email).orElse(null);
        if (user2 == null) {
            user2 = new Jugador();
            user2.setNombre("User2");
            user2.setEmail(user2Email);
            user2.setPassword(passwordEncoder.encode("user2123"));
            user2.setRole(Role.USER);
            user2 = jugadorRepositorio.save(user2);
            System.out.println("Created second test user: " + user2Email + "/user2123");
        }
        ensureDefaultBarco(user2);
    }

    /**
     * Crea un barco inicial para el jugador si no tiene ninguno
     * Evita bloquear el flujo de creación de partidas para usuarios nuevos
     */
    private void ensureDefaultBarco(Jugador jugador) {
        if (jugador == null) return;
        
        if (jugador.getBarcos() != null && !jugador.getBarcos().isEmpty()) {
            return; 
        }
        
        Modelo defaultModel = modeloRepositorio.findByNombreModelo("Default").orElseGet(() -> {
            Modelo m = new Modelo("Default", "Azul");
            return modeloRepositorio.save(m);
        });
        Barco barco = new Barco("Barco Inicial", 0, 0, 0, 0);
        barco.setJugador(jugador);
        barco.setModelo(defaultModel);
        barcoRepositorio.save(barco);
        
        System.out.println("Asignado barco inicial a jugador: " + jugador.getEmail());
    }
}

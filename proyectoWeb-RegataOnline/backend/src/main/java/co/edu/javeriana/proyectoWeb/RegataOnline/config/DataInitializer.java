package co.edu.javeriana.proyectoWeb.RegataOnline.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.proyectoWeb.RegataOnline.model.Jugador;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Role;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.JugadorRepositorio;
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
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // create admin if not present
        String adminEmail = "admin@local";
        if (jugadorRepositorio.findByEmail(adminEmail).isEmpty()) {
            Jugador admin = new Jugador();
            admin.setNombre("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            jugadorRepositorio.save(admin);
            System.out.println("Created default admin: " + adminEmail + "/admin123");
        }

        String userEmail = "user@local";
        if (jugadorRepositorio.findByEmail(userEmail).isEmpty()) {
            Jugador user = new Jugador();
            user.setNombre("User");
            user.setEmail(userEmail);
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(Role.USER);
            jugadorRepositorio.save(user);
            System.out.println("Created default user: " + userEmail + "/user123");
        }
    }
}

package co.edu.javeriana.proyectoWeb.RegataOnline.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import co.edu.javeriana.proyectoWeb.RegataOnline.dto.JwtAuthenticationResponse;
import co.edu.javeriana.proyectoWeb.RegataOnline.dto.LoginDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.model.Jugador;
import co.edu.javeriana.proyectoWeb.RegataOnline.repository.JugadorRepositorio;

@Service
public class AuthenticationService {

    @Autowired
    private JugadorRepositorio jugadorRepositorio;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse login(LoginDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        Jugador jugador = jugadorRepositorio.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    String jwt = jwtService.generateToken(jugador); // incluye claim jugadorId
    return new JwtAuthenticationResponse(jwt, jugador.getEmail(), jugador.getRole(), jugador.getId());
    }

}

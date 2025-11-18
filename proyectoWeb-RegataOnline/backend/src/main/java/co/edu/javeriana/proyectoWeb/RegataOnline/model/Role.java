package co.edu.javeriana.proyectoWeb.RegataOnline.model;

import org.springframework.security.core.GrantedAuthority;

// Type safe roles for Spring Security
// https://stackoverflow.com/a/54713712

public enum Role implements GrantedAuthority {
    USER("USER"),
    ADMIN("ADMIN");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    // Backwards-compatible compile-time constants used in @Secured annotations
    public static class Code {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }
}

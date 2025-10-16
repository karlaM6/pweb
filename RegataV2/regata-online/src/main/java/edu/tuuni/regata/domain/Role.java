package edu.tuuni.regata.domain;

public enum Role {
    PLAYER("Jugador"),
    ADMINISTRATOR("Administrador");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

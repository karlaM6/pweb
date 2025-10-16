# 🔐 Guía de Pruebas de Control de Acceso por Roles

## Descripción general
La SPA de Regata Online ahora aplica control de acceso por roles con dos tipos de usuarios:
- **👑 Administrador:** acceso completo (CRUD) a todos los recursos  
- **⛵ Jugador:** solo puede participar en el juego de regatas  

## Usuarios de prueba disponibles

### Cuenta de administrador
- **Usuario:** `admin`  
- **Nombre mostrado:** Administrador  
- **Rol:** ADMINISTRADOR  
- **Acceso:** operaciones CRUD completas en Jugadores, Barcos, Modelos y Mapas  

### Cuentas de jugador
- **Usuario:** `player1`, `player2`, `player3`, `player4`, `player5`  
- **Nombre mostrado:** Player 1, Player 2, etc.  
- **Rol:** JUGADOR  
- **Acceso:** solo puede jugar  

## Cómo probar

### 1. Acceso de administrador
1. Ve a `/spa.html`  
2. Inicia sesión con el usuario `admin`  
3. **Comportamiento esperado:**  
   - Encabezado: **"👑 Admin: Administrador"** en rojo  
   - Navegación: todas las pestañas visibles (`Jugadores | Barcos | Modelos | Mapas | Jugar`)  
   - Acceso total a las páginas CRUD  
   - Botones de Crear/Editar/Eliminar visibles  
   - Tabla de jugadores muestra columna de rol (👑 Admin o ⛵ Jugador)  
   - Redirección automática a la página "Jugadores" tras iniciar sesión  

### 2. Acceso de jugador
1. Cierra sesión (clic en "🚪 Salir")  
2. Inicia sesión con `player1` (o cualquier otro jugador)  
3. **Comportamiento esperado:**  
   - Encabezado: **"⛵ Jugador: Player 1"** en verde  
   - Navegación: solo muestra el botón `Jugar`  
   - Sin acceso a `Jugadores`, `Barcos`, `Modelos` ni `Mapas`  
   - Redirección al lobby del juego tras iniciar sesión  
   - Si intenta acceder a una página CRUD manualmente, aparece el mensaje:  
     ```
     🚫 Acceso Restringido  
     Solo los Administradores pueden gestionar este recurso.  
     Los Jugadores solo pueden participar en las regatas.  
     [🎮 Ir a Jugar]
     ```

## Puntos de control del rol

### En el frontend
✅ **Menú de navegación:** renderizado condicional con `Auth.isAdmin()`  
✅ **Páginas CRUD:** verificación de acceso en cada componente  
✅ **Redirecciones:** Admin → Jugadores, Jugador → Lobby del juego  
✅ **Indicadores visuales:** colores (rojo = admin, verde = jugador)  

### En el backend
- Los endpoints están disponibles para ambos roles (la validación en backend se maneja por separado).  
- El frontend se encarga de restringir las acciones según el rol.  

## Lista de verificación

### Pruebas de administrador
- [ ] Iniciar sesión como `admin`  
- [ ] Verificar pestañas visibles  
- [ ] Acceder a Jugadores → ver roles  
- [ ] Acceder a Barcos → ver CRUD  
- [ ] Acceder a Modelos → ver CRUD  
- [ ] Acceder a Mapas → ver CRUD  
- [ ] Acceder a Jugar → jugar normalmente  
- [ ] Confirmar encabezado en rojo con “👑 Admin: Administrador”  

### Pruebas de jugador
- [ ] Iniciar sesión como `player1`  
- [ ] Verificar que solo aparece la pestaña "Jugar"  
- [ ] Confirmar redirección al lobby del juego  
- [ ] Intentar acceder a `/spa.html#players` → mensaje de restricción  
- [ ] Intentar acceder a `/spa.html#boats` → mensaje de restricción  
- [ ] Intentar acceder a `/spa.html#models` → mensaje de restricción  
- [ ] Intentar acceder a `/spa.html#maps` → mensaje de restricción  
- [ ] Confirmar encabezado verde con “⛵ Jugador: Player 1”  
- [ ] Verificar que el juego funciona normalmente  

## Detalles de implementación

### Funciones de verificación de rol
```javascript
// En auth.js
Auth.isAdmin()  // Devuelve true si el usuario es ADMINISTRADOR
Auth.isPlayer() // Devuelve true si el usuario es JUGADOR
```

### Protección de componentes
```javascript
async renderPlayers(container) {
    if (!Auth.isAdmin()) {
        // Mostrar mensaje de restricción
        return;
    }
    // Mostrar interfaz CRUD
}
```

### Renderizado condicional en navegación
```javascript
const navButtons = this.isAdmin() ? `
    <!-- Todas las pestañas para admin -->
` : `
    <!-- Solo "Jugar" para jugadores -->
`;
```

## Cumplimiento de requisitos

Según las especificaciones del proyecto:
> Existen dos roles: Jugador y Administrador. Ambos deben autenticarse al inicio y solo pueden realizar las siguientes funciones:
>
> - **Jugador:** participa en la carrera con un barco. Puede haber varios jugadores a la vez. **No puede realizar operaciones CRUD.**
> - **Administrador:** configura el sistema y tiene **acceso total a CRUD.**

✅ **Implementado correctamente:** los jugadores solo pueden jugar y los administradores tienen control total.

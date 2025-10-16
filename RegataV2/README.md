# Regata Online - Sistema de Gestión de Regatas

## Descripción general
Regata Online es una aplicación web desarrollada con **Spring Boot** para gestionar regatas de vela. Incluye **dos interfaces**: una versión tradicional generada desde el servidor y una **SPA** (Single Page Application) moderna que usa servicios REST API.

## Arquitectura del proyecto
- **Framework:** Spring Boot 3.3.3  
- **Lenguaje:** Java 19  
- **Base de datos:** H2 (local, basada en archivos)  
- **Frontend:**  
  - **SPA:** Aplicación de una sola página con JavaScript puro (usa REST API)  
  - **Versión clásica:** Plantillas Thymeleaf con CSS  
- **API:** REST JSON con controladores `@RestController`  
- **Compilación:** Maven  
- **Servidor:** Tomcat embebido  

## Funcionalidades principales
- **🎮 Juego de regatas jugable:** carrera de veleros con movimiento en tiempo real  
- **🌊 Diseño visual:** fondos con degradados oceánicos y estética moderna  
- **⛵ Carreras interactivas:** navegación de barcos, obstáculos y meta  
- **🏁 Configuración de partida:** elección de navegante, barco y mapa  
- **🎯 Condiciones de victoria:** el primer barco en llegar gana  
- **👥 Modo multijugador:** varios jugadores con barcos independientes  
- **🗺️ Gestión de mapas:** crear y editar recorridos con obstáculos  
- **📊 CRUD completo:** gestión de jugadores, barcos, modelos y mapas  
- **🎨 Interfaz moderna:** diseño profesional con temática náutica  

## Configuración actual
- **Servidor:** puerto 5000 (modo desarrollo)  
- **Base de datos:** archivos en `.localdb/regata`  
- **Plantillas:** `src/main/resources/templates/`  
- **Archivos estáticos:** `src/main/resources/static/`  

## Cambios recientes

### 🆕 Segunda entrega – SPA + REST API (16 de octubre de 2025)
- **✅ SPA completa:** construida con JavaScript puro  
- **Capa REST API:** endpoints `@RestController` para todas las operaciones CRUD  
- **✅ Control por roles:** diferencia entre jugadores y administradores  
  - **Jugadores:** solo pueden jugar  
  - **Administradores:** tienen acceso completo a CRUD  
  - **Interfaz adaptada al rol:** navegación, botones y vistas restringidas  
  - **Indicadores visuales:** distintivos de color (👑 Admin rojo, ⛵ Jugador verde)  
- **Integración API:** la SPA usa REST para todas las operaciones  
- **Ruteo en cliente:** navegación sin recargar página  
- **Gestión de estado:** módulos centrales para lógica del juego y API  
- **Compatibilidad:** las páginas Thymeleaf siguen funcionando  
- **Migración:** entidad Player actualizada con enum de Roles  
- **Guía de pruebas:** ver `ROLE_TESTING_GUIDE.md`  

### 9 de septiembre de 2025
- **Transformado en juego completo:** de sistema de gestión a juego funcional  
- **Diseño oceánico:** implementación de fondos con degradados y navegación moderna  
- **GameController:** controla la lógica de carrera  
- **Tablero 10x10:** cuadrícula con agua, obstáculos, inicio y meta  
- **Sistema de movimiento:** control de barco, colisiones y victoria  
- **Pantalla de inicio:** selección de jugador, barco y mapa  
- **Lógica en JS:** movimiento en tiempo real y seguimiento de estado  
- **Corrección de plantillas:** errores de alcance y funciones JS corregidos  
- **Modo multijugador:** soporte de sesiones múltiples  
- **Sistema de victoria:** animaciones y condiciones completas  

## Flujo de desarrollo
Para ejecutar el servidor:
```
cd regata-online && mvn spring-boot:run
```

## Despliegue
- **Tipo:** aplicación web sin estado  
- **Compilación:** `mvn clean package`  
- **Ejecución:** `java -jar` en el puerto 5000  

## Estructura de URLs

### 🆕 SPA (REST API)
- **`/spa.html`** – punto de entrada principal  
  - 🔐 Login con autenticación por roles  
  - 👥 Gestión de jugadores  
  - 🚢 Gestión de barcos  
  - 📋 Gestión de modelos  
  - 🗺️ Gestión de mapas  
  - 🎮 Sala de juego: selección de jugador, barco y mapa  
  - 🏁 Tablero: juego interactivo por REST API  

### Endpoints REST
- `/api/players` – CRUD de jugadores  
- `/api/boats` – CRUD de barcos  
- `/api/models` – CRUD de modelos  
- `/api/maps` – CRUD de mapas  
- `/api/game/start` – iniciar partida  
- `/api/game/move` – mover barco  
- `/api/game/state` – estado del juego  
- `/api/auth/login` – autenticación  
- `/api/auth/current` – sesión actual  

### Páginas Thymeleaf (modo clásico)
- `/` – redirige a lista de jugadores  
- `/game` – lobby del juego  
- `/game/board` – tablero  
- `/players` – gestión de jugadores  
- `/boats` – gestión de barcos  
- `/models` – gestión de modelos  
- `/maps` – gestión de mapas  
- `/h2` – consola H2 (solo desarrollo)  

## Notas técnicas
- Interfaz en español ("Jugador", "Barco", etc.)  
- Relaciones JPA correctas entre entidades  
- Validaciones de negocio (por ejemplo, evitar eliminar jugadores con barcos asignados)  
- Configuración para entornos de desarrollo y producción  

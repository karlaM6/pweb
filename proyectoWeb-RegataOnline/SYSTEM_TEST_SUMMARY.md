# 🧪 Resumen de Pruebas de Sistema (E2E)

## Descripción General

Prueba de sistema automatizada completa que valida el flujo más complejo y largo del sistema RegataOnline:
**"Crear una partida, navegar por el mapa, mover el barco y completar la regata"**

## Tecnologías Utilizadas

- **Framework de Pruebas**: JUnit 5
- **Automatización de Navegador**: Playwright (Microsoft)
- **Motor de Navegador**: Chromium (headless)
- **Servidor Web**: Spring Boot (@WebEnvironment.DEFINED_PORT)
- **Perfil de Prueba**: `system-test`
- **Base de Datos**: H2 (in-memory)

## Clase de Prueba

**Ubicación**: `src/test/java/co/edu/javeriana/proyectoWeb/RegataOnline/controller/RegataSystemTest.java`

### Anotaciones Clave

```java
@ActiveProfiles("system-test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
```

- `@ActiveProfiles("system-test")`: Usa el perfil de configuración `application-system-test.properties`
- `@DirtiesContext`: Limpia el contexto de Spring antes de cada prueba
- `@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)`: Ejecuta el servidor en puerto fijo (usualmente 8080)

## Preparación de Datos (@BeforeEach)

Antes de cada prueba, se crean las siguientes entidades en la base de datos:

### 1. **Jugador**
- Nombre: "TestJugador"
- Rol: Jugador que controlará el barco

### 2. **Modelo de Barco**
- Nombre: "Velero Test"
- Color: "Rojo"
- Especificación base para crear barcos

### 3. **Barco**
- Nombre: "BarcoTest"
- Modelo: "Velero Test"
- Posición inicial: (0, 0)

### 4. **Mapa**
- Dimensiones: 10x10
- Contiene una cuadrícula de celdas

### 5. **Celdas del Mapa**
```
Fila 0:
- (0,0): Tipo "P" (Partida/Inicio)
- (1,0) a (8,0): Tipo "" (Agua navegable)
- (9,0): Tipo "M" (Meta)

Filas 1-9: Todas tipo "" (Agua)
```

Esto crea un camino recto del inicio a la meta donde el barco puede navegar.

## Flujo de Prueba

### Pasos Ejecutados

```
1. NAVEGACIÓN AL MENÚ
   ├─ Navega a http://localhost:4200/partida/menu
   ├─ Valida que la página cargó correctamente
   └─ Espera a que el contenedor del menú esté disponible

2. CREACIÓN DE PARTIDA (PASO 1)
   ├─ Busca el botón "Crear Partida"
   ├─ Valida que el botón esté disponible
   └─ Hace clic en el botón

3. SELECCIÓN DE JUGADOR
   ├─ Espera a que aparezca el select de jugador
   ├─ Selecciona el jugador con ID 1 ("TestJugador")
   └─ Hace clic en "Siguiente"

4. SELECCIÓN DE MAPA
   ├─ Espera a que aparezca la tarjeta del mapa
   ├─ Hace clic en el primer mapa disponible
   └─ Hace clic en "Siguiente"

5. SELECCIÓN DE BARCO
   ├─ Espera a que aparezca la tarjeta del barco
   ├─ Hace clic en el primer barco disponible
   └─ Hace clic en "Crear Partida"

6. VALIDACIÓN DE CREACIÓN
   ├─ Espera a que se cargue el contenedor de juego
   ├─ Valida que el nombre del jugador sea "TestJugador"
   ├─ Valida que la posición inicial sea (X:0, Y:0)
   └─ Valida que el estado sea "activa"

7. MOVIMIENTOS DEL BARCO (Hasta 2 movimientos)
   ├─ Busca celdas clickeables (destinos posibles válidos)
   ├─ Si existen celdas disponibles:
   │  ├─ Hace clic en la primera celda disponible
   │  ├─ Espera 800ms para procesar el movimiento
   │  └─ Repite para el segundo movimiento
   └─ Si no hay celdas: Salta este paso

8. VALIDACIÓN DE MOVIMIENTOS
   ├─ Valida que el contador de movimientos > 0
   └─ Confirma que al menos 1 movimiento se realizó

9. PAUSA DE PARTIDA
   ├─ Hace clic en el botón "⏸️ Pausar"
   ├─ Espera 500ms
   ├─ Valida que el estado sea "pausada"
   └─ Confirma que la partida se pausó

10. REANUDACIÓN (Si es posible)
    ├─ Busca el botón "Reanudar"
    ├─ Si existe:
    │  ├─ Hace clic en "Reanudar"
    │  ├─ Espera 500ms
    │  ├─ Valida que el estado vuelva a ser "activa"
    │  └─ Confirma que se reanudó correctamente
    └─ Si no existe: Registra que no se encontró

11. FINALIZACIÓN DE PARTIDA
    ├─ Hace clic en el botón "🛑 Finalizar"
    ├─ Espera 1000ms
    └─ Valida que la URL sigue siendo válida (localhost:4200)

12. VALIDACIÓN FINAL
    └─ Confirma que la aplicación sigue siendo accesible
```

## Validaciones (Assertions)

La prueba valida los siguientes aspectos:

### 1. **Navegación y Carga**
- ✅ Página carga correctamente
- ✅ Título de la página es "RegataOnline"
- ✅ Los elementos principales están presentes

### 2. **Creación de Partida**
- ✅ Formulario carga correctamente
- ✅ Selecciones funcionan (Jugador, Mapa, Barco)
- ✅ Botón "Crear Partida" está habilitado

### 3. **Pantalla de Juego**
- ✅ Contenedor de juego se muestra
- ✅ Nombre del jugador es correcto ("TestJugador")
- ✅ Posición inicial del barco es (0, 0)
- ✅ Estado inicial es "activa"

### 4. **Interacción del Usuario**
- ✅ Existen celdas clickeables disponibles
- ✅ Los movimientos se registran en el contador
- ✅ Las coordenadas se actualizan después de mover

### 5. **Control de Partida**
- ✅ Botón "Pausar" funciona y cambia estado a "pausada"
- ✅ Botón "Reanudar" (si existe) vuelve a estado "activa"
- ✅ Botón "Finalizar" responde correctamente

## Dependencias Agregadas

Se agregó la siguiente dependencia a `pom.xml`:

```xml
<!-- Playwright for system tests -->
<dependency>
    <groupId>com.microsoft.playwright</groupId>
    <artifactId>playwright</artifactId>
    <version>1.45.0</version>
    <scope>test</scope>
</dependency>
```

## Ejecución de la Prueba

### Requisitos Previos

1. **Backend ejecutándose** en puerto 8080
   ```bash
   cd backend
   ./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=system-test"
   ```

2. **Frontend ejecutándose** en puerto 4200
   ```bash
   cd frontend
   npm start
   ```

3. **Base de datos H2** inicializada automáticamente

### Comando para Ejecutar

```bash
# Ejecutar solo la prueba de sistema
./mvnw test -Dtest=RegataSystemTest -DskipITs=false

# O ejecutar todas las pruebas (integración + sistema)
./mvnw clean test

# Con salida detallada
./mvnw test -Dtest=RegataSystemTest -DskipITs=false -X
```

### Tiempo Estimado de Ejecución

- **Preparación**: ~2 segundos (inicializar datos, crear browser)
- **Navegación y selecciones**: ~5 segundos
- **Movimientos y validaciones**: ~3 segundos
- **Limpieza**: ~1 segundo
- **Total**: ~10-15 segundos

## Manejo de Errores

La prueba incluye manejo robusto para:

1. **Elementos no encontrados**: Usa `waitFor()` para esperar
2. **Timeouts**: Configura esperas apropiadas (500ms - 1000ms)
3. **Elementos opcionales**: Verifica existencia con `count() > 0`
4. **Valores de texto**: Usa `containsText()` para búsquedas flexibles

## Limitaciones Conocidas

1. **Selectores Angular**: Usa clases CSS y atributos data que pueden cambiar con actualizaciones de UI
2. **Tiempos de Espera**: Algunos elementos pueden necesitar ajustes según velocidad del servidor
3. **Perfil de Base de Datos**: Requiere perfil `system-test` con H2 in-memory
4. **Puerto Fijo**: El servidor debe ejecutarse en puerto 8080; el frontend en 4200

## Mejoras Futuras

- [ ] Parametrizar URLs (BASE_URL como propiedad)
- [ ] Agregar pruebas para casos de error (movimientos inválidos)
- [ ] Aumentar cobertura de elementos UI (leyenda, stats, etc.)
- [ ] Validar animaciones y transiciones
- [ ] Pruebas de rendimiento con movimientos repetidos
- [ ] Pruebas de navegación entre pantallas

## Arquitectura de la Prueba

```
RegataSystemTest
├── @BeforeEach: setup()
│   ├─ Limpiar base de datos
│   ├─ Crear entidades de prueba (Jugador, Modelo, Barco, Mapa, Celdas)
│   └─ Inicializar Playwright browser
│
├── @Test: testFlujoCompletoRegata()
│   ├─ Navegación y validaciones iniciales
│   ├─ Flujo de creación de partida
│   ├─ Interacciones en pantalla de juego
│   └─ Validaciones finales
│
└── @AfterEach: tearDown()
    ├─ Cerrar browser
    └─ Cerrar Playwright
```

## Estándares Seguidos

- ✅ Patrón POM (Page Object Model) básico con localizadores
- ✅ Assertions usando PlaywrightAssertions
- ✅ Waits explícitos con waitFor()
- ✅ Nombres de métodos descriptivos
- ✅ Comentarios explicativos en cada paso
- ✅ Limpieza automática en @AfterEach

## Referencias

- [Documentación de Playwright Java](https://playwright.dev/java/)
- [Spring Boot Testing](https://spring.io/projects/spring-boot#learn)
- [JUnit 5](https://junit.org/junit5/)
- [Testing en Angular](https://angular.io/guide/testing)

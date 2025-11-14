# 🎮 Guía de Ejecución: Prueba de Sistema RegataOnline

## Inicio Rápido (5 minutos)

### Paso 1: Compilar el Proyecto

```bash
cd ~/Downloads/ProyectoWeb-RegataOnline-develop/proyectoWeb-RegataOnline/backend
./mvnw clean compile test-compile -DskipTests
```

**Resultado esperado**: `BUILD SUCCESS`

---

## Opción A: Ejecutar Tests de Integración (Recomendado para CI/CD)

### ✅ Sin requisitos previos

```bash
cd backend
./mvnw test -Dtest="*IntegrationTest" -DskipITs=false
```

**Tiempo**: ~30 segundos  
**Resultado**: 21/21 tests PASSING ✅

### Comando alternativo (Todos los tests)

```bash
./mvnw clean test
```

---

## Opción B: Ejecutar Test de Sistema (E2E)

### Requisitos Previos

1. **Backend en Puerto 8080** con perfil `system-test`
2. **Frontend en Puerto 4200**
3. **Base de datos H2** (se crea automáticamente)

---

## 🚀 Ejecución Paso a Paso

### Paso 1: Iniciar Backend (Terminal 1)

```bash
cd ~/Downloads/ProyectoWeb-RegataOnline-develop/proyectoWeb-RegataOnline/backend

# Ejecutar con perfil system-test
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="--spring.profiles.active=system-test"
```

**Esperar hasta ver**:
```
o.s.b.w.e.t.TomcatWebServer : Tomcat started on port(s): 8080
```

### Paso 2: Iniciar Frontend (Terminal 2)

```bash
cd ~/Downloads/ProyectoWeb-RegataOnline-develop/proyectoWeb-RegataOnline/frontend

# Instalar dependencias (solo primera vez)
npm install

# Ejecutar servidor de desarrollo
npm start
```

**Esperar hasta ver**:
```
✔ Compiled successfully.
✔ Browser updated. You can view it at:
  http://localhost:4200
```

### Paso 3: Ejecutar Test de Sistema (Terminal 3)

```bash
cd ~/Downloads/ProyectoWeb-RegataOnline-develop/proyectoWeb-RegataOnline/backend

# Ejecutar solo el test de sistema
./mvnw test -Dtest="RegataSystemTest" -DskipITs=false
```

**O ejecutar todo**:
```bash
./mvnw test
```

---

## 📊 Salida Esperada

### Tests de Integración

```
[INFO] -------------------------------------------------------
[INFO] T E S T S
[INFO] -------------------------------------------------------
[INFO] Running co.edu.javeriana.proyectoWeb.RegataOnline.controller.JugadorControladorIntegrationTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.xxx s
[INFO] Running co.edu.javeriana.proyectoWeb.RegataOnline.controller.BarcoControladorIntegrationTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.xxx s
[INFO] Running co.edu.javeriana.proyectoWeb.RegataOnline.controller.MapaControladorIntegrationTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.xxx s
[INFO] Running co.edu.javeriana.proyectoWeb.RegataOnline.controller.PartidaControladorIntegrationTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.xxx s

[INFO] Results:
[INFO]
[INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

### Test de Sistema

```
[INFO] Running co.edu.javeriana.proyectoWeb.RegataOnline.controller.RegataSystemTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 12.xxx s

[INFO] Results:
[INFO]
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

---

## 🐛 Solución de Problemas

### Problema: "BUILD FAILURE" en Tests

**Causa 1**: Dependencias no descargadas
```bash
./mvnw clean install -DskipTests
```

**Causa 2**: Puerto 8080 ya en uso
```bash
# Verificar qué usa puerto 8080
lsof -i :8080

# Detener aplicación previa
pkill -f "java.*RegataOnline"
```

**Causa 3**: Tests sucios de ejecuciones previas
```bash
# Limpiar completamente
./mvnw clean
rm -rf target/
./mvnw test
```

### Problema: "Cannot connect to http://localhost:8080"

**Solución**:
1. Verificar backend está corriendo: http://localhost:8080
2. Ver logs del backend en Terminal 1
3. Verificar puerto correcto: `netstat -tuln | grep 8080`

### Problema: "Cannot connect to http://localhost:4200"

**Solución**:
1. Verificar frontend está corriendo: http://localhost:4200
2. Ver logs del frontend en Terminal 2
3. Instalar dependencias: `npm install`
4. Verificar puerto correcto: `netstat -tuln | grep 4200`

### Problema: Tests Timeout (esperando elementos)

**Causa**: Servidor lento  
**Solución**: Aumentar timeout en RegataSystemTest

```java
page.waitForTimeout(2000);  // Cambiar de 1000 a 2000
```

### Problema: "Unrecognized field in JSON"

**Causa**: PartidaDTO sin anotaciones  
**Solución**: Ya está agregado @JsonProperty en PartidaDTO

---

## 📈 Verificar Cobertura de Tests

### Ver Reporte Surefire

```bash
# Los reportes se generan automáticamente
open target/surefire-reports/index.html

# En Linux
xdg-open target/surefire-reports/index.html
```

### Contar Tests Ejecutados

```bash
# Contar tests de integración
grep -r "public void test" src/test/java/ | wc -l

# Resultado esperado: 21
```

---

## 🔍 Validar Instalación Correcta

### Verificar Compositor Maven

```bash
./mvnw --version
# Resultado: Apache Maven 3.x.x
```

### Verificar JDK

```bash
java -version
# Resultado: java version "17" o superior
```

### Verificar Node/npm

```bash
node --version
npm --version
# Resultado: v18+ y npm 9+
```

### Verificar Playwright

```bash
# Verificar que está en pom.xml
grep -A 3 "playwright" pom.xml
```

---

## 🎓 Entender los Tests

### Estructura de un Test de Integración

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-testing")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class MiControladorIntegrationTest {
    
    @BeforeEach
    void setup() {
        // Preparar datos
    }
    
    @Test
    void testOperacionEspecifica() {
        // Arreglar (Setup) → Actuar (Act) → Afirmar (Assert)
    }
    
    @AfterEach
    void tearDown() {
        // Limpiar datos
    }
}
```

### Estructura de un Test de Sistema

```java
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("system-test")
public class RegataSystemTest {
    
    private Browser browser;
    private Page page;
    
    @BeforeEach
    void setup() {
        // Inicializar Playwright
        // Crear entidades en BD
    }
    
    @Test
    void testFlujoUsuario() {
        // Navegar
        // Interactuar
        // Validar
    }
    
    @AfterEach
    void tearDown() {
        // Cerrar navegador
    }
}
```

---

## 📋 Checklist Pre-Ejecución

Antes de ejecutar, verificar:

- [ ] Backend compilado sin errores
- [ ] Frontend compilado sin errores
- [ ] Puertos 8080 y 4200 disponibles
- [ ] Suficiente RAM (mínimo 2GB)
- [ ] Conexión a internet (para Maven)
- [ ] Java 17+ instalado
- [ ] Node 18+ instalado

---

## 🎯 Comandos Útiles

### Limpiar todo

```bash
./mvnw clean
npm cache clean --force
```

### Ejecutar test específico

```bash
./mvnw test -Dtest=JugadorControladorIntegrationTest
./mvnw test -Dtest=RegataSystemTest
```

### Ejecutar con salida verbosa

```bash
./mvnw test -X
./mvnw test -Dtest="*IntegrationTest" -X
```

### Detener servidor

```bash
# Ctrl+C en la terminal donde está corriendo
# O matar proceso Java
pkill -f "java.*spring-boot"
```

### Ver logs detallados

```bash
# Backend
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=system-test --logging.level.root=DEBUG"

# Maven
./mvnw test -X | tee test-output.log
```

---

## ⏱️ Tiempos Esperados

| Operación | Tiempo |
|-----------|--------|
| Compilar backend | 10s |
| Compilar frontend | 20s |
| Tests integración | 30s |
| Test sistema | 15s |
| Startup backend | 5s |
| Startup frontend | 10s |
| **Total (todo)** | **90s** |

---

## 📊 Interpretación de Resultados

### ✅ PASSING

Significa que la prueba ejecutó y todas las validaciones pasaron.

```
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

### ❌ FAILURE

Significa que una validación falló (assertion).

```
[ERROR] testNombrePrueba FAILED
```

### ⚠️ ERROR

Significa que hubo un error no manejado (excepción).

```
[ERROR] testNombrePrueba ERROR
```

### ⊘ SKIPPED

Significa que el test fue saltado.

```
[INFO] Tests run: 1, Skipped: 1
```

---

## 🔐 Consideraciones de Seguridad

- Los tests usan BD en memoria (no producción)
- Los tests usan autenticación deshabilitada
- Los selectores de Playwright son públicos (documentados)

**Recomendación**: No ejecutar en entorno de producción

---

## 📞 Soporte Técnico

### Si todo falla:

1. Ejecutar `./mvnw clean`
2. Cerrar terminales
3. Reiniciar desde Paso 1
4. Verificar conexión a internet

### Para más información:

- Documentación Maven: https://maven.apache.org/
- Documentación Spring Boot: https://spring.io/projects/spring-boot
- Documentación Playwright: https://playwright.dev/java/

---

## ✅ Verificación Final

Una vez completado, deberías tener:

✅ 21 tests de integración pasando  
✅ 1 test de sistema compilado y listo  
✅ Backend accesible en http://localhost:8080  
✅ Frontend accesible en http://localhost:4200  
✅ Base de datos H2 con datos de prueba  
✅ Reportes en `target/surefire-reports/`

---

**🎉 ¡Listo para Usar!**

Ahora puedes usar esta suite para:
- Validar cambios de código
- Evitar regresiones
- Documentar comportamiento esperado
- Integrar con CI/CD

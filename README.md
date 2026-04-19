# 🏗️ WorkCount

*
*WorkCount
**
es
una
aplicacion
backend
para
registrar
fichajes (
`IN/OUT`)
por
dia
y
calcular
el
balance
mensual
de
horas.

El
proyecto
aplica
*
*Arquitectura
Hexagonal (
Ports &
Adapters)
**
para
separar
la
logica
de
negocio
del
framework
y
de
la
persistencia.

---

## 🚀 Arranque rapido

### Requisitos

-
Java
17
-
Maven

### Ejecutar la app

```bash
mvn spring-boot:run
```

### Ejecutar tests

```bash
mvn test
```

### URLs utiles

-
API
base:
`http://localhost:8080`
-
H2
console:
`http://localhost:8080/h2-console`
    -
    JDBC:
    `jdbc:h2:mem:testdb`
    -
    user:
    `sa`
    -
    password:
    vacio

---

## 🛠️ Stack tecnico

-
Java
17
-
Spring
Boot
3
-
Spring
Web
-
Spring
Data
JPA
-
H2 (
desarrollo)
-
PostgreSQL
driver (
runtime)
-
Maven

---

## 📐 Arquitectura hexagonal

```text
           +-------------------------------+
           |         Infrastructure        |
           |-------------------------------|
           | - Web Controllers             |
           | - JPA Adapters                |
           | - Spring Config               |
           +---------------+---------------+
                           |
                           v
           +-------------------------------+
           |          Application          |
           |-------------------------------|
           | - Input ports (*UseCase)      |
           | - Services                    |
           | - Output ports (*Repository)  |
           +---------------+---------------+
                           |
                           v
           +-------------------------------+
           |             Domain            |
           |-------------------------------|
           | - WorkDay                     |
           | - Clocking                    |
           | - DailyPolicy                 |
           | - WorkMonth                   |
           +-------------------------------+
```

### Regla clave

-
`domain`
y
`application`
no
deben
depender
de
Spring/JPA.
-
`infrastructure`
depende
de
capas
internas,
nunca
al
reves.

---

## 🔄 Flujo principal de fichaje

```text
Client
  |
  | POST /api/clockings/clock-in
  v
ClockingWebController
  |
  | clockIn()
  v
ClockInUseCase (port)
  |
  v
ClockingService
  |
  | findByDate(today)
  v
WorkDayRepository
  |
  v
WorkDay (create if absent)
  |
  | addClocking(now, IN/OUT)
  | calculateNetTimeWorked(policy)
  v
WorkDayRepository.save(workDay)
  |
  v
ClockingWebController -> 201 + WorkDayWebResponse
```

---

## 🔌 Endpoints actuales

### WorkDay

-
`GET /api/workdays?month=YYYY-MM`
-
`GET /api/workdays/{date}`
-
`GET /api/workdays/range?from=YYYY-MM-DD&to=YYYY-MM-DD`
-
`GET /api/workdays/balance?month=YYYY-MM`
-
`POST /api/workdays`
-
`PUT /api/workdays`
-
`DELETE /api/workdays/{date}`

### Clocking

-
`POST /api/clockings/clock-in`
-
`POST /api/clockings`
-
`PUT /api/clockings`
-
`DELETE /api/clockings/{date}/{time}` (
`time`
esperado:
`HH:mm`)

---

## 🧭 Use cases y rutas

| Endpoint                              | Use case                         | Resultado esperado           |
|---------------------------------------|----------------------------------|------------------------------|
| `POST /api/clockings/clock-in`        | `ClockInUseCase`                 | `201` + `WorkDayWebResponse` |
| `POST /api/clockings`                 | `CreateClockingUseCase`          | `201` + `WorkDayWebResponse` |
| `PUT /api/clockings`                  | `UpdateClockingUseCase`          | `200` + `WorkDayWebResponse` |
| `DELETE /api/clockings/{date}/{time}` | `DeleteClockingUseCase`          | `204 No Content`             |
| `GET /api/workdays?month=...`         | `FindWorkDaysByMonthUseCase`     | `200` + lista                |
| `GET /api/workdays/{date}`            | `FindWorkDayByDateUseCase`       | `200` + item                 |
| `GET /api/workdays/range?...`         | `FindWorkDaysByDateRangeUseCase` | `200` + lista                |
| `GET /api/workdays/balance?month=...` | `CalculateMonthlyBalanceUseCase` | `200` + `double`             |

---

## 🧪 Ejemplos cURL

```bash
# Workdays por mes
curl "http://localhost:8080/api/workdays?month=2023-10"

# Balance mensual
curl "http://localhost:8080/api/workdays/balance?month=2023-10"

# Clock-in automatico
curl -X POST "http://localhost:8080/api/clockings/clock-in"

# Crear fichaje manual
curl -X POST "http://localhost:8080/api/clockings" \
  -H "Content-Type: application/json" \
  -d '{"date":"2023-10-02","time":"15:00:00","type":"IN"}'

# Borrar fichaje por fecha/hora
curl -X DELETE "http://localhost:8080/api/clockings/2023-10-02/15:00"
```

---

## ⚙️ Configuracion relevante

Archivo:
`src/main/resources/application.properties`

-
`spring.jpa.open-in-view=false` (
evita
sesión
abierta
en
capa
web)
-
`ss.policy.target-weekly-hours=PT37H30M` (
objetivo
semanal
contractual)

---

## ✅ Estrategia de testing

-
Domain
tests:
unitarios
puros (
sin
Spring)
-
Application
service
tests:
Mockito
-
Web
controller
tests:
`@WebMvcTest` +
`MockMvc`

### Errores HTTP comunes

-
`400 Bad Request`:
formato
invalido (
fecha/hora/enum/json)
-
`404 Not Found`:
recurso
o
configuracion
no
encontrada
-
`409 Conflict`:
recurso
ya
existente (
segun
regla
de
negocio)
-
`500 Internal Server Error`:
error
no
controlado

---

## 🗺️ Roadmap corto de proximas features

-
Gestion
de
usuarios
y
autenticacion (
Spring
Security)
-
Configuración
de
roles
y
permisos (
admin,
user)
-
Frontend
con
Angular
-
Documentar
API
con
OpenAPI/Swagger
-
Implementación
de
PostgreSQL
para
producción
-
Persistencia
de
configuracion
en
BD (
en
lugar
de
properties)
-
Configuracion
de
politicas
diarias/semanales (
horas
objetivo,
tolerancias,
etc)
-
Exportacion
de
reportes (
PDF/Excel)
-
Integracion
con
calendarios (
Google
Calendar,
Outlook)
-
Notificaciones (
email,
push)
para
recordatorios
de
fichaje
o
alertas
de
balance
-
Dashboard
web
con
estadisticas
y
visualizaciones (
Grafana/Metabase)
-
Mobile
app (
Flutter/React
Native)
-
Integracion
con
sistemas
de
control
de
acceso (
tarjetas,
biometria)
-
Integracion
con
sistemas
de
RRHH (
SAP,
Workday,
etc)


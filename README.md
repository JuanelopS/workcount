# 🏗️ WorkCount

**WorkCount** es una aplicación que permite registrar las jornadas laborales (entradas y salidas) y calcula automáticamente el balance de horas mensuales, ayudándote a saber si vas al día, si te sobran horas o si te faltan.

El proyecto está construido siguiendo los principios de la **Arquitectura Hexagonal (Ports & Adapters)** para garantizar un código mantenible, testeable y totalmente desacoplado de la infraestructura.

---

## 🚀 Estado del Proyecto
Actualmente, el proyecto se encuentra en **fase inicial de desarrollo**, centrando los esfuerzos en el **Backend**.

---

## 🛠️ Tecnologías Utilizadas

### Backend
* **Java 17+**
* **Spring Boot 3**
* **Spring Data JPA** (Persistencia)
* **H2 Database** (Base de datos en memoria para desarrollo ágil y agnóstico).
* **Maven** (Gestión de dependencias).

### Frontend (Próximamente)
* **Angular 17+**
* **Tailwind CSS** (aun no lo tengo claro...)

---

## 📐 Arquitectura: El Hexágono

Para mantener la lógica de negocio aislada de agentes externos, el backend se organiza en tres capas principales:

| Capa | Responsabilidad |
| :--- | :--- |
| **Domain** | El corazón. Entidades de negocio, reglas de cálculo y excepciones propias. Cero dependencias externas. |
| **Application** | Casos de uso (servicios) e interfaces de entrada/salida (Ports). |
| **Infrastructure** | Implementaciones técnicas (Adapters): Controladores REST, Repositorios JPA, Configuración de Spring. |


> Al usar Arquitectura Hexagonal, el cambio de la base de datos **H2** a otra de producción (como PostgreSQL) solo requiere crear un nuevo Adaptador en la capa de infraestructura, sin tocar una sola línea de la lógica de negocio.

---

## 📋 Funcionalidades Principales

1.  **Registro de Fichajes**: Registro de entrada y salida.
2.  **Cálculo Automático**: Cálculo de horas netas trabajadas.
3.  **Balance Mensual**: Visualización de horas extra o faltantes respecto a la jornada teórica.
4.  **Agnosticismo de Datos**: Persistencia desacoplada para facilitar migraciones de DB.

---

## 📝 Licencia
Este proyecto es de código abierto bajo la licencia **MIT**.

---

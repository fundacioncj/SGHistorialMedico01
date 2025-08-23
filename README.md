# SGHistorialMedico01 — Microservicio de Historia Clínica Única

## 📄 Descripción

Este microservicio implementa la gestión digital del **Formulario 002 CONSULTA EXTERNA 2021** del Sistema de Historia Clínica Única (HCU) del Ecuador. El sistema digitaliza completamente el proceso de consulta externa médica, siguiendo las normativas y estándares del Ministerio de Salud Pública del Ecuador.

---

## 🏛️ Arquitectura

El proyecto implementa una **Arquitectura Hexagonal** (Puertos y Adaptadores) combinada con **Domain-Driven Design (DDD)** y patrones **CQRS**, garantizando separación de responsabilidades, alta cohesión y bajo acoplamiento.

### 📂 Estructura del Proyecto Refactorizada

```text
🏛️ ARQUITECTURA HEXAGONAL + DDD + CQRS
├── 🔵 DOMINIO (CORE BUSINESS)
│   ├── 📋 domain/consultaexterna/
│   │   ├── 🏢 ConsultaExterna.java (Aggregate Root)
│   │   ├── 💎 valueobjects/
│   │   └── 🔢 enums/
├── 🟡 APLICACIÓN (ORCHESTRATION - CQRS)
│   ├── 📤 commands/ (Write Operations)
│   ├── 📥 queries/ (Read Operations)
│   └── 🎯 handlers/ (Command/Query Handlers)
├── 🟢 INFRAESTRUCTURA (ADAPTERS)
│   ├── 🌐 rest/consultaexterna/
│   ├── 🗄️ persistence/consultaexterna/
│   └── ⚙️ config/
└── 📋 DOCUMENTACIÓN Y PRUEBAS/
    ├── 📂 examples/ (Cuerpos de solicitud JSON)
    ├── 📜 curl-suite.sh (Suite de pruebas de API)
    ├── 📜 endpoints.json (Definición de API para máquinas)
    └── 📜 ENDPOINTS-REPORT.md (Reporte de API para humanos)
```

---

## 📋 Mapeo del Formulario 002

El sistema mapea las secciones del formulario oficial a Objetos de Valor de dominio, asegurando una representación digital fiel y validada.

| Sección | Descripción                          | `Value Object` Correspondiente     | Estado         |
| ------- | ------------------------------------ | ---------------------------------- | -------------- |
| A-B     | Datos del Establecimiento y Paciente | `DatosFormulario`, `DatosPaciente` | ✅ Implementado |
| C, E    | Motivo y Enfermedad Actual           | `Anamnesis`                        | ✅ Implementado |
| G       | Constantes Vitales                   | `SignosVitales`                    | ⚡ Mejorado     |
| H       | Examen Físico                        | `ExamenFisico`                     | ✅ Implementado |
| I       | Diagnóstico CIE-10                   | `Diagnostico`                      | ✅ Implementado |
| J-M     | Plan, Interconsultas, Prescripciones | `PlanTratamiento`, `Interconsulta` | ⚡ Mejorado     |

---

## 🌐 API REST: Endpoints Validados

La siguiente tabla resume los endpoints expuestos por la API, los cuales han sido validados a través de análisis de código y pruebas de integración.

| Verbo  | Ruta                                                 | Descripción                                                   |
| ------ | ---------------------------------------------------- | ------------------------------------------------------------- |
| POST   | `/api/v1/consultas-externas`                         | Crea una nueva consulta externa.                              |
| GET    | `/api/v1/consultas-externas/{id}`                    | Obtiene una consulta por su ID único.                         |
| GET    | `/api/v1/consultas-externas/paciente/{cedula}`       | Busca el historial de consultas de un paciente por su cédula. |
| GET    | `/api/v1/consultas-externas/numero/{numeroConsulta}` | Busca una consulta por su número de formulario.               |
| GET    | `/api/v1/consultas-externas/buscar`                  | Realiza una búsqueda avanzada por fechas, especialidad, etc.  |
| PUT    | `/api/v1/consultas-externas/{id}`                    | Actualiza una consulta existente.                             |
| DELETE | `/api/v1/consultas-externas/{id}`                    | Elimina una consulta (borrado lógico).                        |

> **Nota:** El cuerpo de las solicitudes `POST` y `PUT` debe corresponder con los comandos `CrearConsultaExternaCommand` y `ActualizarConsultaExternaCommand`, respectivamente. Para ejemplos detallados, consulte la carpeta `/examples`.

---

## 🚀 Automatización y Herramientas de Desarrollo

Para facilitar el desarrollo y las pruebas, se han generado automáticamente los siguientes artefactos:

* `ENDPOINTS-REPORT.md`: Informe legible de todos los endpoints, con parámetros, formatos de datos y notas de configuración.
* `endpoints.json`: Especificación de la API en JSON, útil para Postman o generadores de clientes.
* `examples/*.json`: Ejemplos de cuerpos de solicitud (payloads) para crear y actualizar consultas.
* `curl-suite.sh`: Script con un conjunto de pruebas de integración que invoca todos los endpoints (ideal para regresión desde la terminal).

### ▶️ Uso de la Suite de Pruebas

Asegúrese de que el script tenga permisos de ejecución y luego invoque alguna función de prueba:

```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x curl-suite.sh

# Ejecutar una de las funciones de prueba, por ejemplo:
./curl-suite.sh get_consulta_por_id "60f1a5c2e8f87a2b94c12345"
```

---

## 🔧 Lógica de Negocio Implementada

El sistema incluye reglas de negocio clave para mejorar la calidad de la atención:

* **Clasificación Automática de Presión Arterial**: Basado en guías actuales (p. ej., ACC/AHA).
* **Detección de Alertas Críticas**: Generación de alertas cuando los signos vitales están en rangos peligrosos.
* **Validación de Integridad**: Anotaciones de validación para asegurar que los datos entrantes sean completos y correctos.
* **Auditoría**: Registro automático de creación y modificación de cada consulta.

---

## ⚙️ Configuración e Índices

La persistencia se gestiona con **MongoDB**. Se han configurado los siguientes índices para optimizar el rendimiento de las consultas:

```javascript
db.consultas_externas.createIndex({"datosPaciente.cedula": 1});
db.consultas_externas.createIndex({"numeroConsulta": 1}, { unique: true });
db.consultas_externas.createIndex({"datosConsulta.fechaConsulta": -1});
```

El proyecto contiene archivos `application-{profile}.properties` para configurar la conexión a la base de datos y otros parámetros para los entornos de `dev`, `test` y `prod`.

---

## 🧪 Testing

El proyecto mantiene un alto estándar de calidad a través de:

* **Tests Unitarios**: JUnit y Mockito para probar la lógica de dominio de forma aislada.
* **Tests de Integración**: Spring Boot Test y Testcontainers para validar el flujo completo (incluida la persistencia).

```bash
# Ejecutar todos los tests
./mvnw verify
```

---

## 📖 Documentación API (en vivo)

La especificación OpenAPI y la interfaz de usuario de Swagger se generan automáticamente y están disponibles en los siguientes endpoints (una vez que la aplicación está en ejecución):

* **Swagger UI**: `http://localhost:8081/swagger-ui.html`
* **OpenAPI Spec (JSON)**: `http://localhost:8081/v3/api-docs`

---

## 🧠 Memoria Técnica: Módulo de Consulta Externa `SGHistorialMedico01`

### 1. Resumen Ejecutivo

Este documento detalla la arquitectura, funcionalidades y estado de validación del microservicio, con foco en **Consulta Externa** y la digitalización del **Formulario 002** del HCU.

Se verificó que el sistema cumple con los requerimientos funcionales y principios de diseño robustos. Se generaron artefactos de documentación y pruebas (informe de endpoints, payloads de ejemplo y suite `curl`), asegurando calidad y confiabilidad del servicio.

### 2. Arquitectura y Diseño Técnico

* **Arquitectura Hexagonal (Puertos y Adaptadores)**: desacopla la lógica de negocio de la infraestructura.
* **DDD**: modela el dominio médico con agregados y objetos de valor.
* **CQRS**: separa operaciones de escritura (comandos) y lectura (consultas).

**Estructura por capas:**

* **Dominio (`domain`)**: Agregado `ConsultaExterna`, objetos de valor y reglas críticas.
* **Aplicación (`application`)**: orquestación de `commands`/`queries` y sus `handlers`.
* **Infraestructura (`infrastructure`)**: adaptadores REST, persistencia MongoDB y configuración.

### 3. Análisis Funcional: Digitalización del Formulario 002

| Sección del Formulario     | Objeto de Valor de Dominio (`Value Object`) | Estado         |
| -------------------------- | ------------------------------------------- | -------------- |
| Datos del Establecimiento  | `DatosFormulario` / `DatosConsulta`         | ✅ Implementado |
| Datos del Paciente         | `DatosPaciente`                             | ✅ Implementado |
| Motivo y Enfermedad Actual | `Anamnesis`                                 | ✅ Implementado |
| Antecedentes y Hábitos     | `Anamnesis`                                 | ✅ Implementado |
| Constantes Vitales         | `SignosVitales`                             | ⚡ Mejorado     |
| Examen Físico              | `ExamenFisico`                              | ✅ Implementado |
| Diagnóstico (CIE-10)       | `Diagnostico`                               | ✅ Implementado |
| Plan de Tratamiento        | `PlanTratamiento`                           | ✅ Implementado |
| Interconsultas             | `Interconsulta`                             | ⚡ Mejorado     |
| Prescripciones             | `Prescripcion`                              | ✅ Implementado |

> Las filas **Mejorado** indican lógica adicional (p. ej., clasificación automática de PA o gestión de estados en interconsultas).

### 4. Validación y Documentación de la API REST

Proceso de validación:

1. **Inspección de Endpoints** en controladores (rutas/verbos/DTOs).
2. \`\`: resumen de endpoints, parámetros y formatos.
3. \`\`: definición estructurada para herramientas automatizadas.
4. \`\`: funciones que invocan cada endpoint con datos de `examples/*.json`.
5. **Validaciones de entrada**: anotaciones de Jakarta en `Commands`.

**Endpoints principales:**

| Verbo    | Ruta                                           | Propósito                          |
| -------- | ---------------------------------------------- | ---------------------------------- |
| `POST`   | `/api/v1/consultas-externas`                   | Crear una nueva consulta externa.  |
| `GET`    | `/api/v1/consultas-externas/{id}`              | Obtener una consulta por su ID.    |
| `GET`    | `/api/v1/consultas-externas/paciente/{cedula}` | Buscar consultas por cédula.       |
| `GET`    | `/api/v1/consultas-externas/numero/{numero}`   | Buscar por número único.           |
| `GET`    | `/api/v1/consultas-externas/buscar`            | Búsqueda avanzada con filtros.     |
| `PUT`    | `/api/v1/consultas-externas/{id}`              | Actualizar una consulta existente. |
| `DELETE` | `/api/v1/consultas-externas/{id}`              | Eliminar una consulta.             |

### 5. Lógica de Negocio y Flujos de Proceso

* **Flujo de consulta**: distingue entre primera vez vs. subsecuente.
* **Signos vitales**: clasificación de PA (`NORMAL`, `ELEVADA`, `CRISIS_HIPERTENSIVA`, etc.) y alertas de riesgo.
* **Interconsultas**: máquina de estados `SOLICITADA → AGENDADA → COMPLETADA/CANCELADA`.
* **Auditoría**: `DatosAuditoria` registra usuario/fecha en operaciones de escritura.

### 6. Infraestructura y Persistencia

* **MongoDB** con índices para cédula, número de consulta y fecha.
* Configuración lista para `dev` y `prod`.

### 7. Conclusión

El módulo de Consulta Externa está **maduro y robusto**. La arquitectura es sólida, escalable y mantenible. La funcionalidad principal está implementada y validada; existen herramientas de documentación y prueba que facilitan integración y evolución futura.
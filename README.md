# SGHistorialMedico01 - Microservicio de Historia Clínica Única

## 📄 Descripción

Este microservicio implementa la gestión digital del **Formulario 002 CONSULTA EXTERNA 2021** del Sistema de Historia Clínica Única (HCU) de Ecuador. El sistema digitaliza completamente el proceso de consulta externa médica, siguiendo las normativas y estándares del Ministerio de Salud Pública del Ecuador.

## 🏛️ Arquitectura

El proyecto implementa una **Arquitectura Hexagonal** (Puertos y Adaptadores) combinada con **Domain-Driven Design (DDD)**, garantizando separación de responsabilidades, alta cohesión y bajo acoplamiento.

### 📂 Estructura del Proyecto Refactorizada

```text
🏛️ ARQUITECTURA HEXAGONAL + DDD
├── 🔵 DOMINIO (CORE BUSINESS)
│   ├── 📋 domain/consultaexterna/
│   │   ├── 🏢 ConsultaExterna.java (Aggregate Root)
│   │   ├── 💎 valueobjects/
│   │   └── 🔢 enums/
├── 🟡 APLICACIÓN (ORQUESTACIÓN)
│   ├── 📤 commands/ (Operaciones de escritura)
│   ├── 📥 queries/ (Operaciones de lectura)
│   └── 🎯 handlers/ (Orquestación de casos de uso)
├── 🟢 INFRAESTRUCTURA (ADAPTERS)
│   ├── 🌐 rest/consultaexterna/
│   ├── 🗄️ persistence/consultaexterna/
│   └── ⚙️ config/
└── 📋 DOCUMENTACIÓN Y PRUEBAS/
    ├── 📂 Collection-postman/ (Cuerpos de solicitud JSON y Colección Postman)
    ├── 📜 curl-suite.sh (Suite de pruebas de API)
    ├── 📜 endpoints.json (Definición de API para máquinas)
    ├── 📜 openapi.yaml (Especificación OpenAPI 3.1.0)
    └── 📜 ENDPOINTS-REPORT.md (Reporte de API para humanos)
```

## 📋 Mapeo del Formulario 002

El sistema mapea las secciones del formulario oficial a Objetos de Valor de dominio, asegurando una representación digital fiel y validada.

| Sección | Descripción | `Value Object` Correspondiente | Estado |
|---------|-------------|--------------------------------|--------|
| A-B | Datos del Establecimiento y Paciente | `DatosFormulario`, `DatosPaciente` | ✅ Implementado |
| C, E | Motivo y Enfermedad Actual | `Anamnesis` | ✅ Implementado |
| G | Constantes Vitales | `SignosVitales` | ⚡ Mejorado |
| H | Examen Físico | `ExamenFisico` | ✅ Implementado |
| I | Diagnóstico CIE-10 | `Diagnostico` | ✅ Implementado |
| J-M | Plan, Interconsultas, Prescripciones | `PlanTratamiento`, `Interconsulta` | ⚡ Mejorado |


## 🌐 API REST: Endpoints Validados

La siguiente tabla resume los endpoints expuestos por la API, los cuales han sido validados a través de análisis de código y pruebas de integración.

| Verbo | Ruta | Descripción |
|-------|------|-------------|
| POST | `/api/v1/consultas-externas` | Crea una nueva consulta externa. |
| GET | `/api/v1/consultas-externas/{id}` | Obtiene una consulta por su ID único. |
| GET | `/api/v1/consultas-externas/paciente/{cedula}` | Busca el historial de consultas de un paciente por su cédula. |
| GET | `/api/v1/consultas-externas/numero/{numero}` | Busca una consulta por su número de formulario. |
| GET | `/api/v1/consultas-externas/buscar` | Realiza una búsqueda avanzada por fechas, especialidad, etc. |
| PUT | `/api/v1/consultas-externas/{id}` | Actualiza una consulta existente. |
| DELETE| `/api/v1/consultas-externas/{id}` | Elimina una consulta (marcado lógico). |

### 📎 Ejemplos prácticos (POST y GET)

> Basados en la colección **SGHistorialMedico01.postman_collection.json** y el análisis del proyecto (payloads y rutas coherentes con los DTOs y controladores).

#### POST — Crear una consulta externa
`POST http://localhost:8081/api/v1/consultas-externas`  
Headers: `Content-Type: application/json`

```json
{
  "usuarioCreador": "admin.sistema",
  "datosFormulario": {
    "numeroFormulario": "HCU-002-2025-0001",
    "establecimiento": "Hospital General",
    "codigoEstablecimiento": "1234",
    "areaSalud": "Zona 8",
    "distrito": "Guayaquil",
    "canton": "Guayaquil",
    "provincia": "Guayas"
  },
  "datosPaciente": {
    "cedula": "0912345678",
    "numeroHistoriaClinica": "HCL-0001",
    "primerNombre": "Juan",
    "segundoNombre": "Carlos",
    "apellidoPaterno": "Pérez",
    "apellidoMaterno": "Gómez",
    "fechaNacimiento": "1990-05-15",
    "sexo": "MASCULINO",
    "direccion": "Av. Principal 123",
    "telefono": "0991234567",
    "email": "juan.perez@example.com",
    "contactoEmergencia": "María Pérez",
    "telefonoEmergencia": "0987654321"
  },
  "datosConsulta": {
    "numeroConsulta": "CE-2025-000123",
    "fechaConsulta": "2025-08-12T10:30:00",
    "especialidad": "Medicina Interna",
    "medicoTratante": "Dr. José Morales",
    "codigoMedico": "MED-123",
    "tipoConsulta": "PRIMERA_VEZ",
    "motivoConsulta": "Dolor torácico de 2 días",
    "observaciones": "Paciente estable"
  },
  "anamnesis": {
    "enfermedadActual": "Dolor torácico opresivo, irradiado a brazo izquierdo",
    "antecedentesPatologicosPersonales": "Hipertensión",
    "antecedentesPatologicosFamiliares": "Cardiopatía en padre",
    "antecedentesQuirurgicos": "Apendicectomía 2010",
    "antecedentesGinecoObstetricos": null,
    "habitos": {
      "fuma": false,
      "bebeAlcohol": false,
      "actividadFisica": "Moderada",
      "dieta": "Balanceada"
    },
    "medicamentosActuales": ["Enalapril 10mg"],
    "alergias": ["Penicilina"],
    "revisionSistemas": "Sin hallazgos relevantes",
    "observaciones": "Ninguna"
  },
  "examenFisico": {
    "signosVitales": {
      "presionArterial": "120/80",
      "frecuenciaCardiaca": 80,
      "frecuenciaRespiratoria": 16,
      "temperatura": 36.8,
      "saturacionOxigeno": 98
    },
    "examenGeneral": "Paciente consciente, orientado x3",
    "examenesRegionales": [
      {
        "region": "Torax",
        "descripcion": "Murmullo vesicular conservado",
        "hallazgos": [],
        "normal": true
      }
    ],
    "medidasAntropometricas": {
      "pesoKg": 75.0,
      "tallaM": 1.75,
      "imc": 24.5,
      "cinturaCm": 85.0,
      "caderaCm": 95.0
    }
  },
  "diagnosticos": [
    {
      "codigoCie10": "I20.0",
      "descripcion": "Angina inestable",
      "tipo": "PRINCIPAL",
      "observaciones": "Evaluar marcadores cardíacos",
      "fechaDiagnostico": "2025-08-12T10:45:00",
      "severidad": "MODERADO",
      "requiereSeguimiento": true,
      "tiempoSeguimientoMeses": 3,
      "planSeguimiento": "Control en 3 meses",
      "manifestacionesClinicas": ["Dolor torácico"],
      "factoresRiesgo": ["HTA"],
      "esCronico": false,
      "requiereInterconsulta": false
    }
  ],
  "planTratamiento": {
    "prescripciones": [
      {
        "medicamento": "Aspirina",
        "dosis": "100 mg",
        "frecuencia": "cada 24h",
        "viaAdministracion": "Oral",
        "duracionDias": 30,
        "indicaciones": "Tomar después de alimentos",
        "fechaPrescripcion": "2025-08-12",
        "cantidad": 30,
        "unidadMedida": "tabletas",
        "medicamentoGenerico": true
      }
    ],
    "indicacionesGenerales": ["Reposo relativo"],
    "citasSeguimiento": [
      {
        "fecha": "2025-09-12T09:00:00",
        "motivo": "Control",
        "urgente": false
      }
    ],
    "interconsultas": [],
    "recomendaciones": ["Dieta baja en sodio"],
    "planEducacional": "Educación sobre factores de riesgo cardiovascular"
  }
}
```

**cURL equivalente:**
```bash
curl -X POST "http://localhost:8081/api/v1/consultas-externas"   -H "Content-Type: application/json"   --data-binary @Collection-postman/CrearConsultaExternaCommand.json
```

#### GET — Consultar una consulta por ID
`GET http://localhost:8081/api/v1/consultas-externas/{id}`

**cURL:**
```bash
curl -X GET "http://localhost:8081/api/v1/consultas-externas/60f1a5c2e8f87a2b94c12345"
```

#### GET — Consultar historial por cédula (con filtros opcionales)
`GET http://localhost:8081/api/v1/consultas-externas/paciente/{cedula}?fechaDesde=YYYY-MM-DD&fechaHasta=YYYY-MM-DD&pagina=0&tamanio=10`

**cURL:**
```bash
curl -G "http://localhost:8081/api/v1/consultas-externas/paciente/0912345678"   --data-urlencode "fechaDesde=2025-01-01"   --data-urlencode "fechaHasta=2025-12-31"   --data-urlencode "pagina=0"   --data-urlencode "tamanio=10"
```

> **Tip**: Si usas `server.servlet.context-path`, antepone el contexto a las rutas (p. ej., `/sg-historial/api/v1/...`).

## 🚀 Automatización y Herramientas de Desarrollo

Para facilitar el desarrollo y las pruebas, se han generado automáticamente los siguientes artefactos:

*   **`ENDPOINTS-REPORT.md`**: Un informe detallado y legible de todos los endpoints, incluyendo parámetros, formatos de datos y notas importantes sobre la configuración.
*   **`endpoints.json`**: Una especificación de la API en formato JSON, ideal para importar en herramientas como Postman o para alimentar generadores de clientes.
*   **`Collection-postman/*.json`**: Archivos JSON con ejemplos de cuerpos de solicitud (`payloads`) para crear y actualizar consultas, listos para ser usados en pruebas.
*   **`Collection-postman/SGHistorialMedico01.postman_collection.json`**: Colección Postman completa y funcional para probar los endpoints.
*   **`openapi.yaml`**: Especificación **OpenAPI 3.1.0** compatible con Swagger UI.
*   **`curl-suite.sh`**: Un script de shell con un conjunto de pruebas de integración que invoca a todos los endpoints de la API. Es una herramienta excelente para realizar pruebas de regresión rápidas desde la línea de comandos.

### Uso de la Suite de Pruebas

Para ejecutar la suite de pruebas completa, asegúrese de que el script tenga permisos de ejecución y luego invóquelo:
```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x curl-suite.sh

# Crear (usa payload de Collections-postman/CrearConsultaExternaCommand.json)
./curl-suite.sh post_consultas_externas_min

# Obtener por ID
./curl-suite.sh get_consulta_por_id 60f1a5c2e8f87a2b94c12345

# Buscar por cédula (simple y con filtros)
./curl-suite.sh get_consultas_por_cedula_min 0912345678
./curl-suite.sh get_consultas_por_cedula_full 0912345678 2025-01-01 2025-12-31 0 10

# Búsqueda avanzada
./curl-suite.sh get_consultas_buscar_min 2025-01-01 2025-12-31
./curl-suite.sh get_consultas_buscar_full 2025-01-01 2025-12-31 Cardiología "Dr.%20Juan%20Pérez" COMPLETADA 0 10

# Actualizar (usa Collections-postman/ActualizarConsultaExternaCommand.json)
./curl-suite.sh put_consulta_actualizar_min 60f1a5c2e8f87a2b94c12345

# Eliminar (lógica)
./curl-suite.sh delete_consulta_min 60f1a5c2e8f87a2b94c12345 "Registro duplicado"
```

## 🔧 Lógica de Negocio Implementada

El sistema incluye reglas de negocio clave para mejorar la calidad de la atención:

*   **Clasificación Automática de Presión Arterial**: Basado en las guías actuales.
*   **Detección de Alertas Críticas**: Generación de alertas automáticas cuando los signos vitales están en rangos peligrosos.
*   **Validación de Integridad**: Uso de anotaciones de validación para asegurar que los datos entrantes son completos y correctos antes de ser procesados.
*   **Auditoría**: Registro automático de la creación y modificación de cada consulta.

## ⚙️ Configuración e Índices

La persistencia se gestiona con **MongoDB**. Se han configurado los siguientes índices para optimizar el rendimiento de las consultas:
```javascript
db.consultas_externas.createIndex({"datosPaciente.cedula": 1});
db.consultas_externas.createIndex({"numeroConsulta": 1}, {unique: true});
db.consultas_externas.createIndex({"datosConsulta.fechaConsulta": -1});
```
El proyecto contiene archivos `application-{profile}.properties` para configurar la conexión a la base de datos y otros parámetros para los entornos de `dev`, `test` y `prod`.

## 🧪 Testing

El proyecto mantiene un alto estándar de calidad a través de:

*   **Tests Unitarios**: Utilizando JUnit y Mockito para probar la lógica de dominio de forma aislada.
*   **Tests de Integración**: Con Spring Boot Test y Testcontainers para validar el flujo completo, incluyendo la capa de persistencia.
```bash
# Ejecutar todos los tests
./mvnw verify
```

## 📖 Documentación API (en vivo)

La especificación OpenAPI y la interfaz de usuario de Swagger se generan automáticamente y están disponibles en los siguientes endpoints una vez que la aplicación está en ejecución:

*   **Swagger UI**: `http://localhost:8081/swagger-ui.html`
*   **OpenAPI Spec (JSON)**: `http://localhost:8081/v3/api-docs`

---

### **Memoria Técnica: Módulo de Consulta Externa SGHistorialMedico01**

#### **1. Resumen Ejecutivo**

El presente documento detalla la arquitectura, funcionalidades y estado de validación del microservicio `SGHistorialMedico01`, con especial foco en el módulo de **Consulta Externa**. El sistema tiene como objetivo principal la digitalización completa y la gestión del **Formulario 002 de Consulta Externa** del sistema de Historia Clínica Única (HCU) de Ecuador.

Tras un análisis exhaustivo del código fuente y la API REST, se ha verificado que el sistema no solo cumple con los requerimientos funcionales, sino que también se adhiere a principios de diseño de software robustos. Se ha generado un conjunto completo de artefactos de documentación y pruebas, incluyendo un informe detallado de endpoints, ejemplos de carga útil (payloads) y una suite de pruebas automatizadas con `curl`, asegurando la calidad y fiabilidad del servicio.

#### **2. Arquitectura y Diseño Técnico**

El microservicio se fundamenta en una **Arquitectura Hexagonal (Puertos y Adaptadores)**, un enfoque moderno que garantiza un excelente desacoplamiento entre la lógica de negocio central y los componentes de infraestructura (p. ej., la API REST, la base de datos).

La arquitectura se complementa con principios de **Domain-Driven Design (DDD)** para modelar el complejo dominio médico y mantener un alto grado de coherencia en los modelos de dominio y sus invariantes.

La estructura del proyecto refleja esta arquitectura de manera clara:

*   **Dominio (`domain`)**: Contiene el núcleo del negocio, incluyendo el Agregado `ConsultaExterna`, los Objetos de Valor (`Value Objects`) que representan cada sección del formulario, y las reglas de negocio críticas. Es la capa más aislada y fundamental del sistema.
*   **Aplicación (`application`)**: Orquesta los flujos de trabajo de los casos de uso, coordinando comandos y consultas sin exponer detalles de infraestructura.
*   **Infraestructura (`infrastructure`)**: Implementa los adaptadores para tecnologías externas. Incluye el controlador REST (`ConsultaExternaController`), la implementación del repositorio con MongoDB (`MongoConsultaExternaRepositoryAdapter`) y la configuración del servicio.

#### **3. Análisis Funcional: Digitalización del Formulario 002**

El sistema ha implementado con éxito la digitalización de todas las secciones principales del Formulario 002 de Consulta Externa. Cada sección se ha mapeado a un Objeto de Valor específico en el dominio, lo que asegura una representación fiel y validada de los datos.

| Sección del Formulario | Objeto de Valor de Dominio (`Value Object`) | Estado Actual |
| :--- | :--- | :--- |
| Datos del Establecimiento | `DatosFormulario` / `DatosConsulta` | ✅ Implementado |
| Datos del Paciente | `DatosPaciente` | ✅ Implementado |
| Motivo y Enfermedad Actual | `Anamnesis` | ✅ Implementado |
| Antecedentes y Hábitos | `Anamnesis` | ✅ Implementado |
| Constantes Vitales | `SignosVitales` | ⚡ Mejorado |
| Examen Físico | `ExamenFisico` | ✅ Implementado |
| Diagnóstico (CIE-10) | `Diagnostico` | ✅ Implementado |
| Plan de Tratamiento | `PlanTratamiento` | ✅ Implementado |
| Interconsultas | `Interconsulta` | ⚡ Mejorado |
| Prescripciones | `Prescripcion` | ✅ Implementado |

Las secciones marcadas como **"Mejorado"** indican que el sistema no solo digitaliza los datos, sino que añade lógica de negocio de valor, como la clasificación automática de la presión arterial o la gestión de estados para las interconsultas.

#### **4. Validación y Documentación de la API REST**

Se ha completado un proceso de validación exhaustivo de la API REST expuesta por el sistema. Este proceso confirma que la implementación se alinea con la definición de diseño y es robusta frente a entradas inválidas.

1.  **Inspección de Endpoints**: Se analizaron los controladores de Spring para identificar todas las rutas, verbos HTTP y DTOs asociados.
2.  **Generación de Informe (`ENDPOINTS-REPORT.md`)**: Se creó un informe legible que resume cada endpoint, sus parámetros, formatos de fecha, enumeraciones y si requiere autenticación (actualmente no requerida).
3.  **Especificación Estructurada (`endpoints.json`)**: Se generó un archivo JSON que detalla la API de forma estructurada, útil para la integración con herramientas automatizadas.
4.  **Suite de Pruebas (`curl-suite.sh`)**: Se creó un script de shell con funciones para invocar cada endpoint de la API, utilizando ejemplos de datos (`Collections-postman/*.json`). Esta suite sirve como un conjunto de pruebas de regresión e integración rápidas.
5.  **Refuerzo de Validaciones**: Se emplean anotaciones de validación de Jakarta en los DTOs/Commands, asegurando la integridad de los datos en la capa de entrada de la API.

El análisis confirma la existencia de los siguientes endpoints principales:

| Verbo | Ruta | Propósito |
| :--- | :--- | :--- |
| `POST` | `/api/v1/consultas-externas` | Crear una nueva consulta externa. |
| `GET` | `/api/v1/consultas-externas/{id}` | Obtener una consulta por su ID. |
| `GET` | `/api/v1/consultas-externas/paciente/{cedula}` | Buscar consultas por cédula de paciente. |
| `GET` | `/api/v1/consultas-externas/numero/{numero}` | Buscar consulta por su número único. |
| `GET` | `/api/v1/consultas-externas/buscar` | Búsqueda avanzada con filtros. |
| `PUT` | `/api/v1/consultas-externas/{id}` | Actualizar una consulta existente. |
| `DELETE`| `/api/v1/consultas-externas/{id}` | Eliminar una consulta. |

#### **5. Lógica de Negocio y Flujos de Proceso**

El sistema implementa flujos de negocio complejos que van más allá de la simple captura de datos, incorporando validaciones clínicas automáticas.

*   **Flujo de Consulta Principal**: Diferencia entre consultas de primera vez y subsecuentes con validaciones acordes.
*   **Evaluación de Signos Vitales**: Clasificación automática de presión arterial y alertas clínicas.
*   **Gestión de Interconsultas**: Seguimiento de estados operativos para su trazabilidad.
*   **Auditoría y Trazabilidad**: Registro de acciones relevantes con usuario/fecha.

#### **6. Infraestructura y Persistencia**

La persistencia de los datos se gestiona a través de una base de datos **MongoDB**. Se han definido y aplicado índices optimizados para las consultas más frecuentes, como la búsqueda por cédula de paciente, número de consulta y fechas, garantizando un rendimiento adecuado a escala. La configuración del proyecto está preparada para distintos entornos (`dev`, `prod`), facilitando el despliegue y la gestión.


## 🗄️ Implementación en MongoDB

### Base de datos y colecciones

- **Base de datos**: `historial_medico`
- **Colección principal**: `consultas-externas`
- **Colecciones relacionadas (opcionales/auxiliares)**: `diagnostico`, `paciente`, `profesional`
- **Convenciones**
    - `_id`: `ObjectId`
    - Fechas: ISO-8601 (`date`/`dateTime`) — ej. `2025-08-12T10:30:00Z`
    - Enums en **MAYÚSCULAS** (p. ej., `PRIMERA_VEZ`, `MASCULINO`, etc.)
    - Eliminación **lógica**: campos `eliminado:boolean`, `motivoEliminacion:string`, `fechaEliminacion:dateTime`

### Estructura del documento (ejemplo realista)

> Corresponde a lo que se observa en `historial_medico.consultas-externas` (MongoDB Compass) y a los DTOs usados por la API/colección Postman.

```json
{
  "_id": "ObjectId('...')",
  "admisionId": "001",
  "institucionSistema": "SNS-MSP",
  "establecimientoSalud": "Hospital General IESS Quito Sur",
  "unicodigo": "HGIESS-QUITO-SUR-001",
  "numeroHistoriaClinica": "HC-2023-0012345",
  "numeroArchivo": "ARCH-2023-0012",
  "hoja": "1",
  "tipoConsulta": "PRIMERA",
  "motivoConsulta": "Dolor abdominal recurrente de 2 semanas de evolución",

  "paciente": {
    "cedula": "0912345678",
    "numeroHistoriaClinica": "HCL-0001",
    "primerNombre": "Juan",
    "apellidoPaterno": "Pérez",
    "fechaNacimiento": "1990-05-15",
    "sexo": "MASCULINO",
    "telefono": "0991234567"
  },

  "enfermedadActual": "Dolor en hipogastrio de tipo cólico...",
  "cronologia": "Inició hace 2 semanas, intermitente",
  "localizacion": "Hipogastrio",
  "caracteristicas": "Cólico, no irradiado",
  "intensidad": "7/10",
  "frecuencia": "3-4 veces por día",
  "factoresAgravantes": "Ingesta de alimentos",

  "antecedentesPersonales": { "hta": true, "dm": false, "...": "..." },
  "antecedentesFamiliares": { "cardiopatia": true },

  "constantesVitales": [
    { "timestamp": "2025-08-12T10:30:00", "pa": "120/80", "fc": 80, "fr": 16, "temp": 36.8, "satO2": 98 }
  ],
  "revisionOrganos": { "...": "..." },

  "examenFisico": {
    "examenGeneral": "Consciente, orientado x3",
    "examenesRegionales": [
      { "region": "Torax", "descripcion": "Murmullo vesicular conservado", "normal": true }
    ],
    "medidasAntropometricas": { "pesoKg": 75, "tallaM": 1.75, "imc": 24.5 }
  },

  "diagnosticos": [
    { "codigoCie10": "I20.0", "descripcion": "Angina inestable", "tipo": "PRINCIPAL", "fechaDiagnostico": "2025-08-12T10:45:00" }
  ],

  "planTratamiento": {
    "prescripciones": [
      { "medicamento": "Aspirina", "dosis": "100 mg", "frecuencia": "cada 24h", "viaAdministracion": "Oral", "duracionDias": 30 }
    ],
    "indicacionesGenerales": ["Reposo relativo"],
    "citasSeguimiento": [{ "fecha": "2025-09-12T09:00:00", "motivo": "Control", "urgente": false }]
  },

  "profesional": { "medicoTratante": "Dr. José Morales", "codigoMedico": "MED-123", "especialidad": "Medicina Interna" },

  "numeroConsulta": "CE-2025-000123",
  "fechaConsulta": "2025-08-12T10:30:00",

  "fechaCreacion": "2025-08-12T10:40:00Z",
  "fechaActualizacion": "2025-08-12T11:00:00Z",
  "eliminado": false
}
```

### Índices y unicidad

> Algunos existen ya; otros son **recomendados** para rendimiento.

```javascript
// Búsqueda por cédula (GET /paciente/{cedula})
db.consultas_externas.createIndex({ "paciente.cedula": 1 });

// Unicidad de número de consulta (GET /numero/{numero})
db.consultas_externas.createIndex({ "numeroConsulta": 1 }, { unique: true });

// Listado reciente
db.consultas_externas.createIndex({ "fechaConsulta": -1 });

// Filtros frecuentes en /buscar
db.consultas_externas.createIndex({ "profesional.medicoTratante": 1 });
db.consultas_externas.createIndex({ "profesional.especialidad": 1 });
db.consultas_externas.createIndex({ "diagnosticos.codigoCie10": 1 });
```

### Mapeo de endpoints → operaciones MongoDB

| Endpoint | Operación en MongoDB |
|---|---|
| `POST /api/v1/consultas-externas` | `insertOne` en `consultas-externas`; set `fechaCreacion` y `fechaActualizacion`. Valida unicidad de `numeroConsulta`. |
| `GET /api/v1/consultas-externas/{id}` | `findOne({ _id: ObjectId(id), eliminado: { $ne: true } })`. |
| `GET /api/v1/consultas-externas/paciente/{cedula}` | `find({ "paciente.cedula": cedula, fechaConsulta: {$gte: desde, $lte: hasta} })` + `sort({fechaConsulta:-1})` + paginación (`skip/limit`). |
| `GET /api/v1/consultas-externas/numero/{numero}` | `findOne({ numeroConsulta: numero, eliminado: { $ne: true } })`. |
| `GET /api/v1/consultas-externas/buscar` | `find` con combinación de filtros: `{ fechaConsulta: rango?, "profesional.especialidad":?, "profesional.medicoTratante":?, estado:? }`. |
| `PUT /api/v1/consultas-externas/{id}` | `findOneAndUpdate({ _id: ObjectId(id) }, { $set: {...}, $currentDate: { fechaActualizacion: true } })`. |
| `DELETE /api/v1/consultas-externas/{id}` | **Eliminación lógica**: `updateOne({ _id: ObjectId(id) }, { $set: { eliminado: true, motivoEliminacion, fechaEliminacion: now } })`. |

### Consultas frecuentes (shell/driver)

**Historial por cédula y rango de fechas (paginado):**
```javascript
const cedula = "0912345678";
const desde = new Date("2025-01-01");
const hasta = new Date("2025-12-31");
const pagina = 0, tamanio = 10;

db.consultas_externas
  .find({ "paciente.cedula": cedula, fechaConsulta: { $gte: desde, $lte: hasta }, eliminado: { $ne: true } })
  .project({ diagnosticos: { $slice: 1 }, planTratamiento: 0 }) // ejemplo de proyección
  .sort({ fechaConsulta: -1 })
  .skip(pagina * tamanio)
  .limit(tamanio);
```

**Búsqueda avanzada (especialidad + médico + estado + fechas):**
```javascript
db.consultas_externas.find({
  fechaConsulta: { $gte: ISODate("2025-01-01T00:00:00Z"), $lte: ISODate("2025-12-31T23:59:59Z") },
  "profesional.especialidad": "Cardiología",
  "profesional.medicoTratante": "Dr. Juan Pérez",
  estado: "COMPLETADA",
  eliminado: { $ne: true }
}).sort({ fechaConsulta: -1 });
```

**Por número de consulta (único):**
```javascript
db.consultas_externas.findOne({ numeroConsulta: "CE-2025-000123", eliminado: { $ne: true } });
```

### Relación con la colección Postman

- La colección **`SGHistorialMedico01.postman_collection.json`** incluye peticiones que reflejan este modelo:
    - **POST** usa un payload completo (campos de paciente, enfermedad actual, examen físico, diagnósticos y plan).
    - **GET** por **ID** y por **cédula** aplican los filtros/parametrización de paginación (`pagina`, `tamanio`) y rango de fechas (`fechaDesde`, `fechaHasta`).
- Los ejemplos del README (POST/GET) son coherentes con ese archivo y con los controladores del proyecto.

### Configuración en `application.properties` (referencia)

> Ajusta host/puerto/credenciales según tu entorno.

```ini
# MongoDB (local)
spring.data.mongodb.uri=mongodb://localhost:27017/historial_medico
spring.data.mongodb.auto-index-creation=true

# Opcional: contexto y puerto de la app
server.port=8081
# server.servlet.context-path=/sg-historial
```

> **Buenas prácticas recomendadas**
> - Mantener **índices** alineados a los parámetros más consultados por la API.
> - Auditar cambios: actualizar `fechaActualizacion` en cada `PUT`.
> - Evitar `deleteOne`: preferir **eliminación lógica** para trazabilidad.
> - Validar enumeraciones y formatos antes de insertar/actualizar (capas de DTO/servicio).
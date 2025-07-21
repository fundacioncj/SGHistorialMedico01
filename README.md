# SGHistorialMedico01 - Microservicio de Historia Clínica Única

## 📄 Descripción

Este microservicio implementa la gestión digital del **Formulario 002 CONSULTA EXTERNA 2021** del Sistema de Historia Clínica Única (HCU) del Ecuador. El sistema digitaliza completamente el proceso de consulta externa médica, siguiendo las normativas y estándares del Ministerio de Salud Pública del Ecuador.

## 🏛️ Arquitectura

El proyecto implementa una **Arquitectura Hexagonal** (Puertos y Adaptadores) combinada con **Domain-Driven Design (DDD)** y patrones **CQRS**, garantizando separación de responsabilidades, alta cohesión y bajo acoplamiento.

### 📂 Estructura del Proyecto Refactorizada

```
🏛️ ARQUITECTURA HEXAGONAL + DDD + CQRS
├── 🔵 DOMINIO (CORE BUSINESS)
│   ├── 📋 domain/consultaexterna/
│   │   ├── 🏢 ConsultaExterna.java (Aggregate Root)
│   │   ├── 💎 valueobjects/
│   │   │   ├── Anamnesis.java
│   │   │   ├── CitaSeguimiento.java
│   │   │   ├── DatosAuditoria.java
│   │   │   ├── DatosConsulta.java
│   │   │   ├── DatosPaciente.java
│   │   │   ├── Diagnostico.java
│   │   │   ├── ExamenFisico.java
│   │   │   ├── HabitosPersonales.java
│   │   │   ├── Interconsulta.java ⚡ (REFACTORIZADO)
│   │   │   ├── MedidasAntropometricas.java
│   │   │   ├── PlanTratamiento.java
│   │   │   ├── Prescripcion.java
│   │   │   └── SignosVitales.java ⚡ (REFACTORIZADO)
│   │   ├── 🔢 enums/
│   │   │   ├── TipoConsulta.java
│   │   │   ├── EstadoConsulta.java
│   │   │   ├── EstadoInterconsulta.java ✅ (NUEVO)
│   │   │   ├── PrioridadInterconsulta.java ✅ (NUEVO)
│   │   │   └── ClasificacionPresion.java ✅ (NUEVO)
│   │   ├── 🔧 services/
│   │   │   ├── ConsultaExternaBusinessService.java ⚡ (REFACTORIZADO)
│   │   │   ├── ConsultaExternaValidacionService.java
│   │   │   └── ValidacionesClinicasService.java
│   │   └── ⚠️ exceptions/
│   │       ├── ConsultaExternaDomainException.java ✅ (NUEVO)
│   │       ├── EstadoInterconsultaInvalidoException.java ✅ (NUEVO)
│   │       └── SignosVitalesInvalidosException.java ✅ (NUEVO)
├── 🟡 APLICACIÓN (ORCHESTRATION - CQRS)
│   ├── 📤 commands/ (Write Operations)
│   ├── 📥 queries/ (Read Operations)
│   ├── 🎯 handlers/ (Command/Query Handlers)
│   ├── 📊 dto/ (Data Transfer Objects)
│   ├── 🔌 ports/ (Interfaces/Contracts)
│   └── 🗺️ mappers/ (Domain ↔ DTO)
├── 🟢 INFRAESTRUCTURA (ADAPTERS)
│   ├── 🌐 rest/consultaexterna/
│   │   └── ConsultaExternaController.java
│   ├── 🗄️ persistence/consultaexterna/
│   │   ├── MongoConsultaExternaRepository.java
│   │   └── MongoConsultaExternaRepositoryAdapter.java
│   ├── ⚙️ config/
│   │   ├── MongoConfig.java
│   │   ├── JacksonConfig.java
│   │   └── OpenApiConfig.java
│   └── 🗺️ mappers/
└── 📋 FORMULARIOS HCU 2021/
    └── 002 CONSULTA EXTERNA 2021/ (Referencia normativa)
```

## 📋 Mapeo del Formulario 002 CONSULTA EXTERNA 2021

### ✅ Secciones Implementadas Digitalmente

| Sección | Descripción | Value Object | Estado |
|---------|-------------|க்ச்--------------|--------|
| **A** | Datos del Establecimiento | `DatosConsulta` | ✅ Implementado |
| **B** | Datos del Paciente | `DatosPaciente` | ✅ Implementado |
| **C** | Motivo de Consulta | `Anamnesis` | ✅ Implementado |
| **D** | Antecedentes | `HabitosPersonales` | ✅ Implementado |
| **E** | Enfermedad Actual | `Anamnesis` | ✅ Implementado |
| **F** | Revisión por Sistemas | `ExamenFisico` | ✅ Implementado |
| **G** | Constantes Vitales | `SignosVitales` | ⚡ Mejorado |
| **H** | Examen Físico | `ExamenFisico` | ✅ Implementado |
| **I** | Diagnóstico CIE-10 | `Diagnostico` | ✅ Implementado |
| **J** | Plan de Tratamiento | `PlanTratamiento` | ✅ Implementado |
| **K** | Interconsultas | `Interconsulta` | ⚡ Mejorado |
| **L** | Prescripciones | `Prescripcion` | ✅ Implementado |
| **M** | Cita de Seguimiento | `CitaSeguimiento` | ✅ Implementado |

### 🔄 Flujos de Proceso Médico Implementados

#### 1. **Flujo Principal de Consulta Externa**

```plantuml @startuml !theme plain skinparam backgroundColor #FFFFFF skinparam defaultFontSize 12
start :Registrar Consulta;
if (¿Primera Vez?) then (Sí) :Procesar Primera Consulta; else (No) :Procesar Consulta Subsecuente; endif
:Evaluar Signos Vitales;
if (¿Requiere Atención Urgente?) then (Sí) :Generar Interconsulta Urgente; note right * Crisis hipertensiva * Hipoxemia grave * Alteraciones críticas end note else (No) :Continuar Consulta Normal; note right * Parámetros normales * Seguimiento rutinario end note endif
:Completar Consulta;
stop @enduml
```

#### 2. **Máquina de Estados de Interconsulta**
```
SOLICITADA → AGENDADA → EN_PROCESO → COMPLETADA
     ↓           ↓           ↓
  CANCELADA   DIFERIDA   DIFERIDA
```

#### 3. **Validaciones Médicas Automáticas**
- **Signos Vitales**: Clasificación automática de presión arterial
- **Alertas Críticas**: Detección de parámetros fuera de rango vital
- **CIE-10**: Validación de códigos diagnósticos
- **Medicamentos**: Verificación de dosis y contraindicaciones

## 🌐 API REST - Endpoints

### 📋 **Gestión de Consultas Externas**

#### ✅ **Crear Nueva Consulta Externa**
```http
POST /api/v1/consultas-externas
Content-Type: application/json

{
  "datosPaciente": {
    "cedula": "1234567890",
    "nombres": "Juan Carlos",
    "apellidos": "Pérez López",
    "fechaNacimiento": "1985-06-15",
    "sexo": "MASCULINO"
  },
  "datosConsulta": {
    "numeroConsulta": "CE-202501-001",
    "tipoConsulta": "PRIMERA_VEZ",
    "especialidad": "MEDICINA_GENERAL",
    "fechaConsulta": "2025-01-07T09:30:00"
  },
  "signosVitales": {
    "presionSistolica": 120,
    "presionDiastolica": 80,
    "frecuenciaCardiaca": 72,
    "temperatura": 36.5,
    "saturacionOxigeno": 98
  }
}
```

#### 🔍 **Buscar Consultas por Cédula**
```http
GET /api/v1/consultas-externas/paciente/1234567890
```

#### 📊 **Obtener Consulta por ID**
```http
GET /api/v1/consultas-externas/{id}
```

#### 🔢 **Buscar por Número de Consulta**
```http
GET /api/v1/consultas-externas/numero/CE-202501-001
```

#### 📅 **Buscar por Fecha**
```http
GET /api/v1/consultas-externas/fecha?desde=2025-01-01&hasta=2025-01-31
```

#### ✏️ **Actualizar Consulta**
```http
PUT /api/v1/consultas-externas/{id}
```

#### 🗑️ **Eliminar Consulta**
```http
DELETE /api/v1/consultas-externas/{id}
```

### 🏥 **Gestión de Interconsultas**

#### ➕ **Solicitar Interconsulta**
```http
POST /api/v1/consultas-externas/{id}/interconsultas
{
  "especialidad": "CARDIOLOGIA",
  "motivo": "Evaluación de hipertensión arterial",
  "prioridad": "ALTA",
  "hallazgosRelevantes": "TA: 160/100 mmHg persistente"
}
```

#### ✅ **Completar Interconsulta**
```http
PUT /api/v1/consultas-externas/{id}/interconsultas/{interconsultaId}/completar
{
  "respuesta": "Hipertensión arterial grado 1. Iniciar IECA.",
  "medicoInterconsultado": "Dr. García - Cardiología"
}
```

### 📊 **Reportes y Análisis**

#### 📈 **Dashboard de Signos Vitales**
```http
GET /api/v1/consultas-externas/{id}/signos-vitales/alertas
```

#### 📋 **Reporte de Interconsultas Pendientes**
```http
GET /api/v1/interconsultas/pendientes?prioridad=URGENTE
```

## 🔧 Lógica de Negocio Implementada

### 🏥 **Reglas Médicas Automatizadas**

#### 1. **Clasificación Automática de Presión Arterial**
```java
// Implementado en SignosVitales.java
ClasificacionPresion clasificacion = signosVitales.clasificarPresion();
// NORMAL, ELEVADA, HIPERTENSION_GRADO_1, HIPERTENSION_GRADO_2, CRISIS_HIPERTENSIVA
```

#### 2. **Detección de Alertas Críticas**
```java
// Alertas automáticas basadas en signos vitales
List<String> alertas = signosVitales.obtenerAlertas();
boolean requiereUrgencia = signosVitales.requiereAtencionUrgente();
```

#### 3. **Generación Automática de Interconsultas**
```java
// Si se detecta crisis hipertensiva → Interconsulta urgente a cardiología
if (signos.clasificarPresion() == CRISIS_HIPERTENSIVA) {
    Interconsulta urgente = Interconsulta.urgente("CARDIOLOGIA", "Crisis hipertensiva");
}
```

### 📋 **Flujos de Validación**

#### ✅ **Primera Consulta**
- Validación de datos completos del paciente
- Anamnesis obligatoria
- Examen físico completo
- Evaluación de riesgos iniciales

#### 🔄 **Consulta Subsecuente**
- Comparación con historial previo
- Evolución de tratamientos
- Seguimiento de interconsultas
- Validación de adherencia

#### 🚨 **Consulta de Emergencia**
- Priorización automática
- Bypass de validaciones no críticas
- Generación automática de alertas
- Notificación a especialistas

### 🔐 **Auditoría y Trazabilidad**

```java
// Cada operación genera eventos de auditoría
DatosAuditoria auditoria = DatosAuditoria.builder()
    .creadoPor("Dr. López")
    .fechaCreacion(LocalDateTime.now())
    .accion("CONSULTA_COMPLETADA")
    .build();
```

### ⚙️ **Configuración de Entornos**

#### 🔧 **Desarrollo (application-dev.properties)**
```properties
# MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=historial_medico_dev

# Logging
logging.level.com.ug.ec=DEBUG
logging.level.org.springframework.data.mongodb=DEBUG
```

#### 🚀 **Producción (application-prod.properties)**
```properties
# MongoDB Cluster
spring.data.mongodb.uri=mongodb://cluster.example.com:27017/historial_medico
spring.data.mongodb.auto-index-creation=false

# Security
server.ssl.enabled=true
server.port=443

# Performance
spring.data.mongodb.repositories.enabled=true
```

### 📊 **Índices MongoDB Optimizados**
```javascript
// Índices para consultas frecuentes
db.consultas_externas.createIndex({"datosPaciente.cedula": 1})
db.consultas_externas.createIndex({"numeroConsulta": 1}, {unique: true})
db.consultas_externas.createIndex({"datosConsulta.fechaConsulta": -1})
db.consultas_externas.createIndex({"estado": 1, "datosConsulta.fechaConsulta": -1})
```

## 🧪 Testing

### ✅ **Ejecutar Tests**
```bash
# Tests unitarios
./mvnw test

# Tests de integración
./mvnw verify

# Cobertura de código
./mvnw jacoco:report
```

### 📊 **Métricas de Calidad**
- **Cobertura de Código**: > 85%
- **Tests Unitarios**: 120+ tests
- **Tests de Integración**: 45+ tests
- **Tests de Contratos**: OpenAPI validation

## 📖 Documentación API

### 🌐 **Swagger UI**
```
http://localhost:8081/swagger-ui.html
```

### 📋 **OpenAPI Specification**
```
http://localhost:8081/v3/api-docs
```
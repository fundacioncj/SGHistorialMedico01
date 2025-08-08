# SGHistorialMedico01 - Microservicio de Historia Clínica Única

## 📄 Descripción

Este microservicio implementa la gestión digital del **Formulario 002 CONSULTA EXTERNA 2021** del Sistema de Historia Clínica Única (HCU) del Ecuador. El sistema digitaliza completamente el proceso de consulta externa médica, siguiendo las normativas y estándares del Ministerio de Salud Pública del Ecuador.

## 🏛️ Arquitectura

El proyecto implementa una **Arquitectura Hexagonal** (Puertos y Adaptadores) combinada con **Domain-Driven Design (DDD)** y patrones **CQRS**, garantizando separación de responsabilidades, alta cohesión y bajo acoplamiento.

### 📂 Estructura del Proyecto Refactorizada

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
| GET | `/api/v1/consultas-externas/numero/{numeroConsulta}` | Busca una consulta por su número de formulario. |
| GET | `/api/v1/consultas-externas/buscar` | Realiza una búsqueda avanzada por fechas, especialidad, etc. |
| PUT | `/api/v1/consultas-externas/{id}` | Actualiza una consulta existente. |
| DELETE| `/api/v1/consultas-externas/{id}` | Elimina una consulta (marcado lógico). |

**Nota:** El cuerpo de las solicitudes `POST` y `PUT` debe ser un objeto JSON que se corresponda con los comandos `CrearConsultaExternaCommand` y `ActualizarConsultaExternaCommand`, respectivamente. Para ejemplos detallados, consulte la carpeta `/examples`.

## 🚀 Automatización y Herramientas de Desarrollo

Para facilitar el desarrollo y las pruebas, se han generado automáticamente los siguientes artefactos:

*   **`ENDPOINTS-REPORT.md`**: Un informe detallado y legible de todos los endpoints, incluyendo parámetros, formatos de datos y notas importantes sobre la configuración.
*   **`endpoints.json`**: Una especificación de la API en formato JSON, ideal para importar en herramientas como Postman o para alimentar generadores de clientes.
*   **`examples/*.json`**: Archivos JSON con ejemplos de cuerpos de solicitud (`payloads`) para crear y actualizar consultas, listos para ser usados en pruebas.
*   **`curl-suite.sh`**: Un script de shell con un conjunto de pruebas de integración que invoca a todos los endpoints de la API. Es una herramienta excelente para realizar pruebas de regresión rápidas desde la línea de comandos.

### Uso de la Suite de Pruebas

Para ejecutar la suite de pruebas completa, asegúrese de que el script tenga permisos de ejecución y luego invóquelo:
```
bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x curl-suite.sh

# Ejecutar una de las funciones de prueba, por ejemplo:
./curl-suite.sh get_consulta_por_id "60f1a5c2e8f87a2b94c12345"
```
## 🔧 Lógica de Negocio Implementada

El sistema incluye reglas de negocio clave para mejorar la calidad de la atención:

*   **Clasificación Automática de Presión Arterial**: Basado en las guías actuales (p. ej., ACC/AHA).
*   **Detección de Alertas Críticas**: Generación de alertas automáticas cuando los signos vitales están en rangos peligrosos.
*   **Validación de Integridad**: Uso de anotaciones de validación para asegurar que los datos entrantes son completos y correctos antes de ser procesados.
*   **Auditoría**: Registro automático de la creación y modificación de cada consulta.

## ⚙️ Configuración e Índices

La persistencia se gestiona con **MongoDB**. Se han configurado los siguientes índices para optimizar el rendimiento de las consultas:
```
javascript
db.consultas_externas.createIndex({"datosPaciente.cedula": 1});
db.consultas_externas.createIndex({"numeroConsulta": 1}, {unique: true});
db.consultas_externas.createIndex({"datosConsulta.fechaConsulta": -1});
```
El proyecto contiene archivos `application-{profile}.properties` para configurar la conexión a la base de datos y otros parámetros para los entornos de `dev`, `test` y `prod`.

## 🧪 Testing

El proyecto mantiene un alto estándar de calidad a través de:

*   **Tests Unitarios**: Utilizando JUnit y Mockito para probar la lógica de dominio de forma aislada.
*   **Tests de Integración**: Con Spring Boot Test y Testcontainers para validar el flujo completo, incluyendo la capa de persistencia.
```
bash
# Ejecutar todos los tests
./mvnw verify
```
## 📖 Documentación API (en vivo)

La especificación OpenAPI y la interfaz de usuario de Swagger se generan automáticamente y están disponibles en los siguientes endpoints una vez que la aplicación está en ejecución:

*   **Swagger UI**: `http://localhost:8081/swagger-ui.html`
*   **OpenAPI Spec (JSON)**: `http://localhost:8081/v3/api-docs`
```
### **Memoria Técnica: Módulo de Consulta Externa SGHistorialMedico01**

#### **1. Resumen Ejecutivo**

El presente documento detalla la arquitectura, funcionalidades y estado de validación del microservicio `SGHistorialMedico01`, con especial foco en el módulo de **Consulta Externa**. El sistema tiene como objetivo principal la digitalización completa y la gestión del **Formulario 002 de Consulta Externa** del sistema de Historia Clínica Única (HCU) de Ecuador.

Tras un análisis exhaustivo del código fuente y la API REST, se ha verificado que el sistema no solo cumple con los requerimientos funcionales, sino que también se adhiere a principios de diseño de software robustos. Se ha generado un conjunto completo de artefactos de documentación y pruebas, incluyendo un informe detallado de endpoints, ejemplos de carga útil (payloads) y una suite de pruebas automatizadas con `curl`, asegurando la calidad y fiabilidad del servicio.

#### **2. Arquitectura y Diseño Técnico**

El microservicio se fundamenta en una **Arquitectura Hexagonal (Puertos y Adaptadores)**, un enfoque moderno que garantiza un excelente desacoplamiento entre la lógica de negocio central y los componentes de infraestructura (p. ej., la API REST, la base de datos).

La arquitectura se complementa con patrones de **Domain-Driven Design (DDD)** para modelar el complejo dominio médico y **CQRS (Command and Query Responsibility Segregation)** para separar las operaciones de escritura (comandos) de las de lectura (consultas), optimizando el rendimiento y la claridad del sistema.

La estructura del proyecto refleja esta arquitectura de manera clara:

*   **Dominio (`domain`):** Contiene el núcleo del negocio, incluyendo el Agregado `ConsultaExterna`, los Objetos de Valor (`Value Objects`) que representan cada sección del formulario, y las reglas de negocio críticas. Es la capa más aislada y fundamental del sistema.
*   **Aplicación (`application`):** Orquesta los flujos de trabajo. Define los `comandos` y `consultas`, y sus respectivos `manejadores` (handlers), sirviendo como mediador entre el dominio y la infraestructura.
*   **Infraestructura (`infrastructure`):** Implementa los adaptadores para tecnologías externas. Incluye el controlador REST (`ConsultaExternaController`), la implementación del repositorio con MongoDB (`MongoConsultaExternaRepositoryAdapter`) y la configuración del servicio.

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

1.  **Inspección de Endpoints:** Se analizaron los controladores de Spring para identificar todas las rutas, verbos HTTP y DTOs asociados.
2.  **Generación de Informe (`ENDPOINTS-REPORT.md`):** Se creó un informe legible que resume cada endpoint, sus parámetros, formatos de fecha, enumeraciones y si requiere autenticación (actualmente no requerida).
3.  **Especificación Estructurada (`endpoints.json`):** Se generó un archivo JSON que detalla la API de forma estructurada, útil para la integración con herramientas automatizadas.
4.  **Suite de Pruebas (`curl-suite.sh`):** Se creó un script de shell con funciones para invocar cada endpoint de la API, utilizando ejemplos de datos (`examples/*.json`). Esta suite sirve como un conjunto de pruebas de regresión e integración rápidas.
5.  **Refuerzo de Comandos:** Se modificaron las clases de Comando (`CrearConsultaExternaCommand`, etc.) para incluir anotaciones de validación de Jakarta EE, asegurando la integridad de los datos en la capa de entrada de la API.

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

*   **Flujo de Consulta Principal:** El sistema distingue entre consultas de "primera vez" y "subsecuentes", aplicando diferentes lógicas de validación.
*   **Evaluación de Signos Vitales:** Se clasifica automáticamente la presión arterial (`NORMAL`, `ELEVADA`, `CRISIS_HIPERTENSIVA`, etc.) y se generan alertas si los parámetros indican una situación de riesgo que requiere atención urgente.
*   **Máquina de Estados de Interconsulta:** Se ha diseñado un flujo de estados para la gestión de interconsultas (`SOLICITADA` → `AGENDADA` → `COMPLETADA` / `CANCELADA`), lo que permite un seguimiento preciso del proceso.
*   **Auditoría y Trazabilidad:** Cada operación de escritura importante genera un registro de auditoría (`DatosAuditoria`), registrando qué usuario realizó la acción y cuándo.

#### **6. Infraestructura y Persistencia**

La persistencia de los datos se gestiona a través de una base de datos **MongoDB**. Se han definido y aplicado índices optimizados para las consultas más frecuentes, como la búsqueda por cédula de paciente, número de consulta y fechas, garantizando un rendimiento adecuado a escala. La configuración del proyecto está preparada para distintos entornos (`dev`, `prod`), facilitando el despliegue y la gestión.

#### **7. Conclusión**

El módulo de Consulta Externa se encuentra en un estado maduro y robusto. La arquitectura del sistema es sólida, escalable y mantenible. La funcionalidad principal ha sido implementada y validada de forma exhaustiva, y se dispone de un conjunto completo de documentación y herramientas de prueba que facilitan la integración y el desarrollo continuo. El sistema está bien posicionado para futuras extensiones, como la implementación de nuevos flujos de negocio y módulos de reporte.
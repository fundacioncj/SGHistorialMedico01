# Endpoints Report

Variables de entorno y por defecto:
- BASE_URL=http://localhost:8081
- CTX=""
- TOKEN (opcional, no requerido por defecto)

Resumen de endpoints (Spring Boot):

| Verbo | Ruta | Clase#método | Consumes | Produces | Params | Body | Auth |
|------|------|---------------|----------|----------|--------|------|------|
| POST | /api/v1/consultas-externas | ConsultaExternaController#crearConsultaExterna | application/json | application/json | — | CrearConsultaExternaCommand | false |
| GET | /api/v1/consultas-externas/{id} | ConsultaExternaController#obtenerConsultaExternaPorId | — | application/json | id:path | — | false |
| GET | /api/v1/consultas-externas/paciente/{cedula} | ConsultaExternaController#buscarConsultasExternasPorCedula | — | application/json | cedula:path, fechaDesde?:query, fechaHasta?:query, pagina=0:query, tamanio=10:query | — | false |
| GET | /api/v1/consultas-externas/numero/{numeroConsulta} | ConsultaExternaController#buscarConsultaExternaPorNumero | — | application/json | numeroConsulta:path | — | false |
| GET | /api/v1/consultas-externas/buscar | ConsultaExternaController#buscarConsultasExternasConFiltros | — | application/json | fechaDesde:query, fechaHasta:query, especialidad?:query, medicoTratante?:query, estado?:query, pagina=0:query, tamanio=10:query | — | false |
| PUT | /api/v1/consultas-externas/{id} | ConsultaExternaController#actualizarConsultaExterna | application/json | application/json | id:path | ActualizarConsultaExternaCommand | false |
| DELETE | /api/v1/consultas-externas/{id} | ConsultaExternaController#eliminarConsultaExterna | — | application/json | id:path, motivo:query, usuario="sistema"?:query | — | false |

Notas:
- HEAD y OPTIONS son gestionados por Spring automáticamente; no hay mapeos explícitos adicionales.
- Formatos de fecha: LocalDate = yyyy-MM-dd, LocalDateTime = yyyy-MM-dd'T'HH:mm:ss.
- Enums: EstadoConsulta [INICIADA, EN_PROCESO, COMPLETADA, CANCELADA, SUSPENDIDA], TipoConsulta [PRIMERA_VEZ, SUBSECUENTE, CONTROL, EMERGENCIA], TipoDiagnostico [PRINCIPAL, SECUNDARIO, DIFERENCIAL, PRESUNTIVO, CONFIRMADO].
- Seguridad: No se detectó configuración de autenticación; requiresAuth=false. curl con Authorization disponible como variante opcional.

cURLs listos para ejecutar (ejemplos rápidos):

- Crear (mínimo):
  curl -sS -X POST "$BASE_URL$CTX/api/v1/consultas-externas" -H 'Content-Type: application/json' --data @examples/CrearConsultaExternaCommand.json

- Obtener por ID:
  curl -sS "$BASE_URL$CTX/api/v1/consultas-externas/60f1a5c2e8f87a2b94c12345"

- Buscar por cédula (completo):
  curl -sS "$BASE_URL$CTX/api/v1/consultas-externas/paciente/0912345678?fechaDesde=2023-01-01&fechaHasta=2023-12-31&pagina=0&tamanio=10"

- Buscar número:
  curl -sS "$BASE_URL$CTX/api/v1/consultas-externas/numero/CE-2023-001234"

- Buscar con filtros (mínimo):
  curl -sS "$BASE_URL$CTX/api/v1/consultas-externas/buscar?fechaDesde=2023-01-01&fechaHasta=2023-12-31"

- Actualizar (mínimo):
  curl -sS -X PUT "$BASE_URL$CTX/api/v1/consultas-externas/60f1a5c2e8f87a2b94c12345" -H 'Content-Type: application/json' --data @examples/ActualizarConsultaExternaCommand.json

- Eliminar (mínimo):
  curl -sS -X DELETE "$BASE_URL$CTX/api/v1/consultas-externas/60f1a5c2e8f87a2b94c12345?motivo=Registro%20duplicado"

Cruce con OpenAPI:
- Config: src/main/java/com/ug/ec/infrastructure/config/OpenApiConfig.java
- Divergencias:
  - OpenAPI registra http://localhost:${serverPort} (por defecto 8080), pero el perfil dev usa 8081. Para local usar BASE_URL=http://localhost:8081.
  - No hay esquemas de seguridad definidos; endpoints sin auth.

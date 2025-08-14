# Auditoría Lógica del Módulo "Consulta Externa"

Fecha: 2025-08-12

Este informe resume la verificación del flujo de código y la lógica de negocio para los endpoints principales del módulo de Consulta Externa, desde la capa REST hasta la persistencia en MongoDB. Se revisaron las operaciones de Crear, Buscar (por ID, por número y por cédula), Actualizar y Eliminar, así como validaciones, mapeos y manejo de errores.

Variables de ejecución locales relevantes:
- Base URL dev: http://localhost:8081
- Context-path: "" (vacío)


## 1) Controlador principal localizado
- Clase: com.ug.ec.infrastructure.rest.consultaexterna.ConsultaExternaController
- Base path: /api/v1/consultas-externas
- Operaciones expuestas:
  - POST /api/v1/consultas-externas (crear)
  - GET /api/v1/consultas-externas/{id}
  - GET /api/v1/consultas-externas/paciente/{cedula}
  - GET /api/v1/consultas-externas/numero/{numeroConsulta}
  - GET /api/v1/consultas-externas/buscar
  - PUT /api/v1/consultas-externas/{id}
  - DELETE /api/v1/consultas-externas/{id}


## 2) Flujo "Crear Consulta" (POST /api/v1/consultas-externas)

Flujo de código verificado:
- ConsultaExternaController.crearConsultaExterna(@Valid @RequestBody CrearConsultaExternaCommand)
  -> ConsultaExternaCommandHandler.handle(CrearConsultaExternaCommand)
    -> ConsultaExternaRepository.save(ConsultaExterna)
      -> ConsultaExternaRepositoryImpl.save(ConsultaExterna)
        -> MongoConsultaExternaRepository.save(ConsultaExternaDocument)

Detalles clave de la lógica:
- Validación: El controlador anota el body con @Valid. El command CrearConsultaExternaCommand define constraints a nivel de campos y objetos anidados:
  - usuarioCreador: @NotBlank
  - datosFormulario, datosPaciente, datosConsulta, anamnesis, examenFisico, diagnosticos (lista con @NotEmpty y @Size(min=1)), planTratamiento: todos @NotNull y @Valid
  - Los value objects (DatosPaciente, DatosConsulta, Anamnesis, ExamenFisico, SignosVitales, Diagnostico, Prescripcion, etc.) incluyen validaciones detalladas (formatos, rangos, @Past, @PastOrPresent, patrones, etc.).
- Mapeo a dominio: En ConsultaExternaCommandHandler.handle(command) se construye la entidad de dominio ConsultaExterna con builder, asignando:
  - numeroConsulta, datosFormulario, datosPaciente, datosConsulta, anamnesis, examenFisico, diagnosticos, planTratamiento
  - estado inicial: EstadoConsulta.EN_PROCESO
  - auditoría: DatosAuditoria.crearNuevo("SISTEMA")
  Nota: Existe un mapper de aplicación (ConsultaExternaMapper.fromCommand) que realiza esencialmente el mismo mapeo; actualmente el handler construye el dominio manualmente. Es consistente pero existe duplicación potencial.
- Persistencia:
  - ConsultaExternaRepositoryImpl usa ConsultaExternaDocumentMapper para convertir dominio <-> documento Mongo (ConsultaExternaDocument) y delega en MongoConsultaExternaRepository.save.
  - ConsultaExternaDocument está anotado con @Document(collection = "consultas_externas"), índices compuestos y @Indexed(unique = true) para numeroConsulta. También indexa estado.
- Excepciones: el bloque try-catch del handler vuelve a lanzar la excepción. El controller captura cualquier Exception y traduce a respuesta HTTP apropiada mediante manejarError(e).

Confirmación de lógica:
- La cadena de mapeos Controller -> Handler -> Repository -> Mongo es coherente y robusta. Las validaciones son exhaustivas y se aplican gracias a @Valid en el Controller.
- La auditoría se inicializa correctamente con timestamps y usuario por defecto.

Posibles problemas o sugerencias:
- Sugerencia: Usar ConsultaExternaMapper.fromCommand(command) en el CommandHandler para evitar duplicación de lógica de mapeo y mantener una única fuente de verdad.
- Sugerencia: Validar unicidad de numeroConsulta antes de guardar (repository.existsByNumeroConsulta) para devolver 409 CONFLICT de forma explícita; hoy, la unicidad depende del índice en Mongo que lanzará error a nivel de base si hay duplicado.
- Mejora: Propagar usuarioCreador real a DatosAuditoria (actualmente se usa "SISTEMA").


## 3) Flujo "Buscar por Cédula" (GET /api/v1/consultas-externas/paciente/{cedula})

Flujo de código verificado:
- ConsultaExternaController.buscarConsultasExternasPorCedula(cedula, fechaDesde?, fechaHasta?, pagina=0, tamanio=10)
  -> Crear BuscarConsultaExternaPorCedulaQuery
  -> ConsultaExternaQueryHandler.handle(BuscarConsultaExternaPorCedulaQuery)
    -> Construye Pageable (sort por auditoria.fechaCreacion desc)
    -> ConsultaExternaRepository.findByDatosPacienteCedulaAndDatosConsultaFechaConsultaBetween(...) si hay rango, sino findByDatosPacienteCedula(...)
      -> ConsultaExternaRepositoryImpl delega a MongoConsultaExternaRepository y mapea documentos a dominio
    -> Page<ConsultaExterna> map -> Page<ConsultaExternaDto> vía ConsultaExternaMapper.entityToDto
  -> Controller arma response con data y paginación

Detalles clave de la lógica:
- Paginación y orden: PageRequest.of(pagina, tamanio, Sort.by("auditoria.fechaCreacion").descending()). Campo disponible en DatosAuditoria; correcto.
- Filtro por fecha: Se convierten LocalDate a LocalDateTime usando atStartOfDay y atTime(23,59,59) para incluir el rango completo.
- Repositorio Mongo:
  - Métodos derivados: findByDatosPacienteCedula(...) y findByDatosPacienteCedulaAndDatosConsultaFechaConsultaBetween(...).
  - Existen además métodos con proyecciones (findResumenBy...) que podrían usarse si se desea optimizar payload; actualmente se trae la entidad completa.
- Mapeo a DTO: ConsultaExternaMapper expone entityToDto consistente con los campos del dominio.

Confirmación de lógica:
- El flujo es correcto y eficiente para búsquedas por cédula con paginación y ordenamiento. El rango de fechas incluye el día completo final; bien.

Posibles problemas o sugerencias:
- Sugerencia de eficiencia: Para listados, considerar utilizar los métodos con proyección (findResumen...) y mapear a un DTO resumido cuando no se requiera toda la carga de campos (anamnesis, plan, etc.). Reduciría ancho de banda y tiempo de serialización.
- Validación adicional: El controller no valida formato de cédula a nivel de path variable. Aunque DatosPaciente lo valida, aquí se recibe solo la cédula como String. Podría añadirse un @Pattern("^\\d{10}$") en el parámetro o validar manualmente y responder 400 si no cumple.


## 4) Otros flujos revisados brevemente

- GET /{id}:
  - Controller -> QueryHandler.handle(BuscarConsultaExternaPorIdQuery) -> repository.findById(id) orElseThrow ConsultaExternaNotFoundException -> mapper a DTO.
  - Correcto: lanza 404 mapeado por manejarError en controller.

- GET /numero/{numeroConsulta}:
  - Controller -> QueryHandler.handle(BuscarConsultaExternaPorNumeroConsultaQuery) -> repository.findByNumeroConsulta(...).
  - Correcto: 404 si no existe.

- GET /buscar (filtros avanzados):
  - QueryHandler arma Pageable con orden por datosConsulta.fechaConsulta desc y delega a repository.findByFiltrosAvanzados(...).
  - Implementación: MongoConsultaExternaRepositoryCustomImpl construye Criteria dinámicos (fecha, especialidad regex i, médico regex i, estado exacto) y pagina con PageableExecutionUtils. Correcto y flexible.

- PUT /{id} (Actualizar):
  - Controller valida que path id == command.id.
  - CommandHandler: busca existente (404 si no), aplica update inmutable usando toBuilder con coalescencia de nulos, actualiza auditoría y save.
  - Sugerencia: usar ConsultaExternaMapper.updateFromCommand para evitar duplicación y aprovechar usuarioActualizador para auditoría.

- DELETE /{id}:
  - Controller recibe motivo y usuario. Valida no vacío.
  - CommandHandler: carga existente (404 si no), y si estado == COMPLETADA lanza IllegalStateException (se traduce a 400 hoy; podría ser 409 CONFLICT). Luego deleteById.
  - Sugerencia: Devolver 409 para estado no eliminable (alineado a documentación del controller) y auditar quién eliminó (hoy no se persiste registro lógico de borrado; es un delete físico). Si se desea eliminación lógica, añadir campo estado o marca.


## 5) Validación, Mapeo y Manejo de Errores (Revisión General)

- Validación (Bean Validation):
  - Commands: CrearConsultaExternaCommand y ActualizarConsultaExternaCommand usan @NotBlank/@NotNull/@Valid/@Size, etc.
  - Value Objects: Cobertura robusta (DatosPaciente, DatosConsulta, Anamnesis, ExamenFisico, SignosVitales, Diagnostico, Prescripcion, PlanTratamiento…). Formatos y rangos fueron definidos con detalle.
  - Controlador aplica @Valid en @RequestBody, por lo que Spring generará 400 en caso de violaciones. El método manejarError también agrupa ciertas excepciones como BAD_REQUEST.

- Mapeo de objetos:
  - Dominio (ConsultaExterna) <-> Documento Mongo (ConsultaExternaDocument) vía ConsultaExternaDocumentMapper: campo a campo, simétrico; consistente.
  - Dominio <-> DTO (ConsultaExternaDto) vía ConsultaExternaMapper: consistente.
  - Command -> Dominio: duplicado entre ConsultaExternaMapper.fromCommand y construcción manual en CommandHandler; consistente pero redundante.

- Manejo de errores:
  - No existe un @ControllerAdvice global en el proyecto. El propio ConsultaExternaController implementa manejarError(Exception) para mapear algunas excepciones a HTTP:
    - ConsultaExternaNotFoundException -> 404
    - ConsultaExternaDuplicadaException -> 409
    - ConsultaExternaNoEditableException -> 409
    - DatosPacienteInvalidosException, DatosConsultaInvalidosException, AnamnesisInvalidaException, ExamenFisicoInvalidoException, DiagnosticoInvalidoException, IllegalArgumentException -> 400
    - Por defecto -> 500
  - Sugerencia: Implementar un @RestControllerAdvice global para centralizar el manejo de errores y estandarizar la respuesta en todo el proyecto (y simplificar controladores). También mapear IllegalStateException en delete a 409, como indican las anotaciones @ApiResponse del controller.


## 6) Conclusiones

- La arquitectura por capas (Controller -> Handlers -> Repository (Adapter) -> MongoRepository) está correctamente aplicada.
- Las validaciones de entrada son sólidas gracias a Bean Validation en Commands y VOs; el controlador aplica @Valid.
- El mapeo entre DTO/Command, dominio y documento es consistente y mantiene los campos clave (incluyendo auditoría e índices en Mongo).
- Búsquedas por cédula implementan paginación/orden adecuados; búsquedas avanzadas con criteria son flexibles.

Mejoras recomendadas (no bloqueantes para el arranque):
1) Añadir @RestControllerAdvice global para manejo de excepciones consistente (incluyendo mapping 409 en casos de conflicto operativo).
2) Usar los métodos del mapper (fromCommand/updateFromCommand) en los CommandHandler para evitar duplicación y asegurar uso de usuario creador/actualizador en auditoría.
3) Validación de cédula en el path con @Pattern o sanitizer.
4) Para listados, considerar proyecciones/resúmenes para reducir payload.
5) Evitar delete físico si se requiere auditoría de borrados; implementar borrado lógico (estado/eliminadoPor/fechaEliminacion) en lugar de deleteById.


## 7) cURLs de verificación rápida

- Crear:
  curl -sS -X POST "http://localhost:8081/api/v1/consultas-externas" -H 'Content-Type: application/json' --data @examples/CrearConsultaExternaCommand.json | jq .

- Buscar por cédula:
  curl -sS "http://localhost:8081/api/v1/consultas-externas/paciente/0912345678?pagina=0&tamanio=5" | jq .

- Obtener por ID:
  curl -sS "http://localhost:8081/api/v1/consultas-externas/<ID_DEVUELTO>" | jq .

- Actualizar:
  curl -sS -X PUT "http://localhost:8081/api/v1/consultas-externas/60f1a5c2e8f87a2b94c12345" -H 'Content-Type: application/json' --data @examples/ActualizarConsultaExternaCommand.json | jq .

- Eliminar:
  curl -sS -G -X DELETE "http://localhost:8081/api/v1/consultas-externas/60f1a5c2e8f87a2b94c12345" --data-urlencode "motivo=Registro duplicado" | jq .

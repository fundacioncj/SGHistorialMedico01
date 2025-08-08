#!/usr/bin/env bash

# Variables
: ${BASE_URL:=http://localhost:8081}
: ${CTX:=}
: ${TOKEN:=}

_auth_header() {
  if [ -n "$TOKEN" ]; then echo "-H \"Authorization: Bearer $TOKEN\""; fi
}

# POST /api/v1/consultas-externas
post_consultas_externas_min() {
  curl -sS -X POST "$BASE_URL$CTX/api/v1/consultas-externas" -H 'Content-Type: application/json' $( _auth_header ) --data @examples/CrearConsultaExternaCommand.json
}

# GET /api/v1/consultas-externas/{id}
get_consulta_por_id() {
  local id="${1:-60f1a5c2e8f87a2b94c12345}"
  curl -sS "$BASE_URL$CTX/api/v1/consultas-externas/${id}" $( _auth_header )
}

# GET /api/v1/consultas-externas/paciente/{cedula}
get_consultas_por_cedula_min() {
  local cedula="${1:-0912345678}"
  curl -sS "$BASE_URL$CTX/api/v1/consultas-externas/paciente/${cedula}" $( _auth_header )
}

get_consultas_por_cedula_full() {
  local cedula="${1:-0912345678}"
  local desde="${2:-2023-01-01}"
  local hasta="${3:-2023-12-31}"
  local pagina="${4:-0}"
  local tamanio="${5:-10}"
  curl -sS "$BASE_URL$CTX/api/v1/consultas-externas/paciente/${cedula}?fechaDesde=${desde}&fechaHasta=${hasta}&pagina=${pagina}&tamanio=${tamanio}" $( _auth_header )
}

# GET /api/v1/consultas-externas/numero/{numeroConsulta}
get_consulta_por_numero() {
  local numero="${1:-CE-2023-001234}"
  curl -sS "$BASE_URL$CTX/api/v1/consultas-externas/numero/${numero}" $( _auth_header )
}

# GET /api/v1/consultas-externas/buscar
get_consultas_buscar_min() {
  local desde="${1:-2023-01-01}"
  local hasta="${2:-2023-12-31}"
  curl -sS "$BASE_URL$CTX/api/v1/consultas-externas/buscar?fechaDesde=${desde}&fechaHasta=${hasta}" $( _auth_header )
}

get_consultas_buscar_full() {
  local desde="${1:-2023-01-01}"
  local hasta="${2:-2023-12-31}"
  local esp="${3:-Cardiolog%C3%ADa}"
  local med="${4:-Juan%20Perez}"
  local estado="${5:-COMPLETADA}"
  local pagina="${6:-0}"
  local tamanio="${7:-10}"
  curl -sS "$BASE_URL$CTX/api/v1/consultas-externas/buscar?fechaDesde=${desde}&fechaHasta=${hasta}&especialidad=${esp}&medicoTratante=${med}&estado=${estado}&pagina=${pagina}&tamanio=${tamanio}" $( _auth_header )
}

# PUT /api/v1/consultas-externas/{id}
put_consulta_actualizar_min() {
  local id="${1:-60f1a5c2e8f87a2b94c12345}"
  curl -sS -X PUT "$BASE_URL$CTX/api/v1/consultas-externas/${id}" -H 'Content-Type: application/json' $( _auth_header ) --data @examples/ActualizarConsultaExternaCommand.json
}

# DELETE /api/v1/consultas-externas/{id}
delete_consulta_min() {
  local id="${1:-60f1a5c2e8f87a2b94c12345}"
  local motivo="${2:-Registro duplicado}"
  curl -sS -G -X DELETE "$BASE_URL$CTX/api/v1/consultas-externas/${id}" --data-urlencode "motivo=${motivo}" $( _auth_header )
}

delete_consulta_full() {
  local id="${1:-60f1a5c2e8f87a2b94c12345}"
  local motivo="${2:-Registro duplicado}"
  local usuario="${3:-admin.sistema}"
  curl -sS -G -X DELETE "$BASE_URL$CTX/api/v1/consultas-externas/${id}" --data-urlencode "motivo=${motivo}" --data-urlencode "usuario=${usuario}" $( _auth_header )
}

# Help
if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
  echo "Loaded curl suite. Examples:"
  echo "  post_consultas_externas_min"
  echo "  get_consulta_por_id 60f1a5c2e8f87a2b94c12345"
  echo "  get_consultas_por_cedula_min 0912345678"
  echo "  get_consultas_por_cedula_full 0912345678 2023-01-01 2023-12-31 0 10"
  echo "  get_consulta_por_numero CE-2023-001234"
  echo "  get_consultas_buscar_min 2023-01-01 2023-12-31"
  echo "  get_consultas_buscar_full 2023-01-01 2023-12-31 Cardiolog%C3%ADa Juan%20Perez COMPLETADA 0 10"
  echo "  put_consulta_actualizar_min 60f1a5c2e8f87a2b94c12345"
  echo "  delete_consulta_min 60f1a5c2e8f87a2b94c12345 'Registro duplicado'"
  echo "  delete_consulta_full 60f1a5c2e8f87a2b94c12345 'Registro duplicado' admin.sistema"
fi

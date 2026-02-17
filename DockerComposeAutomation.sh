#!/usr/bin/env bash
set -euo pipefail

# ============================================
# Script de despliegue Docker - SGH Historial Médico
# Solo dockeriza y arranca la aplicación
# ============================================

COMPOSE_FILE="docker_compose_sghm_historial.yml"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;90m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# Variables de la aplicación (puedes exportar más antes de ejecutar este script)
export JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx512m}"
export SERVER_PORT="${SERVER_PORT:-8080}"

# ============================================

echo -e "${CYAN}============================================${NC}"
echo -e "${CYAN}  SGH HISTORIAL MEDICO - Docker Deployment${NC}"
echo -e "${CYAN}============================================${NC}"
echo ""
echo -e "${YELLOW}[1/4] Verificando herramientas...${NC}"
if ! command -v docker >/dev/null 2>&1; then
  echo -e "${RED}Error: Docker no está instalado o no está en PATH.${NC}"
  exit 1
fi
if ! docker compose version >/dev/null 2>&1; then
  echo -e "${RED}Error: Docker Compose (v2) no disponible.${NC}"
  exit 1
fi
echo -e "${GREEN}  ✓ Docker y Docker Compose disponibles${NC}"

echo ""
echo -e "${YELLOW}[2/4] Construyendo imagen de la aplicación...${NC}"
if ! docker compose -f "${COMPOSE_FILE}" build --no-cache; then
  echo -e "${RED}Error al construir la imagen${NC}"
  exit 1
fi
echo -e "${GREEN}  ✓ Imagen construida exitosamente${NC}"

echo ""
echo -e "${YELLOW}[3/4] Iniciando aplicación...${NC}"
if ! docker compose -f "${COMPOSE_FILE}" up -d --remove-orphans; then
  echo -e "${RED}Error al iniciar la aplicación${NC}"
  exit 1
fi
echo -e "${GREEN}  ✓ Aplicación iniciada correctamente${NC}"

echo ""
echo -e "${YELLOW}[4/4] Estado y logs iniciales${NC}"
docker compose -f "${COMPOSE_FILE}" ps
echo -e "${CYAN}Logs (Ctrl+C para salir)...${NC}"
docker compose -f "${COMPOSE_FILE}" logs -f app

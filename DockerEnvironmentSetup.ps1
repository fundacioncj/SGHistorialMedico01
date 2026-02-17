#!/usr/bin/env pwsh
$ErrorActionPreference = "Stop"

# ============================================
# Script de despliegue Docker - SGH Historial Médico
# Solo automatiza build/arranque de la app
# ============================================

# Variables de la aplicación (puedes exportar más antes de ejecutar este script)
if (-not $Env:JAVA_OPTS) { $Env:JAVA_OPTS = "-Xms256m -Xmx512m" }
if (-not $Env:SERVER_PORT) { $Env:SERVER_PORT = "8080" }

$composeFile = "docker_compose_sghm_historial.yml"

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  SGH HISTORIAL MEDICO - Docker Deployment" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/4] Verificando herramientas..." -ForegroundColor Yellow
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) { 
    throw "Docker no está instalado o no está en PATH." 
}
try { 
    docker compose version | Out-Null 
} catch { 
    throw "Docker Compose (v2) no disponible." 
}
Write-Host "  ✓ Docker y Docker Compose disponibles" -ForegroundColor Green

Write-Host ""
Write-Host "[2/4] Construyendo imagen..." -ForegroundColor Yellow
docker compose -f $composeFile build --no-cache
if ($LASTEXITCODE -ne 0) {
    throw "Error al construir la imagen"
}
Write-Host "  ✓ Imagen construida exitosamente" -ForegroundColor Green

Write-Host ""
Write-Host "[3/4] Iniciando aplicación..." -ForegroundColor Yellow
docker compose -f $composeFile up -d --remove-orphans
if ($LASTEXITCODE -ne 0) {
    throw "Error al iniciar la aplicación"
}
Write-Host "  ✓ Aplicación iniciada correctamente" -ForegroundColor Green

Write-Host ""
Write-Host "[4/4] Estado y logs:" -ForegroundColor Yellow
docker compose -f $composeFile ps
Write-Host "Logs (Ctrl+C para salir)..." -ForegroundColor Cyan
docker compose -f $composeFile logs -f app

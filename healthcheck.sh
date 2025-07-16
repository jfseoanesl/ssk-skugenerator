#!/bin/bash
# ===================================================================
# healthcheck.sh - Docker Health Check Script
# ===================================================================
# Script para verificar el estado de salud del contenedor
# ===================================================================

set -e

# Configuración
HEALTH_URL="http://localhost:${SERVER_PORT:-8080}/actuator/health"
TIMEOUT=10
MAX_RETRIES=3

# Función para logging
log() {
    echo "[HEALTHCHECK] $(date +'%Y-%m-%d %H:%M:%S') - $1"
}

# Función para verificar conectividad básica
check_basic_connectivity() {
    # Verificar que el puerto esté abierto
    if ! nc -z localhost "${SERVER_PORT:-8080}"; then
        log "ERROR: Puerto ${SERVER_PORT:-8080} no está disponible"
        return 1
    fi

    log "Puerto ${SERVER_PORT:-8080} está disponible"
    return 0
}

# Función para verificar endpoint de health
check_health_endpoint() {
    local retry_count=0

    while [ $retry_count -lt $MAX_RETRIES ]; do

        # Hacer petición HTTP al endpoint de health
        if response=$(wget --timeout=$TIMEOUT --tries=1 --quiet --output-document=- "$HEALTH_URL" 2>&1); then
            # Verificar que la respuesta contenga "UP"
            if echo "$response" | grep -q '"status":"UP"'; then
                log "SUCCESS: Aplicación está saludable"
                return 0
            else
                log "WARNING: Aplicación responde pero no está UP: $response"
            fi
        else
            log "ERROR: No se pudo conectar al endpoint de health: $response"
        fi

        retry_count=$((retry_count + 1))
        if [ $retry_count -lt $MAX_RETRIES ]; then
            sleep 2
        fi
    done

    log "ERROR: Health check falló después de $MAX_RETRIES intentos"
    return 1
}

# Función para verificar procesos Java
check_java_process() {
    if ! pgrep -f "java.*JarLauncher" > /dev/null; then
        log "ERROR: Proceso Java no encontrado"
        return 1
    fi

    log "Proceso Java ejecutándose correctamente"
    return 0
}

# Función para verificar recursos del sistema
check_system_resources() {
    # Verificar memoria disponible (al menos 100MB libres)
    local free_memory=$(free -m | awk 'NR==2{printf "%.0f", $7}')
    if [ "$free_memory" -lt 100 ]; then
        log "WARNING: Memoria libre baja: ${free_memory}MB"
    else
        log "Memoria disponible: ${free_memory}MB"
    fi

    # Verificar espacio en disco (al menos 1GB libre)
    local free_disk=$(df /app | awk 'NR==2{print $4}')
    local free_disk_gb=$((free_disk / 1024 / 1024))
    if [ "$free_disk_gb" -lt 1 ]; then
        log "WARNING: Espacio en disco bajo: ${free_disk_gb}GB"
    else
        log "Espacio en disco disponible: ${free_disk_gb}GB"
    fi

    return 0
}

# Función principal de health check
main() {
    log "Iniciando health check..."

    # Verificar conectividad básica
    if ! check_basic_connectivity; then
        exit 1
    fi

    # Verificar proceso Java
    if ! check_java_process; then
        exit 1
    fi

    # Verificar recursos del sistema
    check_system_resources

    # Verificar endpoint de health
    if ! check_health_endpoint; then
        exit 1
    fi

    log "Health check completado exitosamente"
    exit 0
}

# Ejecutar health check
main "$@"
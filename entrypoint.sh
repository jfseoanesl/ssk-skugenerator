#!/bin/bash
# ===================================================================
# entrypoint.sh - Docker Entry Point Script
# ===================================================================
# Script de entrada para el contenedor Docker de SKU Generator
# ===================================================================

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para logging
log() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

log_success() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] ✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] ⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ❌ $1${NC}"
}

# Banner de inicio
echo "================================================================="
echo "🧸 SKU Generator Application - Docker Container"
echo "================================================================="

# Verificar variables de entorno requeridas
log "Verificando variables de entorno..."

required_vars=(
    "DB_HOST"
    "DB_USERNAME"
    "DB_PASSWORD"
)

missing_vars=()
for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        missing_vars+=("$var")
    fi
done

if [ ${#missing_vars[@]} -ne 0 ]; then
    log_error "Variables de entorno requeridas faltantes:"
    for var in "${missing_vars[@]}"; do
        log_error "  - $var"
    done
    exit 1
fi

log_success "Variables de entorno verificadas"

# Establecer valores por defecto
export DB_PORT=${DB_PORT:-3306}
export DB_NAME=${DB_NAME:-sku_generator}
export SERVER_PORT=${SERVER_PORT:-8080}
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}

# Crear directorios necesarios
log "Creando directorios necesarios..."
mkdir -p /app/logs
mkdir -p /app/storage/backups
mkdir -p /app/storage/exports
mkdir -p /app/storage/temp
mkdir -p /app/storage/uploads

# Establecer permisos
chown -R skuapp:skuapp /app/logs /app/storage
log_success "Directorios creados"

# Función para esperar a que la base de datos esté disponible
wait_for_database() {
    log "Esperando a que la base de datos esté disponible en $DB_HOST:$DB_PORT..."

    timeout=60
    counter=0

    while ! nc -z "$DB_HOST" "$DB_PORT" >/dev/null 2>&1; do
        if [ $counter -eq $timeout ]; then
            log_error "Timeout esperando la base de datos"
            exit 1
        fi

        log "Intentando conectar a la base de datos... ($counter/$timeout)"
        sleep 2
        counter=$((counter + 1))
    done

    log_success "Base de datos disponible"

    # Esperar un poco más para asegurar que la base de datos esté completamente lista
    sleep 5
}

# Función para verificar conectividad de red
check_network() {
    log "Verificando conectividad de red..."

    if ! ping -c 1 "$DB_HOST" >/dev/null 2>&1; then
        log_warning "No se puede hacer ping a $DB_HOST, pero intentando continuar..."
    else
        log_success "Conectividad de red verificada"
    fi
}

# Función para configurar JVM
configure_jvm() {
    log "Configurando JVM..."

    # Configurar heap size basado en memoria disponible
    MEMORY_LIMIT=$(cat /sys/fs/cgroup/memory/memory.limit_in_bytes 2>/dev/null || echo "2147483648")

    # Convertir a MB
    MEMORY_MB=$((MEMORY_LIMIT / 1024 / 1024))

    # Asignar 75% de la memoria disponible como heap máximo
    MAX_HEAP=$((MEMORY_MB * 75 / 100))

    # Mínimo 512MB, máximo 2GB por defecto
    if [ $MAX_HEAP -lt 512 ]; then
        MAX_HEAP=512
    elif [ $MAX_HEAP -gt 2048 ]; then
        MAX_HEAP=2048
    fi

    # Heap inicial es 50% del máximo
    INITIAL_HEAP=$((MAX_HEAP / 2))

    export JAVA_OPTS="$JAVA_OPTS -Xms${INITIAL_HEAP}m -Xmx${MAX_HEAP}m"

    log_success "JVM configurado: heap inicial ${INITIAL_HEAP}MB, heap máximo ${MAX_HEAP}MB"
}

# Función para configurar timezone
configure_timezone() {
    log "Configurando zona horaria..."

    TZ=${TZ:-America/Bogota}
    export TZ

    if [ -f /usr/share/zoneinfo/$TZ ]; then
        ln -snf /usr/share/zoneinfo/$TZ /etc/localtime
        echo $TZ > /etc/timezone
        log_success "Zona horaria configurada: $TZ"
    else
        log_warning "Zona horaria $TZ no encontrada, usando UTC"
        export TZ=UTC
    fi
}

# Función para configurar logging
configure_logging() {
    log "Configurando logging..."

    # Asegurar que el directorio de logs existe
    mkdir -p "$(dirname "${LOG_FILE:-/app/logs/sku-generator.log}")"

    log_success "Logging configurado"
}

# Función para mostrar información del sistema
show_system_info() {
    log "Información del sistema:"
    echo "  🐧 OS: $(cat /etc/os-release | grep PRETTY_NAME | cut -d'"' -f2)"
    echo "  ☕ Java: $(java -version 2>&1 | head -n1)"
    echo "  🧠 Memoria total: $(free -h | awk '/^Mem:/ {print $2}')"
    echo "  💾 Espacio en disco: $(df -h / | awk 'NR==2 {print $4}')"
    echo "  🌐 Hostname: $(hostname)"
    echo "  🔧 Perfil Spring: $SPRING_PROFILES_ACTIVE"
}

# Función de cleanup para señales
cleanup() {
    log "Recibida señal de terminación, iniciando shutdown graceful..."

    if [ -n "$APP_PID" ]; then
        log "Enviando SIGTERM al proceso de la aplicación..."
        kill -TERM "$APP_PID" 2>/dev/null || true

        # Esperar hasta 30 segundos para shutdown graceful
        for i in $(seq 1 30); do
            if ! kill -0 "$APP_PID" 2>/dev/null; then
                log_success "Aplicación terminada correctamente"
                break
            fi
            sleep 1
        done

        # Si aún está corriendo, forzar terminación
        if kill -0 "$APP_PID" 2>/dev/null; then
            log_warning "Forzando terminación de la aplicación..."
            kill -KILL "$APP_PID" 2>/dev/null || true
        fi
    fi

    log "Cleanup completado"
    exit 0
}

# Configurar handlers para señales
trap cleanup SIGTERM SIGINT

# Ejecutar configuraciones
check_network
configure_timezone
configure_jvm
configure_logging
show_system_info

# Esperar a la base de datos solo si no estamos en modo de testing
if [ "$SPRING_PROFILES_ACTIVE" != "test" ]; then
    wait_for_database
fi

# Mostrar configuración final
log "Configuración final:"
echo "  🗄️  Base de datos: $DB_HOST:$DB_PORT/$DB_NAME"
echo "  🔐 Usuario DB: $DB_USERNAME"
echo "  🌐 Puerto servidor: $SERVER_PORT"
echo "  📁 Directorio storage: ${STORAGE_BASE_DIR:-/app/storage}"
echo "  📝 Archivo de log: ${LOG_FILE:-/app/logs/sku-generator.log}"

log "Iniciando aplicación SKU Generator..."

# Ejecutar la aplicación
exec java $JAVA_OPTS \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active="$SPRING_PROFILES_ACTIVE" \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone="$TZ" \
    org.springframework.boot.loader.JarLauncher &

APP_PID=$!

log_success "Aplicación iniciada con PID: $APP_PID"

# Esperar a que la aplicación termine
wait $APP_PID
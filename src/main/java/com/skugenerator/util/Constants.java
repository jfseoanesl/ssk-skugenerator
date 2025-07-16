package com.skugenerator.util;

/**
 * Clase de constantes para la aplicación SKU Generator.
 *
 * Esta clase centraliza todas las constantes utilizadas en la aplicación
 * para evitar la duplicación de valores y facilitar el mantenimiento.
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
public final class Constants {

    /**
     * Constructor privado para evitar instanciación.
     */
    private Constants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidades y no puede ser instanciada");
    }

    // ===================================================================
    // CONSTANTES DE CÓDIGOS SKU
    // ===================================================================

    /**
     * Constantes relacionadas con la estructura de códigos SKU.
     */
    public static final class SkuCodes {

        /** Longitud total del código SKU */
        public static final int TOTAL_LENGTH = 12;

        /** Longitud del tipo de producto (1 dígito) */
        public static final int TYPE_LENGTH = 1;

        /** Longitud del código de categoría (2 dígitos) */
        public static final int CATEGORY_LENGTH = 2;

        /** Longitud del código de subcategoría (1 dígito) */
        public static final int SUBCATEGORY_LENGTH = 1;

        /** Longitud del código de talla (2 dígitos) */
        public static final int SIZE_LENGTH = 2;

        /** Longitud del código de color (2 dígitos) */
        public static final int COLOR_LENGTH = 2;

        /** Longitud del código de temporada (1 dígito) */
        public static final int SEASON_LENGTH = 1;

        /** Longitud del consecutivo (3 dígitos) */
        public static final int CONSECUTIVE_LENGTH = 3;

        /** Valor máximo del consecutivo */
        public static final int MAX_CONSECUTIVE_VALUE = 999;

        /** Valor mínimo del consecutivo */
        public static final int MIN_CONSECUTIVE_VALUE = 1;

        /** Patrón regex para validar código SKU completo */
        public static final String SKU_PATTERN = "^\\d{12}$";

        /** Formato para el consecutivo con padding de ceros */
        public static final String CONSECUTIVE_FORMAT = "%03d";

        private SkuCodes() {}
    }

    // ===================================================================
    // CONSTANTES DE CONFIGURACIÓN
    // ===================================================================

    /**
     * Constantes para los códigos de configuración del sistema.
     */
    public static final class ConfigCodes {

        /** Código de talla única */
        public static final String SIZE_ONE_SIZE = "00";

        /** Código de temporada "Todo el año" */
        public static final String SEASON_ALL_YEAR = "4";

        /** Código de color multicolor */
        public static final String COLOR_MULTICOLOR = "11";

        /** Rango de códigos para estampados (11-15) */
        public static final String COLOR_PATTERN_START = "11";
        public static final String COLOR_PATTERN_END = "15";

        /** Códigos reservados para tipos especiales */
        public static final String TYPE_SIMPLE = "1";
        public static final String TYPE_COMPOSITE = "2";
        public static final String TYPE_SET = "3";
        public static final String TYPE_PROMOTIONAL = "4";
        public static final String TYPE_SEASONAL = "5";

        private ConfigCodes() {}
    }

    // ===================================================================
    // CONSTANTES DE VALIDACIÓN
    // ===================================================================

    /**
     * Constantes para validaciones de datos.
     */
    public static final class Validation {

        /** Longitud mínima para nombres de productos */
        public static final int PRODUCT_NAME_MIN_LENGTH = 5;

        /** Longitud máxima para nombres de productos */
        public static final int PRODUCT_NAME_MAX_LENGTH = 255;

        /** Longitud mínima para nombres de configuración */
        public static final int CONFIG_NAME_MIN_LENGTH = 3;

        /** Longitud máxima para nombres de configuración */
        public static final int CONFIG_NAME_MAX_LENGTH = 100;

        /** Patrón para códigos de tipo (1 dígito) */
        public static final String TYPE_CODE_PATTERN = "^[0-9]$";

        /** Patrón para códigos de categoría (2 dígitos) */
        public static final String CATEGORY_CODE_PATTERN = "^[0-9]{2}$";

        /** Patrón para códigos de subcategoría (1 dígito) */
        public static final String SUBCATEGORY_CODE_PATTERN = "^[0-9]$";

        /** Patrón para códigos de talla (2 dígitos) */
        public static final String SIZE_CODE_PATTERN = "^[0-9]{2}$";

        /** Patrón para códigos de color (2 dígitos) */
        public static final String COLOR_CODE_PATTERN = "^[0-9]{2}$";

        /** Patrón para códigos de temporada (1 dígito) */
        public static final String SEASON_CODE_PATTERN = "^[0-9]$";

        /** Patrón para validar email */
        public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";

        /** Patrón para validar nombres de usuario */
        public static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]{3,20}$";

        private Validation() {}
    }

    // ===================================================================
    // CONSTANTES DE MENSAJES
    // ===================================================================

    /**
     * Constantes para mensajes del sistema.
     */
    public static final class Messages {

        /** Mensajes de éxito */
        public static final String SUCCESS_PRODUCT_CREATED = "Producto creado exitosamente";
        public static final String SUCCESS_PRODUCT_UPDATED = "Producto actualizado exitosamente";
        public static final String SUCCESS_PRODUCT_DELETED = "Producto eliminado exitosamente";
        public static final String SUCCESS_CONFIG_SAVED = "Configuración guardada exitosamente";
        public static final String SUCCESS_LOGIN = "Inicio de sesión exitoso";
        public static final String SUCCESS_LOGOUT = "Cierre de sesión exitoso";

        /** Mensajes de error */
        public static final String ERROR_PRODUCT_NOT_FOUND = "Producto no encontrado";
        public static final String ERROR_DUPLICATE_CODE = "Ya existe un producto con este código";
        public static final String ERROR_INVALID_SKU = "Código SKU inválido";
        public static final String ERROR_CONFIG_IN_USE = "No se puede eliminar: configuración en uso";
        public static final String ERROR_UNAUTHORIZED = "No tiene permisos para realizar esta acción";
        public static final String ERROR_INVALID_CREDENTIALS = "Credenciales inválidas";
        public static final String ERROR_DATABASE = "Error de base de datos";
        public static final String ERROR_VALIDATION = "Error de validación";

        /** Mensajes de validación */
        public static final String VALIDATION_REQUIRED_FIELD = "Este campo es requerido";
        public static final String VALIDATION_INVALID_FORMAT = "Formato inválido";
        public static final String VALIDATION_LENGTH_EXCEEDED = "Longitud excedida";
        public static final String VALIDATION_INVALID_CODE = "Código inválido";

        private Messages() {}
    }

    // ===================================================================
    // CONSTANTES DE ROLES Y SEGURIDAD
    // ===================================================================

    /**
     * Constantes para roles y seguridad.
     */
    public static final class Security {

        /** Roles del sistema */
        public static final String ROLE_ADMIN = "ADMIN";
        public static final String ROLE_USER = "USER";
        public static final String ROLE_VIEWER = "VIEWER";

        /** Authorities con prefijo ROLE_ */
        public static final String AUTHORITY_ADMIN = "ROLE_ADMIN";
        public static final String AUTHORITY_USER = "ROLE_USER";
        public static final String AUTHORITY_VIEWER = "ROLE_VIEWER";

        /** Headers de autenticación */
        public static final String AUTH_HEADER = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";

        /** Claims del JWT */
        public static final String JWT_CLAIM_ROLES = "roles";
        public static final String JWT_CLAIM_USER_ID = "userId";
        public static final String JWT_CLAIM_USERNAME = "username";

        /** Configuración de contraseñas */
        public static final int PASSWORD_MIN_LENGTH = 8;
        public static final int PASSWORD_MAX_LENGTH = 50;

        /** Configuración de sesiones */
        public static final int MAX_SESSIONS_PER_USER = 3;
        public static final long SESSION_TIMEOUT_MINUTES = 480; // 8 horas

        private Security() {}
    }

    // ===================================================================
    // CONSTANTES DE CACHE
    // ===================================================================

    /**
     * Constantes para configuración de cache.
     */
    public static final class Cache {

        /** Nombres de cache */
        public static final String PRODUCTS_CACHE = "products";
        public static final String CONFIGURATIONS_CACHE = "configurations";
        public static final String USERS_CACHE = "users";
        public static final String REPORTS_CACHE = "reports";
        public static final String STATISTICS_CACHE = "statistics";

        /** Tiempos de expiración en segundos */
        public static final long PRODUCTS_TTL = 300; // 5 minutos
        public static final long CONFIGURATIONS_TTL = 3600; // 1 hora
        public static final long USERS_TTL = 1800; // 30 minutos
        public static final long REPORTS_TTL = 600; // 10 minutos
        public static final long STATISTICS_TTL = 120; // 2 minutos

        private Cache() {}
    }

    // ===================================================================
    // CONSTANTES DE EXPORTACIÓN
    // ===================================================================

    /**
     * Constantes para funcionalidades de exportación.
     */
    public static final class Export {

        /** Formatos de exportación */
        public static final String FORMAT_CSV = "CSV";
        public static final String FORMAT_EXCEL = "EXCEL";
        public static final String FORMAT_JSON = "JSON";
        public static final String FORMAT_PDF = "PDF";

        /** Extensiones de archivo */
        public static final String EXTENSION_CSV = ".csv";
        public static final String EXTENSION_EXCEL = ".xlsx";
        public static final String EXTENSION_JSON = ".json";
        public static final String EXTENSION_PDF = ".pdf";

        /** Content types */
        public static final String CONTENT_TYPE_CSV = "text/csv";
        public static final String CONTENT_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String CONTENT_TYPE_PDF = "application/pdf";

        /** Límites de registros por formato */
        public static final int MAX_RECORDS_CSV = 100000;
        public static final int MAX_RECORDS_EXCEL = 65000;
        public static final int MAX_RECORDS_JSON = 50000;
        public static final int MAX_RECORDS_PDF = 10000;

        /** Configuración de archivos */
        public static final String DEFAULT_CHARSET = "UTF-8";
        public static final String CSV_SEPARATOR = ",";
        public static final String CSV_QUOTE = "\"";

        private Export() {}
    }

    // ===================================================================
    // CONSTANTES DE PAGINACIÓN
    // ===================================================================

    /**
     * Constantes para paginación.
     */
    public static final class Pagination {

        /** Tamaño de página por defecto */
        public static final int DEFAULT_PAGE_SIZE = 20;

        /** Tamaño máximo de página */
        public static final int MAX_PAGE_SIZE = 100;

        /** Tamaño mínimo de página */
        public static final int MIN_PAGE_SIZE = 5;

        /** Página por defecto */
        public static final int DEFAULT_PAGE = 0;

        /** Criterios de ordenamiento por defecto */
        public static final String DEFAULT_SORT_BY = "createdDate";
        public static final String DEFAULT_SORT_DIRECTION = "DESC";

        private Pagination() {}
    }

    // ===================================================================
    // CONSTANTES DE AUDITORÍA
    // ===================================================================

    /**
     * Constantes para auditoría del sistema.
     */
    public static final class Audit {

        /** Tipos de operación */
        public static final String OPERATION_CREATE = "CREATE";
        public static final String OPERATION_UPDATE = "UPDATE";
        public static final String OPERATION_DELETE = "DELETE";
        public static final String OPERATION_LOGIN = "LOGIN";
        public static final String OPERATION_LOGOUT = "LOGOUT";
        public static final String OPERATION_EXPORT = "EXPORT";
        public static final String OPERATION_IMPORT = "IMPORT";

        /** Entidades auditables */
        public static final String ENTITY_PRODUCT = "Product";
        public static final String ENTITY_USER = "User";
        public static final String ENTITY_CONFIG = "Configuration";

        private Audit() {}
    }

    // ===================================================================
    // CONSTANTES DE FECHAS Y TIEMPO
    // ===================================================================

    /**
     * Constantes para manejo de fechas y tiempo.
     */
    public static final class DateTime {

        /** Formato de fecha por defecto */
        public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

        /** Formato de fecha y hora por defecto */
        public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

        /** Formato para nombres de archivo */
        public static final String FILE_DATETIME_FORMAT = "yyyyMMdd_HHmmss";

        /** Zona horaria por defecto */
        public static final String DEFAULT_TIMEZONE = "America/Bogota";

        /** Formatos de fecha para API */
        public static final String API_DATE_FORMAT = "yyyy-MM-dd";
        public static final String API_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

        private DateTime() {}
    }

    // ===================================================================
    // CONSTANTES DE ARCHIVOS Y DIRECTORIOS
    // ===================================================================

    /**
     * Constantes para manejo de archivos y directorios.
     */
    public static final class Files {

        /** Directorios base */
        public static final String BASE_STORAGE_DIR = "storage";
        public static final String BACKUPS_DIR = "backups";
        public static final String EXPORTS_DIR = "exports";
        public static final String TEMP_DIR = "temp";
        public static final String UPLOADS_DIR = "uploads";
        public static final String LOGS_DIR = "logs";

        /** Tamaños máximos de archivo */
        public static final long MAX_UPLOAD_SIZE = 50 * 1024 * 1024; // 50MB
        public static final long MAX_BACKUP_SIZE = 1024 * 1024 * 1024; // 1GB

        /** Tipos de archivo permitidos */
        public static final String[] ALLOWED_UPLOAD_TYPES = {
                "text/csv", "application/json",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        };

        private Files() {}
    }

    // ===================================================================
    // CONSTANTES DE API
    // ===================================================================

    /**
     * Constantes para la API REST.
     */
    public static final class Api {

        /** Versión de la API */
        public static final String API_VERSION = "v1";

        /** Prefijo base de la API */
        public static final String API_BASE_PATH = "/api/" + API_VERSION;

        /** Endpoints principales */
        public static final String PRODUCTS_ENDPOINT = API_BASE_PATH + "/products";
        public static final String CONFIG_ENDPOINT = API_BASE_PATH + "/config";
        public static final String REPORTS_ENDPOINT = API_BASE_PATH + "/reports";
        public static final String AUTH_ENDPOINT = API_BASE_PATH + "/auth";
        public static final String USERS_ENDPOINT = API_BASE_PATH + "/users";

        /** Headers personalizados */
        public static final String HEADER_TOTAL_COUNT = "X-Total-Count";
        public static final String HEADER_PAGE_COUNT = "X-Page-Count";
        public static final String HEADER_CURRENT_PAGE = "X-Current-Page";

        /** Parámetros de consulta */
        public static final String PARAM_PAGE = "page";
        public static final String PARAM_SIZE = "size";
        public static final String PARAM_SORT = "sort";
        public static final String PARAM_SEARCH = "search";
        public static final String PARAM_FILTER = "filter";

        private Api() {}
    }

    // ===================================================================
    // CONSTANTES DE CONFIGURACIÓN POR DEFECTO
    // ===================================================================

    /**
     * Constantes para valores por defecto del sistema.
     */
    public static final class Defaults {

        /** Usuario administrador por defecto */
        public static final String DEFAULT_ADMIN_USERNAME = "admin";
        public static final String DEFAULT_ADMIN_EMAIL = "admin@skugenerator.com";

        /** Configuraciones por defecto */
        public static final int DEFAULT_BACKUP_RETENTION_DAYS = 30;
        public static final int DEFAULT_LOG_RETENTION_DAYS = 90;
        public static final int DEFAULT_TEMP_FILES_RETENTION_HOURS = 24;

        /** Límites por defecto */
        public static final int DEFAULT_MAX_LOGIN_ATTEMPTS = 5;
        public static final int DEFAULT_LOCKOUT_DURATION_MINUTES = 30;

        private Defaults() {}
    }
}
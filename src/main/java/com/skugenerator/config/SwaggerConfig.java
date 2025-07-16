package com.skugenerator.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de Swagger/OpenAPI para la documentación de la API REST.
 *
 * Esta clase configura la documentación automática de la API incluyendo:
 * - Información general de la API
 * - Esquemas de autenticación (JWT)
 * - Agrupación de endpoints por funcionalidad
 * - Configuración de servidores
 * - Tags y categorización
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Slf4j
@Configuration
@Profile("!prod") // No habilitar en producción por seguridad
public class SwaggerConfig {

    // ===================================================================
    // PROPIEDADES DE CONFIGURACIÓN
    // ===================================================================

    @Value("${spring.application.name:SKU Generator}")
    private String applicationName;

    @Value("${app.name:SKU Generator}")
    private String appName;

    @Value("${app.description:Sistema de Generación de Códigos SKU para Tienda de Ropa Infantil}")
    private String appDescription;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.contact.name:Development Team}")
    private String contactName;

    @Value("${app.contact.email:dev@empresa.com}")
    private String contactEmail;

    @Value("${app.contact.url:https://empresa.com}")
    private String contactUrl;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    // ===================================================================
    // CONFIGURACIÓN PRINCIPAL DE OPENAPI
    // ===================================================================

    /**
     * Configuración principal de OpenAPI con información completa de la API.
     *
     * @return configuración de OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        log.info("Configurando OpenAPI/Swagger para documentación de API");

        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .components(securityComponents())
                .security(securityRequirements())
                .tags(apiTags());
    }

    /**
     * Información general de la API.
     *
     * @return información de la API
     */
    private Info apiInfo() {
        return new Info()
                .title(appName + " - API Documentation")
                .description(buildApiDescription())
                .version(appVersion)
                .contact(apiContact())
                .license(apiLicense())
                .termsOfService("https://empresa.com/terms");
    }

    /**
     * Información de contacto para la API.
     *
     * @return contacto de la API
     */
    private Contact apiContact() {
        return new Contact()
                .name(contactName)
                .email(contactEmail)
                .url(contactUrl);
    }

    /**
     * Información de licencia de la API.
     *
     * @return licencia de la API
     */
    private License apiLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Configuración de servidores de la API.
     *
     * @return lista de servidores
     */
    private List<Server> apiServers() {
        String baseUrl = "http://localhost:" + serverPort + contextPath;

        Server localServer = new Server()
                .url(baseUrl)
                .description("Servidor de Desarrollo Local");

        Server dockerServer = new Server()
                .url("http://localhost:8080" + contextPath)
                .description("Servidor Docker Local");

        return Arrays.asList(localServer, dockerServer);
    }

    /**
     * Construir descripción detallada de la API.
     *
     * @return descripción de la API
     */
    private String buildApiDescription() {
        return """
                ## 🧸 SKU Generator API
                
                API REST completa para el sistema de generación de códigos SKU de productos para tienda de ropa infantil.
                
                ### 🚀 Funcionalidades Principales:
                
                - **🏷️ Generación de Códigos SKU**: Códigos únicos de 12 dígitos con estructura específica
                - **🧸 Gestión de Productos**: CRUD completo con validaciones de negocio
                - **⚙️ Configuración de Catálogos**: Administración de tipos, categorías, tallas, colores y temporadas
                - **📊 Reportes y Estadísticas**: Análisis detallados y métricas en tiempo real
                - **👥 Gestión de Usuarios**: Control de acceso con roles y permisos
                - **📤 Importación/Exportación**: Múltiples formatos (CSV, Excel, JSON)
                - **💾 Backup y Restauración**: Gestión segura de datos
                
                ### 🔐 Autenticación:
                
                La API utiliza autenticación JWT (JSON Web Tokens). Para acceder a los endpoints protegidos:
                
                1. Realizar login en `/api/auth/login` con credenciales válidas
                2. Usar el token JWT en el header `Authorization: Bearer <token>`
                3. El token expira en 24 horas (configurable)
                
                ### 📋 Estructura del Código SKU:
                
                ```
                T CC S TT CC S ###
                │ │  │ │  │  │ └── Consecutivo (3 dígitos)
                │ │  │ │  │  └── Temporada (1 dígito)
                │ │  │ │  └── Color (2 dígitos)
                │ │  │ └── Talla (2 dígitos)
                │ │  └── Subcategoría (1 dígito)
                │ └── Categoría (2 dígitos)
                └── Tipo de Producto (1 dígito)
                ```
                
                ### 🎯 Roles de Usuario:
                
                - **ADMIN**: Acceso total al sistema
                - **USER**: Gestión de productos y reportes
                - **VIEWER**: Solo lectura
                
                ### 📧 Soporte:
                
                Para soporte técnico, contactar a: """ + contactEmail;
    }

    // ===================================================================
    // CONFIGURACIÓN DE SEGURIDAD
    // ===================================================================

    /**
     * Componentes de seguridad para JWT.
     *
     * @return componentes de seguridad
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", jwtSecurityScheme())
                .addSecuritySchemes("apiKey", apiKeySecurityScheme());
    }

    /**
     * Esquema de seguridad JWT.
     *
     * @return esquema de seguridad JWT
     */
    private SecurityScheme jwtSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Token JWT obtenido del endpoint de login. Formato: Bearer <token>");
    }

    /**
     * Esquema de seguridad con API Key (para uso futuro).
     *
     * @return esquema de seguridad API Key
     */
    private SecurityScheme apiKeySecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-API-Key")
                .description("API Key para acceso programático");
    }

    /**
     * Requerimientos de seguridad para los endpoints.
     *
     * @return lista de requerimientos de seguridad
     */
    private List<SecurityRequirement> securityRequirements() {
        SecurityRequirement bearerAuthRequirement = new SecurityRequirement().addList("bearerAuth");
        return Arrays.asList(bearerAuthRequirement);
    }

    // ===================================================================
    // CONFIGURACIÓN DE TAGS
    // ===================================================================

    /**
     * Tags para categorizar los endpoints de la API.
     *
     * @return lista de tags
     */
    private List<Tag> apiTags() {
        return Arrays.asList(
                new Tag().name("🔐 Autenticación")
                        .description("Endpoints para login, logout y gestión de tokens JWT"),

                new Tag().name("🧸 Productos")
                        .description("CRUD completo de productos y generación de códigos SKU"),

                new Tag().name("⚙️ Configuraciones")
                        .description("Gestión de catálogos: tipos, categorías, tallas, colores y temporadas"),

                new Tag().name("👥 Usuarios")
                        .description("Gestión de usuarios, roles y permisos"),

                new Tag().name("📊 Reportes")
                        .description("Estadísticas, análisis y reportes detallados"),

                new Tag().name("📤 Exportación")
                        .description("Exportación e importación de datos en múltiples formatos"),

                new Tag().name("💾 Base de Datos")
                        .description("Gestión de backup, restauración y consultas SQL"),

                new Tag().name("🔧 Sistema")
                        .description("Endpoints de salud, métricas y administración")
        );
    }

    // ===================================================================
    // CONFIGURACIÓN DE GRUPOS DE API
    // ===================================================================

    /**
     * Grupo de API para endpoints de autenticación.
     *
     * @return grupo de API de autenticación
     */
    @Bean
    public GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
                .group("01-authentication")
                .displayName("🔐 Autenticación")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    /**
     * Grupo de API para endpoints de productos.
     *
     * @return grupo de API de productos
     */
    @Bean
    public GroupedOpenApi productsApi() {
        return GroupedOpenApi.builder()
                .group("02-products")
                .displayName("🧸 Productos")
                .pathsToMatch("/api/products/**")
                .build();
    }

    /**
     * Grupo de API para endpoints de configuración.
     *
     * @return grupo de API de configuración
     */
    @Bean
    public GroupedOpenApi configurationApi() {
        return GroupedOpenApi.builder()
                .group("03-configuration")
                .displayName("⚙️ Configuraciones")
                .pathsToMatch("/api/config/**")
                .build();
    }

    /**
     * Grupo de API para endpoints de usuarios.
     *
     * @return grupo de API de usuarios
     */
    @Bean
    public GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder()
                .group("04-users")
                .displayName("👥 Usuarios")
                .pathsToMatch("/api/users/**")
                .build();
    }

    /**
     * Grupo de API para endpoints de reportes.
     *
     * @return grupo de API de reportes
     */
    @Bean
    public GroupedOpenApi reportsApi() {
        return GroupedOpenApi.builder()
                .group("05-reports")
                .displayName("📊 Reportes")
                .pathsToMatch("/api/reports/**")
                .build();
    }

    /**
     * Grupo de API para endpoints de exportación.
     *
     * @return grupo de API de exportación
     */
    @Bean
    public GroupedOpenApi exportApi() {
        return GroupedOpenApi.builder()
                .group("06-export")
                .displayName("📤 Exportación")
                .pathsToMatch("/api/export/**", "/api/import/**")
                .build();
    }

    /**
     * Grupo de API para endpoints de base de datos.
     *
     * @return grupo de API de base de datos
     */
    @Bean
    public GroupedOpenApi databaseApi() {
        return GroupedOpenApi.builder()
                .group("07-database")
                .displayName("💾 Base de Datos")
                .pathsToMatch("/api/database/**", "/api/backup/**")
                .build();
    }

    /**
     * Grupo de API para endpoints del sistema y monitoreo.
     *
     * @return grupo de API del sistema
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("08-system")
                .displayName("🔧 Sistema")
                .pathsToMatch("/actuator/**")
                .build();
    }

    /**
     * Grupo de API completa (todos los endpoints).
     *
     * @return grupo de API completa
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("00-all")
                .displayName("📋 API Completa")
                .pathsToMatch("/api/**", "/actuator/**")
                .build();
    }

    // ===================================================================
    // CONFIGURACIÓN POST-CONSTRUCT
    // ===================================================================

    /**
     * Inicialización post-construcción para logging de configuración.
     */
    @jakarta.annotation.PostConstruct
    public void logSwaggerConfiguration() {
        log.info("=".repeat(50));
        log.info("📚 SWAGGER/OPENAPI CONFIGURATION INITIALIZED");
        log.info("=".repeat(50));
        log.info("📖 Application: {} v{}", appName, appVersion);
        log.info("🌐 Swagger UI: http://localhost:{}{}/swagger-ui.html", serverPort, contextPath);
        log.info("📋 API Docs: http://localhost:{}{}/api-docs", serverPort, contextPath);
        log.info("🔐 Security: JWT Bearer Token");
        log.info("👥 Contact: {} ({})", contactName, contactEmail);
        log.info("📄 License: MIT License");
        log.info("🏷️  Tags: 8 categorías de endpoints");
        log.info("📦 Groups: 9 grupos de API configurados");
        log.info("=".repeat(50));
    }
}
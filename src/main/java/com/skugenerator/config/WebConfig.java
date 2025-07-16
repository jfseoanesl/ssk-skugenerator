package com.skugenerator.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.http.CacheControl;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Configuración web general para la aplicación SKU Generator.
 *
 * Esta clase configura aspectos fundamentales del comportamiento web incluyendo:
 * - CORS (Cross-Origin Resource Sharing)
 * - Recursos estáticos y caching
 * - Internacionalización (i18n)
 * - Interceptors personalizados
 * - Formatters para tipos de datos
 * - View controllers
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Slf4j
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // ===================================================================
    // PROPIEDADES DE CONFIGURACIÓN
    // ===================================================================

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private List<String> allowedHeaders;

    @Value("${app.cors.exposed-headers}")
    private List<String> exposedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Value("${spring.web.resources.cache.period:31536000}")
    private long cachePeriod;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    // ===================================================================
    // CONFIGURACIÓN DE CORS
    // ===================================================================

    /**
     * Configura CORS (Cross-Origin Resource Sharing) para permitir
     * el acceso desde diferentes dominios de frontend.
     *
     * @param registry registro de configuraciones CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configurando CORS mappings");

        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins.toArray(new String[0]))
                .allowedMethods(allowedMethods.toArray(new String[0]))
                .allowedHeaders(allowedHeaders.toArray(new String[0]))
                .exposedHeaders(exposedHeaders.toArray(new String[0]))
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);

        // CORS para Actuator endpoints
        registry.addMapping("/actuator/**")
                .allowedOriginPatterns(allowedOrigins.toArray(new String[0]))
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(maxAge);

        // CORS para Swagger UI
        registry.addMapping("/swagger-ui/**")
                .allowedOriginPatterns(allowedOrigins.toArray(new String[0]))
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(maxAge);

        log.info("CORS configurado para orígenes: {}", allowedOrigins);
    }

    /**
     * Bean alternativo de configuración CORS para mayor control.
     *
     * @return fuente de configuración CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.debug("Configurando CorsConfigurationSource");

        CorsConfiguration configuration = new CorsConfiguration();

        // Configurar orígenes permitidos
        configuration.setAllowedOriginPatterns(allowedOrigins);

        // Configurar métodos permitidos
        configuration.setAllowedMethods(allowedMethods);

        // Configurar headers permitidos
        configuration.setAllowedHeaders(allowedHeaders);

        // Configurar headers expuestos
        configuration.setExposedHeaders(exposedHeaders);

        // Configurar credenciales
        configuration.setAllowCredentials(allowCredentials);

        // Configurar tiempo de cache
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // ===================================================================
    // CONFIGURACIÓN DE RECURSOS ESTÁTICOS
    // ===================================================================

    /**
     * Configura el manejo de recursos estáticos con cache optimizado.
     *
     * @param registry registro de recursos
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("Configurando manejadores de recursos estáticos");

        // Configurar cache para recursos estáticos
        CacheControl cacheControl = CacheControl.maxAge(Duration.ofSeconds(cachePeriod))
                .cachePublic()
                .mustRevalidate();

        // Recursos CSS
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCacheControl(cacheControl)
                .resourceChain(true);

        // Recursos JavaScript
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCacheControl(cacheControl)
                .resourceChain(true);

        // Recursos de imágenes
        registry.addResourceHandler("/img/**", "/images/**")
                .addResourceLocations("classpath:/static/img/", "classpath:/static/images/")
                .setCacheControl(cacheControl)
                .resourceChain(true);

        // Recursos de fuentes
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic())
                .resourceChain(true);

        // Favicon
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic());

        // Swagger UI resources (solo en desarrollo)
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                .setCacheControl(CacheControl.noCache());

        log.debug("Manejadores de recursos configurados con cache de {} segundos", cachePeriod);
    }

    // ===================================================================
    // CONFIGURACIÓN DE INTERNACIONALIZACIÓN
    // ===================================================================

    /**
     * Configurar resolver de locale para internacionalización.
     *
     * @return resolver de locale basado en sesión
     */
    @Bean
    public LocaleResolver localeResolver() {
        log.debug("Configurando LocaleResolver");

        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("es", "CO")); // Español Colombia por defecto
        return localeResolver;
    }

    /**
     * Interceptor para cambio de idioma basado en parámetro.
     *
     * @return interceptor de cambio de locale
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        log.debug("Configurando LocaleChangeInterceptor");

        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // Parámetro ?lang=en para cambiar idioma
        return interceptor;
    }

    /**
     * Registrar interceptors personalizados.
     *
     * @param registry registro de interceptors
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Registrando interceptors");

        // Interceptor de cambio de idioma
        registry.addInterceptor(localeChangeInterceptor());

        // Interceptor personalizado para auditoría (se implementará en fase posterior)
        registry.addInterceptor(new RequestLoggingInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/actuator/**");

        log.debug("Interceptors registrados exitosamente");
    }

    // ===================================================================
    // CONFIGURACIÓN DE FORMATTERS
    // ===================================================================

    /**
     * Configurar formatters personalizados para tipos de datos.
     *
     * @param registry registro de formatters
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        log.debug("Configurando formatters personalizados");

        // Formatter para fechas
        registry.addFormatter(new DateFormatter("yyyy-MM-dd"));

        // Formatter personalizado para códigos SKU (se implementará en fase posterior)
        // registry.addFormatter(new SkuCodeFormatter());

        log.debug("Formatters configurados");
    }

    // ===================================================================
    // CONFIGURACIÓN DE VIEW CONTROLLERS
    // ===================================================================

    /**
     * Configurar controladores de vista simples.
     *
     * @param registry registro de view controllers
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        log.debug("Configurando view controllers");

        // Página de inicio por defecto
        registry.addViewController("/").setViewName("forward:/index");

        // Páginas de error personalizadas
        registry.addViewController("/error/404").setViewName("error/404");
        registry.addViewController("/error/500").setViewName("error/500");
        registry.addViewController("/error/access-denied").setViewName("error/access-denied");

        // Redirección de Swagger UI
        registry.addRedirectViewController("/swagger", "/swagger-ui.html");
        registry.addRedirectViewController("/docs", "/swagger-ui.html");

        // Establecer orden de prioridad
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);

        log.debug("View controllers configurados");
    }

    // ===================================================================
    // CONFIGURACIÓN DE CONTENT NEGOTIATION
    // ===================================================================

    /**
     * Configurar negociación de contenido para diferentes tipos de respuesta.
     *
     * @param configurer configurador de content negotiation
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        log.debug("Configurando content negotiation");

        configurer
                .favorParameter(true) // Permitir parámetro ?format=json
                .parameterName("format")
                .ignoreAcceptHeader(false) // Considerar Accept header
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .mediaType("json", org.springframework.http.MediaType.APPLICATION_JSON)
                .mediaType("xml", org.springframework.http.MediaType.APPLICATION_XML)
                .mediaType("html", org.springframework.http.MediaType.TEXT_HTML);
    }

    // ===================================================================
    // BEANS AUXILIARES
    // ===================================================================

    /**
     * Bean para configuración de cache de recursos.
     */
    @Bean
    public CacheProfile cacheProfile() {
        return new CacheProfile(cachePeriod);
    }

    /**
     * Interceptor personalizado para logging de requests.
     */
    public static class RequestLoggingInterceptor implements HandlerInterceptor {
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RequestLoggingInterceptor.class);

        @Override
        public boolean preHandle(jakarta.servlet.http.HttpServletRequest request,
                                 jakarta.servlet.http.HttpServletResponse response,
                                 Object handler) {

            String method = request.getMethod();
            String uri = request.getRequestURI();
            String userAgent = request.getHeader("User-Agent");
            String remoteAddr = getClientIpAddress(request);

            logger.debug("🌐 {} {} from {} - {}", method, uri, remoteAddr, userAgent);

            // Agregar timestamp al request para medir duración
            request.setAttribute("startTime", System.currentTimeMillis());

            return true;
        }

        @Override
        public void afterCompletion(jakarta.servlet.http.HttpServletRequest request,
                                    jakarta.servlet.http.HttpServletResponse response,
                                    Object handler,
                                    Exception ex) {

            Long startTime = (Long) request.getAttribute("startTime");
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                String method = request.getMethod();
                String uri = request.getRequestURI();
                int status = response.getStatus();

                logger.debug("✅ {} {} - {} ({}ms)", method, uri, status, duration);

                // Log de requests lentos (más de 2 segundos)
                if (duration > 2000) {
                    logger.warn("🐌 Slow request: {} {} took {}ms", method, uri, duration);
                }
            }
        }

        /**
         * Obtener la dirección IP real del cliente considerando proxies.
         */
        private String getClientIpAddress(jakarta.servlet.http.HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }

            return request.getRemoteAddr();
        }
    }

    /**
     * Clase para configuración de cache de recursos.
     */
    public static class CacheProfile {
        private final long cacheSeconds;

        public CacheProfile(long cacheSeconds) {
            this.cacheSeconds = cacheSeconds;
        }

        public long getCacheSeconds() {
            return cacheSeconds;
        }
    }
}

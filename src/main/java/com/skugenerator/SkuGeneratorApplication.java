package com.skugenerator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * Clase principal de la aplicación SKU Generator.
 *
 * Esta aplicación proporciona un sistema completo para la generación automática
 * de códigos SKU de 12 dígitos para productos de una tienda de ropa infantil.
 *
 * Funcionalidades principales:
 * - Generación automática de códigos SKU únicos
 * - Gestión completa de productos y configuraciones
 * - Sistema de reportes y estadísticas
 * - Control de acceso con autenticación JWT
 * - Exportación e importación de datos
 * - Sistema de backup y restauración
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Slf4j
@SpringBootApplication(
		scanBasePackages = "com.skugenerator"
)
@EnableJpaRepositories(
		basePackages = "com.skugenerator.repository"
)
@EnableJpaAuditing(
		auditorAwareRef = "auditingAwareProvider"
)
@EnableTransactionManagement
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
public class SkuGeneratorApplication {

	/**
	 * Método principal para iniciar la aplicación Spring Boot.
	 *
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		// Configurar propiedades del sistema antes de iniciar
		configureSystemProperties();

		// Iniciar la aplicación
		SpringApplication app = new SpringApplication(SkuGeneratorApplication.class);

		// Configuraciones adicionales de la aplicación
		configureApplication(app);

		// Ejecutar la aplicación
		var context = app.run(args);

		// Log de información de inicio
		logApplicationStartup(context);
	}

	/**
	 * Configuración inicial después de la construcción del bean.
	 * Se ejecuta una sola vez al inicializar la aplicación.
	 */
	@PostConstruct
	public void init() {
		// Configurar zona horaria por defecto
		TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));

		log.info("=".repeat(60));
		log.info("🧸 SKU GENERATOR APPLICATION INITIALIZED");
		log.info("=".repeat(60));
		log.info("📅 Timezone configured: {}", TimeZone.getDefault().getID());
		log.info("☕ Java version: {}", System.getProperty("java.version"));
		log.info("🚀 Spring Boot version: {}", getClass().getPackage().getImplementationVersion());
		log.info("💾 Available processors: {}", Runtime.getRuntime().availableProcessors());
		log.info("🧠 Max memory: {} MB", Runtime.getRuntime().maxMemory() / 1024 / 1024);
		log.info("=".repeat(60));

		// Log de funcionalidades principales
		logMainFeatures();
	}

	/**
	 * Configura propiedades del sistema necesarias para la aplicación.
	 */
	private static void configureSystemProperties() {
		// Configurar propiedades de red para mejor performance
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.awt.headless", "true");

		// Configurar propiedades de JPA/Hibernate
		System.setProperty("hibernate.types.print.banner", "false");

		// Configurar encoding por defecto
		System.setProperty("file.encoding", "UTF-8");

		log.debug("System properties configured successfully");
	}

	/**
	 * Configura la aplicación Spring Boot con settings adicionales.
	 *
	 * @param app la instancia de SpringApplication
	 */
	private static void configureApplication(SpringApplication app) {
		// Configurar banner personalizado
		app.setBannerMode(org.springframework.boot.Banner.Mode.CONSOLE);

		// Configurar propiedades adicionales
		app.setRegisterShutdownHook(true);

		// Configurar listeners adicionales si es necesario
		// app.addListeners(new CustomApplicationListener());

		log.debug("SpringApplication configured successfully");
	}

	/**
	 * Log de información importante al iniciar la aplicación.
	 *
	 * @param context el contexto de la aplicación Spring
	 */
	private static void logApplicationStartup(org.springframework.context.ConfigurableApplicationContext context) {
		String serverPort = context.getEnvironment().getProperty("server.port", "8080");
		String contextPath = context.getEnvironment().getProperty("server.servlet.context-path", "");
		String activeProfiles = String.join(", ", context.getEnvironment().getActiveProfiles());

		log.info("");
		log.info("🎉 APPLICATION STARTED SUCCESSFULLY!");
		log.info("🌐 Local URL: http://localhost:{}{}", serverPort, contextPath);
		log.info("📊 Swagger UI: http://localhost:{}{}/swagger-ui.html", serverPort, contextPath);
		log.info("❤️ Actuator Health: http://localhost:{}{}/actuator/health", serverPort, contextPath);
		log.info("🏷️ Active Profiles: {}", activeProfiles.isEmpty() ? "default" : activeProfiles);
		log.info("📝 Application Name: {}", context.getEnvironment().getProperty("spring.application.name", "SKU Generator"));
		log.info("");
	}

	/**
	 * Log de las funcionalidades principales de la aplicación.
	 */
	private void logMainFeatures() {
		log.info("🔧 MAIN FEATURES AVAILABLE:");
		log.info("  ✅ Automatic SKU Code Generation (12-digit format)");
		log.info("  👥 User Management & JWT Authentication");
		log.info("  ⚙️ Product Configuration Management");
		log.info("  🧸 Complete Product CRUD Operations");
		log.info("  📊 Advanced Reports & Statistics");
		log.info("  📤 Multi-format Export (CSV, Excel, JSON)");
		log.info("  💾 Database Backup & Restoration");
		log.info("  🔍 Advanced Search & Filtering");
		log.info("  📱 Responsive Web Interface");
		log.info("  🔒 Role-based Access Control");
		log.info("=".repeat(60));
	}
}
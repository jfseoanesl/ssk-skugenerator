package com.skugenerator.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuración de base de datos para la aplicación SKU Generator.
 *
 * Esta clase configura el pool de conexiones HikariCP con optimizaciones
 * específicas para MySQL y diferentes entornos (dev, test, prod).
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Slf4j
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    // HikariCP Configuration Properties
    @Value("${spring.datasource.hikari.pool-name:SkuGeneratorHikariPool}")
    private String poolName;

    @Value("${spring.datasource.hikari.maximum-pool-size:20}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.idle-timeout:300000}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.connection-timeout:20000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:1800000}")
    private long maxLifetime;

    @Value("${spring.datasource.hikari.leak-detection-threshold:60000}")
    private long leakDetectionThreshold;

    @Value("${spring.datasource.hikari.validation-timeout:5000}")
    private long validationTimeout;

    @Value("${spring.datasource.hikari.connection-test-query:SELECT 1}")
    private String connectionTestQuery;

    @Value("${spring.datasource.hikari.auto-commit:true}")
    private boolean autoCommit;

    /**
     * Configuración del DataSource principal usando HikariCP.
     *
     * @return DataSource configurado con HikariCP
     */
    @Bean
    @Primary
    @Profile("!test")
    public DataSource primaryDataSource() {
        log.info("Configurando DataSource principal con HikariCP");

        HikariConfig config = new HikariConfig();

        // Configuración básica de conexión
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);

        // Configuración del pool
        config.setPoolName(poolName);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setIdleTimeout(idleTimeout);
        config.setConnectionTimeout(connectionTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setLeakDetectionThreshold(leakDetectionThreshold);
        config.setValidationTimeout(validationTimeout);
        config.setConnectionTestQuery(connectionTestQuery);
        config.setAutoCommit(autoCommit);

        // Configurar propiedades específicas de MySQL
        configureMySQLProperties(config);

        // Configurar propiedades de performance
        configurePerformanceProperties(config);

        // Configurar propiedades de seguridad
        configureSecurityProperties(config);

        HikariDataSource dataSource = new HikariDataSource(config);

        log.info("DataSource configurado exitosamente:");
        log.info("  📊 Pool name: {}", poolName);
        log.info("  🔗 JDBC URL: {}", maskJdbcUrl(jdbcUrl));
        log.info("  👤 Username: {}", username);
        log.info("  🏊 Pool size: {} min, {} max", minimumIdle, maximumPoolSize);
        log.info("  ⏱️  Connection timeout: {}ms", connectionTimeout);
        log.info("  🔄 Max lifetime: {}ms", maxLifetime);

        return dataSource;
    }

    /**
     * Configuración del DataSource para testing con H2.
     *
     * @return DataSource configurado para tests
     */
    @Bean
    @Primary
    @Profile("test")
    public DataSource testDataSource() {
        log.info("Configurando DataSource para testing con H2");

        HikariConfig config = new HikariConfig();

        // Configuración para H2 en memoria
        config.setJdbcUrl("jdbc:h2:mem:sku_generator_test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");

        // Configuración optimizada para tests
        config.setPoolName("SkuGeneratorTestPool");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setIdleTimeout(60000);
        config.setConnectionTimeout(5000);
        config.setMaxLifetime(300000);
        config.setConnectionTestQuery("SELECT 1");
        config.setAutoCommit(true);

        // Configurar propiedades específicas para H2
        configureH2Properties(config);

        HikariDataSource dataSource = new HikariDataSource(config);

        log.info("DataSource para testing configurado exitosamente");

        return dataSource;
    }

    /**
     * Configura propiedades específicas de MySQL para optimización.
     *
     * @param config configuración de HikariCP
     */
    private void configureMySQLProperties(HikariConfig config) {
        Properties dataSourceProperties = new Properties();

        // Optimizaciones de performance para MySQL
        dataSourceProperties.setProperty("cachePrepStmts", "true");
        dataSourceProperties.setProperty("prepStmtCacheSize", "250");
        dataSourceProperties.setProperty("prepStmtCacheSqlLimit", "2048");
        dataSourceProperties.setProperty("useServerPrepStmts", "true");
        dataSourceProperties.setProperty("useLocalSessionState", "true");
        dataSourceProperties.setProperty("rewriteBatchedStatements", "true");
        dataSourceProperties.setProperty("cacheResultSetMetadata", "true");
        dataSourceProperties.setProperty("cacheServerConfiguration", "true");
        dataSourceProperties.setProperty("elideSetAutoCommits", "true");
        dataSourceProperties.setProperty("maintainTimeStats", "false");

        // Configuración de encoding
        dataSourceProperties.setProperty("useUnicode", "true");
        dataSourceProperties.setProperty("characterEncoding", "UTF-8");

        // Configuración de timezone
        dataSourceProperties.setProperty("serverTimezone", "America/Bogota");

        // Configuración de reconexión
        dataSourceProperties.setProperty("autoReconnect", "true");
        dataSourceProperties.setProperty("failOverReadOnly", "false");
        dataSourceProperties.setProperty("maxReconnects", "10");

        config.setDataSourceProperties(dataSourceProperties);

        log.debug("Propiedades específicas de MySQL configuradas");
    }

    /**
     * Configura propiedades de performance adicionales.
     *
     * @param config configuración de HikariCP
     */
    private void configurePerformanceProperties(HikariConfig config) {
        // Registrar métricas de HikariCP
        config.setRegisterMbeans(true);

        // Configurar health check
        config.setConnectionInitSql("SELECT 1");

        log.debug("Propiedades de performance configuradas");
    }

    /**
     * Configura propiedades de seguridad.
     *
     * @param config configuración de HikariCP
     */
    private void configureSecurityProperties(HikariConfig config) {
        // No permitir acceso público por defecto
        Properties securityProps = config.getDataSourceProperties();
        if (securityProps == null) {
            securityProps = new Properties();
        }

        // Configurar SSL si está habilitado
        if (jdbcUrl.contains("useSSL=true")) {
            securityProps.setProperty("verifyServerCertificate", "true");
            securityProps.setProperty("requireSSL", "true");
            log.info("SSL habilitado para conexiones a base de datos");
        }

        config.setDataSourceProperties(securityProps);

        log.debug("Propiedades de seguridad configuradas");
    }

    /**
     * Configura propiedades específicas para H2 en tests.
     *
     * @param config configuración de HikariCP
     */
    private void configureH2Properties(HikariConfig config) {
        Properties dataSourceProperties = new Properties();

        // Configuración para H2 con compatibilidad MySQL
        dataSourceProperties.setProperty("MODE", "MySQL");
        dataSourceProperties.setProperty("DB_CLOSE_DELAY", "-1");
        dataSourceProperties.setProperty("DB_CLOSE_ON_EXIT", "FALSE");

        config.setDataSourceProperties(dataSourceProperties);

        log.debug("Propiedades específicas de H2 configuradas");
    }

    /**
     * Enmascara la URL de JDBC para logging seguro.
     *
     * @param jdbcUrl URL de JDBC original
     * @return URL enmascarada
     */
    private String maskJdbcUrl(String jdbcUrl) {
        if (jdbcUrl == null) {
            return "null";
        }

        // Enmascarar parámetros sensibles como passwords en la URL
        return jdbcUrl.replaceAll("password=([^&]+)", "password=****");
    }

    /**
     * Bean para configuración personalizada de propiedades de base de datos.
     * Útil para configuraciones dinámicas o específicas del entorno.
     *
     * @return Properties con configuraciones adicionales
     */
    @Bean
    public Properties additionalDataSourceProperties() {
        Properties props = new Properties();

        // Configuraciones adicionales que pueden ser específicas del entorno
        props.setProperty("application.name", "sku-generator");
        props.setProperty("connection.provider_disables_autocommit", "true");

        return props;
    }

    /**
     * Bean de configuración para estadísticas de HikariCP.
     * Útil para monitoreo y debugging.
     *
     * @param dataSource el DataSource configurado
     * @return configuración de estadísticas
     */
    @Bean
    public HikariDataSourceMetrics hikariMetrics(DataSource dataSource) {
        if (dataSource instanceof HikariDataSource) {
            return new HikariDataSourceMetrics((HikariDataSource) dataSource);
        }
        return null;
    }

    /**
     * Clase interna para métricas de HikariCP.
     */
    public static class HikariDataSourceMetrics {
        private final HikariDataSource dataSource;

        public HikariDataSourceMetrics(HikariDataSource dataSource) {
            this.dataSource = dataSource;
        }

        public int getActiveConnections() {
            return dataSource.getHikariPoolMXBean().getActiveConnections();
        }

        public int getIdleConnections() {
            return dataSource.getHikariPoolMXBean().getIdleConnections();
        }

        public int getTotalConnections() {
            return dataSource.getHikariPoolMXBean().getTotalConnections();
        }

        public int getThreadsAwaitingConnection() {
            return dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection();
        }

        public void logCurrentStats() {
            log.info("📊 HikariCP Stats - Active: {}, Idle: {}, Total: {}, Waiting: {}",
                    getActiveConnections(),
                    getIdleConnections(),
                    getTotalConnections(),
                    getThreadsAwaitingConnection());
        }
    }
}
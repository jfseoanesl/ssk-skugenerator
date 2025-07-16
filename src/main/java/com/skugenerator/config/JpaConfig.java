package com.skugenerator.config;

import com.skugenerator.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Optional;
import java.util.Properties;

/**
 * Configuración específica de JPA/Hibernate para la aplicación SKU Generator.
 *
 * Esta clase configura aspectos avanzados de JPA incluyendo:
 * - Auditoría automática de entidades
 * - Configuración específica de Hibernate
 * - Gestión de transacciones
 * - Configuración de repositorios
 * - Optimizaciones de performance
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Slf4j
@Configuration
@EnableJpaRepositories(
        basePackages = "com.skugenerator.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
@EnableJpaAuditing(auditorAwareRef = "auditingAwareProvider")
@EnableTransactionManagement
public class JpaConfig {

    // ===================================================================
    // PROPIEDADES DE CONFIGURACIÓN
    // ===================================================================

    @Value("${spring.jpa.hibernate.ddl-auto:validate}")
    private String ddlAuto;

    @Value("${spring.jpa.show-sql:false}")
    private boolean showSql;

    @Value("${spring.jpa.properties.hibernate.format_sql:false}")
    private boolean formatSql;

    @Value("${spring.jpa.properties.hibernate.use_sql_comments:false}")
    private boolean useSqlComments;

    @Value("${spring.jpa.properties.hibernate.generate_statistics:false}")
    private boolean generateStatistics;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:25}")
    private int batchSize;

    @Value("${spring.jpa.properties.hibernate.cache.use_second_level_cache:true}")
    private boolean useSecondLevelCache;

    @Value("${spring.jpa.properties.hibernate.cache.use_query_cache:true}")
    private boolean useQueryCache;

    // ===================================================================
    // CONFIGURACIÓN DE ENTITY MANAGER
    // ===================================================================

    /**
     * Configuración del EntityManagerFactory con optimizaciones específicas.
     *
     * @param dataSource fuente de datos configurada
     * @return EntityManagerFactory configurado
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        log.info("Configurando EntityManagerFactory con Hibernate");

        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

        // Configurar datasource
        entityManagerFactory.setDataSource(dataSource);

        // Configurar paquetes de entidades
        entityManagerFactory.setPackagesToScan("com.skugenerator.model.entity");

        // Configurar vendor adapter
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
        vendorAdapter.setShowSql(showSql);
        vendorAdapter.setGenerateDdl(false); // Usamos Flyway para DDL
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

        // Configurar propiedades de Hibernate
        entityManagerFactory.setJpaProperties(hibernateProperties());

        // Configurar nombre de la unidad de persistencia
        entityManagerFactory.setPersistenceUnitName("skuGeneratorPU");

        log.info("EntityManagerFactory configurado exitosamente");
        log.debug("  📦 Paquetes de entidades: com.skugenerator.model.entity");
        log.debug("  🗄️  Dialecto: MySQL8Dialect");
        log.debug("  📊 Mostrar SQL: {}", showSql);
        log.debug("  🔄 DDL Auto: {}", ddlAuto);

        return entityManagerFactory;
    }

    /**
     * Configuración específica de propiedades de Hibernate.
     *
     * @return propiedades de Hibernate optimizadas
     */
    @Bean
    public Properties hibernateProperties() {
        log.debug("Configurando propiedades específicas de Hibernate");

        Properties properties = new Properties();

        // ===== CONFIGURACIÓN BÁSICA =====
        properties.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty("hibernate.show_sql", String.valueOf(showSql));
        properties.setProperty("hibernate.format_sql", String.valueOf(formatSql));
        properties.setProperty("hibernate.use_sql_comments", String.valueOf(useSqlComments));

        // ===== CONFIGURACIÓN DE PERFORMANCE =====

        // Configuración de batch processing
        properties.setProperty("hibernate.jdbc.batch_size", String.valueOf(batchSize));
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");

        // Configuración de fetch
        properties.setProperty("hibernate.enable_lazy_load_no_trans", "false");
        properties.setProperty("hibernate.max_fetch_depth", "3");

        // ===== CONFIGURACIÓN DE CACHE =====
        if (useSecondLevelCache) {
            properties.setProperty("hibernate.cache.use_second_level_cache", "true");
            properties.setProperty("hibernate.cache.use_query_cache", String.valueOf(useQueryCache));
            properties.setProperty("hibernate.cache.region.factory_class",
                    "org.hibernate.cache.jcache.JCacheRegionFactory");
            properties.setProperty("hibernate.javax.cache.provider",
                    "com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider");
        }

        // ===== CONFIGURACIÓN DE ESTADÍSTICAS =====
        properties.setProperty("hibernate.generate_statistics", String.valueOf(generateStatistics));
        if (generateStatistics) {
            properties.setProperty("hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS", "1000");
        }

        // ===== CONFIGURACIÓN DE NAMING STRATEGY =====
        properties.setProperty("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        properties.setProperty("hibernate.implicit_naming_strategy",
                "org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl");

        // ===== CONFIGURACIÓN DE TIMEZONE =====
        properties.setProperty("hibernate.jdbc.time_zone", Constants.DateTime.DEFAULT_TIMEZONE);

        // ===== CONFIGURACIÓN DE VALIDACIÓN =====
        properties.setProperty("hibernate.validator.apply_to_ddl", "false");
        properties.setProperty("hibernate.validator.autoregister_listeners", "false");

        // ===== CONFIGURACIÓN DE CONNECTION HANDLING =====
        properties.setProperty("hibernate.connection.provider_disables_autocommit", "true");
        properties.setProperty("hibernate.connection.autocommit", "false");

        // ===== CONFIGURACIÓN ESPECÍFICA PARA DESARROLLO =====
        if ("dev".equals(System.getProperty("spring.profiles.active"))) {
            properties.setProperty("hibernate.highlight_sql", "true");
            properties.setProperty("hibernate.type.descriptor.sql.BasicBinder", "TRACE");
        }

        log.debug("Propiedades de Hibernate configuradas:");
        log.debug("  🏊 Batch size: {}", batchSize);
        log.debug("  💾 Second level cache: {}", useSecondLevelCache);
        log.debug("  📊 Generate statistics: {}", generateStatistics);

        return properties;
    }

    // ===================================================================
    // CONFIGURACIÓN DE TRANSACTION MANAGER
    // ===================================================================

    /**
     * Configuración del gestor de transacciones JPA.
     *
     * @param entityManagerFactory factory de entity manager
     * @return gestor de transacciones configurado
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        log.info("Configurando JpaTransactionManager");

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        // Configuraciones adicionales del transaction manager
        transactionManager.setDefaultTimeout(30); // 30 segundos timeout por defecto
        transactionManager.setRollbackOnCommitFailure(true);
        transactionManager.setValidateExistingTransaction(true);

        log.debug("JpaTransactionManager configurado con timeout de 30 segundos");

        return transactionManager;
    }

    // ===================================================================
    // CONFIGURACIÓN DE AUDITORÍA
    // ===================================================================

    /**
     * Proveedor de auditoría para obtener el usuario actual.
     *
     * @return proveedor de auditoría
     */
    @Bean
    public AuditorAware<String> auditingAwareProvider() {
        return new SpringSecurityAuditorAware();
    }

    /**
     * Implementación de AuditorAware que obtiene el usuario desde Spring Security.
     */
    public static class SpringSecurityAuditorAware implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null ||
                    !authentication.isAuthenticated() ||
                    "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of("system");
            }

            String username = authentication.getName();
            return Optional.ofNullable(username);
        }
    }

    // ===================================================================
    // CONFIGURACIÓN DE CUSTOM REPOSITORIES
    // ===================================================================

    /**
     * Bean para configurar comportamientos personalizados de repositorios.
     * Se implementará en fases posteriores cuando tengamos las entidades.
     */
    // @Bean
    // public CustomRepositoryFactoryBean customRepositoryFactoryBean() {
    //     return new CustomRepositoryFactoryBean();
    // }

    // ===================================================================
    // BEANS DE UTILIDADES JPA
    // ===================================================================

    /**
     * Bean para monitoreo de estadísticas de Hibernate (solo en desarrollo).
     *
     * @param entityManagerFactory factory de entity manager
     * @return monitor de estadísticas
     */
    @Bean
    public HibernateStatsMonitor hibernateStatsMonitor(EntityManagerFactory entityManagerFactory) {
        return new HibernateStatsMonitor(entityManagerFactory);
    }

    /**
     * Clase para monitorear estadísticas de Hibernate.
     */
    public static class HibernateStatsMonitor {
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HibernateStatsMonitor.class);

        private final EntityManagerFactory entityManagerFactory;

        public HibernateStatsMonitor(EntityManagerFactory entityManagerFactory) {
            this.entityManagerFactory = entityManagerFactory;
        }

        /**
         * Obtener estadísticas actuales de Hibernate.
         */
        public void logCurrentStats() {
            try {
                org.hibernate.SessionFactory sessionFactory = entityManagerFactory.unwrap(org.hibernate.SessionFactory.class);
                org.hibernate.stat.Statistics stats = sessionFactory.getStatistics();

                if (stats.isStatisticsEnabled()) {
                    logger.info("📊 Hibernate Statistics:");
                    logger.info("  🔍 Queries executed: {}", stats.getQueryExecutionCount());
                    logger.info("  ⏱️  Query execution max time: {}ms", stats.getQueryExecutionMaxTime());
                    logger.info("  💾 Second level cache hit ratio: {:.2f}%",
                            stats.getSecondLevelCacheHitCount() * 100.0 /
                                    Math.max(1, stats.getSecondLevelCacheHitCount() + stats.getSecondLevelCacheMissCount()));
                    logger.info("  🏊 Connections obtained: {}", stats.getConnectCount());
                    logger.info("  📈 Entities loaded: {}", stats.getEntityLoadCount());
                    logger.info("  💾 Entities inserted: {}", stats.getEntityInsertCount());
                    logger.info("  🔄 Entities updated: {}", stats.getEntityUpdateCount());
                    logger.info("  🗑️  Entities deleted: {}", stats.getEntityDeleteCount());
                }
            } catch (Exception e) {
                logger.debug("No se pudieron obtener estadísticas de Hibernate: {}", e.getMessage());
            }
        }

        /**
         * Resetear estadísticas de Hibernate.
         */
        public void resetStats() {
            try {
                org.hibernate.SessionFactory sessionFactory = entityManagerFactory.unwrap(org.hibernate.SessionFactory.class);
                sessionFactory.getStatistics().clear();
                logger.info("🔄 Estadísticas de Hibernate reseteadas");
            } catch (Exception e) {
                logger.warn("No se pudieron resetear las estadísticas de Hibernate: {}", e.getMessage());
            }
        }
    }

    // ===================================================================
    // CONFIGURACIÓN POST-CONSTRUCT
    // ===================================================================

    /**
     * Inicialización post-construcción para logging de configuración.
     */
    @jakarta.annotation.PostConstruct
    public void logJpaConfiguration() {
        log.info("=".repeat(50));
        log.info("🗄️  JPA CONFIGURATION INITIALIZED");
        log.info("=".repeat(50));
        log.info("📦 Entity packages: com.skugenerator.model.entity");
        log.info("🏭 Repository packages: com.skugenerator.repository");
        log.info("🔄 DDL Auto: {}", ddlAuto);
        log.info("📊 Show SQL: {}", showSql);
        log.info("🏊 Batch size: {}", batchSize);
        log.info("💾 Second level cache: {}", useSecondLevelCache);
        log.info("🔍 Query cache: {}", useQueryCache);
        log.info("📈 Generate statistics: {}", generateStatistics);
        log.info("=".repeat(50));
    }
}
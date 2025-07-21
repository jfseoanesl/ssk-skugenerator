package com.skugenerator.repository;

import com.skugenerator.model.entity.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de Temporadas.
 * Proporciona métodos especializados para consultas y operaciones CRUD.
 */
@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {

    /**
     * Buscar temporada por código.
     * @param code Código de la temporada
     * @return Optional con la temporada si existe
     */
    Optional<Season> findByCode(String code);

    /**
     * Buscar temporada por código, incluyendo inactivas.
     * @param code Código de la temporada
     * @return Optional con la temporada si existe
     */
    @Query("SELECT s FROM Season s WHERE s.code = :code")
    Optional<Season> findByCodeIncludingInactive(@Param("code") String code);

    /**
     * Verificar si existe una temporada con el código dado.
     * @param code Código a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Verificar si existe una temporada con el código dado (excluyendo un ID específico).
     * @param code Código a verificar
     * @param id ID a excluir
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Obtener todas las temporadas activas.
     * @return Lista de temporadas activas
     */
    List<Season> findByActiveTrue();

    /**
     * Obtener todas las temporadas activas ordenadas por displayOrder.
     * @return Lista de temporadas activas ordenadas
     */
    List<Season> findByActiveTrueOrderByDisplayOrder();

    /**
     * Obtener todas las temporadas ordenadas por displayOrder.
     * @return Lista de todas las temporadas ordenadas
     */
    List<Season> findAllByOrderByDisplayOrder();

    /**
     * Buscar temporadas por nombre (búsqueda parcial, case-insensitive).
     * @param name Nombre a buscar
     * @return Lista de temporadas que coinciden
     */
    @Query("SELECT s FROM Season s WHERE s.active = true AND LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Season> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Obtener temporadas paginadas con filtros.
     * @param active Filtro por estado activo/inactivo (null para todos)
     * @param seasonType Filtro por tipo de temporada (null para todos)
     * @param search Término de búsqueda en nombre o código (null para todos)
     * @param pageable Información de paginación
     * @return Página de temporadas
     */
    @Query("SELECT s FROM Season s WHERE " +
            "(:active IS NULL OR s.active = :active) AND " +
            "(:seasonType IS NULL OR s.seasonType = :seasonType) AND " +
            "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Season> findWithFilters(@Param("active") Boolean active,
                                 @Param("seasonType") Season.SeasonType seasonType,
                                 @Param("search") String search,
                                 Pageable pageable);

    /**
     * Contar temporadas activas.
     * @return Cantidad de temporadas activas
     */
    long countByActiveTrue();

    /**
     * Contar temporadas inactivas.
     * @return Cantidad de temporadas inactivas
     */
    long countByActiveFalse();

    /**
     * Obtener temporadas por tipo.
     * @param seasonType Tipo de temporada
     * @return Lista de temporadas del tipo especificado
     */
    List<Season> findBySeasonTypeAndActiveTrueOrderByDisplayOrder(Season.SeasonType seasonType);

    /**
     * Obtener temporadas regulares (tipo REGULAR).
     * @return Lista de temporadas regulares
     */
    @Query("SELECT s FROM Season s WHERE s.active = true AND s.seasonType = com.skugenerator.model.entity.Season.SeasonType.REGULAR ORDER BY s.displayOrder")
    List<Season> findRegularSeasons();

    /**
     * Obtener temporadas especiales (tipos SPECIAL y EVENT).
     * @return Lista de temporadas especiales
     */
    @Query("SELECT s FROM Season s WHERE s.active = true AND s.seasonType IN (com.skugenerator.model.entity.Season.SeasonType.SPECIAL, com.skugenerator.model.entity.Season.SeasonType.EVENT) ORDER BY s.displayOrder")
    List<Season> findSpecialSeasons();

    /**
     * Obtener la temporada "Todo el año" (código "4").
     * @return Optional con la temporada todo el año si existe
     */
    @Query("SELECT s FROM Season s WHERE s.code = '4' AND s.active = true")
    Optional<Season> findAllYearSeason();

    /**
     * Obtener temporadas de tipo "Todo el año".
     * @return Lista de temporadas de todo el año
     */
    @Query("SELECT s FROM Season s WHERE s.active = true AND (s.seasonType = com.skugenerator.model.entity.Season.SeasonType.YEAR_ROUND OR s.code = '4') ORDER BY s.displayOrder")
    List<Season> findYearRoundSeasons();

    /**
     * Obtener la temporada actual (marcada como actual).
     * @return Optional con la temporada actual si existe
     */
    Optional<Season> findByIsCurrentTrueAndActiveTrue();

    /**
     * Obtener todas las temporadas marcadas como actuales.
     * @return Lista de temporadas actuales
     */
    List<Season> findByIsCurrentTrueAndActiveTrueOrderByDisplayOrder();

    /**
     * Buscar temporadas activas en un mes específico.
     * @param month Mes (1-12)
     * @return Lista de temporadas activas en el mes
     */
    @Query("SELECT s FROM Season s WHERE s.active = true AND " +
            "((s.startMonth <= s.endMonth AND :month BETWEEN s.startMonth AND s.endMonth) OR " +
            "(s.startMonth > s.endMonth AND (:month >= s.startMonth OR :month <= s.endMonth)) OR " +
            "s.seasonType = com.skugenerator.model.entity.Season.SeasonType.YEAR_ROUND)")
    List<Season> findActiveInMonth(@Param("month") Integer month);

    /**
     * Buscar temporadas activas en la fecha actual.
     * @return Lista de temporadas activas hoy
     */
    @Query("SELECT s FROM Season s WHERE s.active = true AND " +
            "((s.startMonth <= s.endMonth AND MONTH(CURRENT_DATE) BETWEEN s.startMonth AND s.endMonth) OR " +
            "(s.startMonth > s.endMonth AND (MONTH(CURRENT_DATE) >= s.startMonth OR MONTH(CURRENT_DATE) <= s.endMonth)) OR " +
            "s.seasonType = com.skugenerator.model.entity.Season.SeasonType.YEAR_ROUND)")
    List<Season> findCurrentlyActive();

    /**
     * Obtener temporadas por rango de meses.
     * @param startMonth Mes de inicio
     * @param endMonth Mes de fin
     * @return Lista de temporadas que incluyen el rango
     */
    @Query("SELECT s FROM Season s WHERE s.active = true AND " +
            "s.startMonth IS NOT NULL AND s.endMonth IS NOT NULL AND " +
            "((s.startMonth <= s.endMonth AND NOT (s.endMonth < :startMonth OR s.startMonth > :endMonth)) OR " +
            "(s.startMonth > s.endMonth AND NOT (s.endMonth < :startMonth AND s.startMonth > :endMonth)))")
    List<Season> findByMonthRange(@Param("startMonth") Integer startMonth, @Param("endMonth") Integer endMonth);

    /**
     * Obtener temporadas modificadas después de una fecha.
     * @param date Fecha de referencia
     * @return Lista de temporadas modificadas
     */
    @Query("SELECT s FROM Season s WHERE s.modifiedDate > :date OR (s.modifiedDate IS NULL AND s.createdDate > :date)")
    List<Season> findModifiedAfter(@Param("date") LocalDateTime date);

    /**
     * Buscar el siguiente orden disponible para una nueva temporada.
     * @return Próximo número de orden disponible
     */
    @Query("SELECT COALESCE(MAX(s.displayOrder), 0) + 1 FROM Season s")
    Integer findNextDisplayOrder();

    /**
     * Buscar temporadas por color asociado.
     * @param color Color hexadecimal
     * @return Lista de temporadas con el color especificado
     */
    List<Season> findByColorAndActiveTrue(String color);

    /**
     * Buscar temporadas por icono.
     * @param icon Nombre del icono
     * @return Lista de temporadas con el icono especificado
     */
    List<Season> findByIconAndActiveTrue(String icon);

    /**
     * Buscar temporadas por abreviatura.
     * @param abbreviation Abreviatura a buscar
     * @return Lista de temporadas con la abreviatura especificada
     */
    List<Season> findByAbbreviationAndActiveTrue(String abbreviation);

    /**
     * Verificar si una temporada tiene productos asociados.
     * NOTA: Esta consulta genera error de compilación porque la entidad Product
     * aún no existe. Se resolverá automáticamente cuando se implemente la
     * entidad Product en la Fase 4.
     * @param seasonId ID de la temporada
     * @return true si tiene productos, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.season.id = :seasonId")
    boolean hasAssociatedProducts(@Param("seasonId") Long seasonId);

    /**
     * Verificar si un código está disponible para uso.
     * @param code Código a verificar
     * @param excludeId ID a excluir de la verificación (null para incluir todos)
     * @return true si el código está disponible, false si ya existe
     */
    @Query("SELECT CASE WHEN COUNT(s) = 0 THEN true ELSE false END FROM Season s WHERE s.code = :code AND (:excludeId IS NULL OR s.id != :excludeId)")
    boolean isCodeAvailable(@Param("code") String code, @Param("excludeId") Long excludeId);

    /**
     * Actualizar estado actual de todas las temporadas basado en la fecha actual.
     * Se ejecuta para recalcular qué temporadas están activas.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Season s SET s.isCurrent = " +
            "CASE WHEN s.seasonType = com.skugenerator.model.entity.Season.SeasonType.YEAR_ROUND THEN true " +
            "WHEN s.startMonth IS NULL OR s.endMonth IS NULL THEN false " +
            "WHEN s.startMonth <= s.endMonth THEN (MONTH(CURRENT_DATE) BETWEEN s.startMonth AND s.endMonth) " +
            "ELSE (MONTH(CURRENT_DATE) >= s.startMonth OR MONTH(CURRENT_DATE) <= s.endMonth) END " +
            "WHERE s.active = true")
    int updateCurrentStatusForAllSeasons();

    /**
     * Obtener estadísticas de temporadas por tipo.
     * @return Lista de arrays [seasonType, count] agrupadas por tipo
     */
    @Query("SELECT s.seasonType, COUNT(s) FROM Season s WHERE s.active = true GROUP BY s.seasonType ORDER BY s.seasonType")
    List<Object[]> getStatisticsBySeasonType();

    /**
     * Obtener estadísticas detalladas de temporadas.
     * @return Array con [total, activas, inactivas, actuales, regulares, especiales]
     */
    @Query("SELECT COUNT(s), " +
            "SUM(CASE WHEN s.active = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.active = false THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.isCurrent = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.seasonType = com.skugenerator.model.entity.Season.SeasonType.REGULAR THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.seasonType IN (com.skugenerator.model.entity.Season.SeasonType.SPECIAL, com.skugenerator.model.entity.Season.SeasonType.EVENT) THEN 1 ELSE 0 END) " +
            "FROM Season s")
    Object[] getDetailedStatistics();

    /**
     * Buscar temporadas creadas por un usuario específico.
     * @param createdBy Usuario que creó las temporadas
     * @return Lista de temporadas creadas por el usuario
     */
    List<Season> findByCreatedBy(String createdBy);

    /**
     * Obtener temporadas más utilizadas.
     * NOTA: Esta consulta genera error de compilación porque la entidad Product
     * aún no existe. Se resolverá automáticamente cuando se implemente la
     * entidad Product en la Fase 4.
     * @param limit Número máximo de resultados
     * @return Lista de temporadas ordenadas por uso
     */
    @Query(value = "SELECT s.* FROM seasons s " +
            "LEFT JOIN products p ON s.id = p.season_id " +
            "WHERE s.active = true " +
            "GROUP BY s.id " +
            "ORDER BY COUNT(p.id) DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Season> findMostUsedSeasons(@Param("limit") int limit);

    /**
     * Buscar temporadas con rango de meses definido.
     * @return Lista de temporadas que tienen startMonth y endMonth
     */
    @Query("SELECT s FROM Season s WHERE s.active = true AND s.startMonth IS NOT NULL AND s.endMonth IS NOT NULL ORDER BY s.startMonth")
    List<Season> findWithMonthRange();

    /**
     * Buscar temporadas sin rango de meses definido.
     * @return Lista de temporadas sin startMonth o endMonth
     */
    @Query("SELECT s FROM Season s WHERE s.active = true AND (s.startMonth IS NULL OR s.endMonth IS NULL) ORDER BY s.displayOrder")
    List<Season> findWithoutMonthRange();

    /**
     * Obtener temporadas ordenadas por mes de inicio.
     * @return Lista de temporadas ordenadas por mes de inicio (nulls last)
     */
    @Query("SELECT s FROM Season s WHERE s.active = true ORDER BY s.startMonth ASC NULLS LAST, s.displayOrder")
    List<Season> findActiveOrderByStartMonth();

    /**
     * Buscar temporadas que se superponen con un rango de fechas.
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de temporadas que se superponen con el rango
     */
    @Query("SELECT s FROM Season s WHERE s.active = true AND " +
            "s.startMonth IS NOT NULL AND s.endMonth IS NOT NULL AND " +
            "((s.startMonth <= s.endMonth AND " +
            "  (MONTH(:startDate) <= s.endMonth AND MONTH(:endDate) >= s.startMonth)) OR " +
            "(s.startMonth > s.endMonth AND " +
            "  (MONTH(:startDate) <= s.endMonth OR MONTH(:endDate) >= s.startMonth OR " +
            "   MONTH(:startDate) >= s.startMonth OR MONTH(:endDate) <= s.endMonth)))")
    List<Season> findOverlappingWithDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Obtener el código de temporada recomendado para el mes actual.
     * @return Código de la temporada más adecuada para el mes actual
     */
    @Query(value = "SELECT s.code FROM seasons s WHERE s.active = true AND " +
            "((s.start_month <= s.end_month AND MONTH(CURRENT_DATE) BETWEEN s.start_month AND s.end_month) OR " +
            "(s.start_month > s.end_month AND (MONTH(CURRENT_DATE) >= s.start_month OR MONTH(CURRENT_DATE) <= s.end_month)) OR " +
            "s.season_type = 'YEAR_ROUND') " +
            "ORDER BY s.season_type = 'REGULAR' DESC, s.display_order " +
            "LIMIT 1", nativeQuery = true)
    Optional<String> getCurrentSeasonCode();

    /**
     * Marcar una temporada como actual y las demás como no actuales.
     * @param seasonId ID de la temporada a marcar como actual
     */
    @Modifying
    @Transactional
    @Query("UPDATE Season s SET s.isCurrent = CASE WHEN s.id = :seasonId THEN true ELSE false END WHERE s.active = true")
    int setCurrentSeason(@Param("seasonId") Long seasonId);
}
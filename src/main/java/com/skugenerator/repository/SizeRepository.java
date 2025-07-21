package com.skugenerator.repository;

import com.skugenerator.model.entity.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de Tallas.
 * Proporciona métodos especializados para consultas y operaciones CRUD.
 */
@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

    /**
     * Buscar talla por código.
     * @param code Código de la talla
     * @return Optional con la talla si existe
     */
    Optional<Size> findByCode(String code);

    /**
     * Buscar talla por código, incluyendo inactivas.
     * @param code Código de la talla
     * @return Optional con la talla si existe
     */
    @Query("SELECT s FROM Size s WHERE s.code = :code")
    Optional<Size> findByCodeIncludingInactive(@Param("code") String code);

    /**
     * Verificar si existe una talla con el código dado.
     * @param code Código a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Verificar si existe una talla con el código dado (excluyendo un ID específico).
     * @param code Código a verificar
     * @param id ID a excluir
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Obtener todas las tallas activas.
     * @return Lista de tallas activas
     */
    List<Size> findByActiveTrue();

    /**
     * Obtener todas las tallas activas ordenadas por displayOrder.
     * @return Lista de tallas activas ordenadas
     */
    List<Size> findByActiveTrueOrderByDisplayOrder();

    /**
     * Obtener todas las tallas ordenadas por displayOrder.
     * @return Lista de todas las tallas ordenadas
     */
    List<Size> findAllByOrderByDisplayOrder();

    /**
     * Obtener todas las tallas ordenadas por código (numérico).
     * @return Lista de tallas ordenadas por código
     */
    @Query("SELECT s FROM Size s WHERE s.active = true ORDER BY CAST(s.code AS INTEGER)")
    List<Size> findActiveOrderByCodeNumeric();

    /**
     * Buscar tallas por nombre (búsqueda parcial, case-insensitive).
     * @param name Nombre a buscar
     * @return Lista de tallas que coinciden
     */
    @Query("SELECT s FROM Size s WHERE s.active = true AND LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Size> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Obtener tallas paginadas con filtros.
     * @param active Filtro por estado activo/inactivo (null para todos)
     * @param ageGroup Filtro por grupo de edad (null para todos)
     * @param search Término de búsqueda en nombre o código (null para todos)
     * @param pageable Información de paginación
     * @return Página de tallas
     */
    @Query("SELECT s FROM Size s WHERE " +
            "(:active IS NULL OR s.active = :active) AND " +
            "(:ageGroup IS NULL OR LOWER(s.ageGroup) = LOWER(:ageGroup)) AND " +
            "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Size> findWithFilters(@Param("active") Boolean active,
                               @Param("ageGroup") String ageGroup,
                               @Param("search") String search,
                               Pageable pageable);

    /**
     * Contar tallas activas.
     * @return Cantidad de tallas activas
     */
    long countByActiveTrue();

    /**
     * Contar tallas inactivas.
     * @return Cantidad de tallas inactivas
     */
    long countByActiveFalse();

    /**
     * Obtener tallas por grupo de edad.
     * @param ageGroup Grupo de edad
     * @return Lista de tallas del grupo especificado
     */
    List<Size> findByAgeGroupAndActiveTrueOrderByDisplayOrder(String ageGroup);

    /**
     * Obtener todos los grupos de edad únicos.
     * @return Lista de grupos de edad
     */
    @Query("SELECT DISTINCT s.ageGroup FROM Size s WHERE s.active = true AND s.ageGroup IS NOT NULL ORDER BY s.ageGroup")
    List<String> findDistinctAgeGroups();

    /**
     * Obtener tallas especiales (marcadas como especiales).
     * @return Lista de tallas especiales
     */
    List<Size> findByIsSpecialTrueAndActiveTrueOrderByDisplayOrder();

    /**
     * Obtener tallas normales (no especiales).
     * @return Lista de tallas normales
     */
    List<Size> findByIsSpecialFalseAndActiveTrueOrderByDisplayOrder();

    /**
     * Obtener la talla "Talla única" (código "00").
     * @return Optional con la talla única si existe
     */
    @Query("SELECT s FROM Size s WHERE s.code = '00' AND s.active = true")
    Optional<Size> findOneSizeOption();

    /**
     * Buscar tallas por rango de edad en meses.
     * @param minAgeMonths Edad mínima en meses
     * @param maxAgeMonths Edad máxima en meses
     * @return Lista de tallas válidas para el rango de edad
     */
    @Query("SELECT s FROM Size s WHERE s.active = true AND " +
            "(s.minAgeMonths IS NULL OR s.minAgeMonths <= :maxAgeMonths) AND " +
            "(s.maxAgeMonths IS NULL OR s.maxAgeMonths >= :minAgeMonths) " +
            "ORDER BY s.displayOrder")
    List<Size> findByAgeRangeInMonths(@Param("minAgeMonths") Integer minAgeMonths,
                                      @Param("maxAgeMonths") Integer maxAgeMonths);

    /**
     * Buscar tallas válidas para una edad específica en meses.
     * @param ageInMonths Edad en meses
     * @return Lista de tallas válidas para la edad
     */
    @Query("SELECT s FROM Size s WHERE s.active = true AND " +
            "(s.minAgeMonths IS NULL OR s.minAgeMonths <= :ageInMonths) AND " +
            "(s.maxAgeMonths IS NULL OR s.maxAgeMonths >= :ageInMonths) " +
            "ORDER BY s.displayOrder")
    List<Size> findValidForAge(@Param("ageInMonths") Integer ageInMonths);

    /**
     * Obtener tallas modificadas después de una fecha.
     * @param date Fecha de referencia
     * @return Lista de tallas modificadas
     */
    @Query("SELECT s FROM Size s WHERE s.modifiedDate > :date OR (s.modifiedDate IS NULL AND s.createdDate > :date)")
    List<Size> findModifiedAfter(@Param("date") LocalDateTime date);

    /**
     * Buscar el siguiente orden disponible para una nueva talla.
     * @return Próximo número de orden disponible
     */
    @Query("SELECT COALESCE(MAX(s.displayOrder), 0) + 1 FROM Size s")
    Integer findNextDisplayOrder();

    /**
     * Obtener tallas por rango de código.
     * @param startCode Código inicial
     * @param endCode Código final
     * @return Lista de tallas en el rango
     */
    @Query("SELECT s FROM Size s WHERE s.active = true AND CAST(s.code AS INTEGER) BETWEEN CAST(:startCode AS INTEGER) AND CAST(:endCode AS INTEGER) ORDER BY CAST(s.code AS INTEGER)")
    List<Size> findByCodeRangeNumeric(@Param("startCode") String startCode, @Param("endCode") String endCode);

    /**
     * Buscar tallas por abreviatura.
     * @param abbreviation Abreviatura a buscar
     * @return Lista de tallas con la abreviatura especificada
     */
    List<Size> findByAbbreviationAndActiveTrue(String abbreviation);

    /**
     * Verificar si una talla tiene productos asociados.
     * NOTA: Esta consulta genera error de compilación porque la entidad Product
     * aún no existe. Se resolverá automáticamente cuando se implemente la
     * entidad Product en la Fase 4.
     * @param sizeId ID de la talla
     * @return true si tiene productos, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.size.id = :sizeId")
    boolean hasAssociatedProducts(@Param("sizeId") Long sizeId);

    /**
     * Verificar si un código está disponible para uso.
     * @param code Código a verificar
     * @param excludeId ID a excluir de la verificación (null para incluir todos)
     * @return true si el código está disponible, false si ya existe
     */
    @Query("SELECT CASE WHEN COUNT(s) = 0 THEN true ELSE false END FROM Size s WHERE s.code = :code AND (:excludeId IS NULL OR s.id != :excludeId)")
    boolean isCodeAvailable(@Param("code") String code, @Param("excludeId") Long excludeId);

    /**
     * Obtener estadísticas de tallas por grupo de edad.
     * @return Lista de arrays [ageGroup, count] agrupadas por grupo de edad
     */
    @Query("SELECT s.ageGroup, COUNT(s) FROM Size s WHERE s.active = true GROUP BY s.ageGroup ORDER BY s.ageGroup")
    List<Object[]> getStatisticsByAgeGroup();

    /**
     * Obtener estadísticas detalladas de tallas.
     * @return Array con [total, activas, inactivas, especiales, normales]
     */
    @Query("SELECT COUNT(s), " +
            "SUM(CASE WHEN s.active = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.active = false THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.isSpecial = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.isSpecial = false THEN 1 ELSE 0 END) " +
            "FROM Size s")
    Object[] getDetailedStatistics();

    /**
     * Buscar tallas creadas por un usuario específico.
     * @param createdBy Usuario que creó las tallas
     * @return Lista de tallas creadas por el usuario
     */
    List<Size> findByCreatedBy(String createdBy);

    /**
     * Obtener tallas más utilizadas.
     * NOTA: Esta consulta genera error de compilación porque la entidad Product
     * aún no existe. Se resolverá automáticamente cuando se implemente la
     * entidad Product en la Fase 4.
     * @param limit Número máximo de resultados
     * @return Lista de tallas ordenadas por uso
     */
    @Query(value = "SELECT s.* FROM sizes s " +
            "LEFT JOIN products p ON s.id = p.size_id " +
            "WHERE s.active = true " +
            "GROUP BY s.id " +
            "ORDER BY COUNT(p.id) DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Size> findMostUsedSizes(@Param("limit") int limit);

    /**
     * Buscar tallas con rango de edad específico.
     * @return Lista de tallas que tienen rango de edad definido
     */
    @Query("SELECT s FROM Size s WHERE s.active = true AND s.minAgeMonths IS NOT NULL AND s.maxAgeMonths IS NOT NULL ORDER BY s.minAgeMonths")
    List<Size> findWithAgeRange();

    /**
     * Buscar tallas sin rango de edad definido.
     * @return Lista de tallas sin rango de edad
     */
    @Query("SELECT s FROM Size s WHERE s.active = true AND (s.minAgeMonths IS NULL OR s.maxAgeMonths IS NULL) ORDER BY s.displayOrder")
    List<Size> findWithoutAgeRange();

    /**
     * Obtener tallas ordenadas por edad mínima.
     * @return Lista de tallas ordenadas por edad mínima (nulls last)
     */
    @Query("SELECT s FROM Size s WHERE s.active = true ORDER BY s.minAgeMonths ASC NULLS LAST, s.displayOrder")
    List<Size> findActiveOrderByMinAge();
}

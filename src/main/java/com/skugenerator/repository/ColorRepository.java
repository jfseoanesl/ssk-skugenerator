package com.skugenerator.repository;

import com.skugenerator.model.entity.Color;
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
 * Repositorio para la gestión de Colores.
 * Proporciona métodos especializados para consultas y operaciones CRUD.
 */
@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

    /**
     * Buscar color por código.
     * @param code Código del color
     * @return Optional con el color si existe
     */
    Optional<Color> findByCode(String code);

    /**
     * Buscar color por código, incluyendo inactivos.
     * @param code Código del color
     * @return Optional con el color si existe
     */
    @Query("SELECT c FROM Color c WHERE c.code = :code")
    Optional<Color> findByCodeIncludingInactive(@Param("code") String code);

    /**
     * Verificar si existe un color con el código dado.
     * @param code Código a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Verificar si existe un color con el código dado (excluyendo un ID específico).
     * @param code Código a verificar
     * @param id ID a excluir
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Obtener todos los colores activos.
     * @return Lista de colores activos
     */
    List<Color> findByActiveTrue();

    /**
     * Obtener todos los colores activos ordenados por displayOrder.
     * @return Lista de colores activos ordenados
     */
    List<Color> findByActiveTrueOrderByDisplayOrder();

    /**
     * Obtener todos los colores ordenados por displayOrder.
     * @return Lista de todos los colores ordenados
     */
    List<Color> findAllByOrderByDisplayOrder();

    /**
     * Obtener colores ordenados por código (numérico).
     * @return Lista de colores ordenados por código
     */
    @Query("SELECT c FROM Color c WHERE c.active = true ORDER BY CAST(c.code AS INTEGER)")
    List<Color> findActiveOrderByCodeNumeric();

    /**
     * Buscar colores por nombre (búsqueda parcial, case-insensitive).
     * @param name Nombre a buscar
     * @return Lista de colores que coinciden
     */
    @Query("SELECT c FROM Color c WHERE c.active = true AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Color> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Obtener colores paginados con filtros.
     * @param active Filtro por estado activo/inactivo (null para todos)
     * @param colorType Filtro por tipo de color (null para todos)
     * @param colorFamily Filtro por familia de color (null para todos)
     * @param search Término de búsqueda en nombre o código (null para todos)
     * @param pageable Información de paginación
     * @return Página de colores
     */
    @Query("SELECT c FROM Color c WHERE " +
            "(:active IS NULL OR c.active = :active) AND " +
            "(:colorType IS NULL OR c.colorType = :colorType) AND " +
            "(:colorFamily IS NULL OR LOWER(c.colorFamily) = LOWER(:colorFamily)) AND " +
            "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Color> findWithFilters(@Param("active") Boolean active,
                                @Param("colorType") Color.ColorType colorType,
                                @Param("colorFamily") String colorFamily,
                                @Param("search") String search,
                                Pageable pageable);

    /**
     * Contar colores activos.
     * @return Cantidad de colores activos
     */
    long countByActiveTrue();

    /**
     * Contar colores inactivos.
     * @return Cantidad de colores inactivos
     */
    long countByActiveFalse();

    /**
     * Obtener colores por tipo.
     * @param colorType Tipo de color
     * @return Lista de colores del tipo especificado
     */
    List<Color> findByColorTypeAndActiveTrueOrderByDisplayOrder(Color.ColorType colorType);

    /**
     * Obtener colores por familia de color.
     * @param colorFamily Familia de color
     * @return Lista de colores de la familia especificada
     */
    List<Color> findByColorFamilyAndActiveTrueOrderByDisplayOrder(String colorFamily);

    /**
     * Obtener todas las familias de color únicas.
     * @return Lista de familias de color
     */
    @Query("SELECT DISTINCT c.colorFamily FROM Color c WHERE c.active = true AND c.colorFamily IS NOT NULL ORDER BY c.colorFamily")
    List<String> findDistinctColorFamilies();

    /**
     * Obtener colores populares.
     * @return Lista de colores populares ordenados
     */
    List<Color> findByIsPopularTrueAndActiveTrueOrderByDisplayOrder();

    /**
     * Obtener colores sólidos.
     * @return Lista de colores sólidos
     */
    @Query("SELECT c FROM Color c WHERE c.active = true AND c.colorType = com.skugenerator.model.entity.Color.ColorType.SOLID ORDER BY c.displayOrder")
    List<Color> findSolidColors();

    /**
     * Obtener colores con patrones/estampados.
     * @return Lista de colores con patrones
     */
    @Query("SELECT c FROM Color c WHERE c.active = true AND c.colorType IN (com.skugenerator.model.entity.Color.ColorType.PATTERN, com.skugenerator.model.entity.Color.ColorType.MULTICOLOR) ORDER BY c.displayOrder")
    List<Color> findPatternColors();

    /**
     * Obtener el color multicolor (código especial "11").
     * @return Optional con el color multicolor si existe
     */
    @Query("SELECT c FROM Color c WHERE c.code = '11' AND c.active = true")
    Optional<Color> findMulticolorOption();

    /**
     * Obtener colores de patrón/estampado por rango de código.
     * @return Lista de colores de patrón (códigos 11-15)
     */
    @Query("SELECT c FROM Color c WHERE c.active = true AND CAST(c.code AS INTEGER) BETWEEN 11 AND 15 ORDER BY CAST(c.code AS INTEGER)")
    List<Color> findPatternColorsByCodeRange();

    /**
     * Buscar colores adecuados para una temporada específica.
     * @param seasonCode Código de temporada
     * @return Lista de colores adecuados para la temporada
     */
    @Query("SELECT c FROM Color c WHERE c.active = true AND " +
            "(c.suitableSeasons IS NULL OR c.suitableSeasons LIKE CONCAT('%', :seasonCode, '%')) " +
            "ORDER BY c.displayOrder")
    List<Color> findSuitableForSeason(@Param("seasonCode") String seasonCode);

    /**
     * Buscar colores por código hexadecimal.
     * @param hexCode Código hexadecimal
     * @return Lista de colores con el código hexadecimal especificado
     */
    List<Color> findByHexCodeAndActiveTrue(String hexCode);

    /**
     * Obtener colores modificados después de una fecha.
     * @param date Fecha de referencia
     * @return Lista de colores modificados
     */
    @Query("SELECT c FROM Color c WHERE c.modifiedDate > :date OR (c.modifiedDate IS NULL AND c.createdDate > :date)")
    List<Color> findModifiedAfter(@Param("date") LocalDateTime date);

    /**
     * Buscar el siguiente orden disponible para un nuevo color.
     * @return Próximo número de orden disponible
     */
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) + 1 FROM Color c")
    Integer findNextDisplayOrder();

    /**
     * Obtener colores por rango de código numérico.
     * @param startCode Código inicial
     * @param endCode Código final
     * @return Lista de colores en el rango
     */
    @Query("SELECT c FROM Color c WHERE c.active = true AND CAST(c.code AS INTEGER) BETWEEN CAST(:startCode AS INTEGER) AND CAST(:endCode AS INTEGER) ORDER BY CAST(c.code AS INTEGER)")
    List<Color> findByCodeRangeNumeric(@Param("startCode") String startCode, @Param("endCode") String endCode);

    /**
     * Buscar colores por nombre alternativo.
     * @param alternativeName Nombre alternativo a buscar
     * @return Lista de colores con el nombre alternativo
     */
    @Query("SELECT c FROM Color c WHERE c.active = true AND LOWER(c.alternativeName) LIKE LOWER(CONCAT('%', :alternativeName, '%'))")
    List<Color> findByAlternativeNameContainingIgnoreCase(@Param("alternativeName") String alternativeName);

    /**
     * Verificar si un color tiene productos asociados.
     * NOTA: Esta consulta genera error de compilación porque la entidad Product
     * aún no existe. Se resolverá automáticamente cuando se implemente la
     * entidad Product en la Fase 4.
     * @param colorId ID del color
     * @return true si tiene productos, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.color.id = :colorId")
    boolean hasAssociatedProducts(@Param("colorId") Long colorId);

    /**
     * Verificar si un código está disponible para uso.
     * @param code Código a verificar
     * @param excludeId ID a excluir de la verificación (null para incluir todos)
     * @return true si el código está disponible, false si ya existe
     */
    @Query("SELECT CASE WHEN COUNT(c) = 0 THEN true ELSE false END FROM Color c WHERE c.code = :code AND (:excludeId IS NULL OR c.id != :excludeId)")
    boolean isCodeAvailable(@Param("code") String code, @Param("excludeId") Long excludeId);

    /**
     * Obtener estadísticas de colores por tipo.
     * @return Lista de arrays [colorType, count] agrupadas por tipo
     */
    @Query("SELECT c.colorType, COUNT(c) FROM Color c WHERE c.active = true GROUP BY c.colorType ORDER BY c.colorType")
    List<Object[]> getStatisticsByColorType();

    /**
     * Obtener estadísticas de colores por familia.
     * @return Lista de arrays [colorFamily, count] agrupadas por familia
     */
    @Query("SELECT c.colorFamily, COUNT(c) FROM Color c WHERE c.active = true AND c.colorFamily IS NOT NULL GROUP BY c.colorFamily ORDER BY c.colorFamily")
    List<Object[]> getStatisticsByColorFamily();

    /**
     * Obtener estadísticas detalladas de colores.
     * @return Array con [total, activos, inactivos, populares, sólidos, patrones]
     */
    @Query("SELECT COUNT(c), " +
            "SUM(CASE WHEN c.active = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.active = false THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.isPopular = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.colorType = com.skugenerator.model.entity.Color.ColorType.SOLID THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.colorType IN (com.skugenerator.model.entity.Color.ColorType.PATTERN, com.skugenerator.model.entity.Color.ColorType.MULTICOLOR) THEN 1 ELSE 0 END) " +
            "FROM Color c")
    Object[] getDetailedStatistics();

    /**
     * Buscar colores creados por un usuario específico.
     * @param createdBy Usuario que creó los colores
     * @return Lista de colores creados por el usuario
     */
    List<Color> findByCreatedBy(String createdBy);

    /**
     * Obtener colores más utilizados.
     * NOTA: Esta consulta genera error de compilación porque la entidad Product
     * aún no existe. Se resolverá automáticamente cuando se implemente la
     * entidad Product en la Fase 4.
     * @param limit Número máximo de resultados
     * @return Lista de colores ordenados por uso
     */
    @Query(value = "SELECT c.* FROM colors c " +
            "LEFT JOIN products p ON c.id = p.color_id " +
            "WHERE c.active = true " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(p.id) DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Color> findMostUsedColors(@Param("limit") int limit);

    /**
     * Buscar colores básicos (códigos 01-10).
     * @return Lista de colores básicos ordenados por código
     */
    @Query("SELECT c FROM Color c WHERE c.active = true AND CAST(c.code AS INTEGER) BETWEEN 1 AND 10 ORDER BY CAST(c.code AS INTEGER)")
    List<Color> findBasicColors();

    /**
     * Buscar colores con código hexadecimal válido.
     * NOTA: MySQL usa REGEXP, para otros RDBMS podría requerir ajuste de sintaxis
     * @return Lista de colores con hexCode no nulo y válido
     */
    @Query("SELECT c FROM Color c WHERE c.active = true AND c.hexCode IS NOT NULL AND LENGTH(c.hexCode) = 7 AND SUBSTRING(c.hexCode, 1, 1) = '#' ORDER BY c.displayOrder")
    List<Color> findWithValidHexCode();

    /**
     * Buscar colores sin código hexadecimal.
     * @return Lista de colores sin hexCode definido
     */
    @Query("SELECT c FROM Color c WHERE c.active = true AND (c.hexCode IS NULL OR c.hexCode = '') ORDER BY c.displayOrder")
    List<Color> findWithoutHexCode();

    /**
     * Obtener colores ordenados por popularidad.
     * @return Lista de colores ordenados por popularidad (populares primero)
     */
    @Query("SELECT c FROM Color c WHERE c.active = true ORDER BY c.isPopular DESC, c.displayOrder")
    List<Color> findActiveOrderByPopularity();
}

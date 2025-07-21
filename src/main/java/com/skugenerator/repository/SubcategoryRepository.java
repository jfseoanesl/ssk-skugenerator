package com.skugenerator.repository;

import com.skugenerator.model.entity.Subcategory;
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
 * Repositorio para la gestión de Subcategorías.
 * Proporciona métodos especializados para consultas y operaciones CRUD.
 */
@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

    /**
     * Buscar subcategoría por código y categoría.
     * @param code Código de la subcategoría
     * @param categoryId ID de la categoría padre
     * @return Optional con la subcategoría si existe
     */
    @Query("SELECT s FROM Subcategory s WHERE s.code = :code AND s.category.id = :categoryId")
    Optional<Subcategory> findByCodeAndCategoryId(@Param("code") String code, @Param("categoryId") Long categoryId);

    /**
     * Buscar subcategoría por código y categoría, incluyendo inactivas.
     * @param code Código de la subcategoría
     * @param categoryId ID de la categoría padre
     * @return Optional con la subcategoría si existe
     */
    @Query("SELECT s FROM Subcategory s WHERE s.code = :code AND s.category.id = :categoryId")
    Optional<Subcategory> findByCodeAndCategoryIdIncludingInactive(@Param("code") String code, @Param("categoryId") Long categoryId);

    /**
     * Verificar si existe una subcategoría con el código dado dentro de una categoría.
     * @param code Código a verificar
     * @param categoryId ID de la categoría
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodeAndCategoryId(String code, Long categoryId);

    /**
     * Verificar si existe una subcategoría con el código dado dentro de una categoría (excluyendo un ID específico).
     * @param code Código a verificar
     * @param categoryId ID de la categoría
     * @param id ID a excluir
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodeAndCategoryIdAndIdNot(String code, Long categoryId, Long id);

    /**
     * Obtener todas las subcategorías activas de una categoría específica.
     * @param categoryId ID de la categoría
     * @return Lista de subcategorías activas
     */
    @Query("SELECT s FROM Subcategory s WHERE s.category.id = :categoryId AND s.active = true ORDER BY s.displayOrder")
    List<Subcategory> findActiveByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Obtener todas las subcategorías de una categoría específica (incluyendo inactivas).
     * @param categoryId ID de la categoría
     * @return Lista de todas las subcategorías
     */
    @Query("SELECT s FROM Subcategory s WHERE s.category.id = :categoryId ORDER BY s.displayOrder")
    List<Subcategory> findAllByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Obtener todas las subcategorías activas.
     * @return Lista de subcategorías activas
     */
    List<Subcategory> findByActiveTrue();

    /**
     * Obtener todas las subcategorías activas ordenadas por categoría y displayOrder.
     * @return Lista de subcategorías activas ordenadas
     */
    @Query("SELECT s FROM Subcategory s WHERE s.active = true ORDER BY s.category.code, s.displayOrder")
    List<Subcategory> findActiveSortedByCategoryAndOrder();

    /**
     * Buscar subcategorías por nombre (búsqueda parcial, case-insensitive).
     * @param name Nombre a buscar
     * @return Lista de subcategorías que coinciden
     */
    @Query("SELECT s FROM Subcategory s WHERE s.active = true AND LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Subcategory> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Obtener subcategorías paginadas con filtros.
     * @param categoryId ID de la categoría (null para todas)
     * @param active Filtro por estado activo/inactivo (null para todos)
     * @param search Término de búsqueda en nombre o código (null para todos)
     * @param pageable Información de paginación
     * @return Página de subcategorías
     */
    @Query("SELECT s FROM Subcategory s WHERE " +
            "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
            "(:active IS NULL OR s.active = :active) AND " +
            "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Subcategory> findWithFilters(@Param("categoryId") Long categoryId,
                                      @Param("active") Boolean active,
                                      @Param("search") String search,
                                      Pageable pageable);

    /**
     * Contar subcategorías activas por categoría.
     * @param categoryId ID de la categoría
     * @return Cantidad de subcategorías activas
     */
    long countByActiveTrueAndCategoryId(Long categoryId);

    /**
     * Contar subcategorías totales por categoría.
     * @param categoryId ID de la categoría
     * @return Cantidad total de subcategorías
     */
    long countByCategoryId(Long categoryId);

    /**
     * Obtener subcategorías disponibles para nuevos productos.
     * @return Lista de subcategorías disponibles
     */
    @Query("SELECT s FROM Subcategory s WHERE s.active = true AND s.availableForNewProducts = true ORDER BY s.category.code, s.displayOrder")
    List<Subcategory> findAvailableForNewProducts();

    /**
     * Obtener subcategorías disponibles para nuevos productos por categoría.
     * @param categoryId ID de la categoría
     * @return Lista de subcategorías disponibles
     */
    @Query("SELECT s FROM Subcategory s WHERE s.category.id = :categoryId AND s.active = true AND s.availableForNewProducts = true ORDER BY s.displayOrder")
    List<Subcategory> findAvailableForNewProductsByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Buscar subcategorías por palabras clave.
     * @param keyword Palabra clave a buscar
     * @return Lista de subcategorías que contienen la palabra clave
     */
    @Query("SELECT s FROM Subcategory s WHERE s.active = true AND LOWER(s.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Subcategory> findByKeywordsContaining(@Param("keyword") String keyword);

    /**
     * Obtener subcategorías modificadas después de una fecha.
     * @param date Fecha de referencia
     * @return Lista de subcategorías modificadas
     */
    @Query("SELECT s FROM Subcategory s WHERE s.modifiedDate > :date OR (s.modifiedDate IS NULL AND s.createdDate > :date)")
    List<Subcategory> findModifiedAfter(@Param("date") LocalDateTime date);

    /**
     * Buscar el siguiente orden disponible para una nueva subcategoría en una categoría.
     * @param categoryId ID de la categoría
     * @return Próximo número de orden disponible
     */
    @Query("SELECT COALESCE(MAX(s.displayOrder), 0) + 1 FROM Subcategory s WHERE s.category.id = :categoryId")
    Integer findNextDisplayOrderByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Obtener subcategorías por código de categoría.
     * @param categoryCode Código de la categoría
     * @return Lista de subcategorías activas
     */
    @Query("SELECT s FROM Subcategory s WHERE s.category.code = :categoryCode AND s.active = true ORDER BY s.displayOrder")
    List<Subcategory> findActiveByCategoryCode(@Param("categoryCode") String categoryCode);

    /**
     * Verificar si una subcategoría tiene productos asociados.
     * NOTA: Esta consulta genera error de compilación porque la entidad Product
     * aún no existe. Se resolverá automáticamente cuando se implemente la
     * entidad Product en la Fase 4.
     * @param subcategoryId ID de la subcategoría
     * @return true si tiene productos, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.subcategory.id = :subcategoryId")
    boolean hasAssociatedProducts(@Param("subcategoryId") Long subcategoryId);

    /**
     * Verificar si un código está disponible para uso dentro de una categoría.
     * @param code Código a verificar
     * @param categoryId ID de la categoría
     * @param excludeId ID a excluir de la verificación (null para incluir todos)
     * @return true si el código está disponible, false si ya existe
     */
    @Query("SELECT CASE WHEN COUNT(s) = 0 THEN true ELSE false END FROM Subcategory s WHERE s.code = :code AND s.category.id = :categoryId AND (:excludeId IS NULL OR s.id != :excludeId)")
    boolean isCodeAvailableInCategory(@Param("code") String code, @Param("categoryId") Long categoryId, @Param("excludeId") Long excludeId);

    /**
     * Obtener subcategorías más utilizadas por categoría.
     * NOTA: Esta consulta genera error de compilación porque la entidad Product
     * aún no existe. Se resolverá automáticamente cuando se implemente la
     * entidad Product en la Fase 4.
     * @param categoryId ID de la categoría
     * @param limit Número máximo de resultados
     * @return Lista de subcategorías ordenadas por uso
     */
    @Query(value = "SELECT s.* FROM subcategories s " +
            "LEFT JOIN products p ON s.id = p.subcategory_id " +
            "WHERE s.category_id = :categoryId AND s.active = true " +
            "GROUP BY s.id " +
            "ORDER BY COUNT(p.id) DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Subcategory> findMostUsedByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit);

    /**
     * Obtener estadísticas de subcategorías por categoría.
     * @param categoryId ID de la categoría
     * @return Array con [total, activas, inactivas, disponibles para nuevos productos]
     */
    @Query("SELECT COUNT(s), " +
            "SUM(CASE WHEN s.active = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.active = false THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.availableForNewProducts = true THEN 1 ELSE 0 END) " +
            "FROM Subcategory s WHERE s.category.id = :categoryId")
    Object[] getStatisticsByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Buscar subcategorías creadas por un usuario específico.
     * @param createdBy Usuario que creó las subcategorías
     * @return Lista de subcategorías creadas por el usuario
     */
    List<Subcategory> findByCreatedBy(String createdBy);

    /**
     * Obtener subcategorías con el código completo (categoría + subcategoría).
     * @return Lista de arrays [Subcategory, fullCode] donde fullCode es la concatenación
     */
    @Query("SELECT s, CONCAT(s.category.code, s.code) as fullCode FROM Subcategory s WHERE s.active = true ORDER BY fullCode")
    List<Object[]> findActiveWithFullCode();

    /**
     * Buscar subcategorías por código completo.
     * @param fullCode Código completo (categoría + subcategoría)
     * @return Optional con la subcategoría si existe
     */
    @Query("SELECT s FROM Subcategory s WHERE CONCAT(s.category.code, s.code) = :fullCode AND s.active = true")
    Optional<Subcategory> findByFullCode(@Param("fullCode") String fullCode);

    /**
     * Obtener subcategorías ordenadas por nombre de categoría.
     * @return Lista de subcategorías ordenadas por categoría
     */
    @Query("SELECT s FROM Subcategory s WHERE s.active = true ORDER BY s.category.name, s.displayOrder")
    List<Subcategory> findAllActiveSortedByCategoryName();
}

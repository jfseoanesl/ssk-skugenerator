package com.skugenerator.repository;

import com.skugenerator.model.entity.Category;
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
 * Repositorio para la gestión de Categorías.
 * Proporciona métodos especializados para consultas y operaciones CRUD.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Buscar categoría por código.
     * @param code Código de la categoría
     * @return Optional con la categoría si existe
     */
    Optional<Category> findByCode(String code);

    /**
     * Buscar categoría por código, incluyendo inactivas.
     * @param code Código de la categoría
     * @return Optional con la categoría si existe
     */
    @Query("SELECT c FROM Category c WHERE c.code = :code")
    Optional<Category> findByCodeIncludingInactive(@Param("code") String code);

    /**
     * Verificar si existe una categoría con el código dado.
     * @param code Código a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Verificar si existe una categoría con el código dado (excluyendo un ID específico).
     * @param code Código a verificar
     * @param id ID a excluir
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Obtener todas las categorías activas.
     * @return Lista de categorías activas
     */
    List<Category> findByActiveTrue();

    /**
     * Obtener todas las categorías activas ordenadas por displayOrder.
     * @return Lista de categorías activas ordenadas
     */
    List<Category> findByActiveTrueOrderByDisplayOrder();

    /**
     * Obtener todas las categorías ordenadas por displayOrder.
     * @return Lista de todas las categorías ordenadas
     */
    List<Category> findAllByOrderByDisplayOrder();

    /**
     * Buscar categorías por nombre (búsqueda parcial, case-insensitive).
     * @param name Nombre a buscar
     * @return Lista de categorías que coinciden
     */
    @Query("SELECT c FROM Category c WHERE c.active = true AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Category> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Obtener categorías paginadas con filtros.
     * @param active Filtro por estado activo/inactivo (null para todos)
     * @param search Término de búsqueda en nombre o código (null para todos)
     * @param pageable Información de paginación
     * @return Página de categorías
     */
    @Query("SELECT c FROM Category c WHERE " +
            "(:active IS NULL OR c.active = :active) AND " +
            "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Category> findWithFilters(@Param("active") Boolean active,
                                   @Param("search") String search,
                                   Pageable pageable);

    /**
     * Contar categorías activas.
     * @return Cantidad de categorías activas
     */
    long countByActiveTrue();

    /**
     * Contar categorías inactivas.
     * @return Cantidad de categorías inactivas
     */
    long countByActiveFalse();

    /**
     * Obtener categorías que permiten subcategorías.
     * @return Lista de categorías que permiten subcategorías
     */
    @Query("SELECT c FROM Category c WHERE c.active = true AND c.allowsSubcategories = true ORDER BY c.displayOrder")
    List<Category> findActiveWithSubcategoriesAllowed();

    /**
     * Obtener categorías con subcategorías asociadas.
     * @return Lista de categorías con subcategorías
     */
    @Query("SELECT DISTINCT c FROM Category c JOIN c.subcategories s WHERE c.active = true AND s.active = true ORDER BY c.displayOrder")
    List<Category> findCategoriesWithActiveSubcategories();

    /**
     * Contar subcategorías activas por categoría.
     * @param categoryId ID de la categoría
     * @return Cantidad de subcategorías activas
     */
    @Query("SELECT COUNT(s) FROM Subcategory s WHERE s.category.id = :categoryId AND s.active = true")
    long countActiveSubcategoriesByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Obtener categorías especiales (códigos predefinidos).
     * @return Lista de categorías especiales
     */
    @Query("SELECT c FROM Category c WHERE c.active = true AND c.code IN ('10', '20', '30', '40', '50', '60', '70', '80', '90') ORDER BY c.displayOrder")
    List<Category> findSpecialCategories();

    /**
     * Obtener categorías modificadas después de una fecha.
     * @param date Fecha de referencia
     * @return Lista de categorías modificadas
     */
    @Query("SELECT c FROM Category c WHERE c.modifiedDate > :date OR (c.modifiedDate IS NULL AND c.createdDate > :date)")
    List<Category> findModifiedAfter(@Param("date") LocalDateTime date);

    /**
     * Buscar el siguiente orden disponible para una nueva categoría.
     * @return Próximo número de orden disponible
     */
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) + 1 FROM Category c")
    Integer findNextDisplayOrder();

    /**
     * Obtener categorías por rango de código.
     * @param startCode Código inicial (inclusive)
     * @param endCode Código final (inclusive)
     * @return Lista de categorías en el rango
     */
    @Query("SELECT c FROM Category c WHERE c.active = true AND c.code BETWEEN :startCode AND :endCode ORDER BY c.code")
    List<Category> findByCodeRange(@Param("startCode") String startCode, @Param("endCode") String endCode);

    /**
     * Buscar categorías por color asociado.
     * @param color Color hexadecimal
     * @return Lista de categorías con el color especificado
     */
    List<Category> findByColorAndActiveTrue(String color);

    /**
     * Buscar categorías por icono.
     * @param icon Nombre del icono
     * @return Lista de categorías con el icono especificado
     */
    List<Category> findByIconAndActiveTrue(String icon);

    /**
     * Verificar si una categoría tiene productos asociados.
     * NOTA: Esta consulta genera error de compilación porque la entidad Product
     * aún no existe. Se resolverá automáticamente cuando se implemente la
     * entidad Product en la Fase 4.
     * @param categoryId ID de la categoría
     * @return true si tiene productos, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.category.id = :categoryId")
    boolean hasAssociatedProducts(@Param("categoryId") Long categoryId);

    /**
     * Verificar si un código está disponible para uso.
     * @param code Código a verificar
     * @param excludeId ID a excluir de la verificación (null para incluir todos)
     * @return true si el código está disponible, false si ya existe
     */
    @Query("SELECT CASE WHEN COUNT(c) = 0 THEN true ELSE false END FROM Category c WHERE c.code = :code AND (:excludeId IS NULL OR c.id != :excludeId)")
    boolean isCodeAvailable(@Param("code") String code, @Param("excludeId") Long excludeId);

    /**
     * Obtener estadísticas detalladas de categorías.
     * @return Array con estadísticas [total, activas, inactivas, con subcategorías, sin subcategorías]
     */
    @Query("SELECT COUNT(c), " +
            "SUM(CASE WHEN c.active = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.active = false THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.allowsSubcategories = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.allowsSubcategories = false THEN 1 ELSE 0 END) " +
            "FROM Category c")
    Object[] getDetailedStatistics();

    /**
     * Buscar categorías con subcategorías activas y contar.
     * @return Lista de arrays [Category, count] donde count es el número de subcategorías activas
     */
    @Query("SELECT c, COUNT(s) FROM Category c LEFT JOIN c.subcategories s " +
            "WHERE c.active = true AND (s.active = true OR s.active IS NULL) " +
            "GROUP BY c ORDER BY c.displayOrder")
    List<Object[]> findCategoriesWithSubcategoryCount();

    /**
     * Buscar categorías creadas por un usuario específico.
     * @param createdBy Usuario que creó las categorías
     * @return Lista de categorías creadas por el usuario
     */
    List<Category> findByCreatedBy(String createdBy);

    /**
     * Obtener categorías más utilizadas (con más productos).
     * NOTA: Esta consulta genera error de compilación porque la entidad Product
     * aún no existe. Se resolverá automáticamente cuando se implemente la
     * entidad Product en la Fase 4.
     * @param limit Número máximo de resultados
     * @return Lista de categorías ordenadas por uso
     */
    @Query(value = "SELECT c.* FROM categories c " +
            "LEFT JOIN products p ON c.id = p.category_id " +
            "WHERE c.active = true " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(p.id) DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Category> findMostUsedCategories(@Param("limit") int limit);
}
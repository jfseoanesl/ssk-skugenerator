package com.skugenerator.repository;

import com.skugenerator.model.entity.ProductType;
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
 * Repositorio para la gestión de Tipos de Producto.
 * Proporciona métodos especializados para consultas y operaciones CRUD.
 */
@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {

    /**
     * Buscar tipo de producto por código.
     * @param code Código del tipo de producto
     * @return Optional con el tipo de producto si existe
     */
    Optional<ProductType> findByCode(String code);

    /**
     * Buscar tipo de producto por código, incluyendo inactivos.
     * @param code Código del tipo de producto
     * @return Optional con el tipo de producto si existe
     */
    @Query("SELECT pt FROM ProductType pt WHERE pt.code = :code")
    Optional<ProductType> findByCodeIncludingInactive(@Param("code") String code);

    /**
     * Verificar si existe un tipo de producto con el código dado.
     * @param code Código a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Verificar si existe un tipo de producto con el código dado (excluyendo un ID específico).
     * @param code Código a verificar
     * @param id ID a excluir
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Obtener todos los tipos de producto activos.
     * @return Lista de tipos de producto activos
     */
    List<ProductType> findByActiveTrue();

    /**
     * Obtener todos los tipos de producto activos ordenados por displayOrder.
     * @return Lista de tipos de producto activos ordenados
     */
    List<ProductType> findByActiveTrueOrderByDisplayOrder();

    /**
     * Obtener todos los tipos de producto ordenados por displayOrder.
     * @return Lista de todos los tipos de producto ordenados
     */
    List<ProductType> findAllByOrderByDisplayOrder();

    /**
     * Buscar tipos de producto por nombre (búsqueda parcial, case-insensitive).
     * @param name Nombre a buscar
     * @return Lista de tipos de producto que coinciden
     */
    @Query("SELECT pt FROM ProductType pt WHERE pt.active = true AND LOWER(pt.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ProductType> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Obtener tipos de producto paginados con filtros.
     * @param active Filtro por estado activo/inactivo (null para todos)
     * @param search Término de búsqueda en nombre o código (null para todos)
     * @param pageable Información de paginación
     * @return Página de tipos de producto
     */
    @Query("SELECT pt FROM ProductType pt WHERE " +
            "(:active IS NULL OR pt.active = :active) AND " +
            "(:search IS NULL OR LOWER(pt.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(pt.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ProductType> findWithFilters(@Param("active") Boolean active,
                                      @Param("search") String search,
                                      Pageable pageable);

    /**
     * Contar tipos de producto activos.
     * @return Cantidad de tipos de producto activos
     */
    long countByActiveTrue();

    /**
     * Contar tipos de producto inactivos.
     * @return Cantidad de tipos de producto inactivos
     */
    long countByActiveFalse();

    /**
     * Obtener tipos de producto que permiten composición.
     * @return Lista de tipos que permiten composición
     */
    @Query("SELECT pt FROM ProductType pt WHERE pt.active = true AND pt.allowsComposition = true ORDER BY pt.displayOrder")
    List<ProductType> findActiveComposableTypes();

    /**
     * Obtener tipos de producto especiales (códigos predefinidos).
     * @return Lista de tipos especiales
     */
    @Query("SELECT pt FROM ProductType pt WHERE pt.active = true AND pt.code IN ('1', '2', '3', '4', '5') ORDER BY pt.displayOrder")
    List<ProductType> findSpecialTypes();

    /**
     * Obtener tipos de producto modificados después de una fecha.
     * @param date Fecha de referencia
     * @return Lista de tipos modificados
     */
    @Query("SELECT pt FROM ProductType pt WHERE pt.modifiedDate > :date OR (pt.modifiedDate IS NULL AND pt.createdDate > :date)")
    List<ProductType> findModifiedAfter(@Param("date") LocalDateTime date);

    /**
     * Buscar el siguiente orden disponible para un nuevo tipo.
     * @return Próximo número de orden disponible
     */
    @Query("SELECT COALESCE(MAX(pt.displayOrder), 0) + 1 FROM ProductType pt")
    Integer findNextDisplayOrder();

    /**
     * Obtener tipos de producto por rango de orden.
     * @param minOrder Orden mínimo
     * @param maxOrder Orden máximo
     * @return Lista de tipos en el rango especificado
     */
    @Query("SELECT pt FROM ProductType pt WHERE pt.active = true AND pt.displayOrder BETWEEN :minOrder AND :maxOrder ORDER BY pt.displayOrder")
    List<ProductType> findByDisplayOrderBetween(@Param("minOrder") Integer minOrder, @Param("maxOrder") Integer maxOrder);

    /**
     * Buscar tipos de producto creados por un usuario específico.
     * @param createdBy Usuario que creó los tipos
     * @return Lista de tipos creados por el usuario
     */
    List<ProductType> findByCreatedBy(String createdBy);

    /**
     * Verificar si un código está disponible para uso.
     * @param code Código a verificar
     * @param excludeId ID a excluir de la verificación (null para incluir todos)
     * @return true si el código está disponible, false si ya existe
     */
    @Query("SELECT CASE WHEN COUNT(pt) = 0 THEN true ELSE false END FROM ProductType pt WHERE pt.code = :code AND (:excludeId IS NULL OR pt.id != :excludeId)")
    boolean isCodeAvailable(@Param("code") String code, @Param("excludeId") Long excludeId);

    /**
     * Obtener estadísticas básicas de tipos de producto.
     * @return Array con [total, activos, inactivos]
     */
    @Query("SELECT COUNT(pt), " +
            "SUM(CASE WHEN pt.active = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pt.active = false THEN 1 ELSE 0 END) " +
            "FROM ProductType pt")
    Object[] getBasicStatistics();
}

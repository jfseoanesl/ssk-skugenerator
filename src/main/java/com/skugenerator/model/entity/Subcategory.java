package com.skugenerator.model.entity;

import com.skugenerator.util.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa las subcategorías de producto en el sistema SKU Generator.
 *
 * Las subcategorías definen la tercera clasificación de un producto
 * y ocupan el dígito 4 del código SKU (S).
 * Cada subcategoría pertenece a una categoría específica.
 *
 * Ejemplos:
 * - 1: Camisetas básicas (dentro de Ropa Superior)
 * - 2: Blusas elegantes (dentro de Ropa Superior)
 * - 3: Suéteres/Chompas (dentro de Ropa Superior)
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Entity
@Table(name = "subcategories",
        indexes = {
                @Index(name = "idx_subcategory_code", columnList = "code"),
                @Index(name = "idx_subcategory_category", columnList = "category_id"),
                @Index(name = "idx_subcategory_active", columnList = "active"),
                @Index(name = "idx_subcategory_category_code", columnList = "category_id, code")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_subcategory_category_code",
                        columnNames = {"category_id", "code"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Subcategory extends BaseEntity {

    /**
     * Código único de la subcategoría dentro de su categoría.
     * Debe ser un solo dígito numérico (0-9).
     * Este código forma parte del SKU en la posición 4.
     */
    @NotBlank(message = "El código de la subcategoría es obligatorio")
    @Pattern(regexp = Constants.Validation.SUBCATEGORY_CODE_PATTERN,
            message = "El código debe ser un solo dígito numérico (0-9)")
    @Column(name = "code", length = 1, nullable = false)
    private String code;

    /**
     * Nombre descriptivo de la subcategoría.
     * Debe ser descriptivo para facilitar la identificación.
     */
    @NotBlank(message = "El nombre de la subcategoría es obligatorio")
    @Size(min = Constants.Validation.CONFIG_NAME_MIN_LENGTH,
            max = Constants.Validation.CONFIG_NAME_MAX_LENGTH,
            message = "El nombre debe tener entre {min} y {max} caracteres")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * Descripción detallada de la subcategoría.
     * Campo opcional para proporcionar más información sobre la subcategoría.
     */
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Orden de visualización dentro de la categoría.
     * Permite controlar el orden en que aparecen las subcategorías en selects y listas.
     */
    @Min(value = 1, message = "El orden debe ser mayor a 0")
    @Max(value = 999, message = "El orden no puede ser mayor a 999")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    /**
     * Indica si esta subcategoría está disponible para nuevos productos.
     */
    @Column(name = "available_for_new_products", nullable = false)
    private Boolean availableForNewProducts = true;

    /**
     * Categoría a la que pertenece esta subcategoría.
     * Relación Many-to-One obligatoria.
     */
    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_subcategory_category"))
    private Category category;

    /**
     * Palabras clave para búsqueda.
     * Separadas por comas para facilitar búsquedas.
     */
    @Size(max = 255, message = "Las palabras clave no pueden exceder los 255 caracteres")
    @Column(name = "keywords", length = 255)
    private String keywords;

    // ===================================================================
    // CONSTRUCTORES DE CONVENIENCIA
    // ===================================================================

    /**
     * Constructor con campos obligatorios.
     *
     * @param code código único de la subcategoría (1 dígito)
     * @param name nombre descriptivo de la subcategoría
     * @param category categoría a la que pertenece
     */
    public Subcategory(String code, String name, Category category) {
        this.code = code;
        this.name = name;
        this.category = category;
    }

    /**
     * Constructor completo.
     *
     * @param code código único de la subcategoría
     * @param name nombre descriptivo de la subcategoría
     * @param description descripción detallada
     * @param category categoría a la que pertenece
     * @param displayOrder orden de visualización
     */
    public Subcategory(String code, String name, String description,
                       Category category, Integer displayOrder) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.displayOrder = displayOrder;
    }

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Obtiene el código completo (categoría + subcategoría).
     *
     * @return string en formato "CC-S" (ej: "10-1")
     */
    public String getFullCode() {
        return category != null ? category.getCode() + code : code;
    }

    /**
     * Obtiene el nombre completo con código para visualización.
     *
     * @return string en formato "código - nombre (categoría)"
     */
    public String getDisplayName() {
        String categoryName = category != null ? category.getName() : "Sin categoría";
        return String.format("%s - %s (%s)", code, name, categoryName);
    }

    /**
     * Obtiene el nombre completo con categoría.
     *
     * @return string en formato "Categoría: Subcategoría"
     */
    public String getFullName() {
        String categoryName = category != null ? category.getName() : "Sin categoría";
        return String.format("%s: %s", categoryName, name);
    }

    /**
     * Verifica si está disponible para nuevos productos.
     *
     * @return true si está disponible para nuevos productos
     */
    public boolean isAvailableForNewProducts() {
        return availableForNewProducts != null && availableForNewProducts;
    }

    /**
     * Obtiene la descripción o una por defecto si no existe.
     *
     * @return descripción de la subcategoría o mensaje por defecto
     */
    public String getDescriptionOrDefault() {
        return description != null && !description.trim().isEmpty()
                ? description
                : "Subcategoría de " + (category != null ? category.getName() : "producto");
    }

    /**
     * Obtiene el nombre de la categoría padre.
     *
     * @return nombre de la categoría o "Sin categoría"
     */
    public String getCategoryName() {
        return category != null ? category.getName() : "Sin categoría";
    }

    /**
     * Obtiene el código de la categoría padre.
     *
     * @return código de la categoría o null
     */
    public String getCategoryCode() {
        return category != null ? category.getCode() : null;
    }

    /**
     * Verifica si las palabras clave contienen un término específico.
     *
     * @param term término a buscar
     * @return true si las palabras clave contienen el término
     */
    public boolean hasKeyword(String term) {
        return keywords != null &&
                keywords.toLowerCase().contains(term.toLowerCase());
    }

    /**
     * Agrega una palabra clave.
     *
     * @param keyword palabra clave a agregar
     */
    public void addKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        if (keywords == null || keywords.trim().isEmpty()) {
            keywords = keyword.trim();
        } else if (!hasKeyword(keyword)) {
            keywords += ", " + keyword.trim();
        }
    }

    // ===================================================================
    // MÉTODOS DE VALIDACIÓN
    // ===================================================================

    /**
     * Valida que el código sea válido para una subcategoría.
     *
     * @return true si el código es válido
     */
    public boolean isValidCode() {
        return code != null &&
                code.matches(Constants.Validation.SUBCATEGORY_CODE_PATTERN) &&
                !code.trim().isEmpty();
    }

    /**
     * Valida que la subcategoría tenga una categoría asignada.
     *
     * @return true si tiene categoría asignada
     */
    public boolean hasValidCategory() {
        return category != null && category.getId() != null;
    }

    /**
     * Normaliza los datos antes de persistir.
     * Limpia espacios y establece valores por defecto.
     */
    private void normalizeSubcategory() {
        if (code != null) {
            code = code.trim();
        }
        if (name != null) {
            name = name.trim();
        }
        if (description != null) {
            description = description.trim();
            if (description.isEmpty()) {
                description = null;
            }
        }
        if (displayOrder == null) {
            displayOrder = 1;
        }
        if (availableForNewProducts == null) {
            availableForNewProducts = true;
        }
        if (keywords != null) {
            keywords = keywords.trim();
            if (keywords.isEmpty()) {
                keywords = null;
            }
        }
    }

    /**
     * Valida la consistencia antes de persistir.
     * Verifica que la subcategoría tenga una categoría válida.
     */
    private void validateConsistency() {
        if (category == null) {
            throw new IllegalStateException("La subcategoría debe tener una categoría asignada");
        }
    }

    // ===================================================================
    // MÉTODOS EQUALS, HASHCODE Y TOSTRING ESPECÍFICOS
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subcategory)) return false;

        Subcategory that = (Subcategory) o;

        // Si ambos tienen ID, comparar por ID
        if (getId() != null && that.getId() != null) {
            return getId().equals(that.getId());
        }

        // Si no tienen ID, comparar por código y categoría (business key)
        return code != null && code.equals(that.code) &&
                category != null && category.equals(that.category);
    }

    @Override
    public int hashCode() {
        // Usar código y categoría como business key para hashCode
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String categoryInfo = category != null ?
                String.format("category='%s'", category.getCode()) : "category=null";
        return String.format("Subcategory{id=%d, code='%s', name='%s', %s, active=%s}",
                getId(), code, name, categoryInfo, getActive());
    }

    // ===================================================================
    // MÉTODOS ESTÁTICOS DE FACTORY
    // ===================================================================

    /**
     * Crea una subcategoría simple con valores por defecto.
     *
     * @param code código de la subcategoría
     * @param name nombre de la subcategoría
     * @param category categoría padre
     * @return nueva instancia de Subcategory
     */
    public static Subcategory createSimple(String code, String name, Category category) {
        Subcategory subcategory = new Subcategory();
        subcategory.setCode(code);
        subcategory.setName(name);
        subcategory.setCategory(category);
        subcategory.setDisplayOrder(1);
        subcategory.setAvailableForNewProducts(true);
        return subcategory;
    }

    /**
     * Crea una subcategoría con descripción.
     *
     * @param code código de la subcategoría
     * @param name nombre de la subcategoría
     * @param description descripción de la subcategoría
     * @param category categoría padre
     * @return nueva instancia de Subcategory
     */
    public static Subcategory createWithDescription(String code, String name,
                                                    String description, Category category) {
        Subcategory subcategory = createSimple(code, name, category);
        subcategory.setDescription(description);
        return subcategory;
    }

    /**
     * Crea subcategorías por defecto para una categoría específica.
     *
     * @param category categoría para la cual crear subcategorías
     * @return array con las subcategorías por defecto según la categoría
     */
    public static Subcategory[] createDefaultSubcategoriesForCategory(Category category) {
        if (category == null || category.getCode() == null) {
            return new Subcategory[0];
        }

        return switch (category.getCode()) {
            case "10" -> new Subcategory[]{
                    createWithDescription("1", "Camisetas básicas", "Camisetas simples y básicas", category),
                    createWithDescription("2", "Blusas elegantes", "Blusas con detalles especiales", category),
                    createWithDescription("3", "Suéteres/Chompas", "Prendas de abrigo ligero", category),
                    createWithDescription("4", "Chaquetas ligeras", "Chaquetas para clima templado", category),
                    createWithDescription("5", "Abrigos", "Abrigos para clima frío", category),
                    createWithDescription("6", "Tops deportivos", "Prendas para actividad física", category)
            };
            case "20" -> new Subcategory[]{
                    createWithDescription("1", "Pantalones", "Pantalones largos", category),
                    createWithDescription("2", "Jeans", "Pantalones de mezclilla", category),
                    createWithDescription("3", "Shorts", "Pantalones cortos", category),
                    createWithDescription("4", "Faldas", "Faldas de diferentes estilos", category),
                    createWithDescription("5", "Leggins", "Leggins y mallas", category)
            };
            case "30" -> new Subcategory[]{
                    createWithDescription("1", "Vestidos casuales", "Vestidos para uso diario", category),
                    createWithDescription("2", "Vestidos elegantes", "Vestidos para ocasiones especiales", category),
                    createWithDescription("3", "Enterizos", "Enterizos y mamelucos", category),
                    createWithDescription("4", "Jumpers", "Jumpers y overoles", category)
            };
            default -> new Subcategory[]{
                    createWithDescription("1", "General", "Subcategoría general", category)
            };
        };
    }
}
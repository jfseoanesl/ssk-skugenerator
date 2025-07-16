package com.skugenerator.model.entity;

import com.skugenerator.util.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa las categorías de producto en el sistema SKU Generator.
 *
 * Las categorías definen la segunda clasificación de un producto
 * y ocupan los dígitos 2-3 del código SKU (CC).
 *
 * Ejemplos:
 * - 10: Ropa Superior
 * - 20: Ropa Inferior
 * - 30: Vestidos y Enterizos
 * - 40: Ropa Interior y Pijamas
 * - 50: Calzado
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Entity
@Table(name = "categories",
        indexes = {
                @Index(name = "idx_category_code", columnList = "code"),
                @Index(name = "idx_category_active", columnList = "active"),
                @Index(name = "idx_category_name", columnList = "name")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_code", columnNames = "code")
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Category extends BaseEntity {

    /**
     * Código único de la categoría.
     * Debe ser de dos dígitos numéricos (10-99).
     * Este código forma parte del SKU en las posiciones 2-3.
     */
    @NotBlank(message = "El código de la categoría es obligatorio")
    @Pattern(regexp = Constants.Validation.CATEGORY_CODE_PATTERN,
            message = "El código debe ser de dos dígitos numéricos (10-99)")
    @Column(name = "code", length = 2, nullable = false, unique = true)
    private String code;

    /**
     * Nombre descriptivo de la categoría.
     * Debe ser único y descriptivo para facilitar la identificación.
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = Constants.Validation.CONFIG_NAME_MIN_LENGTH,
            max = Constants.Validation.CONFIG_NAME_MAX_LENGTH,
            message = "El nombre debe tener entre {min} y {max} caracteres")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * Descripción detallada de la categoría.
     * Campo opcional para proporcionar más información sobre la categoría.
     */
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Orden de visualización en las interfaces.
     * Permite controlar el orden en que aparecen las categorías en selects y listas.
     */
    @Min(value = 1, message = "El orden debe ser mayor a 0")
    @Max(value = 999, message = "El orden no puede ser mayor a 999")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    /**
     * Indica si esta categoría permite múltiples subcategorías.
     */
    @Column(name = "allows_subcategories", nullable = false)
    private Boolean allowsSubcategories = true;

    /**
     * Color hexadecimal para representación visual.
     * Útil para interfaces gráficas y reportes.
     */
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$",
            message = "El color debe ser un código hexadecimal válido (#RRGGBB)")
    @Column(name = "color", length = 7)
    private String color;

    /**
     * Icono CSS para representación visual.
     * Clase CSS para mostrar iconos en la interfaz.
     */
    @Size(max = 50, message = "El icono no puede exceder los 50 caracteres")
    @Column(name = "icon", length = 50)
    private String icon;

    /**
     * Relación One-to-Many con Subcategorías.
     * Una categoría puede tener múltiples subcategorías.
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subcategory> subcategories = new ArrayList<>();

    // ===================================================================
    // CONSTRUCTORES DE CONVENIENCIA
    // ===================================================================

    /**
     * Constructor con campos obligatorios.
     *
     * @param code código único de la categoría (2 dígitos)
     * @param name nombre descriptivo de la categoría
     */
    public Category(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Constructor completo.
     *
     * @param code código único de la categoría
     * @param name nombre descriptivo de la categoría
     * @param description descripción detallada
     * @param displayOrder orden de visualización
     */
    public Category(String code, String name, String description, Integer displayOrder) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Verifica si esta categoría es una categoría especial predefinida.
     *
     * @return true si es una categoría especial del sistema
     */
    public boolean isSpecialCategory() {
        return "10".equals(code) || "20".equals(code) || "30".equals(code) ||
                "40".equals(code) || "50".equals(code) || "60".equals(code) ||
                "70".equals(code) || "80".equals(code) || "90".equals(code);
    }

    /**
     * Obtiene el nombre completo con código para visualización.
     *
     * @return string en formato "código - nombre"
     */
    public String getDisplayName() {
        return String.format("%s - %s", code, name);
    }

    /**
     * Verifica si la categoría permite subcategorías.
     *
     * @return true si permite subcategorías
     */
    public boolean canHaveSubcategories() {
        return allowsSubcategories != null && allowsSubcategories;
    }

    /**
     * Obtiene la descripción o una por defecto si no existe.
     *
     * @return descripción de la categoría o mensaje por defecto
     */
    public String getDescriptionOrDefault() {
        return description != null && !description.trim().isEmpty()
                ? description
                : "Categoría de producto: " + name;
    }

    /**
     * Agrega una subcategoría a esta categoría.
     *
     * @param subcategory subcategoría a agregar
     */
    public void addSubcategory(Subcategory subcategory) {
        subcategories.add(subcategory);
        subcategory.setCategory(this);
    }

    /**
     * Remueve una subcategoría de esta categoría.
     *
     * @param subcategory subcategoría a remover
     */
    public void removeSubcategory(Subcategory subcategory) {
        subcategories.remove(subcategory);
        subcategory.setCategory(null);
    }

    /**
     * Obtiene el número de subcategorías activas.
     *
     * @return cantidad de subcategorías activas
     */
    public long getActiveSubcategoriesCount() {
        return subcategories.stream()
                .filter(sub -> sub.getActive() != null && sub.getActive())
                .count();
    }

    // ===================================================================
    // MÉTODOS DE VALIDACIÓN
    // ===================================================================

    /**
     * Valida que el código sea válido para una categoría.
     *
     * @return true si el código es válido
     */
    public boolean isValidCode() {
        return code != null &&
                code.matches(Constants.Validation.CATEGORY_CODE_PATTERN) &&
                !code.trim().isEmpty();
    }

    /**
     * Normaliza los datos antes de persistir.
     * Limpia espacios y establece valores por defecto.
     */
    @PrePersist
    @PreUpdate
    private void normalize() {
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
        if (allowsSubcategories == null) {
            allowsSubcategories = true;
        }
        if (icon != null) {
            icon = icon.trim();
            if (icon.isEmpty()) {
                icon = null;
            }
        }
    }

    // ===================================================================
    // MÉTODOS EQUALS, HASHCODE Y TOSTRING ESPECÍFICOS
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category category = (Category) o;

        // Si ambos tienen ID, comparar por ID
        if (getId() != null && category.getId() != null) {
            return getId().equals(category.getId());
        }

        // Si no tienen ID, comparar por código (business key)
        return code != null && code.equals(category.code);
    }

    @Override
    public int hashCode() {
        // Usar código como business key para hashCode
        return code != null ? code.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Category{id=%d, code='%s', name='%s', active=%s, subcategories=%d}",
                getId(), code, name, getActive(), subcategories.size());
    }

    // ===================================================================
    // MÉTODOS ESTÁTICOS DE FACTORY
    // ===================================================================

    /**
     * Crea una categoría simple con valores por defecto.
     *
     * @param code código de la categoría
     * @param name nombre de la categoría
     * @return nueva instancia de Category
     */
    public static Category createSimple(String code, String name) {
        Category category = new Category();
        category.setCode(code);
        category.setName(name);
        category.setDisplayOrder(1);
        category.setAllowsSubcategories(true);
        return category;
    }

    /**
     * Crea una categoría con descripción.
     *
     * @param code código de la categoría
     * @param name nombre de la categoría
     * @param description descripción de la categoría
     * @return nueva instancia de Category
     */
    public static Category createWithDescription(String code, String name, String description) {
        Category category = createSimple(code, name);
        category.setDescription(description);
        return category;
    }

    /**
     * Crea las categorías por defecto del sistema.
     *
     * @return array con las categorías por defecto
     */
    public static Category[] createDefaultCategories() {
        return new Category[]{
                createWithDescription("10", "Ropa Superior",
                        "Prendas para la parte superior del cuerpo"),
                createWithDescription("20", "Ropa Inferior",
                        "Prendas para la parte inferior del cuerpo"),
                createWithDescription("30", "Vestidos y Enterizos",
                        "Prendas de una sola pieza"),
                createWithDescription("40", "Ropa Interior y Pijamas",
                        "Ropa íntima y para dormir"),
                createWithDescription("50", "Calzado",
                        "Zapatos, sandalias y calzado en general"),
                createWithDescription("60", "Accesorios para Cabello",
                        "Diademas, moños, clips y accesorios capilares"),
                createWithDescription("70", "Bolsos y Mochilas",
                        "Bolsos, mochilas y carteras"),
                createWithDescription("80", "Joyería Infantil",
                        "Collares, pulseras, aretes seguros para niños"),
                createWithDescription("90", "Ropa de Ocasión",
                        "Vestidos de fiesta, trajes elegantes y disfraces")
        };
    }
}

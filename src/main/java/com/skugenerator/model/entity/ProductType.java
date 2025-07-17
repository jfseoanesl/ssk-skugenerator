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
 * Entidad que representa los tipos de producto en el sistema SKU Generator.
 *
 * Los tipos de producto definen la primera clasificación de un producto
 * y ocupan el primer dígito del código SKU (T).
 *
 * Ejemplos:
 * - 1: Producto Simple
 * - 2: Producto Compuesto
 * - 3: Set/Conjunto
 * - 4: Producto Promocional
 * - 5: Producto de Temporada
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Entity
@Table(name = "product_types",
        indexes = {
                @Index(name = "idx_product_type_code", columnList = "code"),
                @Index(name = "idx_product_type_active", columnList = "active"),
                @Index(name = "idx_product_type_name", columnList = "name")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_type_code", columnNames = "code")
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ProductType extends BaseEntity {

    /**
     * Código único del tipo de producto.
     * Debe ser un solo dígito numérico (0-9).
     * Este código forma parte del SKU en la primera posición.
     */
    @NotBlank(message = "El código del tipo de producto es obligatorio")
    @Pattern(regexp = Constants.Validation.TYPE_CODE_PATTERN,
            message = "El código debe ser un solo dígito numérico (0-9)")
    @Column(name = "code", length = 1, nullable = false, unique = true)
    private String code;

    /**
     * Nombre descriptivo del tipo de producto.
     * Debe ser único y descriptivo para facilitar la identificación.
     */
    @NotBlank(message = "El nombre del tipo de producto es obligatorio")
    @Size(min = Constants.Validation.CONFIG_NAME_MIN_LENGTH,
            max = Constants.Validation.CONFIG_NAME_MAX_LENGTH,
            message = "El nombre debe tener entre {min} y {max} caracteres")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * Descripción detallada del tipo de producto.
     * Campo opcional para proporcionar más información sobre el tipo.
     */
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Orden de visualización en las interfaces.
     * Permite controlar el orden en que aparecen los tipos en selects y listas.
     */
    @Min(value = 1, message = "El orden debe ser mayor a 0")
    @Max(value = 999, message = "El orden no puede ser mayor a 999")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    /**
     * Indica si este tipo permite productos compuestos.
     * Algunos tipos pueden tener restricciones especiales.
     */
    @Column(name = "allows_composition", nullable = false)
    private Boolean allowsComposition = true;

    /**
     * Color hexadecimal para representación visual.
     * Útil para interfaces gráficas y reportes.
     */
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$",
            message = "El color debe ser un código hexadecimal válido (#RRGGBB)")
    @Column(name = "color", length = 7)
    private String color;

    // ===================================================================
    // CONSTRUCTORES DE CONVENIENCIA
    // ===================================================================

    /**
     * Constructor con campos obligatorios.
     *
     * @param code código único del tipo (1 dígito)
     * @param name nombre descriptivo del tipo
     */
    public ProductType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Constructor completo.
     *
     * @param code código único del tipo
     * @param name nombre descriptivo del tipo
     * @param description descripción detallada
     * @param displayOrder orden de visualización
     */
    public ProductType(String code, String name, String description, Integer displayOrder) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Verifica si este tipo es un tipo especial predefinido.
     *
     * @return true si es un tipo especial del sistema
     */
    public boolean isSpecialType() {
        return Constants.ConfigCodes.TYPE_SIMPLE.equals(code) ||
                Constants.ConfigCodes.TYPE_COMPOSITE.equals(code) ||
                Constants.ConfigCodes.TYPE_SET.equals(code) ||
                Constants.ConfigCodes.TYPE_PROMOTIONAL.equals(code) ||
                Constants.ConfigCodes.TYPE_SEASONAL.equals(code);
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
     * Verifica si el tipo permite la composición de productos.
     *
     * @return true si permite productos compuestos
     */
    public boolean canHaveComposition() {
        return allowsComposition != null && allowsComposition;
    }

    /**
     * Obtiene la descripción o una por defecto si no existe.
     *
     * @return descripción del tipo o mensaje por defecto
     */
    public String getDescriptionOrDefault() {
        return description != null && !description.trim().isEmpty()
                ? description
                : "Tipo de producto: " + name;
    }

    // ===================================================================
    // MÉTODOS DE VALIDACIÓN
    // ===================================================================

    /**
     * Valida que el código sea un dígito válido.
     *
     * @return true si el código es válido
     */
    public boolean isValidCode() {
        return code != null &&
                code.matches(Constants.Validation.TYPE_CODE_PATTERN) &&
                !code.trim().isEmpty();
    }

    /**
     * Normaliza los datos antes de persistir.
     * Limpia espacios y establece valores por defecto.
     */
    private void normalizeProductType() {
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
        if (allowsComposition == null) {
            allowsComposition = true;
        }
    }

    // ===================================================================
    // MÉTODOS EQUALS, HASHCODE Y TOSTRING ESPECÍFICOS
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductType)) return false;

        ProductType that = (ProductType) o;

        // Si ambos tienen ID, comparar por ID
        if (getId() != null && that.getId() != null) {
            return getId().equals(that.getId());
        }

        // Si no tienen ID, comparar por código (business key)
        return code != null && code.equals(that.code);
    }

    @Override
    public int hashCode() {
        // Usar código como business key para hashCode
        return code != null ? code.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("ProductType{id=%d, code='%s', name='%s', active=%s, displayOrder=%d}",
                getId(), code, name, getActive(), displayOrder);
    }

    // ===================================================================
    // MÉTODOS ESTÁTICOS DE FACTORY
    // ===================================================================

    /**
     * Crea un tipo de producto simple con valores por defecto.
     *
     * @param code código del tipo
     * @param name nombre del tipo
     * @return nueva instancia de ProductType
     */
    public static ProductType createSimple(String code, String name) {
        ProductType type = new ProductType();
        type.setCode(code);
        type.setName(name);
        type.setDisplayOrder(1);
        type.setAllowsComposition(true);
        return type;
    }

    /**
     * Crea un tipo de producto con descripción.
     *
     * @param code código del tipo
     * @param name nombre del tipo
     * @param description descripción del tipo
     * @return nueva instancia de ProductType
     */
    public static ProductType createWithDescription(String code, String name, String description) {
        ProductType type = createSimple(code, name);
        type.setDescription(description);
        return type;
    }

    /**
     * Crea los tipos de producto por defecto del sistema.
     *
     * @return array con los tipos por defecto
     */
    public static ProductType[] createDefaultTypes() {
        return new ProductType[]{
                createWithDescription("1", "Producto Simple",
                        "Productos individuales básicos sin composición compleja"),
                createWithDescription("2", "Producto Compuesto",
                        "Productos formados por múltiples componentes o materiales"),
                createWithDescription("3", "Set/Conjunto",
                        "Conjuntos de productos que se venden como una unidad"),
                createWithDescription("4", "Producto Promocional",
                        "Productos especiales para promociones y ofertas"),
                createWithDescription("5", "Producto de Temporada",
                        "Productos específicos para temporadas o eventos especiales")
        };
    }
}
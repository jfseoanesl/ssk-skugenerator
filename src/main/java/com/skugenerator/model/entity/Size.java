package com.skugenerator.model.entity;

import com.skugenerator.util.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa las tallas disponibles en el sistema SKU Generator.
 *
 * Las tallas definen la quinta clasificación de un producto
 * y ocupan los dígitos 5-6 del código SKU (TT).
 *
 * Ejemplos:
 * - 00: Talla única
 * - 01: Recién nacido (0-3 meses)
 * - 02: 4-6 años
 * - 03: 7-9 años
 * - 04: 10-12 años
 * - 05: 13-15 años
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Entity
@Table(name = "sizes",
        indexes = {
                @Index(name = "idx_size_code", columnList = "code"),
                @Index(name = "idx_size_active", columnList = "active"),
                @Index(name = "idx_size_name", columnList = "name"),
                @Index(name = "idx_size_age_group", columnList = "age_group")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_size_code", columnNames = "code")
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Size extends BaseEntity {

    /**
     * Código único de la talla.
     * Debe ser de dos dígitos numéricos (00-99).
     * Este código forma parte del SKU en las posiciones 5-6.
     */
    @NotBlank(message = "El código de la talla es obligatorio")
    @Pattern(regexp = Constants.Validation.SIZE_CODE_PATTERN,
            message = "El código debe ser de dos dígitos numéricos (00-99)")
    @Column(name = "code", length = 2, nullable = false, unique = true)
    private String code;

    /**
     * Nombre descriptivo de la talla.
     * Debe ser único y descriptivo para facilitar la identificación.
     */
    @NotBlank(message = "El nombre de la talla es obligatorio")
    @jakarta.validation.constraints.Size(min = Constants.Validation.CONFIG_NAME_MIN_LENGTH,
            max = Constants.Validation.CONFIG_NAME_MAX_LENGTH,
            message = "El nombre debe tener entre {min} y {max} caracteres")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * Descripción detallada de la talla.
     * Campo opcional para proporcionar más información sobre la talla.
     */
    @jakarta.validation.constraints.Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Orden de visualización en las interfaces.
     * Permite controlar el orden en que aparecen las tallas en selects y listas.
     */
    @Min(value = 1, message = "El orden debe ser mayor a 0")
    @Max(value = 999, message = "El orden no puede ser mayor a 999")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    /**
     * Grupo de edad al que pertenece la talla.
     * Útil para categorizar y filtrar tallas por rango de edad.
     */
    @jakarta.validation.constraints.Size(max = 50, message = "El grupo de edad no puede exceder los 50 caracteres")
    @Column(name = "age_group", length = 50)
    private String ageGroup;

    /**
     * Rango de edad mínimo en meses.
     * Útil para ordenamiento y filtrado por edad.
     */
    @Min(value = 0, message = "La edad mínima debe ser mayor o igual a 0")
    @Max(value = 216, message = "La edad mínima no puede ser mayor a 216 meses (18 años)")
    @Column(name = "min_age_months")
    private Integer minAgeMonths;

    /**
     * Rango de edad máximo en meses.
     * Útil para ordenamiento y filtrado por edad.
     */
    @Min(value = 0, message = "La edad máxima debe ser mayor o igual a 0")
    @Max(value = 216, message = "La edad máxima no puede ser mayor a 216 meses (18 años)")
    @Column(name = "max_age_months")
    private Integer maxAgeMonths;

    /**
     * Indica si esta talla es una talla especial (ej: talla única).
     */
    @Column(name = "is_special", nullable = false)
    private Boolean isSpecial = false;

    /**
     * Abreviatura de la talla para uso en interfaces compactas.
     */
    @jakarta.validation.constraints.Size(max = 10, message = "La abreviatura no puede exceder los 10 caracteres")
    @Column(name = "abbreviation", length = 10)
    private String abbreviation;

    // ===================================================================
    // CONSTRUCTORES DE CONVENIENCIA
    // ===================================================================

    /**
     * Constructor con campos obligatorios.
     *
     * @param code código único de la talla (2 dígitos)
     * @param name nombre descriptivo de la talla
     */
    public Size(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Constructor completo.
     *
     * @param code código único de la talla
     * @param name nombre descriptivo de la talla
     * @param description descripción detallada
     * @param ageGroup grupo de edad
     * @param displayOrder orden de visualización
     */
    public Size(String code, String name, String description, String ageGroup, Integer displayOrder) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.ageGroup = ageGroup;
        this.displayOrder = displayOrder;
    }

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Verifica si esta talla es la talla única especial.
     *
     * @return true si es talla única (código "00")
     */
    public boolean isOneSize() {
        return Constants.ConfigCodes.SIZE_ONE_SIZE.equals(code);
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
     * Verifica si la talla es especial.
     *
     * @return true si es una talla especial
     */
    public boolean isSpecialSize() {
        return isSpecial != null && isSpecial;
    }

    /**
     * Obtiene la descripción o una por defecto si no existe.
     *
     * @return descripción de la talla o mensaje por defecto
     */
    public String getDescriptionOrDefault() {
        return description != null && !description.trim().isEmpty()
                ? description
                : "Talla: " + name;
    }

    /**
     * Obtiene el rango de edad como string descriptivo.
     *
     * @return string con el rango de edad o vacío si no está definido
     */
    public String getAgeRangeDescription() {
        if (minAgeMonths == null && maxAgeMonths == null) {
            return "";
        }

        if (minAgeMonths != null && maxAgeMonths != null) {
            return String.format("%s - %s",
                    formatAgeMonths(minAgeMonths),
                    formatAgeMonths(maxAgeMonths));
        }

        if (minAgeMonths != null) {
            return "Desde " + formatAgeMonths(minAgeMonths);
        }

        return "Hasta " + formatAgeMonths(maxAgeMonths);
    }

    /**
     * Verifica si la talla está dentro de un rango de edad específico.
     *
     * @param ageInMonths edad en meses a verificar
     * @return true si la edad está dentro del rango de la talla
     */
    public boolean isValidForAge(int ageInMonths) {
        if (minAgeMonths != null && ageInMonths < minAgeMonths) {
            return false;
        }
        if (maxAgeMonths != null && ageInMonths > maxAgeMonths) {
            return false;
        }
        return true;
    }

    /**
     * Obtiene la abreviatura o código si no existe abreviatura.
     *
     * @return abreviatura de la talla
     */
    public String getAbbreviationOrCode() {
        return abbreviation != null && !abbreviation.trim().isEmpty()
                ? abbreviation
                : code;
    }

    // ===================================================================
    // MÉTODOS DE VALIDACIÓN
    // ===================================================================

    /**
     * Valida que el código sea válido para una talla.
     *
     * @return true si el código es válido
     */
    public boolean isValidCode() {
        return code != null &&
                code.matches(Constants.Validation.SIZE_CODE_PATTERN) &&
                !code.trim().isEmpty();
    }

    /**
     * Valida que el rango de edad sea consistente.
     *
     * @return true si el rango de edad es válido
     */
    public boolean isValidAgeRange() {
        if (minAgeMonths == null || maxAgeMonths == null) {
            return true; // Opcional tener ambos
        }
        return minAgeMonths <= maxAgeMonths;
    }

    // ===================================================================
    // MÉTODOS EQUALS, HASHCODE Y TOSTRING ESPECÍFICOS
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Size)) return false;

        Size size = (Size) o;

        // Si ambos tienen ID, comparar por ID
        if (getId() != null && size.getId() != null) {
            return getId().equals(size.getId());
        }

        // Si no tienen ID, comparar por código (business key)
        return code != null && code.equals(size.code);
    }

    @Override
    public int hashCode() {
        // Usar código como business key para hashCode
        return code != null ? code.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Size{id=%d, code='%s', name='%s', ageGroup='%s', active=%s}",
                getId(), code, name, ageGroup, getActive());
    }

    // ===================================================================
    // MÉTODOS ESTÁTICOS DE FACTORY
    // ===================================================================

    /**
     * Crea una talla simple con valores por defecto.
     *
     * @param code código de la talla
     * @param name nombre de la talla
     * @return nueva instancia de Size
     */
    public static Size createSimple(String code, String name) {
        Size size = new Size();
        size.setCode(code);
        size.setName(name);
        size.setDisplayOrder(1);
        size.setIsSpecial(false);
        return size;
    }

    /**
     * Crea una talla con descripción y grupo de edad.
     *
     * @param code código de la talla
     * @param name nombre de la talla
     * @param description descripción de la talla
     * @param ageGroup grupo de edad
     * @return nueva instancia de Size
     */
    public static Size createWithAgeGroup(String code, String name, String description, String ageGroup) {
        Size size = createSimple(code, name);
        size.setDescription(description);
        size.setAgeGroup(ageGroup);
        return size;
    }

    /**
     * Crea las tallas por defecto del sistema.
     *
     * @return array con las tallas por defecto
     */
    public static Size[] createDefaultSizes() {
        return new Size[]{
                createWithAgeGroup("00", "Talla única",
                        "Talla única para productos sin clasificación específica", "Universal"),
                createWithAgeGroup("01", "Recién nacido (0-3 meses)",
                        "Talla para bebés recién nacidos", "Bebé"),
                createWithAgeGroup("02", "3-6 meses",
                        "Talla para bebés de 3 a 6 meses", "Bebé"),
                createWithAgeGroup("03", "6-9 meses",
                        "Talla para bebés de 6 a 9 meses", "Bebé"),
                createWithAgeGroup("04", "9-12 meses",
                        "Talla para bebés de 9 a 12 meses", "Bebé"),
                createWithAgeGroup("05", "12-18 meses",
                        "Talla para niños de 12 a 18 meses", "Pequeño"),
                createWithAgeGroup("06", "18-24 meses",
                        "Talla para niños de 18 a 24 meses", "Pequeño"),
                createWithAgeGroup("07", "2-3 años",
                        "Talla para niños de 2 a 3 años", "Pequeño"),
                createWithAgeGroup("08", "4-5 años",
                        "Talla para niños de 4 a 5 años", "Mediano"),
                createWithAgeGroup("09", "6-7 años",
                        "Talla para niños de 6 a 7 años", "Mediano"),
                createWithAgeGroup("10", "8-9 años",
                        "Talla para niños de 8 a 9 años", "Grande"),
                createWithAgeGroup("11", "10-11 años",
                        "Talla para niños de 10 a 11 años", "Grande"),
                createWithAgeGroup("12", "12-13 años",
                        "Talla para niños de 12 a 13 años", "Extra Grande"),
                createWithAgeGroup("13", "14-15 años",
                        "Talla para adolescentes de 14 a 15 años", "Extra Grande")
        };
    }

    // ===================================================================
    // MÉTODOS UTILITARIOS PRIVADOS
    // ===================================================================

    /**
     * Convierte meses a una representación legible.
     *
     * @param months meses a convertir
     * @return string legible con años y meses
     */
    private String formatAgeMonths(int months) {
        if (months == 0) {
            return "0 meses";
        }

        if (months < 12) {
            return months + (months == 1 ? " mes" : " meses");
        }

        int years = months / 12;
        int remainingMonths = months % 12;

        String yearStr = years + (years == 1 ? " año" : " años");

        if (remainingMonths == 0) {
            return yearStr;
        }

        String monthStr = remainingMonths + (remainingMonths == 1 ? " mes" : " meses");
        return yearStr + " " + monthStr;
    }
}
package com.skugenerator.model.entity;

import com.skugenerator.util.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa los colores disponibles en el sistema SKU Generator.
 *
 * Los colores definen la sexta clasificación de un producto
 * y ocupan los dígitos 7-8 del código SKU (CC).
 *
 * Ejemplos:
 * - 01: Blanco
 * - 02: Negro
 * - 03: Rojo
 * - 04: Azul
 * - 05: Verde
 * - 11-15: Rango reservado para estampados
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Entity
@Table(name = "colors",
        indexes = {
                @Index(name = "idx_color_code", columnList = "code"),
                @Index(name = "idx_color_active", columnList = "active"),
                @Index(name = "idx_color_name", columnList = "name"),
                @Index(name = "idx_color_family", columnList = "color_family"),
                @Index(name = "idx_color_type", columnList = "color_type")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_color_code", columnNames = "code")
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Color extends BaseEntity {

    /**
     * Código único del color.
     * Debe ser de dos dígitos numéricos (01-99).
     * Este código forma parte del SKU en las posiciones 7-8.
     */
    @NotBlank(message = "El código del color es obligatorio")
    @Pattern(regexp = Constants.Validation.COLOR_CODE_PATTERN,
            message = "El código debe ser de dos dígitos numéricos (01-99)")
    @Column(name = "code", length = 2, nullable = false, unique = true)
    private String code;

    /**
     * Nombre descriptivo del color.
     * Debe ser único y descriptivo para facilitar la identificación.
     */
    @NotBlank(message = "El nombre del color es obligatorio")
    @jakarta.validation.constraints.Size(min = Constants.Validation.CONFIG_NAME_MIN_LENGTH,
            max = Constants.Validation.CONFIG_NAME_MAX_LENGTH,
            message = "El nombre debe tener entre {min} y {max} caracteres")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * Descripción detallada del color.
     * Campo opcional para proporcionar más información sobre el color.
     */
    @jakarta.validation.constraints.Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Orden de visualización en las interfaces.
     * Permite controlar el orden en que aparecen los colores en selects y listas.
     */
    @Min(value = 1, message = "El orden debe ser mayor a 0")
    @Max(value = 999, message = "El orden no puede ser mayor a 999")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    /**
     * Código de color hexadecimal para representación visual.
     * Formato: #RRGGBB
     */
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$",
            message = "El código hexadecimal debe tener el formato #RRGGBB")
    @Column(name = "hex_code", length = 7)
    private String hexCode;

    /**
     * Familia de color a la que pertenece (ej: Rojos, Azules, Neutros).
     * Útil para categorización y filtrado.
     */
    @jakarta.validation.constraints.Size(max = 50, message = "La familia de color no puede exceder los 50 caracteres")
    @Column(name = "color_family", length = 50)
    private String colorFamily;

    /**
     * Tipo de color: SOLID (sólido), PATTERN (estampado), MULTICOLOR (multicolor).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "color_type", length = 20, nullable = false)
    private ColorType colorType = ColorType.SOLID;

    /**
     * Indica si este color es popular o destacado.
     * Útil para mostrar primero en interfaces.
     */
    @Column(name = "is_popular", nullable = false)
    private Boolean isPopular = false;

    /**
     * Temporadas en las que este color es más apropiado.
     * Separadas por comas (ej: "1,2" para Primavera-Verano y Otoño-Invierno).
     */
    @jakarta.validation.constraints.Size(max = 20, message = "Las temporadas no pueden exceder los 20 caracteres")
    @Column(name = "suitable_seasons", length = 20)
    private String suitableSeasons;

    /**
     * Nombre alternativo o nombre comercial del color.
     */
    @jakarta.validation.constraints.Size(max = 100, message = "El nombre alternativo no puede exceder los 100 caracteres")
    @Column(name = "alternative_name", length = 100)
    private String alternativeName;

    /**
     * Enum para tipos de color.
     */
    public enum ColorType {
        SOLID("Sólido"),
        PATTERN("Estampado"),
        MULTICOLOR("Multicolor"),
        GRADIENT("Degradado");

        private final String displayName;

        ColorType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // ===================================================================
    // CONSTRUCTORES DE CONVENIENCIA
    // ===================================================================

    /**
     * Constructor con campos obligatorios.
     *
     * @param code código único del color (2 dígitos)
     * @param name nombre descriptivo del color
     */
    public Color(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Constructor completo.
     *
     * @param code        código único del color
     * @param name        nombre descriptivo del color
     * @param hexCode     código hexadecimal del color
     * @param colorFamily familia de color
     * @param colorType   tipo de color
     */
    public Color(String code, String name, String hexCode, String colorFamily, ColorType colorType) {
        this.code = code;
        this.name = name;
        this.hexCode = hexCode;
        this.colorFamily = colorFamily;
        this.colorType = colorType;
    }

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Verifica si este color es multicolor (código especial).
     *
     * @return true si es multicolor
     */
    public boolean isMulticolor() {
        return Constants.ConfigCodes.COLOR_MULTICOLOR.equals(code);
    }

    /**
     * Verifica si este color es un estampado (códigos 11-15).
     *
     * @return true si es un estampado
     */
    public boolean isPattern() {
        if (code == null) return false;
        try {
            int codeNum = Integer.parseInt(code);
            int patternStart = Integer.parseInt(Constants.ConfigCodes.COLOR_PATTERN_START);
            int patternEnd = Integer.parseInt(Constants.ConfigCodes.COLOR_PATTERN_END);
            return codeNum >= patternStart && codeNum <= patternEnd;
        } catch (NumberFormatException e) {
            return false;
        }
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
     * Verifica si el color es popular.
     *
     * @return true si es un color popular
     */
    public boolean isPopularColor() {
        return isPopular != null && isPopular;
    }

    /**
     * Obtiene la descripción o una por defecto si no existe.
     *
     * @return descripción del color o mensaje por defecto
     */
    public String getDescriptionOrDefault() {
        return description != null && !description.trim().isEmpty()
                ? description
                : "Color: " + name;
    }

    /**
     * Obtiene el nombre para mostrar, priorizando el nombre alternativo si existe.
     *
     * @return nombre alternativo o nombre principal
     */
    public String getDisplayNamePreferred() {
        return alternativeName != null && !alternativeName.trim().isEmpty()
                ? alternativeName
                : name;
    }

    /**
     * Verifica si el color es apropiado para una temporada específica.
     *
     * @param seasonCode código de temporada a verificar
     * @return true si el color es apropiado para la temporada
     */
    public boolean isSuitableForSeason(String seasonCode) {
        if (suitableSeasons == null || suitableSeasons.trim().isEmpty()) {
            return true; // Si no se especifica, es apropiado para todas las temporadas
        }
        return suitableSeasons.contains(seasonCode);
    }

    /**
     * Obtiene el código hexadecimal o un color por defecto si no existe.
     *
     * @return código hexadecimal del color o gris por defecto
     */
    public String getHexCodeOrDefault() {
        return hexCode != null && !hexCode.trim().isEmpty() ? hexCode : "#808080";
    }

    /**
     * Obtiene el tipo de color como string.
     *
     * @return nombre del tipo de color
     */
    public String getColorTypeDisplayName() {
        return colorType != null ? colorType.getDisplayName() : "No especificado";
    }

    // ===================================================================
    // MÉTODOS DE VALIDACIÓN
    // ===================================================================

    /**
     * Valida que el código sea válido para un color.
     *
     * @return true si el código es válido
     */
    public boolean isValidCode() {
        return code != null &&
                code.matches(Constants.Validation.COLOR_CODE_PATTERN) &&
                !code.trim().isEmpty();
    }

    /**
     * Valida que el código hexadecimal sea válido.
     *
     * @return true si el código hexadecimal es válido o está vacío
     */
    public boolean isValidHexCode() {
        if (hexCode == null || hexCode.trim().isEmpty()) {
            return true; // Es opcional
        }
        return hexCode.matches("^#[0-9A-Fa-f]{6}$");
    }

    // ===================================================================
    // MÉTODOS EQUALS, HASHCODE Y TOSTRING ESPECÍFICOS
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Color)) return false;

        Color color = (Color) o;

        // Si ambos tienen ID, comparar por ID
        if (getId() != null && color.getId() != null) {
            return getId().equals(color.getId());
        }

        // Si no tienen ID, comparar por código (business key)
        return code != null && code.equals(color.code);
    }

    @Override
    public int hashCode() {
        // Usar código como business key para hashCode
        return code != null ? code.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Color{id=%d, code='%s', name='%s', family='%s', type=%s, active=%s}",
                getId(), code, name, colorFamily, colorType, getActive());
    }

    // ===================================================================
    // MÉTODOS ESTÁTICOS DE FACTORY
    // ===================================================================

    /**
     * Crea un color simple con valores por defecto.
     *
     * @param code código del color
     * @param name nombre del color
     * @return nueva instancia de Color
     */
    public static Color createSimple(String code, String name) {
        Color color = new Color();
        color.setCode(code);
        color.setName(name);
        color.setDisplayOrder(1);
        color.setColorType(ColorType.SOLID);
        color.setIsPopular(false);
        return color;
    }

    /**
     * Crea un color con código hexadecimal.
     *
     * @param code        código del color
     * @param name        nombre del color
     * @param hexCode     código hexadecimal
     * @param colorFamily familia de color
     * @return nueva instancia de Color
     */
    public static Color createWithHex(String code, String name, String hexCode, String colorFamily) {
        Color color = createSimple(code, name);
        color.setHexCode(hexCode);
        color.setColorFamily(colorFamily);
        return color;
    }

    /**
     * Crea un color estampado.
     *
     * @param code        código del color (debe estar en rango 11-15)
     * @param name        nombre del estampado
     * @param description descripción del estampado
     * @return nueva instancia de Color para estampado
     */
    public static Color createPattern(String code, String name, String description) {
        Color color = createSimple(code, name);
        color.setColorType(ColorType.PATTERN);
        color.setDescription(description);
        color.setColorFamily("Estampados");
        return color;
    }

    /**
     * Crea los colores por defecto del sistema.
     *
     * @return array con los colores por defecto
     */
    public static Color[] createDefaultColors() {
        return new Color[]{
                // Colores básicos
                createWithHex("01", "Blanco", "#FFFFFF", "Neutros"),
                createWithHex("02", "Negro", "#000000", "Neutros"),
                createWithHex("03", "Gris", "#808080", "Neutros"),
                createWithHex("04", "Rojo", "#FF0000", "Rojos"),
                createWithHex("05", "Azul", "#0000FF", "Azules"),
                createWithHex("06", "Verde", "#00FF00", "Verdes"),
                createWithHex("07", "Amarillo", "#FFFF00", "Amarillos"),
                createWithHex("08", "Rosa", "#FFC0CB", "Rosas"),
                createWithHex("09", "Morado", "#800080", "Morados"),
                createWithHex("10", "Naranja", "#FFA500", "Naranjas"),

                // Estampados (códigos especiales 11-15)
                createPattern("11", "Multicolor", "Producto con múltiples colores"),
                createPattern("12", "Rayas", "Estampado de rayas"),
                createPattern("13", "Lunares", "Estampado de lunares o puntos"),
                createPattern("14", "Flores", "Estampado floral"),
                createPattern("15", "Animales", "Estampado con figuras de animales"),

                // Colores adicionales
                createWithHex("16", "Beige", "#F5F5DC", "Neutros"),
                createWithHex("17", "Marino", "#000080", "Azules"),
                createWithHex("18", "Turquesa", "#40E0D0", "Azules"),
                createWithHex("19", "Coral", "#FF7F50", "Naranjas"),
                createWithHex("20", "Lavanda", "#E6E6FA", "Morados")
        };
    }

    /**
     * Obtiene los colores más populares para mostrar primero en interfaces.
     *
     * @return array con códigos de colores populares
     */
    public static String[] getPopularColorCodes() {
        return new String[]{"01", "02", "04", "05", "08", "11"};
    }

    /**
     * Obtiene los colores apropiados para una temporada específica.
     *
     * @param seasonCode código de temporada
     * @return array con códigos de colores apropiados
     */
    public static String[] getSeasonalColorCodes(String seasonCode) {
        return switch (seasonCode) {
            case "1" -> new String[]{"04", "07", "08", "10", "19"}; // Primavera-Verano
            case "2" -> new String[]{"02", "03", "05", "09", "17"}; // Otoño-Invierno
            case "3" -> new String[]{"04", "05", "06", "14", "15"}; // Navidad
            default -> new String[]{"01", "02", "03", "11"}; // Todo el año
        };
    }
}
package com.skugenerator.model.entity;

import com.skugenerator.util.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.Month;

/**
 * Entidad que representa las temporadas disponibles en el sistema SKU Generator.
 *
 * Las temporadas definen la séptima clasificación de un producto
 * y ocupan el dígito 9 del código SKU (S).
 *
 * Ejemplos:
 * - 1: Primavera-Verano
 * - 2: Otoño-Invierno
 * - 3: Navidad
 * - 4: Todo el año
 * - 5: Regreso a clases
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Entity
@Table(name = "seasons",
        indexes = {
                @Index(name = "idx_season_code", columnList = "code"),
                @Index(name = "idx_season_active", columnList = "active"),
                @Index(name = "idx_season_name", columnList = "name"),
                @Index(name = "idx_season_type", columnList = "season_type"),
                @Index(name = "idx_season_start_month", columnList = "start_month")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_season_code", columnNames = "code")
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Season extends BaseEntity {

    /**
     * Código único de la temporada.
     * Debe ser un solo dígito numérico (0-9).
     * Este código forma parte del SKU en la posición 9.
     */
    @NotBlank(message = "El código de la temporada es obligatorio")
    @Pattern(regexp = Constants.Validation.SEASON_CODE_PATTERN,
            message = "El código debe ser un solo dígito numérico (0-9)")
    @Column(name = "code", length = 1, nullable = false, unique = true)
    private String code;

    /**
     * Nombre descriptivo de la temporada.
     * Debe ser único y descriptivo para facilitar la identificación.
     */
    @NotBlank(message = "El nombre de la temporada es obligatorio")
    @jakarta.validation.constraints.Size(min = Constants.Validation.CONFIG_NAME_MIN_LENGTH,
            max = Constants.Validation.CONFIG_NAME_MAX_LENGTH,
            message = "El nombre debe tener entre {min} y {max} caracteres")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * Descripción detallada de la temporada.
     * Campo opcional para proporcionar más información sobre la temporada.
     */
    @jakarta.validation.constraints.Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Orden de visualización en las interfaces.
     * Permite controlar el orden en que aparecen las temporadas en selects y listas.
     */
    @Min(value = 1, message = "El orden debe ser mayor a 0")
    @Max(value = 999, message = "El orden no puede ser mayor a 999")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    /**
     * Tipo de temporada: REGULAR, SPECIAL, YEAR_ROUND.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "season_type", length = 20, nullable = false)
    private SeasonType seasonType = SeasonType.REGULAR;

    /**
     * Mes de inicio de la temporada (1-12).
     * Útil para determinar automáticamente la temporada según la fecha.
     */
    @Min(value = 1, message = "El mes de inicio debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes de inicio debe estar entre 1 y 12")
    @Column(name = "start_month")
    private Integer startMonth;

    /**
     * Mes de fin de la temporada (1-12).
     * Útil para determinar automáticamente la temporada según la fecha.
     */
    @Min(value = 1, message = "El mes de fin debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes de fin debe estar entre 1 y 12")
    @Column(name = "end_month")
    private Integer endMonth;

    /**
     * Indica si esta temporada está actualmente en vigencia.
     * Se puede calcular automáticamente basado en los meses.
     */
    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = false;

    /**
     * Color hexadecimal asociado a la temporada para representación visual.
     */
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$",
            message = "El color debe ser un código hexadecimal válido (#RRGGBB)")
    @Column(name = "color", length = 7)
    private String color;

    /**
     * Icono CSS para representación visual de la temporada.
     */
    @jakarta.validation.constraints.Size(max = 50, message = "El icono no puede exceder los 50 caracteres")
    @Column(name = "icon", length = 50)
    private String icon;

    /**
     * Abreviatura de la temporada para uso en interfaces compactas.
     */
    @jakarta.validation.constraints.Size(max = 10, message = "La abreviatura no puede exceder los 10 caracteres")
    @Column(name = "abbreviation", length = 10)
    private String abbreviation;

    /**
     * Enum para tipos de temporada.
     */
    public enum SeasonType {
        REGULAR("Regular"),
        SPECIAL("Especial"),
        YEAR_ROUND("Todo el año"),
        EVENT("Evento");

        private final String displayName;

        SeasonType(String displayName) {
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
     * @param code código único de la temporada (1 dígito)
     * @param name nombre descriptivo de la temporada
     */
    public Season(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Constructor completo.
     *
     * @param code código único de la temporada
     * @param name nombre descriptivo de la temporada
     * @param description descripción detallada
     * @param seasonType tipo de temporada
     * @param startMonth mes de inicio
     * @param endMonth mes de fin
     */
    public Season(String code, String name, String description, SeasonType seasonType,
                  Integer startMonth, Integer endMonth) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.seasonType = seasonType;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
    }

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Verifica si esta temporada es "Todo el año".
     *
     * @return true si es temporada de todo el año
     */
    public boolean isAllYear() {
        return Constants.ConfigCodes.SEASON_ALL_YEAR.equals(code) ||
                seasonType == SeasonType.YEAR_ROUND;
    }

    /**
     * Verifica si esta temporada es especial.
     *
     * @return true si es una temporada especial
     */
    public boolean isSpecialSeason() {
        return seasonType == SeasonType.SPECIAL || seasonType == SeasonType.EVENT;
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
     * Obtiene la descripción o una por defecto si no existe.
     *
     * @return descripción de la temporada o mensaje por defecto
     */
    public String getDescriptionOrDefault() {
        return description != null && !description.trim().isEmpty()
                ? description
                : "Temporada: " + name;
    }

    /**
     * Obtiene la abreviatura o código si no existe abreviatura.
     *
     * @return abreviatura de la temporada
     */
    public String getAbbreviationOrCode() {
        return abbreviation != null && !abbreviation.trim().isEmpty()
                ? abbreviation
                : code;
    }

    /**
     * Obtiene el tipo de temporada como string.
     *
     * @return nombre del tipo de temporada
     */
    public String getSeasonTypeDisplayName() {
        return seasonType != null ? seasonType.getDisplayName() : "No especificado";
    }

    /**
     * Verifica si la temporada está activa en una fecha específica.
     *
     * @param date fecha a verificar
     * @return true si la temporada está activa en esa fecha
     */
    public boolean isActiveInDate(LocalDate date) {
        if (isAllYear()) {
            return true;
        }

        if (startMonth == null || endMonth == null) {
            return true; // Si no se especifican meses, se considera activa
        }

        int currentMonth = date.getMonthValue();

        // Si la temporada no cruza el año (ej: marzo a agosto)
        if (startMonth <= endMonth) {
            return currentMonth >= startMonth && currentMonth <= endMonth;
        }
        // Si la temporada cruza el año (ej: octubre a febrero)
        else {
            return currentMonth >= startMonth || currentMonth <= endMonth;
        }
    }

    /**
     * Verifica si la temporada está actualmente activa.
     *
     * @return true si la temporada está activa ahora
     */
    public boolean isCurrentlyActive() {
        return isActiveInDate(LocalDate.now());
    }

    /**
     * Obtiene el rango de meses como string descriptivo.
     *
     * @return string con el rango de meses
     */
    public String getMonthRangeDescription() {
        if (startMonth == null || endMonth == null) {
            return "No especificado";
        }

        if (isAllYear()) {
            return "Todo el año";
        }

        String startMonthName = Month.of(startMonth).name();
        String endMonthName = Month.of(endMonth).name();

        if (startMonth.equals(endMonth)) {
            return startMonthName;
        }

        return String.format("%s - %s", startMonthName, endMonthName);
    }

    /**
     * Calcula automáticamente si debe estar marcada como actual.
     */
    public void updateCurrentStatus() {
        this.isCurrent = isCurrentlyActive();
    }

    // ===================================================================
    // MÉTODOS DE VALIDACIÓN
    // ===================================================================

    /**
     * Valida que el código sea válido para una temporada.
     *
     * @return true si el código es válido
     */
    public boolean isValidCode() {
        return code != null &&
                code.matches(Constants.Validation.SEASON_CODE_PATTERN) &&
                !code.trim().isEmpty();
    }

    /**
     * Valida que el rango de meses sea consistente.
     *
     * @return true si el rango de meses es válido
     */
    public boolean isValidMonthRange() {
        if (startMonth == null || endMonth == null) {
            return true; // Es opcional tener ambos
        }

        // Para temporadas de todo el año, el rango puede ser cualquiera
        if (isAllYear()) {
            return true;
        }

        // Los meses deben estar en rango válido
        return startMonth >= 1 && startMonth <= 12 &&
                endMonth >= 1 && endMonth <= 12;
    }

    // ===================================================================
    // MÉTODOS EQUALS, HASHCODE Y TOSTRING ESPECÍFICOS
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Season)) return false;

        Season season = (Season) o;

        // Si ambos tienen ID, comparar por ID
        if (getId() != null && season.getId() != null) {
            return getId().equals(season.getId());
        }

        // Si no tienen ID, comparar por código (business key)
        return code != null && code.equals(season.code);
    }

    @Override
    public int hashCode() {
        // Usar código como business key para hashCode
        return code != null ? code.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Season{id=%d, code='%s', name='%s', type=%s, current=%s, active=%s}",
                getId(), code, name, seasonType, isCurrent, getActive());
    }

    // ===================================================================
    // MÉTODOS ESTÁTICOS DE FACTORY
    // ===================================================================

    /**
     * Crea una temporada simple con valores por defecto.
     *
     * @param code código de la temporada
     * @param name nombre de la temporada
     * @return nueva instancia de Season
     */
    public static Season createSimple(String code, String name) {
        Season season = new Season();
        season.setCode(code);
        season.setName(name);
        season.setDisplayOrder(1);
        season.setSeasonType(SeasonType.REGULAR);
        season.setIsCurrent(false);
        return season;
    }

    /**
     * Crea una temporada con meses específicos.
     *
     * @param code código de la temporada
     * @param name nombre de la temporada
     * @param description descripción de la temporada
     * @param startMonth mes de inicio
     * @param endMonth mes de fin
     * @return nueva instancia de Season
     */
    public static Season createWithMonths(String code, String name, String description,
                                          Integer startMonth, Integer endMonth) {
        Season season = createSimple(code, name);
        season.setDescription(description);
        season.setStartMonth(startMonth);
        season.setEndMonth(endMonth);
        season.updateCurrentStatus();
        return season;
    }

    /**
     * Crea una temporada especial.
     *
     * @param code código de la temporada
     * @param name nombre de la temporada
     * @param description descripción de la temporada
     * @param seasonType tipo de temporada especial
     * @return nueva instancia de Season
     */
    public static Season createSpecial(String code, String name, String description, SeasonType seasonType) {
        Season season = createSimple(code, name);
        season.setDescription(description);
        season.setSeasonType(seasonType);
        return season;
    }

    /**
     * Crea las temporadas por defecto del sistema.
     *
     * @return array con las temporadas por defecto
     */
    public static Season[] createDefaultSeasons() {
        return new Season[]{
                createWithMonths("1", "Primavera-Verano",
                        "Temporada de primavera y verano con colores claros y frescos", 3, 8),
                createWithMonths("2", "Otoño-Invierno",
                        "Temporada de otoño e invierno con colores cálidos y abrigos", 9, 2),
                createWithMonths("3", "Navidad",
                        "Temporada navideña con diseños festivos y colores tradicionales", 11, 12),
                createSpecial("4", "Todo el año",
                        "Productos atemporales apropiados para cualquier época del año", SeasonType.YEAR_ROUND),
                createWithMonths("5", "Regreso a clases",
                        "Temporada de regreso a clases con diseños educativos", 1, 2),
                createSpecial("6", "Verano intenso",
                        "Temporada de verano con productos específicos para calor extremo", SeasonType.SPECIAL),
                createWithMonths("7", "Pascua",
                        "Temporada de Pascua con diseños primaverales y pasteles", 3, 4),
                createSpecial("8", "Halloween",
                        "Temporada de Halloween con diseños temáticos de octubre", SeasonType.EVENT),
                createSpecial("9", "San Valentín",
                        "Temporada de San Valentín con diseños románticos y rosados", SeasonType.EVENT)
        };
    }

    /**
     * Obtiene la temporada actual basada en la fecha.
     *
     * @return código de la temporada actual
     */
    public static String getCurrentSeasonCode() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();

        // Lógica básica para determinar temporada
        if (currentMonth >= 3 && currentMonth <= 8) {
            return "1"; // Primavera-Verano
        } else if (currentMonth >= 9 || currentMonth <= 2) {
            return "2"; // Otoño-Invierno
        } else if (currentMonth == 11 || currentMonth == 12) {
            return "3"; // Navidad
        }

        return "4"; // Todo el año por defecto
    }

    /**
     * Obtiene las temporadas apropiadas para un mes específico.
     *
     * @param month mes a verificar (1-12)
     * @return array con códigos de temporadas apropiadas
     */
    public static String[] getSeasonsForMonth(int month) {
        return switch (month) {
            case 1, 2 -> new String[]{"2", "4", "5"}; // Otoño-Invierno, Todo el año, Regreso a clases
            case 3, 4 -> new String[]{"1", "4", "7"}; // Primavera-Verano, Todo el año, Pascua
            case 5, 6, 7, 8 -> new String[]{"1", "4", "6"}; // Primavera-Verano, Todo el año, Verano intenso
            case 9, 10 -> new String[]{"2", "4", "8"}; // Otoño-Invierno, Todo el año, Halloween
            case 11, 12 -> new String[]{"2", "3", "4"}; // Otoño-Invierno, Navidad, Todo el año
            default -> new String[]{"4"}; // Todo el año por defecto
        };
    }
}

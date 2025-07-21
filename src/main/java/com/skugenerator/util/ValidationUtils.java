package com.skugenerator.util;

import com.skugenerator.model.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;
import java.util.List;

/**
 * Utilidades para validación de datos en la aplicación SKU Generator.
 * Proporciona métodos centralizados para validar códigos, nombres, formatos y reglas de negocio.
 */
@Slf4j
public final class ValidationUtils {

    private ValidationUtils() {
        throw new UnsupportedOperationException("Esta es una clase de utilidades y no puede ser instanciada");
    }

    // Patrones de validación compilados para mejor performance
    private static final Pattern TYPE_CODE_PATTERN = Pattern.compile(Constants.Validation.TYPE_CODE_PATTERN);
    private static final Pattern CATEGORY_CODE_PATTERN = Pattern.compile(Constants.Validation.CATEGORY_CODE_PATTERN);
    private static final Pattern SUBCATEGORY_CODE_PATTERN = Pattern.compile(Constants.Validation.SUBCATEGORY_CODE_PATTERN);
    private static final Pattern SIZE_CODE_PATTERN = Pattern.compile(Constants.Validation.SIZE_CODE_PATTERN);
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile(Constants.Validation.COLOR_CODE_PATTERN);
    private static final Pattern SEASON_CODE_PATTERN = Pattern.compile(Constants.Validation.SEASON_CODE_PATTERN);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(Constants.Validation.EMAIL_PATTERN);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(Constants.Validation.USERNAME_PATTERN);
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    private static final Pattern SKU_CODE_PATTERN = Pattern.compile(Constants.SkuCodes.SKU_PATTERN);

    // ==========================================
    // VALIDACIONES DE CÓDIGOS SKU
    // ==========================================

    /**
     * Valida que un código de tipo de producto sea correcto.
     * @param code Código a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidProductTypeCode(String code) {
        if (StringUtils.isBlank(code)) {
            log.debug("Código de tipo de producto vacío o nulo");
            return false;
        }
        boolean isValid = TYPE_CODE_PATTERN.matcher(code.trim()).matches();
        log.debug("Validación código tipo producto '{}': {}", code, isValid);
        return isValid;
    }

    /**
     * Valida que un código de categoría sea correcto.
     * @param code Código a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidCategoryCode(String code) {
        if (StringUtils.isBlank(code)) {
            log.debug("Código de categoría vacío o nulo");
            return false;
        }
        boolean isValid = CATEGORY_CODE_PATTERN.matcher(code.trim()).matches();
        log.debug("Validación código categoría '{}': {}", code, isValid);
        return isValid;
    }

    /**
     * Valida que un código de subcategoría sea correcto.
     * @param code Código a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidSubcategoryCode(String code) {
        if (StringUtils.isBlank(code)) {
            log.debug("Código de subcategoría vacío o nulo");
            return false;
        }
        boolean isValid = SUBCATEGORY_CODE_PATTERN.matcher(code.trim()).matches();
        log.debug("Validación código subcategoría '{}': {}", code, isValid);
        return isValid;
    }

    /**
     * Valida que un código de talla sea correcto.
     * @param code Código a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidSizeCode(String code) {
        if (StringUtils.isBlank(code)) {
            log.debug("Código de talla vacío o nulo");
            return false;
        }
        boolean isValid = SIZE_CODE_PATTERN.matcher(code.trim()).matches();
        log.debug("Validación código talla '{}': {}", code, isValid);
        return isValid;
    }

    /**
     * Valida que un código de color sea correcto.
     * @param code Código a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidColorCode(String code) {
        if (StringUtils.isBlank(code)) {
            log.debug("Código de color vacío o nulo");
            return false;
        }
        boolean isValid = COLOR_CODE_PATTERN.matcher(code.trim()).matches();
        log.debug("Validación código color '{}': {}", code, isValid);
        return isValid;
    }

    /**
     * Valida que un código de temporada sea correcto.
     * @param code Código a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidSeasonCode(String code) {
        if (StringUtils.isBlank(code)) {
            log.debug("Código de temporada vacío o nulo");
            return false;
        }
        boolean isValid = SEASON_CODE_PATTERN.matcher(code.trim()).matches();
        log.debug("Validación código temporada '{}': {}", code, isValid);
        return isValid;
    }

    /**
     * Valida que un código SKU completo sea correcto.
     * @param skuCode Código SKU completo a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidSkuCode(String skuCode) {
        if (StringUtils.isBlank(skuCode)) {
            log.debug("Código SKU vacío o nulo");
            return false;
        }

        String trimmedCode = skuCode.trim();
        if (trimmedCode.length() != Constants.SkuCodes.TOTAL_LENGTH) {
            log.debug("Código SKU '{}' no tiene la longitud correcta de {} dígitos", skuCode, Constants.SkuCodes.TOTAL_LENGTH);
            return false;
        }

        boolean isValid = SKU_CODE_PATTERN.matcher(trimmedCode).matches();
        log.debug("Validación código SKU completo '{}': {}", skuCode, isValid);
        return isValid;
    }

    // ==========================================
    // VALIDACIONES DE NOMBRES Y TEXTOS
    // ==========================================

    /**
     * Valida que un nombre de configuración sea válido.
     * @param name Nombre a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidConfigName(String name) {
        if (StringUtils.isBlank(name)) {
            log.debug("Nombre de configuración vacío o nulo");
            return false;
        }

        String trimmedName = name.trim();
        int length = trimmedName.length();
        boolean isValid = length >= Constants.Validation.CONFIG_NAME_MIN_LENGTH &&
                length <= Constants.Validation.CONFIG_NAME_MAX_LENGTH;

        log.debug("Validación nombre configuración '{}' (longitud {}): {}", name, length, isValid);
        return isValid;
    }

    /**
     * Valida que un nombre de producto sea válido.
     * @param name Nombre a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidProductName(String name) {
        if (StringUtils.isBlank(name)) {
            log.debug("Nombre de producto vacío o nulo");
            return false;
        }

        String trimmedName = name.trim();
        int length = trimmedName.length();
        boolean isValid = length >= Constants.Validation.PRODUCT_NAME_MIN_LENGTH &&
                length <= Constants.Validation.PRODUCT_NAME_MAX_LENGTH;

        log.debug("Validación nombre producto '{}' (longitud {}): {}", name, length, isValid);
        return isValid;
    }

    // ==========================================
    // VALIDACIONES DE USUARIO Y SEGURIDAD
    // ==========================================

    /**
     * Valida que un email tenga formato correcto.
     * @param email Email a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidEmail(String email) {
        if (StringUtils.isBlank(email)) {
            log.debug("Email vacío o nulo");
            return false;
        }

        boolean isValid = EMAIL_PATTERN.matcher(email.trim()).matches();
        log.debug("Validación email '{}': {}", email, isValid);
        return isValid;
    }

    /**
     * Valida que un username sea válido.
     * @param username Username a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidUsername(String username) {
        if (StringUtils.isBlank(username)) {
            log.debug("Username vacío o nulo");
            return false;
        }

        boolean isValid = USERNAME_PATTERN.matcher(username.trim()).matches();
        log.debug("Validación username '{}': {}", username, isValid);
        return isValid;
    }

    /**
     * Valida que una contraseña cumpla con los requisitos mínimos.
     * @param password Contraseña a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValidPassword(String password) {
        if (StringUtils.isBlank(password)) {
            log.debug("Contraseña vacía o nula");
            return false;
        }

        int length = password.length();
        boolean isValid = length >= Constants.Security.PASSWORD_MIN_LENGTH &&
                length <= Constants.Security.PASSWORD_MAX_LENGTH;

        log.debug("Validación contraseña (longitud {}): {}", length, isValid);
        return isValid;
    }

    // ==========================================
    // VALIDACIONES DE COLORES Y FORMATOS
    // ==========================================

    /**
     * Valida que un código hexadecimal de color sea válido.
     * @param hexCode Código hexadecimal a validar (ej: #FF0000)
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidHexColor(String hexCode) {
        if (StringUtils.isBlank(hexCode)) {
            log.debug("Código hexadecimal vacío o nulo");
            return false;
        }

        boolean isValid = HEX_COLOR_PATTERN.matcher(hexCode.trim()).matches();
        log.debug("Validación código hexadecimal '{}': {}", hexCode, isValid);
        return isValid;
    }

    // ==========================================
    // VALIDACIONES NUMÉRICAS
    // ==========================================

    /**
     * Valida que un valor esté dentro de un rango específico.
     * @param value Valor a validar
     * @param min Valor mínimo (inclusive)
     * @param max Valor máximo (inclusive)
     * @return true si está en el rango, false en caso contrario
     */
    public static boolean isInRange(Integer value, int min, int max) {
        if (value == null) {
            log.debug("Valor nulo para validación de rango [{}, {}]", min, max);
            return false;
        }

        boolean isValid = value >= min && value <= max;
        log.debug("Validación rango valor {} en [{}, {}]: {}", value, min, max, isValid);
        return isValid;
    }

    /**
     * Valida que un mes sea válido (1-12).
     * @param month Mes a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidMonth(Integer month) {
        return isInRange(month, 1, 12);
    }

    /**
     * Valida que una edad en meses sea válida para el sistema.
     * @param ageInMonths Edad en meses
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValidAgeInMonths(Integer ageInMonths) {
        return isInRange(ageInMonths, 0, 216); // 0 a 18 años
    }

    /**
     * Valida que un orden de visualización sea válido.
     * @param displayOrder Orden a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidDisplayOrder(Integer displayOrder) {
        return isInRange(displayOrder, 1, 999);
    }

    // ==========================================
    // VALIDACIONES DE REGLAS DE NEGOCIO
    // ==========================================

    /**
     * Valida que un rango de edad sea consistente.
     * @param minAge Edad mínima en meses
     * @param maxAge Edad máxima en meses
     * @return true si el rango es válido, false en caso contrario
     */
    public static boolean isValidAgeRange(Integer minAge, Integer maxAge) {
        if (minAge == null || maxAge == null) {
            log.debug("Edad mínima o máxima es nula");
            return true; // Permitir valores nulos
        }

        boolean isValid = minAge <= maxAge && isValidAgeInMonths(minAge) && isValidAgeInMonths(maxAge);
        log.debug("Validación rango de edad [{}, {}]: {}", minAge, maxAge, isValid);
        return isValid;
    }

    /**
     * Valida que un rango de meses para temporada sea válido.
     * @param startMonth Mes de inicio (1-12)
     * @param endMonth Mes de fin (1-12)
     * @return true si el rango es válido, false en caso contrario
     */
    public static boolean isValidSeasonMonthRange(Integer startMonth, Integer endMonth) {
        if (startMonth == null || endMonth == null) {
            log.debug("Mes de inicio o fin es nulo");
            return true; // Permitir valores nulos para temporadas sin rango específico
        }

        boolean isValid = isValidMonth(startMonth) && isValidMonth(endMonth);
        log.debug("Validación rango meses temporada [{}, {}]: {}", startMonth, endMonth, isValid);
        return isValid;
    }

    /**
     * Valida que un código de color sea apropiado para patrones/estampados.
     * @param colorCode Código de color a validar
     * @return true si es código de patrón, false en caso contrario
     */
    public static boolean isPatternColorCode(String colorCode) {
        if (!isValidColorCode(colorCode)) {
            return false;
        }

        try {
            int code = Integer.parseInt(colorCode);
            int patternStart = Integer.parseInt(Constants.ConfigCodes.COLOR_PATTERN_START);
            int patternEnd = Integer.parseInt(Constants.ConfigCodes.COLOR_PATTERN_END);

            boolean isPattern = code >= patternStart && code <= patternEnd;
            log.debug("Validación código patrón '{}': {}", colorCode, isPattern);
            return isPattern;
        } catch (NumberFormatException e) {
            log.debug("Error al validar código de patrón '{}': {}", colorCode, e.getMessage());
            return false;
        }
    }

    // ==========================================
    // VALIDACIONES DE ENTIDADES
    // ==========================================

    /**
     * Valida que una entidad ProductType sea válida para guardar.
     * @param productType Entidad a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValidProductType(ProductType productType) {
        if (productType == null) {
            log.debug("ProductType es nulo");
            return false;
        }

        boolean isValid = isValidProductTypeCode(productType.getCode()) &&
                isValidConfigName(productType.getName()) &&
                isValidDisplayOrder(productType.getDisplayOrder());

        log.debug("Validación ProductType '{}': {}", productType.getCode(), isValid);
        return isValid;
    }

    /**
     * Valida que una entidad Category sea válida para guardar.
     * @param category Entidad a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValidCategory(Category category) {
        if (category == null) {
            log.debug("Category es nula");
            return false;
        }

        boolean codeValid = isValidCategoryCode(category.getCode());
        boolean nameValid = isValidConfigName(category.getName());
        boolean orderValid = isValidDisplayOrder(category.getDisplayOrder());
        boolean colorValid = category.getColor() == null || isValidHexColor(category.getColor());

        boolean isValid = codeValid && nameValid && orderValid && colorValid;
        log.debug("Validación Category '{}': code={}, name={}, order={}, color={}, result={}",
                category.getCode(), codeValid, nameValid, orderValid, colorValid, isValid);
        return isValid;
    }

    /**
     * Valida que una entidad Size sea válida para guardar.
     * @param size Entidad a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValidSize(Size size) {
        if (size == null) {
            log.debug("Size es nulo");
            return false;
        }

        boolean codeValid = isValidSizeCode(size.getCode());
        boolean nameValid = isValidConfigName(size.getName());
        boolean orderValid = isValidDisplayOrder(size.getDisplayOrder());
        boolean ageRangeValid = isValidAgeRange(size.getMinAgeMonths(), size.getMaxAgeMonths());

        boolean isValid = codeValid && nameValid && orderValid && ageRangeValid;
        log.debug("Validación Size '{}': code={}, name={}, order={}, ageRange={}, result={}",
                size.getCode(), codeValid, nameValid, orderValid, ageRangeValid, isValid);
        return isValid;
    }

    /**
     * Valida que una entidad Color sea válida para guardar.
     * @param color Entidad a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValidColor(Color color) {
        if (color == null) {
            log.debug("Color es nulo");
            return false;
        }

        boolean codeValid = isValidColorCode(color.getCode());
        boolean nameValid = isValidConfigName(color.getName());
        boolean orderValid = isValidDisplayOrder(color.getDisplayOrder());
        boolean hexValid = color.getHexCode() == null || isValidHexColor(color.getHexCode());

        boolean isValid = codeValid && nameValid && orderValid && hexValid;
        log.debug("Validación Color '{}': code={}, name={}, order={}, hex={}, result={}",
                color.getCode(), codeValid, nameValid, orderValid, hexValid, isValid);
        return isValid;
    }

    /**
     * Valida que una entidad Season sea válida para guardar.
     * @param season Entidad a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValidSeason(Season season) {
        if (season == null) {
            log.debug("Season es nulo");
            return false;
        }

        boolean codeValid = isValidSeasonCode(season.getCode());
        boolean nameValid = isValidConfigName(season.getName());
        boolean orderValid = isValidDisplayOrder(season.getDisplayOrder());
        boolean monthRangeValid = isValidSeasonMonthRange(season.getStartMonth(), season.getEndMonth());
        boolean colorValid = season.getColor() == null || isValidHexColor(season.getColor());

        boolean isValid = codeValid && nameValid && orderValid && monthRangeValid && colorValid;
        log.debug("Validación Season '{}': code={}, name={}, order={}, monthRange={}, color={}, result={}",
                season.getCode(), codeValid, nameValid, orderValid, monthRangeValid, colorValid, isValid);
        return isValid;
    }

    // ==========================================
    // UTILIDADES DE SANITIZACIÓN
    // ==========================================

    /**
     * Sanitiza un texto removiendo caracteres peligrosos y normalizando espacios.
     * @param text Texto a sanitizar
     * @return Texto sanitizado o null si el input era nulo
     */
    public static String sanitizeText(String text) {
        if (text == null) {
            return null;
        }

        // Remover caracteres peligrosos y normalizar espacios
        String sanitized = text.trim()
                .replaceAll("[<>\"'&]", "") // Remover caracteres HTML peligrosos
                .replaceAll("\\s+", " "); // Normalizar espacios múltiples

        log.debug("Texto sanitizado: '{}' -> '{}'", text, sanitized);
        return sanitized.isEmpty() ? null : sanitized;
    }

    /**
     * Normaliza un código removiendo espacios y convirtiendo a uppercase.
     * @param code Código a normalizar
     * @return Código normalizado o null si el input era nulo o vacío
     */
    public static String normalizeCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        String normalized = code.trim().toUpperCase();
        log.debug("Código normalizado: '{}' -> '{}'", code, normalized);
        return normalized;
    }

    // ==========================================
    // UTILIDADES DE VALIDACIÓN DE LISTAS
    // ==========================================

    /**
     * Valida que una lista no sea nula ni esté vacía.
     * @param list Lista a validar
     * @return true si contiene elementos, false en caso contrario
     */
    public static boolean isNotEmptyList(List<?> list) {
        boolean isValid = list != null && !list.isEmpty();
        log.debug("Validación lista no vacía: {}", isValid);
        return isValid;
    }

    /**
     * Valida que todos los códigos en una lista sean únicos.
     * @param codes Lista de códigos a validar
     * @return true si todos son únicos, false si hay duplicados
     */
    public static boolean areCodesUnique(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return true;
        }

        long uniqueCount = codes.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .distinct()
                .count();

        long totalCount = codes.stream()
                .filter(StringUtils::isNotBlank)
                .count();

        boolean isValid = uniqueCount == totalCount;
        log.debug("Validación códigos únicos: {} únicos de {} totales, válido: {}",
                uniqueCount, totalCount, isValid);
        return isValid;
    }
}
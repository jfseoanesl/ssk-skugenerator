package com.skugenerator.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Utilidades para manejo de fechas y tiempo en la aplicación SKU Generator.
 * Proporciona métodos centralizados para formateo, conversiones y cálculos de fechas.
 */
@Slf4j
public final class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("Esta es una clase de utilidades y no puede ser instanciada");
    }

    // Zona horaria por defecto del sistema
    private static final ZoneId DEFAULT_ZONE = ZoneId.of(Constants.DateTime.DEFAULT_TIMEZONE);

    // Locale por defecto (Colombia)
    private static final Locale DEFAULT_LOCALE = new Locale("es", "CO");

    // Formatters predefinidos para mejor performance
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DateTime.DEFAULT_DATE_FORMAT);

    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DateTime.DEFAULT_DATETIME_FORMAT);

    private static final DateTimeFormatter FILE_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DateTime.FILE_DATETIME_FORMAT);

    private static final DateTimeFormatter API_DATE_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DateTime.API_DATE_FORMAT);

    private static final DateTimeFormatter API_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DateTime.API_DATETIME_FORMAT);

    // ==========================================
    // OBTENCIÓN DE FECHAS ACTUALES
    // ==========================================

    /**
     * Obtiene la fecha y hora actual en la zona horaria del sistema.
     * @return LocalDateTime actual
     */
    public static LocalDateTime now() {
        LocalDateTime now = LocalDateTime.now(DEFAULT_ZONE);
        log.debug("Fecha/hora actual: {}", now);
        return now;
    }

    /**
     * Obtiene la fecha actual (sin hora) en la zona horaria del sistema.
     * @return LocalDate actual
     */
    public static LocalDate today() {
        LocalDate today = LocalDate.now(DEFAULT_ZONE);
        log.debug("Fecha actual: {}", today);
        return today;
    }

    /**
     * Obtiene la hora actual (sin fecha) en la zona horaria del sistema.
     * @return LocalTime actual
     */
    public static LocalTime currentTime() {
        LocalTime time = LocalTime.now(DEFAULT_ZONE);
        log.debug("Hora actual: {}", time);
        return time;
    }

    /**
     * Obtiene un timestamp en milisegundos para la fecha/hora actual.
     * @return Timestamp en milisegundos
     */
    public static long currentTimestamp() {
        long timestamp = Instant.now().toEpochMilli();
        log.debug("Timestamp actual: {}", timestamp);
        return timestamp;
    }

    // ==========================================
    // FORMATEO DE FECHAS
    // ==========================================

    /**
     * Formatea una fecha usando el formato por defecto (yyyy-MM-dd).
     * @param date Fecha a formatear
     * @return String formateado o null si la fecha es null
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            log.debug("Fecha nula para formatear");
            return null;
        }

        String formatted = date.format(DEFAULT_DATE_FORMATTER);
        log.debug("Fecha formateada: {} -> {}", date, formatted);
        return formatted;
    }

    /**
     * Formatea una fecha y hora usando el formato por defecto (yyyy-MM-dd HH:mm:ss).
     * @param dateTime Fecha y hora a formatear
     * @return String formateado o null si la fecha es null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.debug("Fecha/hora nula para formatear");
            return null;
        }

        String formatted = dateTime.format(DEFAULT_DATETIME_FORMATTER);
        log.debug("Fecha/hora formateada: {} -> {}", dateTime, formatted);
        return formatted;
    }

    /**
     * Formatea una fecha y hora para uso en nombres de archivos (yyyyMMdd_HHmmss).
     * @param dateTime Fecha y hora a formatear
     * @return String formateado para archivos
     */
    public static String formatForFilename(LocalDateTime dateTime) {
        if (dateTime == null) {
            dateTime = now();
        }

        String formatted = dateTime.format(FILE_DATETIME_FORMATTER);
        log.debug("Fecha/hora para archivo: {} -> {}", dateTime, formatted);
        return formatted;
    }

    /**
     * Formatea una fecha para APIs (yyyy-MM-dd).
     * @param date Fecha a formatear
     * @return String formateado para API
     */
    public static String formatDateForApi(LocalDate date) {
        if (date == null) {
            return null;
        }

        String formatted = date.format(API_DATE_FORMATTER);
        log.debug("Fecha para API: {} -> {}", date, formatted);
        return formatted;
    }

    /**
     * Formatea una fecha y hora para APIs (yyyy-MM-dd'T'HH:mm:ss.SSS'Z').
     * @param dateTime Fecha y hora a formatear
     * @return String formateado para API
     */
    public static String formatDateTimeForApi(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        String formatted = dateTime.atZone(DEFAULT_ZONE).format(API_DATETIME_FORMATTER);
        log.debug("Fecha/hora para API: {} -> {}", dateTime, formatted);
        return formatted;
    }

    /**
     * Formatea una fecha con un patrón personalizado.
     * @param date Fecha a formatear
     * @param pattern Patrón de formato
     * @return String formateado o null si hay error
     */
    public static String formatWithPattern(LocalDate date, String pattern) {
        if (date == null || StringUtils.isBlank(pattern)) {
            log.debug("Fecha nula o patrón vacío para formateo personalizado");
            return null;
        }

        try {
            DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCALE);
            String formattedResult = date.format(customFormatter);
            log.debug("Fecha con patrón personalizado: {} con '{}' -> {}", date, pattern, formattedResult);
            return formattedResult;
        } catch (Exception e) {
            log.error("Error al formatear fecha {} con patrón '{}': {}", date, pattern, e.getMessage());
            return null;
        }
    }

    // ==========================================
    // PARSEO DE FECHAS
    // ==========================================

    /**
     * Parsea una cadena de fecha usando el formato por defecto (yyyy-MM-dd).
     * @param dateString Cadena de fecha a parsear
     * @return LocalDate parseado o null si hay error
     */
    public static LocalDate parseDate(String dateString) {
        if (StringUtils.isBlank(dateString)) {
            log.debug("Cadena de fecha vacía para parsear");
            return null;
        }

        try {
            LocalDate parsed = LocalDate.parse(dateString.trim(), DEFAULT_DATE_FORMATTER);
            log.debug("Fecha parseada: '{}' -> {}", dateString, parsed);
            return parsed;
        } catch (DateTimeParseException e) {
            log.error("Error al parsear fecha '{}': {}", dateString, e.getMessage());
            return null;
        }
    }

    /**
     * Parsea una cadena de fecha y hora usando el formato por defecto.
     * @param dateTimeString Cadena de fecha y hora a parsear
     * @return LocalDateTime parseado o null si hay error
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (StringUtils.isBlank(dateTimeString)) {
            log.debug("Cadena de fecha/hora vacía para parsear");
            return null;
        }

        try {
            LocalDateTime parsed = LocalDateTime.parse(dateTimeString.trim(), DEFAULT_DATETIME_FORMATTER);
            log.debug("Fecha/hora parseada: '{}' -> {}", dateTimeString, parsed);
            return parsed;
        } catch (DateTimeParseException e) {
            log.error("Error al parsear fecha/hora '{}': {}", dateTimeString, e.getMessage());
            return null;
        }
    }

    /**
     * Parsea una fecha con un patrón personalizado.
     * @param dateString Cadena de fecha a parsear
     * @param pattern Patrón de formato
     * @return LocalDate parseado o null si hay error
     */
    public static LocalDate parseDateWithPattern(String dateString, String pattern) {
        if (StringUtils.isBlank(dateString) || StringUtils.isBlank(pattern)) {
            log.debug("Cadena de fecha o patrón vacío para parseo personalizado");
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCALE);
            LocalDate parsed = LocalDate.parse(dateString.trim(), formatter);
            log.debug("Fecha parseada con patrón: '{}' con '{}' -> {}", dateString, pattern, parsed);
            return parsed;
        } catch (Exception e) {
            log.error("Error al parsear fecha '{}' con patrón '{}': {}", dateString, pattern, e.getMessage());
            return null;
        }
    }

    // ==========================================
    // CÁLCULOS DE DIFERENCIAS
    // ==========================================

    /**
     * Calcula la diferencia en días entre dos fechas.
     * @param startDate Fecha inicial
     * @param endDate Fecha final
     * @return Número de días de diferencia (puede ser negativo)
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            log.debug("Fecha inicial o final nula para cálculo de días");
            return 0;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        log.debug("Días entre {} y {}: {}", startDate, endDate, days);
        return days;
    }

    /**
     * Calcula la diferencia en horas entre dos fechas y horas.
     * @param startDateTime Fecha y hora inicial
     * @param endDateTime Fecha y hora final
     * @return Número de horas de diferencia (puede ser negativo)
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            log.debug("Fecha/hora inicial o final nula para cálculo de horas");
            return 0;
        }

        long hours = ChronoUnit.HOURS.between(startDateTime, endDateTime);
        log.debug("Horas entre {} y {}: {}", startDateTime, endDateTime, hours);
        return hours;
    }

    /**
     * Calcula la diferencia en minutos entre dos fechas y horas.
     * @param startDateTime Fecha y hora inicial
     * @param endDateTime Fecha y hora final
     * @return Número de minutos de diferencia (puede ser negativo)
     */
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            log.debug("Fecha/hora inicial o final nula para cálculo de minutos");
            return 0;
        }

        long minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime);
        log.debug("Minutos entre {} y {}: {}", startDateTime, endDateTime, minutes);
        return minutes;
    }

    /**
     * Calcula la edad en años completos desde una fecha de nacimiento.
     * @param birthDate Fecha de nacimiento
     * @param referenceDate Fecha de referencia (null para usar fecha actual)
     * @return Edad en años completos
     */
    public static int calculateAge(LocalDate birthDate, LocalDate referenceDate) {
        if (birthDate == null) {
            log.debug("Fecha de nacimiento nula para cálculo de edad");
            return 0;
        }

        if (referenceDate == null) {
            referenceDate = today();
        }

        int age = Period.between(birthDate, referenceDate).getYears();
        log.debug("Edad calculada desde {} hasta {}: {} años", birthDate, referenceDate, age);
        return age;
    }

    /**
     * Calcula la edad en meses completos desde una fecha de nacimiento.
     * @param birthDate Fecha de nacimiento
     * @param referenceDate Fecha de referencia (null para usar fecha actual)
     * @return Edad en meses completos
     */
    public static int calculateAgeInMonths(LocalDate birthDate, LocalDate referenceDate) {
        if (birthDate == null) {
            log.debug("Fecha de nacimiento nula para cálculo de edad en meses");
            return 0;
        }

        if (referenceDate == null) {
            referenceDate = today();
        }

        Period period = Period.between(birthDate, referenceDate);
        int ageInMonths = period.getYears() * 12 + period.getMonths();
        log.debug("Edad en meses desde {} hasta {}: {} meses", birthDate, referenceDate, ageInMonths);
        return ageInMonths;
    }

    // ==========================================
    // OPERACIONES DE FECHA
    // ==========================================

    /**
     * Agrega días a una fecha.
     * @param date Fecha base
     * @param days Días a agregar (puede ser negativo)
     * @return Nueva fecha o null si la fecha base es null
     */
    public static LocalDate addDays(LocalDate date, long days) {
        if (date == null) {
            log.debug("Fecha nula para agregar días");
            return null;
        }

        LocalDate result = date.plusDays(days);
        log.debug("Agregando {} días a {}: {}", days, date, result);
        return result;
    }

    /**
     * Agrega meses a una fecha.
     * @param date Fecha base
     * @param months Meses a agregar (puede ser negativo)
     * @return Nueva fecha o null si la fecha base es null
     */
    public static LocalDate addMonths(LocalDate date, long months) {
        if (date == null) {
            log.debug("Fecha nula para agregar meses");
            return null;
        }

        LocalDate result = date.plusMonths(months);
        log.debug("Agregando {} meses a {}: {}", months, date, result);
        return result;
    }

    /**
     * Agrega años a una fecha.
     * @param date Fecha base
     * @param years Años a agregar (puede ser negativo)
     * @return Nueva fecha o null si la fecha base es null
     */
    public static LocalDate addYears(LocalDate date, long years) {
        if (date == null) {
            log.debug("Fecha nula para agregar años");
            return null;
        }

        LocalDate result = date.plusYears(years);
        log.debug("Agregando {} años a {}: {}", years, date, result);
        return result;
    }

    /**
     * Obtiene el primer día del mes para una fecha dada.
     * @param date Fecha de referencia
     * @return Primer día del mes o null si la fecha es null
     */
    public static LocalDate startOfMonth(LocalDate date) {
        if (date == null) {
            log.debug("Fecha nula para obtener inicio de mes");
            return null;
        }

        LocalDate result = date.withDayOfMonth(1);
        log.debug("Inicio de mes para {}: {}", date, result);
        return result;
    }

    /**
     * Obtiene el último día del mes para una fecha dada.
     * @param date Fecha de referencia
     * @return Último día del mes o null si la fecha es null
     */
    public static LocalDate endOfMonth(LocalDate date) {
        if (date == null) {
            log.debug("Fecha nula para obtener fin de mes");
            return null;
        }

        LocalDate result = date.withDayOfMonth(date.lengthOfMonth());
        log.debug("Fin de mes para {}: {}", date, result);
        return result;
    }

    /**
     * Obtiene el inicio del día (00:00:00) para una fecha dada.
     * @param date Fecha de referencia
     * @return LocalDateTime al inicio del día o null si la fecha es null
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            log.debug("Fecha nula para obtener inicio de día");
            return null;
        }

        LocalDateTime result = date.atStartOfDay();
        log.debug("Inicio de día para {}: {}", date, result);
        return result;
    }

    /**
     * Obtiene el final del día (23:59:59.999999999) para una fecha dada.
     * @param date Fecha de referencia
     * @return LocalDateTime al final del día o null si la fecha es null
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            log.debug("Fecha nula para obtener fin de día");
            return null;
        }

        LocalDateTime result = date.atTime(LocalTime.MAX);
        log.debug("Fin de día para {}: {}", date, result);
        return result;
    }

    // ==========================================
    // UTILIDADES PARA TEMPORADAS
    // ==========================================

    /**
     * Determina si una fecha está dentro de un rango de meses.
     * Maneja rangos que cruzan años (ej: Noviembre a Febrero).
     * @param date Fecha a verificar
     * @param startMonth Mes de inicio (1-12)
     * @param endMonth Mes de fin (1-12)
     * @return true si la fecha está en el rango, false en caso contrario
     */
    public static boolean isDateInMonthRange(LocalDate date, int startMonth, int endMonth) {
        if (date == null) {
            log.debug("Fecha nula para verificar rango de meses");
            return false;
        }

        int dateMonth = date.getMonthValue();
        boolean inRange;

        if (startMonth <= endMonth) {
            // Rango normal (ej: Marzo a Agosto)
            inRange = dateMonth >= startMonth && dateMonth <= endMonth;
        } else {
            // Rango que cruza años (ej: Noviembre a Febrero)
            inRange = dateMonth >= startMonth || dateMonth <= endMonth;
        }

        log.debug("Fecha {} (mes {}) en rango [{}, {}]: {}",
                date, dateMonth, startMonth, endMonth, inRange);
        return inRange;
    }

    /**
     * Obtiene el mes actual como número (1-12).
     * @return Número del mes actual
     */
    public static int getCurrentMonth() {
        int month = today().getMonthValue();
        log.debug("Mes actual: {}", month);
        return month;
    }

    /**
     * Obtiene el año actual.
     * @return Año actual
     */
    public static int getCurrentYear() {
        int year = today().getYear();
        log.debug("Año actual: {}", year);
        return year;
    }

    /**
     * Convierte meses a años y meses para representación textual.
     * @param totalMonths Total de meses
     * @return String descriptivo (ej: "2 años 3 meses")
     */
    public static String formatMonthsToAge(int totalMonths) {
        if (totalMonths < 0) {
            log.debug("Meses negativos para formateo: {}", totalMonths);
            return "0 meses";
        }

        if (totalMonths == 0) {
            return "0 meses";
        }

        if (totalMonths < 12) {
            String result = totalMonths + (totalMonths == 1 ? " mes" : " meses");
            log.debug("Formateo de {} meses: {}", totalMonths, result);
            return result;
        }

        int years = totalMonths / 12;
        int months = totalMonths % 12;

        StringBuilder sb = new StringBuilder();
        sb.append(years).append(years == 1 ? " año" : " años");

        if (months > 0) {
            sb.append(" ").append(months).append(months == 1 ? " mes" : " meses");
        }

        String result = sb.toString();
        log.debug("Formateo de {} meses: {}", totalMonths, result);
        return result;
    }

    // ==========================================
    // UTILIDADES DE VALIDACIÓN
    // ==========================================

    /**
     * Verifica si una fecha está en el pasado.
     * @param date Fecha a verificar
     * @return true si está en el pasado, false en caso contrario
     */
    public static boolean isInPast(LocalDate date) {
        if (date == null) {
            return false;
        }

        boolean isPast = date.isBefore(today());
        log.debug("Fecha {} está en el pasado: {}", date, isPast);
        return isPast;
    }

    /**
     * Verifica si una fecha está en el futuro.
     * @param date Fecha a verificar
     * @return true si está en el futuro, false en caso contrario
     */
    public static boolean isInFuture(LocalDate date) {
        if (date == null) {
            return false;
        }

        boolean isFuture = date.isAfter(today());
        log.debug("Fecha {} está en el futuro: {}", date, isFuture);
        return isFuture;
    }

    /**
     * Verifica si una fecha es hoy.
     * @param date Fecha a verificar
     * @return true si es hoy, false en caso contrario
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) {
            return false;
        }

        boolean isToday = date.equals(today());
        log.debug("Fecha {} es hoy: {}", date, isToday);
        return isToday;
    }

    /**
     * Verifica si un año es bisiesto.
     * @param year Año a verificar
     * @return true si es bisiesto, false en caso contrario
     */
    public static boolean isLeapYear(int year) {
        boolean isLeap = Year.of(year).isLeap();
        log.debug("Año {} es bisiesto: {}", year, isLeap);
        return isLeap;
    }

    // ==========================================
    // UTILIDADES PARA TIMESTAMPS
    // ==========================================

    /**
     * Convierte un LocalDateTime a timestamp en milisegundos.
     * @param dateTime Fecha y hora a convertir
     * @return Timestamp en milisegundos o 0 si dateTime es null
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.debug("DateTime nulo para conversión a timestamp");
            return 0;
        }

        long timestamp = dateTime.atZone(DEFAULT_ZONE).toInstant().toEpochMilli();
        log.debug("DateTime {} convertido a timestamp: {}", dateTime, timestamp);
        return timestamp;
    }

    /**
     * Convierte un timestamp en milisegundos a LocalDateTime.
     * @param timestamp Timestamp en milisegundos
     * @return LocalDateTime o null si hay error
     */
    public static LocalDateTime fromTimestamp(long timestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), DEFAULT_ZONE);
            log.debug("Timestamp {} convertido a DateTime: {}", timestamp, dateTime);
            return dateTime;
        } catch (Exception e) {
            log.error("Error al convertir timestamp {}: {}", timestamp, e.getMessage());
            return null;
        }
    }

    // ==========================================
    // UTILIDADES PARA RANGOS DE FECHAS
    // ==========================================

    /**
     * Genera una lista de fechas entre dos fechas (inclusive).
     * @param startDate Fecha inicial
     * @param endDate Fecha final
     * @return Lista de fechas en el rango
     */
    public static List<LocalDate> getDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();

        if (startDate == null || endDate == null) {
            log.debug("Fecha inicial o final nula para generar rango");
            return dates;
        }

        if (startDate.isAfter(endDate)) {
            log.debug("Fecha inicial {} es posterior a fecha final {}", startDate, endDate);
            return dates;
        }

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dates.add(current);
            current = current.plusDays(1);
        }

        log.debug("Generado rango de fechas de {} a {}: {} fechas",
                startDate, endDate, dates.size());
        return dates;
    }

    /**
     * Verifica si dos rangos de fechas se superponen.
     * @param start1 Inicio del primer rango
     * @param end1 Fin del primer rango
     * @param start2 Inicio del segundo rango
     * @param end2 Fin del segundo rango
     * @return true si se superponen, false en caso contrario
     */
    public static boolean dateRangesOverlap(LocalDate start1, LocalDate end1,
                                            LocalDate start2, LocalDate end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            log.debug("Alguna fecha nula para verificar superposición");
            return false;
        }

        boolean overlaps = !start1.isAfter(end2) && !start2.isAfter(end1);
        log.debug("Rangos [{}, {}] y [{}, {}] se superponen: {}",
                start1, end1, start2, end2, overlaps);
        return overlaps;
    }
}

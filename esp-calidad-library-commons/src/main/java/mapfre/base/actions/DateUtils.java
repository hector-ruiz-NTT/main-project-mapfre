package mapfre.base.actions;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    // -------------------- OBTENER FECHA Y HORA ACTUAL --------------------
    /**
     * Obtiene la fecha actual en el formato especificado.
     * @param format Formato de la fecha (por ejemplo, "dd/MM/yyyy").
     * @return Fecha actual en formato de cadena.
     */
    public static String getCurrentDate(String format) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Obtiene la fecha y hora actual en el formato especificado.
     * @param format Formato de la fecha y hora (por ejemplo, "dd/MM/yyyy HH:mm").
     * @return Fecha y hora actual en formato de cadena.
     */
    public static String getCurrentDateTime(String format) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Obtiene la fecha actual como un objeto LocalDate.
     * @return Fecha actual como LocalDate.
     */
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }

    /**
     * Obtiene la fecha y hora actual como un objeto LocalDateTime.
     * @return Fecha y hora actual como LocalDateTime.
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }

    // -------------------- CONVERTIR FECHAS --------------------
    /**
     * Convierte una cadena de fecha en un objeto LocalDate con el formato especificado.
     * @param date Fecha en formato de cadena.
     * @param format Formato de la fecha.
     * @return Objeto LocalDate convertido.
     */
    public static LocalDate stringToLocalDate(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(date, formatter);
    }

    /**
     * Convierte una cadena de fecha y hora en un objeto LocalDateTime con el formato especificado.
     * @param date Fecha y hora en formato de cadena.
     * @param format Formato de la fecha y hora.
     * @return Objeto LocalDateTime convertido.
     */
    public static LocalDateTime stringToLocalDateTime(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(date, formatter);
    }

    /**
     * Convierte un objeto LocalDate en una cadena con el formato especificado.
     * @param date Objeto LocalDate.
     * @param format Formato de la fecha.
     * @return Fecha convertida a cadena.
     */
    public static String localDateToString(LocalDate date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Convierte un objeto LocalDateTime en una cadena con el formato especificado.
     * @param dateTime Objeto LocalDateTime.
     * @param format Formato de la fecha y hora.
     * @return Fecha y hora convertida a cadena.
     */
    public static String localDateTimeToString(LocalDateTime dateTime, String format) {
        return dateTime.format(DateTimeFormatter.ofPattern(format));
    }

    // -------------------- CALCULAR DIFERENCIAS ENTRE FECHAS --------------------
    /**
     * Calcula la diferencia en días entre dos fechas.
     * @param start Fecha inicial.
     * @param end Fecha final.
     * @return Número de días entre las dos fechas.
     */
    public static long getDaysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calcula la diferencia en horas entre dos fechas y horas.
     * @param start Fecha y hora inicial.
     * @param end Fecha y hora final.
     * @return Número de horas entre las dos fechas.
     */
    public static long getHoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Calcula la diferencia en minutos entre dos fechas y horas.
     * @param start Fecha y hora inicial.
     * @param end Fecha y hora final.
     * @return Número de minutos entre las dos fechas.
     */
    public static long getMinutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Calcula la diferencia en segundos entre dos fechas y horas.
     * @param start Fecha y hora inicial.
     * @param end Fecha y hora final.
     * @return Número de segundos entre las dos fechas.
     */
    public static long getSecondsBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.SECONDS.between(start, end);
    }

    // -------------------- MODIFICAR FECHAS --------------------
    /**
     * Agrega días a una fecha dada.
     * @param date Fecha base.
     * @param days Número de días a agregar.
     * @return Nueva fecha con los días agregados.
     */
    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    /**
     * Agrega meses a una fecha dada.
     * @param date Fecha base.
     * @param months Número de meses a agregar.
     * @return Nueva fecha con los meses agregados.
     */
    public static LocalDate addMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }

    /**
     * Agrega años a una fecha dada.
     * @param date Fecha base.
     * @param years Número de años a agregar.
     * @return Nueva fecha con los años agregados.
     */
    public static LocalDate addYears(LocalDate date, int years) {
        return date.plusYears(years);
    }

    /**
     * Resta días a una fecha dada.
     * @param date Fecha base.
     * @param days Número de días a restar.
     * @return Nueva fecha con los días restados.
     */
    public static LocalDate subtractDays(LocalDate date, int days) {
        return date.minusDays(days);
    }

    /**
     * Resta meses a una fecha dada.
     * @param date Fecha base.
     * @param months Número de meses a restar.
     * @return Nueva fecha con los meses restados.
     */
    public static LocalDate subtractMonths(LocalDate date, int months) {
        return date.minusMonths(months);
    }

    /**
     * Resta años a una fecha dada.
     * @param date Fecha base.
     * @param years Número de años a restar.
     * @return Nueva fecha con los años restados.
     */
    public static LocalDate subtractYears(LocalDate date, int years) {
        return date.minusYears(years);
    }

    // -------------------- VALIDACIONES DE FECHA --------------------
    /**
     * Verifica si una fecha dada está en el pasado.
     * @param date Fecha a evaluar.
     * @return true si la fecha es anterior a la actual, false en caso contrario.
     */
    public static boolean isPastDate(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Verifica si una fecha dada está en el futuro.
     * @param date Fecha a evaluar.
     * @return true si la fecha es posterior a la actual, false en caso contrario.
     */
    public static boolean isFutureDate(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }

    /**
     * Verifica si una fecha dada es el día actual.
     * @param date Fecha a evaluar.
     * @return true si la fecha es hoy, false en caso contrario.
     */
    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    // -------------------- PRIMER Y ÚLTIMO DÍA DEL MES --------------------
    /**
     * Obtiene el primer día del mes de una fecha dada.
     * @param date Fecha base.
     * @return Primer día del mes de la fecha proporcionada.
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    /**
     * Obtiene el último día del mes de una fecha dada.
     * @param date Fecha base.
     * @return Último día del mes de la fecha proporcionada.
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    // -------------------- OBTENER DÍA O MES EN TEXTO --------------------
    /**
     * Obtiene el nombre del día de la semana de una fecha dada.
     * @param date Fecha base.
     * @return Nombre del día de la semana.
     */
    public static String getDayOfWeek(LocalDate date) {
        return date.getDayOfWeek().toString();
    }

    /**
     * Obtiene el nombre del mes de una fecha dada.
     * @param date Fecha base.
     * @return Nombre del mes.
     */
    public static String getMonthName(LocalDate date) {
        return date.getMonth().toString();
    }

    // -------------------- OBTENER FECHA EN ZONA HORARIA --------------------
    /**
     * Obtiene la fecha y hora actual en una zona horaria específica.
     * @param zoneId ID de la zona horaria (por ejemplo, "Europe/Madrid").
     * @return Fecha y hora actual en la zona horaria especificada.
     */
    public static LocalDateTime getCurrentDateTimeInZone(String zoneId) {
        return LocalDateTime.now(ZoneId.of(zoneId));
    }

    /**
     * Convierte una fecha y hora a una zona horaria específica.
     * @param dateTime Fecha y hora a convertir.
     * @param zoneId ID de la zona horaria destino.
     * @return Fecha y hora convertida en la nueva zona horaria.
     */
    public static ZonedDateTime convertToTimeZone(LocalDateTime dateTime, String zoneId) {
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of(zoneId));
    }


}
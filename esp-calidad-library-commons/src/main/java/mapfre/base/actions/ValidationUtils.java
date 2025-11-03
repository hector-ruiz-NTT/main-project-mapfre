package mapfre.base.actions;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.regex.Pattern;

public class ValidationUtils {

    // -------------------- VALIDACIONES DE STRINGS --------------------
    /**
     * Verifica si una cadena es nula o está vacía.
     * @param str Cadena a evaluar.
     * @return true si la cadena es nula o vacía, false en caso contrario.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Verifica si una cadena contiene solo letras.
     * @param str Cadena a evaluar.
     * @return true si solo contiene letras, false en caso contrario.
     */
    public static boolean isOnlyLetters(String str) {
        return str != null && str.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+$");
    }

    /**
     * Verifica si una cadena contiene solo números.
     * @param str Cadena a evaluar.
     * @return true si solo contiene números, false en caso contrario.
     */
    public static boolean isOnlyNumbers(String str) {
        return str != null && str.matches("^[0-9]+$");
    }

    /**
     * Verifica si una cadena es alfanumérica.
     * @param str Cadena a evaluar.
     * @return true si es alfanumérica, false en caso contrario.
     */
    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Verifica si un correo electrónico tiene un formato válido.
     * @param email Correo electrónico a evaluar.
     * @return true si el formato es válido, false en caso contrario.
     */
    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && Pattern.matches(regex, email);
    }

    /**
     * Verifica si una URL tiene un formato válido.
     * @param url URL a evaluar.
     * @return true si el formato es válido, false en caso contrario.
     */
    public static boolean isValidURL(String url) {
        String regex = "^(https?|ftp)://[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+(/\\S*)?$";
        return url != null && Pattern.matches(regex, url);
    }

    /**
     * Verifica si una cadena tiene una longitud mínima.
     * @param str Cadena a evaluar.
     * @param length Longitud mínima requerida.
     * @return true si cumple la longitud mínima, false en caso contrario.
     */
    public static boolean hasMinLength(String str, int length) {
        return str != null && str.length() >= length;
    }

    /**
     * Verifica si una cadena tiene una longitud máxima.
     * @param str Cadena a evaluar.
     * @param length Longitud máxima permitida.
     * @return true si cumple la longitud máxima, false en caso contrario.
     */
    public static boolean hasMaxLength(String str, int length) {
        return str != null && str.length() <= length;
    }

    // -------------------- VALIDACIONES DE NÚMEROS --------------------
    /**
     * Verifica si un número entero es positivo.
     * @param number Número a evaluar.
     * @return true si es positivo, false en caso contrario.
     */
    public static boolean isPositive(int number) {
        return number > 0;
    }

    /**
     * Verifica si un número entero es negativo.
     * @param number Número a evaluar.
     * @return true si es negativo, false en caso contrario.
     */
    public static boolean isNegative(int number) {
        return number < 0;
    }

    /**
     * Verifica si un número decimal es positivo.
     * @param number Número a evaluar.
     * @return true si es positivo, false en caso contrario.
     */
    public static boolean isPositive(double number) {
        return number > 0;
    }

    /**
     * Verifica si un número decimal es negativo.
     * @param number Número a evaluar.
     * @return true si es negativo, false en caso contrario.
     */
    public static boolean isNegative(double number) {
        return number < 0;
    }

    /**
     * Verifica si un número entero está dentro de un rango especificado.
     * @param number Número a evaluar.
     * @param min Valor mínimo del rango.
     * @param max Valor máximo del rango.
     * @return true si está dentro del rango, false en caso contrario.
     */
    public static boolean isInRange(int number, int min, int max) {
        return number >= min && number <= max;
    }

    /**
     * Verifica si un número decimal está dentro de un rango especificado.
     * @param number Número a evaluar.
     * @param min Valor mínimo del rango.
     * @param max Valor máximo del rango.
     * @return true si está dentro del rango, false en caso contrario.
     */
    public static boolean isInRange(double number, double min, double max) {
        return number >= min && number <= max;
    }

    // -------------------- VALIDACIONES DE FECHAS --------------------
    /**
     * Verifica si una fecha es válida según un formato específico.
     * @param date Fecha en formato de cadena.
     * @param format Formato de la fecha.
     * @return true si la fecha es válida, false en caso contrario.
     */
    public static boolean isValidDate(String date, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDate.parse(date, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si una fecha es anterior a la fecha actual.
     * @param date Fecha a evaluar.
     * @return true si la fecha es pasada, false en caso contrario.
     */
    public static boolean isPastDate(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Verifica si una fecha es posterior a la fecha actual.
     * @param date Fecha a evaluar.
     * @return true si la fecha es futura, false en caso contrario.
     */
    public static boolean isFutureDate(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }

    /**
     * Verifica si una fecha corresponde al día actual.
     * @param date Fecha a evaluar.
     * @return true si la fecha es hoy, false en caso contrario.
     */
    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    // -------------------- VALIDACIONES DE COLECCIONES --------------------
    /**
     * Verifica si una colección es nula o está vacía.
     * @param collection Colección a evaluar.
     * @return true si la colección es nula o vacía, false en caso contrario.
     */
    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Verifica si una colección tiene un tamaño mínimo.
     * @param collection Colección a evaluar.
     * @param minSize Tamaño mínimo requerido.
     * @return true si la colección cumple con el tamaño mínimo, false en caso contrario.
     */
    public static boolean hasMinSize(Collection<?> collection, int minSize) {
        return collection != null && collection.size() >= minSize;
    }

    /**
     * Verifica si una colección tiene un tamaño máximo.
     * @param collection Colección a evaluar.
     * @param maxSize Tamaño máximo permitido.
     * @return true si la colección cumple con el tamaño máximo, false en caso contrario.
     */
    public static boolean hasMaxSize(Collection<?> collection, int maxSize) {
        return collection != null && collection.size() <= maxSize;
    }

    // -------------------- VALIDACIONES BOOLEANAS --------------------
    /**
     * Verifica si un valor booleano es verdadero.
     * @param value Valor booleano a evaluar.
     * @return true si el valor es verdadero, false en caso contrario.
     */
    public static boolean isTrue(boolean value) {
        return value;
    }

    /**
     * Verifica si un valor booleano es falso.
     * @param value Valor booleano a evaluar.
     * @return true si el valor es falso, false en caso contrario.
     */
    public static boolean isFalse(boolean value) {
        return !value;
    }

// -------------------- VALIDACIONES DE ARCHIVOS --------------------
    /**
     * Verifica si un archivo existe en la ruta especificada.
     * @param filePath Ruta del archivo a verificar.
     * @return true si el archivo existe, false en caso contrario.
     */
    public static boolean fileExists(String filePath) {
        return filePath != null && new File(filePath).exists();
    }

    /**
     * Verifica si una ruta especificada corresponde a un directorio.
     * @param path Ruta a evaluar.
     * @return true si la ruta es un directorio, false en caso contrario.
     */
    public static boolean isDirectory(String path) {
        return path != null && new File(path).isDirectory();
    }

    /**
     * Verifica si un archivo tiene una extensión específica.
     * @param filePath Ruta del archivo a evaluar.
     * @param extension Extensión esperada (sin punto inicial).
     * @return true si el archivo tiene la extensión indicada, false en caso contrario.
     */
    public static boolean hasFileExtension(String filePath, String extension) {
        return filePath != null && filePath.toLowerCase().endsWith("." + extension.toLowerCase());
    }

}
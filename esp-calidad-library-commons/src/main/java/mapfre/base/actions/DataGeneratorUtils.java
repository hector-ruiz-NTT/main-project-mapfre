package mapfre.base.actions;

import java.util.Random;

public class DataGeneratorUtils {

    private static final Random random = new Random();

    // -------------------- GENERACIÓN DE NÚMEROS DE TELÉFONO --------------------
    /**
     * Genera un número de teléfono válido para España.
     * @return Número de teléfono en formato de 9 dígitos.
     */
    public static String generatePhoneNumberSpain() {
        String prefix = "6" + random.nextInt(10); // Empieza con 6 o 7 en España
        String number = String.format("%07d", random.nextInt(10000000));
        return prefix + number;
    }

    // -------------------- GENERACIÓN DE DNI/NIE --------------------
    /**
     * Genera un número de DNI válido en España.
     * @return Número de DNI con letra de control.
     */
    public static String generateDNI() {
        int number = random.nextInt(99999999);
        char letter = calculateDNILetter(number);
        return number + String.valueOf(letter);
    }

    /**
     * Genera un número de NIE válido en España.
     * @return Número de NIE con letra de control.
     */
    public static String generateNIE() {
        char firstLetter = "XYZ".charAt(random.nextInt(3));
        int number = random.nextInt(9999999);
        char letter = calculateDNILetter(number);
        return firstLetter + String.format("%07d", number) + letter;
    }

    /**
     * Calcula la letra de control para un DNI o NIE.
     * @param number Número base del DNI o NIE.
     * @return Letra de control correspondiente.
     */
    private static char calculateDNILetter(int number) {
        String dniLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
        return dniLetters.charAt(number % 23);
    }

    // -------------------- GENERACIÓN DE MATRÍCULAS --------------------
    /**
     * Genera una matrícula de coche en formato moderno (números seguidos de letras).
     * @return Matrícula moderna en formato "NNNNLLL".
     */
    public static String generateCarPlateModern() {
        String letters = generateRandomLetters(3);
        String numbers = String.format("%04d", random.nextInt(10000));
        return numbers + letters;
    }

    /**
     * Genera una matrícula de coche en formato antiguo (provincia seguida de números).
     * @return Matrícula antigua en formato "PLNNNN".
     */
    public static String generateCarPlateOld() {
        String province = generateRandomLetters(1);
        String numbers = String.format("%04d", random.nextInt(10000));
        return province + numbers;
    }

    // -------------------- GENERACIÓN DE TARJETAS DE CRÉDITO --------------------
    /**
     * Genera un número de tarjeta de crédito válido según el tipo especificado.
     * @param type Tipo de tarjeta (Visa, Mastercard, Amex).
     * @return Número de tarjeta de crédito generado.
     */
    public static String generateCreditCardNumber(String type) {
        String prefix;
        switch (type.toLowerCase()) {
            case "visa":
                prefix = "4";
                break;
            case "mastercard":
                prefix = "5" + (1 + random.nextInt(5));
                break;
            case "amex":
                prefix = "34";
                break;
            default:
                prefix = "4"; // Por defecto, genera una Visa
        }
        String number = prefix + String.format("%014d", Math.abs(random.nextLong()) % 100000000000000L);
        return number;
    }

    // -------------------- GENERACIÓN DE IBAN --------------------
    /**
     * Genera un número de IBAN español aleatorio.
     * @return IBAN en formato "ESXXBBBBCCCCAAAAAAAAAA".
     */
    public static String generateIBAN() {
        String countryCode = "ES";
        String controlDigits = String.format("%02d", random.nextInt(100));
        String bankCode = String.format("%04d", random.nextInt(10000));
        String branchCode = String.format("%04d", random.nextInt(10000));
        String accountNumber = String.format("%010d", random.nextInt(1000000000));
        return countryCode + controlDigits + bankCode + branchCode + accountNumber;
    }

    // -------------------- GENERACIÓN DE PASAPORTE --------------------
    /**
     * Genera un número de pasaporte aleatorio con código de país.
     * @return Número de pasaporte en formato "XXXNNNNNNNN".
     */
    public static String generatePassport() {
        String countryCode = generateRandomLetters(3).toUpperCase();
        String number = String.format("%08d", random.nextInt(100000000));
        return countryCode + number;
    }

    // -------------------- GENERACIÓN DE CÓDIGOS POSTALES --------------------
    /**
     * Genera un código postal español aleatorio dentro del rango válido.
     * @return Código postal en formato "NNNNN".
     */
    public static String generatePostalCode() {
        return String.format("%05d", random.nextInt(52999) + 1000);
    }

    // -------------------- MÉTODOS AUXILIARES --------------------
    /**
     * Genera una cadena de letras aleatorias.
     * @param count Número de letras a generar.
     * @return Cadena de letras generada.
     */
    private static String generateRandomLetters(int count) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

}
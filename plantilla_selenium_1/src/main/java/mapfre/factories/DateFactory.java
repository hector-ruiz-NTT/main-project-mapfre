package mapfre.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static org.apache.commons.lang3.RandomUtils.nextInt;

public class DateFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateFactory.class);
    private static final Random RAND = new Random();

    public static String getTodayDatePlusDays(int plusDays) {
        LocalDate todayDate = LocalDate.now();
        return localDatePlusDays(todayDate, plusDays);
    }

    private static String localDatePlusDays(LocalDate localDate, int plusDays) {
        localDate = localDate.plusDays(plusDays);
        String date = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LOGGER.debug("fecha='{}'", date);
        return date;
    }

    private static String localDateMinusDays(LocalDate localDate, int minusDays) {
        localDate = localDate.minusDays(minusDays);
        String date = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LOGGER.debug("fecha='{}'", date);
        return date;
    }


    public static String getTodayDatePlusMonths(int plusMonths) {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.plusMonths(plusMonths);
        String date = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LOGGER.debug("fecha='{}'", date);
        return date;
    }

    private static String localDatePlusMonths(LocalDate localDate, int plusMonths) {
        localDate = localDate.plusMonths(plusMonths);
        String date = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LOGGER.debug("fecha='{}'", date);
        return date;
    }

    private static String localDateMinusMonths(LocalDate localDate, int minusMonths) {
        localDate = localDate.minusMonths(minusMonths);
        String date = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LOGGER.debug("fecha='{}'", date);
        return date;
    }

    public static String actualDatePlusYears(int plusYears) {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.plusYears(plusYears);
        String date = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LOGGER.debug("fecha='{}'", date);
        return date;
    }

    public static String stringDatePlusYears(String date, int plusYears) {
        LocalDate localDate = stringDateToLocalDate(date);
        localDate = localDate.plusYears(plusYears);
        date = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LOGGER.debug("fecha='{}'", date);
        return date;
    }


    public static LocalDate stringDateToLocalDate(String date) {
        String[] dateArray = date.split("/");
        return LocalDate.of(
                Integer.parseInt(dateArray[2]),//yyyy
                Integer.parseInt(dateArray[1]),//MM
                Integer.parseInt(dateArray[0])//dd
        );
    }

    public static String stringDatePlusDays(String date, int plusDays) {
        LocalDate localDate = stringDateToLocalDate(date);
        return localDatePlusDays(localDate, plusDays);
    }

    public static String stringDateMinusDays(String date, int minusDays) {
        LocalDate localDate = stringDateToLocalDate(date);
        return localDateMinusDays(localDate, minusDays);
    }

    public static String stringDatePlusMonths(String date, int plusMonths) {
        LocalDate localDate = stringDateToLocalDate(date);
        return localDatePlusMonths(localDate, plusMonths);
    }

    public static String stringDateMinusMonths(String date, int minusMonths) {
        LocalDate localDate = stringDateToLocalDate(date);
        return localDateMinusMonths(localDate, minusMonths);
    }

    public static String getRandomAdultBirthDate() {
        int plusDays = (RAND.nextInt(365 * (69 - 18)) + (365 * 18)) * -1;
        LOGGER.debug("PlusDays='{}'", plusDays);
        String randomAdultBirthDate = DateFactory.getTodayDatePlusDays(plusDays);
        LOGGER.debug("RandomAdultBirthDate='{}'", randomAdultBirthDate);
        return randomAdultBirthDate;
    }

    public static String getRandomPlus26AdultBirthDate() {
        int plusDays = (RAND.nextInt(365 * (69 - 26)) + (365 * 26)) * -1;
        LOGGER.debug("PlusDays='{}'", plusDays);
        String randomAdultBirthDate = DateFactory.getTodayDatePlusDays(plusDays);
        LOGGER.debug("RandomAdultBirthDate='{}'", randomAdultBirthDate);
        return randomAdultBirthDate;
    }


    public static String getRandomMinorBirthDate() {
        int plusDays = (RAND.nextInt(365 * 17) + 1) * -1;
        LOGGER.debug("PlusDays='{}'", plusDays);
        String randomMinorBirthDate = DateFactory.getTodayDatePlusDays(plusDays);
        LOGGER.debug("RandomMinorBirthDate='{}'", randomMinorBirthDate);
        return randomMinorBirthDate;
    }

    public static String plusDayWithoutWeekend(int i) {
        LocalDate startDate = LocalDate.now();
        if (i < 0) {
            startDate = startDate.minusDays(Math.abs(i));
        } else if (i > 0) {
            startDate = startDate.plusDays(i);
        }
        if (startDate.getDayOfWeek().name().equals("SATURDAY") ||
                startDate.getDayOfWeek().name().equals("SUNDAY"))
            startDate = startDate.plusDays(2);
        String date = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LOGGER.debug("fecha='" + date + "'" + "El dia de la semana es=" + startDate.getDayOfWeek());
        return date;
    }

    public static String fechaYhora() {
        String hora=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy hh_mm_ss"));
        return hora;
    }
}

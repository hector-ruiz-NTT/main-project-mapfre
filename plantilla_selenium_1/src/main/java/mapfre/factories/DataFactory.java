package mapfre.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class DataFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataFactory.class);
    private static final Random RAND = new Random();


    public static int getRandomInt(int max) {
        int rand = RAND.nextInt(max);
        LOGGER.debug("Num random entre 0 y {}='{}'", max, rand);
        return rand;
    }

    public static String generateCIF() {
        String first = "A";
        String second = "28";
        String third = Integer.toString(nextInt(10000, 99999));
        String chain = second + third;
        char[] characters = chain.toCharArray();
        char[] odd = new char[4];
        char[] even = new char[3];
        int j = 0;
        int k = 0;

        for (int i = 0; i < characters.length; i++) {
            switch (i) {
                case 0:
                case 2:
                case 4:
                case 6:
                    odd[j] = characters[i];
                    j++;
                    break;
                default:
                    even[k] = characters[i];
                    k++;
                    break;
            }
        }

        int evenAddition = 0;
        for (int i = 0; i < even.length; i++) {
            Integer num = Character.getNumericValue(even[i]);
            evenAddition = evenAddition + num;
        }

        int oddAddition = 0;
        for (int i = 0; i < odd.length; i++) {
            int result = 0;
            Integer num = Character.getNumericValue(odd[i]);
            num = num * 2;
            int number = num;
            while (number > 0) {
                result += number % 10;
                number = number / 10;
            }
            oddAddition = oddAddition + result;
        }

        int totalAddition = evenAddition + oddAddition;

        int unit = 0;
        while (totalAddition > 0) {
            unit += totalAddition % 10;
            totalAddition = 0;
        }

        String last = Integer.toString(10 - unit);
        String cif;
        if (last == "10") {
            cif = first + chain + "0";
        } else {
            cif = first + chain + last;
        }
        LOGGER.debug("CIF='" + cif + "'");
        return cif;
    }

    public static String getMatriculaAleatoria() {
        String letras = "DFGBCHWRTYPSJKLZXVNM";
        char letra2 = letras.charAt(nextInt(0, letras.length()));
        char letra3 = letras.charAt(nextInt(0, letras.length()));
        return getNumeroAleatorio(4) + "-" + "K" + letra2 + letra3;

    }
    public static String getMatriculaAleatoriaCiclomotor() {
        String letras = "DFGBCHWRTYPSJKLZXVNM";
        char letra2 = letras.charAt(nextInt(0, letras.length()));
        char letra3 = letras.charAt(nextInt(0, letras.length()));
        return "C-"+getNumeroAleatorio(4) + "-" + "B" + letra2 + letra3;

    }

    public static String getRandomPostalCode() {
        String mad = "2800";
        String rid = Integer.toString(nextInt(1, 10));
        String pc = mad + rid;
        LOGGER.debug("Postal Code='" + pc + "'");
        return pc;
    }

    public static String getCCC() {
        Integer DC1 = 1;

        Random random = new Random();
        List<Integer> numbers = new ArrayList<Integer>();
        StringBuilder CCC = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            numbers.add(random.nextInt(9) + 1);
            CCC.append(numbers.get(i).toString());
        }

        Integer D = (numbers.get(0))
                + (numbers.get(1) * 2)
                + (numbers.get(2) * 4)
                + (numbers.get(3) * 8)
                + (numbers.get(4) * 5)
                + (numbers.get(5) * 10)
                + (numbers.get(6) * 9)
                + (numbers.get(7) * 7)
                + (numbers.get(8) * 3)
                + (numbers.get(9) * 6);

        Integer DC2 = 11 - (D % 11);

        if (DC2 == 10) {
            DC2 = 1;
        }
        if (DC2 == 11) {
            DC2 = 0;
        }

        String DC = DC1.toString() + DC2.toString();

        String accountNumber = "00811461" + DC + CCC;

        LOGGER.debug("Numero de cuenta='" + accountNumber + "'");

        return accountNumber;
    }

    public static boolean random() {
        boolean rand = nextBoolean();
        LOGGER.debug("RandomBoolean='" + rand + "'");
        return rand;
    }

    public static String getNifRandom() {

        String[] wordDNI = {"T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E"};

        Random randomGenerator = new Random();
        int max = 99999999;
        int min = 80000000;
        int numberNif = randomGenerator.nextInt(max - min) + min;
        String nif = numberNif + wordDNI[numberNif % 23];
        LOGGER.debug("RandomNIF='" + nif + "'");
        return nif;
    }

    public static String getNumeroAleatorio(int cifras) {
        String numero = "";
        for (int i = 0; i < cifras; i++) {
            numero = numero + nextInt(0, 10);
        }
        return numero;
    }

    public static String getRandomString() {

        String[] letters = {"A", "E", "I", "O", "U", "Q", "W", "R", "T", "Y", "P", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M"};
        String random = "";

        for (int i = 0; i < nextInt(5, 9); i++) {
            random = random + letters[nextInt(0, letters.length)];
        }
        LOGGER.debug("RandomString='" + random + "'");
        return random;
    }

    public static String getRandomBirthDate() {
        String randomBirthDate = nextInt(1, 25) + "-" + nextInt(1, 12) + "-" + nextInt(1980, 1999);
        LOGGER.debug("RandomBirthDate='" + randomBirthDate + "'");
        return randomBirthDate;
    }

    public static String getRandomPhone() {
        String randomPhone = Integer.toString(nextInt(600000000, 799999999));
        LOGGER.debug("RandomPhone='" + randomPhone + "'");
        return randomPhone;
    }

    public static String getRandomEmail() {
        String randomEmail = getRandomString() + "@gmail.com";
        LOGGER.debug("RandomEmail='" + randomEmail + "'");
        return randomEmail;
    }

    public static String getRandomNumAddress() {
        String randomNumAddress = Integer.toString(nextInt(1, 99));
        LOGGER.debug("RandomNumAddress='" + randomNumAddress + "'");
        return randomNumAddress;
    }


//    private static String generaMatricula() {
//
//        String[] array;
//        array = {'B', 'C',
//                'D', 'F', 'G', 'H', 'J', 'K', 'L',
//                'M', 'N', 'P', 'R', 'S', 'T', 'V',
//                'W', 'X', 'Y', 'Z'};
//
//        String matricula = "";
//
//        for (int i = 0; i < 7; i++) {
//            Random rnd = new Random();
//            int ale = (int) (rnd.nextDouble() * array.length); //Aleatorio para la letraint ale2 = (int)(rnd.nextDouble() * 10); //Aleatorio entre 0-9if (i>3) {
//            matricula += array[ale];
//        } else{
//            matricula += ale2;
//        }
//    }
//
//    return matricula;

//}
}

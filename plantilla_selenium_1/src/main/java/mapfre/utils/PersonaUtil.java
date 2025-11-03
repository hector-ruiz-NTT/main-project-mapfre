package mapfre.utils;

import java.util.Random;

public class PersonaUtil {
    public static String[] CIF_BASE = {"N8376698J", "D27190651", "Q6201508F", "W7781261H", "V02159945", "B76842749",
            "P2198407E", "D86979879", "B35318997", "R1651129G", "U51323822", "W0707548D",
            "C76125665", "C66076712", "W2308199E", "U53485132", "U46081758", "U58090903", "S3140690C", "A39196035"};
    public static String[] NOMBRE_RAZON_BASE = {"mYGON", "EXTRACIONES PROFUNDAS", "DOBLADORES DE ESQUINAS", "PILAS DURADERAS", "TOALLAS ULTRASECANTES", "TALLERES EXQUISITOS"};
    public static String[] TIPOS_SOCIEDADES_BASE = {"S.L.", "S.L.U", " S.A.", "S.A.D", "S.C."};
    public static String[] NOMBRES_BASE = {"Andrea", "David", "Baldomero", "Balduino", "Baldwin", "Baltasar", "Enzo",
            "Bartolo", "Bartolome", "Baruc", "iker", "Candelaria", "Asier", "Canela", "Caridad", "Carina", "Hugo",
            "Caritina", "Carlota", "Carolina", "Roberto", "Raul", "Sara", "Laura", "Beatriz", "Maria", "Juanma", "Ibai",
            "Carlota",};
    public static String[] NOMBRES_BASE_MUJER = {"Andrea", "Candelaria", "Canela", "Susana", "Carina", "Caritina",
            "Carlota", "Carolina", "Lorena", "Sara", "Laura", "Beatriz", "Maria", "Carlota", "Marina"};

    public static String[] APELLIDOS_BASE = {"Gomez", "Guerrero", "Cardenas", "Cardiel", "Cardona", "Burgos",
            "Cariaga", "Carillo", "Carion", "Castillo", "Castorena", "Castro", "Garcia", "Grangenal", "Grano", "Montes",
            "Griego", "Grigalva"};

    public static String[] MATRICULA_BASE = {"1521DLL", "8760BHJ", "7854KBN", "4695GNL"};

    public static String[] DNI_BASE = {"69061233F", "32298317S", "09128771W", "04571963T", "90040655W", "63366947S",
            "97823641H", "73110763L", "87247431J", "17252091K", "40853934T", "69783529B", "15503739Z", "21705578H",
            "51974680Q", "57335127Z", "27399718W", "46857375G"};

    public static String[] CATASTRO_BASE = {"6102039UX9977X6424MI", "4028136FY8596Z1181UH", "8117675JH6364C8804ZI",
            "49164U304420222655PJ", "08154K638353370889BZ"};

    public static String[] NACIMIENTO_BASE = {"27111993", "03091965", "05041965", "13101993", "08091985"};

    private static final char[] LETRAS_NIF = {'T', 'R', 'B', 'L', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J',
            'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};


    public static String generaNif() {
        return generaNif(null);
    }

    public static String generaNif(String seed) {
        if (seed != null) {
            try {
                Integer.parseInt(seed);
            } catch (NumberFormatException ex) {
                return "KO";
            }
        } else {
            seed = "";
        }
        String numeroDNI = String.valueOf(Math.random()).concat(seed);
        String fullDNI = numeroDNI.substring(numeroDNI.length() - 8);

        int dniInt = Integer.valueOf(fullDNI);
        fullDNI = fullDNI + LETRAS_NIF[dniInt % 23];
        return fullDNI;
    }

       public static String generarCifAleatorio() {
        return CIF_BASE[(int) (Math.floor(Math.random() * ((CIF_BASE.length - 1) - 0 + 1) + 0))];
    }

    public static String generarRazonSocialAleatorio() {
        String nombreBase = NOMBRE_RAZON_BASE[(int) (Math.floor(Math.random() * ((NOMBRE_RAZON_BASE.length - 1) - 0 + 1) + 0))];
        String tipoSocidades = TIPOS_SOCIEDADES_BASE[(int) (Math.floor(Math.random() * ((TIPOS_SOCIEDADES_BASE.length - 1) - 0 + 1) + 0))];
        String sociedad = nombreBase + " " + tipoSocidades;
        return sociedad;
    }

    public static String generarNombreAleatorio() {
        return NOMBRES_BASE[(int) (Math.floor(Math.random() * ((NOMBRES_BASE.length - 1) - 0 + 1) + 0))];
    }

    public static String generarApellidoAleatorio() {
        return APELLIDOS_BASE[(int) (Math.floor(Math.random() * ((APELLIDOS_BASE.length - 1) - 0 + 1) + 0))];
    }

    public static String generaMatricula() {
        // Letras vÃ¡lidas para matrÃ­cula
        char[] array = {'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'R', 'S', 'T', 'V', 'W', 'X', 'Y',
                'Z'};

        String matricula = "";

        for (int i = 0; i < 7; i++) {
            Random rnd = new Random();
            int ale = (int) (rnd.nextDouble() * array.length); // Aleatorio para la letra
            int ale2 = (int) (rnd.nextDouble() * 10); // Aleatorio entre 0-9
            if (i > 3) {
                matricula += array[ale];
            } else {
                matricula += ale2;
            }
        }

        return matricula;

    }

    public static String generarFNacimientoAleatorio() {
        return NACIMIENTO_BASE[(int) (Math.floor(Math.random() * ((NACIMIENTO_BASE.length - 1) - 0 + 1) + 0))];
    }

}

package mapfre.utils.functional_utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CsvUtils {
    public static final String DATA_FILES_FOLDER_PATH = System.getProperty("user.dir") + File.separator + "DataFiles";

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtils.class);

    public static void writeCSV(String fileName, boolean append, String... contentArray) {
        BufferedWriter bw = null;

        try {
            File folder = new File(DATA_FILES_FOLDER_PATH);
            if (!folder.exists()) {
                LOGGER.debug("Creando nuevo directorio: \"{}\".", folder.getAbsolutePath());
                folder.mkdir();
            }
            File file = new File(folder.getAbsolutePath() + File.separator + fileName+".csv");
            if (!file.exists()) {
                LOGGER.debug("Creando nuevo archivo: \"{}\".", file.getAbsolutePath());
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file, append);
            bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.ISO_8859_1));

            for (int i = 0; i < contentArray.length; i++) {
                bw.write("" + contentArray[i]);
                if (i != contentArray.length - 1) {
                    bw.write(";");
                }
            }
            if (contentArray.length > 0) {
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //Cierra instancias de FileWriter y BufferedWriter
                if (bw != null) bw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static List<List<String>> readCSVFile(String fileName) {
        List<List<String>> csvList = new ArrayList<>();
        BufferedReader br = null;
        try {
            FileInputStream fis = new FileInputStream(DATA_FILES_FOLDER_PATH + File.separator + fileName+".csv");
            br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.ISO_8859_1));

            br.lines().forEach(line -> {
                String[] lineArray = line.split(";");
                csvList.add(Arrays.asList(lineArray));
            });
        } catch (IOException e) {
            LOGGER.error("Error reading file {}: {}", fileName, e.getMessage());
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.error("Error closing the file {}: {}", fileName, e.getMessage());
                }
            }
        }

        return csvList;


    }

    public static List<List<String>> writeInLineCSVFile(String fileName, String toCheck, String toWrite) {
        List<List<String>> csvList = new ArrayList<>();
        BufferedReader br = null;
        try {
            FileInputStream fis = new FileInputStream(DATA_FILES_FOLDER_PATH + File.separator + fileName);
            br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.ISO_8859_1));


        } catch (IOException e) {
            LOGGER.error("Error reading file {}: {}", fileName, e.getMessage());
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.error("Error closing the file {}: {}", fileName, e.getMessage());
                }
            }
        }

        return csvList;
    }

    public static void reWrite(ArrayList<String> arrayDocs, String fileName) throws IOException {
        //VOLVEMOS A ESCRIBIR LOS DATOS EXCEPTO EL DEL ÍNDICE PASADO POR PARÁMETRO, PORQUE ES EL QUE HEMOS QUEMADO
        String path = System.getProperty("user.dir") + File.separator + "DataFiles" + File.separator + fileName + ".csv";
        LOGGER.debug("ESCRIBIENDO LOS DATOS EXCEPTO EL DEL ÍNDICE PASADO POR PARÁMETRO para el archivo: '" + path + "'");
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(path, false));
        for (int i = 0; i < arrayDocs.size(); i++) {

            bw2.write(arrayDocs.get(i));
            bw2.newLine();
        }
        bw2.close();

    }

    public static void changeCsvText(String fileName, String textToCheck, String textToChange) throws IOException {
        ArrayList<String> arrayDocs = new ArrayList<String>();
        boolean flag=true;
        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "DataFiles" + File.separator + fileName + ".csv"));
        String linea = br.readLine();
        while (linea != null) {
            if (linea.contains(textToCheck)) {
                flag=false;
                arrayDocs.add(linea+textToChange);
            } else {
                arrayDocs.add(linea);
            }
            linea = br.readLine();
        }
        if(flag){
            arrayDocs.add(textToCheck+textToChange);
        }
        reWrite(arrayDocs, fileName);

    }
}

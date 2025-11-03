package mapfre.utils.functional_utils;


//import es.mapfre.nse.utils.constant_strings.ConstantStrings.DataFiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static org.apache.commons.lang3.RandomUtils.nextInt;


public class TxtUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TxtUtils.class);
    public static final String DATA_FILES_FOLDER_PATH = System.getProperty("user.dir") + File.separator + "DataFiles";

    public static void writeResults(String text, String fileName, boolean append) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            File folder = new File(DATA_FILES_FOLDER_PATH);
            if (!folder.exists()) {
                LOGGER.debug("Creando nuevo directorio: \"{}\".", folder.getAbsolutePath());
                folder.mkdir();
            }
            File file = new File(folder.getAbsolutePath() + File.separator + fileName + ".txt");
            if (!file.exists()) {
                LOGGER.debug("Creando nuevo archivo: \"{}\".", file.getAbsolutePath());
                file.createNewFile();
            }

            fw = new FileWriter(file.getAbsoluteFile(), append);
            bw = new BufferedWriter(fw);

            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //Cierra instancias de FileWriter y BufferedWriter
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String readResult(String fileName) throws IOException {
        ArrayList<String> arrayDocs = new ArrayList<String>();
        String path = System.getProperty("user.dir") + File.separator + "DataFiles" + File.separator + fileName + ".txt";
        LOGGER.debug("Leyendo archivo: '" + path + "'");
        BufferedReader br = new BufferedReader(new FileReader(path));
        String linea = br.readLine();
        while (linea != null) {
            arrayDocs.add(linea);
            linea = br.readLine();
        }

        int index = nextInt(0, arrayDocs.size());
        String doc = arrayDocs.get(index);
//        reWrite(arrayDocs, index, fileName);
        LOGGER.debug("Valor leido='" + doc + "'");
        return doc;
    }

    public static void reWrite(String datoABorrar, String fileName) throws IOException {
        //VOLVEMOS A ESCRIBIR LOS DATOS EXCEPTO EL DEL ÍNDICE PASADO POR PARÁMETRO, PORQUE ES EL QUE HEMOS QUEMADO
        ArrayList<String> arrayDocs = new ArrayList<String>();
        String path = System.getProperty("user.dir") + File.separator + "DataFiles" + File.separator + fileName + ".txt";
        LOGGER.debug("Leyendo archivo: '" + path + "'");
        BufferedReader br = new BufferedReader(new FileReader(path));
        String linea = br.readLine();
        while (linea != null) {
            if (!linea.equals(datoABorrar))
                arrayDocs.add(linea);
            linea = br.readLine();
        }

        LOGGER.debug("ESCRIBIENDO LOS DATOS EXCEPTO EL DEL ÍNDICE PASADO POR PARÁMETRO para el archivo: '" + path + "'");
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(path, false));
        for (int i = 0; i < arrayDocs.size(); i++) {
                bw2.write(arrayDocs.get(i));
                bw2.newLine();
        }
        bw2.close();
    }

    public static String ponerPuntosMiles(int numero){
        DecimalFormat formatea = new DecimalFormat("###,###.##");
        return formatea.format(numero);
    }

}

package mapfre.utils.functional_utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;


public class FilesUtils {
    private static Logger log = LogManager.getLogger();
    private JSUtils jsUtils = new JSUtils();

    public boolean ficheroDescargado(String nombreArchivo, String rutaArchivo, int finEspera) throws InterruptedException {
        boolean flag = false;
        int espera = 0;
        File ficheroExcel = new File(rutaArchivo + nombreArchivo);
        while (!ficheroExcel.exists()) {
            Thread.sleep(1000);
            if (ficheroExcel.exists()) {
                flag = true;
                break;
            }
            if (espera == finEspera)
                flag = false;
            espera++;
        }
        return flag;
    }

    public void borrarFichero(String nombreArchivo, String rutaArchivo) {
        File ficheroExcel = new File(rutaArchivo + nombreArchivo);
        if (ficheroExcel.exists()) {
            ficheroExcel.delete();
            // Comprobamos si el fichero se borró
            if (!ficheroExcel.exists())
                log.info("El fichero '" + nombreArchivo + "' ha sido borrado");
            else log.warn("El fichero " + rutaArchivo + nombreArchivo + " no se ha borrado");
        } else {
            log.warn("El fichero " + rutaArchivo + nombreArchivo + " no existe");
        }
    }



}

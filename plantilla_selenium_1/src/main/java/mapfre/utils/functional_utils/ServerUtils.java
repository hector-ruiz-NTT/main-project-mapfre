package mapfre.utils.functional_utils;


import mapfre.Browser;
import mapfre.general.Log;
import mapfre.general.ProjectProperties;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.Assert;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.*;

public class ServerUtils {


    private static String urlRobot;
    private static String puerto="";
    private static String puertoAws="8801";
    private static String urlWindowsUtils;
    private static Properties datosConfig = ProjectProperties.getConfigProperties();
    private static String urlPdf;
    private static String rutapdf = "-home-seluser-Downloads";
    private static String urlScreenShots;
    private static String urlVideo;
    private static String url ;
    public static void CapturaPantalla(String urlScreenShots,String nombre,String ruta) throws IOException {
        url = urlScreenShots+"?dato="+nombre+"&ruta="+ruta;
        System.out.println("Se ha realizado la llamada a "+url);
        llamadaServer(url);
    }

    public ArrayList<String> obtenerUrls(String ipNodo, String nameCP) {
        Map<String, Object> bodyJson = leerFicheroJson();
        List<String> IPsNodos = leerIPsNodos(bodyJson);
        String puertoRobot = "";
        //String IPEjecucion = IPNodo();
        for (int i = 0; i < IPsNodos.size(); i++) {
            if (IPsNodos.get(i).equals(ipNodo)) {
                System.out.println("La ip en ejecución es: " + IPsNodos.get(i));
                puertoRobot = leerPuertosRobot(bodyJson).get(i);
                break;
            }
        }
        //Incializamos un arraylist para listar todas las url de los procesos adhoc
        ArrayList<String> urlsAdHoc = new ArrayList<String>();
        //String urlHub = "http://selenium-220824-085312-grid-6a81cd27396308ce.elb.eu-west-1.amazonaws.com:";
//        String urlHub = Browser.urlaws+":";
        String urlHub;
        //Se filtra para ejecución en local
        if(datosConfig.getProperty("Local").equals("OK")){
            puertoRobot="8888";
            urlHub = Browser.urlawsLocal+":";
        }else{
            urlHub = Browser.urlaws+":";
        }
        System.out.println(nameCP + ": El puerto de escucha de los procesos adhoc es el " + puertoRobot);

        // Url de la clase Robot
        urlRobot=urlHub+puertoRobot+"/robot";
        System.out.println(nameCP + ": la url del robot es: " + urlRobot);
        urlsAdHoc.add(urlRobot);

        // Url de la clase WindowsUtils
        urlWindowsUtils=urlHub+puertoRobot+"/windowsutils";
        System.out.println(nameCP + ": la url de las  utilidades de windows es: " + urlWindowsUtils);
        urlsAdHoc.add(urlWindowsUtils);

        // Url de la clase PDF
        urlPdf=urlHub+puertoRobot+"/pdf";
        System.out.println(nameCP + ": la url del robot de pdf es: " + urlPdf);
        urlsAdHoc.add(urlPdf);

        // Url de la Clase ScreenShot
        urlScreenShots=urlHub+puertoRobot+"/screenshot";
        System.out.println(nameCP + ": la url para la captura de screens es: " + urlScreenShots);
        urlsAdHoc.add(urlScreenShots);

        // Url de la Clase Video
        urlVideo=urlHub+puertoRobot+"/video";
        System.out.println(nameCP + ": la url para la captura de video es: " + urlVideo);
        urlsAdHoc.add(urlVideo);


        return urlsAdHoc;
    }

    private static String llamadaServer(String url) throws IOException {
        String texto = "";

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        int statuscode = response.getStatusLine().getStatusCode();
        if (statuscode == 200) {
            System.out.println("Se ha enviado correctamente la petici�n");
        }

        if (url.contains("pdf")) {
            texto = response.getAllHeaders()[0].toString();
            texto = texto.replace("Variable: ", "");

        }
        else{
            texto = "0000";
        }
        return texto;
    }


    public static void RobotPulsarTAB(String urlRobot) throws IOException {
        url = urlRobot+"?accion=TAB&dato=";
        llamadaServer(url);
    }

    public static void RobotPulsarEnter(String urlRobot) throws IOException {
        url = urlRobot+"?accion=ENTER&dato=";
        llamadaServer(url);
    }
    public static void RobotEscribir(String urlRobot,String dato) throws IOException {
        url = urlRobot+"?accion=TEXTO"+"&dato="+dato;
        System.out.println("La url para escribir es : "+ url);
        llamadaServer(url);
    }

    public static void ComprobarExisteArchivo(String ruta) throws IOException {
        ruta=ruta.replace("\\","*");
        url = urlWindowsUtils+"?accion=FICHERO&dato="+ruta;
        System.out.println(ruta);
        llamadaServer(url);
        
    }
    public static void EmpezarGrabar(String subRuta, String nombreCaso) throws IOException {
        subRuta=subRuta.replace(" ","");
        nombreCaso=nombreCaso.replace(" ","");
        Grabar("GRABAR", subRuta, nombreCaso);
    }

    public static void TerminarGrabar() throws IOException {
        Grabar("PARAR", "", "");
    }

    public static void Grabar(String accion, String subRuta, String nombreCaso) throws IOException {
        String trueurl = url + "/video" + "?accion=" + accion.toUpperCase() + "&subRuta=" + subRuta + "&nombreCaso=" + nombreCaso;
        System.out.println(url);
        llamadaServer(trueurl);
    }

    public String RobotPDF() throws IOException {

       // url = urlPdf;
         url = urlPdf+ "?ruta=" + rutapdf;
        System.out.println("Se ha realizado la llamada a "+url);
        return llamadaServer(url);
    }

    public String PDF() throws IOException {

        return RobotPDF();
    }

    public static String RutaAbsolutaImagen(String ruta) throws IOException {

        return RobotRutaAbsolutaImagen(ruta);
    }

    public static String RobotRutaAbsolutaImagen(String ruta) throws IOException {
        url = urlWindowsUtils+"?accion=RUTAABSOLUTA&dato="+ruta;
        return llamadaServer(url);
    }

    public static Map<String, Object> leerFicheroJson() {
        Gson gson = new Gson();
        String filepath;
        if(datosConfig.getProperty("Jenkins").equals("OK")){
            Log.info("Entra en jenkins ok en obtenerurl");
            filepath = System.getenv("WORKSPACE")+"/response_create.json";
        }else{
            Log.info("Entra en jenkins no en obtenerurl");
            filepath = System.getProperty("user.dir") + "/response_create.json";
        }
        Map<String, Object> body = new HashMap<>();
        try (FileReader reader = new FileReader(filepath)) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> mapDatos = gson.fromJson(reader, type);
            if (mapDatos.containsKey("body")) {
                body = (Map<String, Object>) mapDatos.get("body");
            } else Assert.fail("No se ha encontrado el body del json: ");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    public static List<String> leerPuertosRobot(Map<String, Object> body) {
        List<String> puertosRobot = new ArrayList<>();
        for (Map<String, Object> item : listPorts(body)) {
            Double puerto = (Double) item.get("robot");
            puertosRobot.add(String.valueOf(puerto.intValue()));
        }
        System.out.println(puertosRobot);
        return puertosRobot;
    }
    public static List<String> leerIPsNodos(Map<String, Object> body) {
        List<String> IPsNodos = new ArrayList<>();
        for (Map<String, Object> item : listPorts(body)) {
            IPsNodos.add(String.valueOf(item.get("nodeIp")));
        }
        System.out.println(IPsNodos);
        return IPsNodos;
    }

    public static List<Map<String, Object>> listPorts(Map<String, Object> body) {
        String lista = "listPorts";
        List<Map<String, Object>> listPorts = new ArrayList<>();
        listPorts = (List<Map<String, Object>>) body.get(lista);
        return listPorts;
    }
}

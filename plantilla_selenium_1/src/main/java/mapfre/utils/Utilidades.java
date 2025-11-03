package mapfre.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Utilidades {
    private static DatosConexion datosPrueba = null;

    private static Configuracion configuracion = null;


    public static Configuracion getConfiguracion() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
	if (configuracion == null) {
	    Gson gson = new Gson();
	    configuracion = gson.fromJson(new FileReader("./data/conf.json"), Configuracion.class);
	}
	return configuracion;
    }

    public static DatosConexion getDatosConexion() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
	if (datosPrueba == null) {
	    Gson gson = new Gson();
	    datosPrueba = gson.fromJson(new FileReader("./data/" + getConfiguracion().getEntorno() + "/conexion.json"),
		    DatosConexion.class);
	}
	return datosPrueba;
    }

    public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
	getDatosConexion();
    }


}

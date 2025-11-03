package mapfre.Tests;

import mapfre.general.BaseLocal;
import mapfre.general.Log;
import mapfre.paginas.Ejemplo.EjemploImp;
import mapfre.utils.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;

import static mapfre.utils.DriverManager.getDriver;

public class CP00XX_Ejemplo {

    private EjemploImp ejemploImp;

    @BeforeMethod
    public void setUp() throws Exception {
        DriverManager.start();
        ejemploImp = new EjemploImp();
    }

    @Test
    public void testAñadirPantalonAlCarrito() {
        Log.info(BaseLocal.idCasoPrueba(this.getClass().getSimpleName()) + ": ***** INICIO EJECUCIÓN *****");
        getDriver().get("https://www.zara.com/es/");

        ejemploImp.rechazarCookies();
        ejemploImp.buscarPantalones();

    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
package mapfre.Tests;

import mapfre.general.BaseLocal;
import mapfre.general.Log;
import mapfre.paginas.Login.LoginImp;
import mapfre.utils.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static mapfre.utils.DriverManager.getDriver;

public class CP001_Login {

    private LoginImp loginImp;
    private static final String MAPFRE_BASE_URL = "https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native";
    private static final String USUARIO = "SGO_PRUEBAS1";
    private static final String CONTRASENA = "Mapfre2023";

    @BeforeMethod
    public void setUp() throws Exception {
        Log.info(BaseLocal.idCasoPrueba(this.getClass().getSimpleName()) + ": ***** INICIO EJECUCIÓN *****");
        DriverManager.start();
        loginImp = new LoginImp();
    }

    @Test
    public void testLoginExitoso() {
        Log.info("Paso 1: Navegar a la página de login");
        getDriver().get(MAPFRE_BASE_URL);
        
        Log.info("Paso 2: Realizar login con credenciales válidas");
        loginImp.realizarLogin(USUARIO, CONTRASENA);
        
        Log.info(BaseLocal.idCasoPrueba(this.getClass().getSimpleName()) + ": ***** TEST COMPLETADO EXITOSAMENTE *****");
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }
}

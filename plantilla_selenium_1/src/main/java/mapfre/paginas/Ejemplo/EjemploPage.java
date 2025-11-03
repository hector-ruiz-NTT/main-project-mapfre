package mapfre.paginas.Ejemplo;

public class EjemploPage {

    private final String popUpCoockies = "//*[contains(text(), 'Rechazar')]";

    private final String boxSearch = "//*[text()='Buscar']";

    // Modal de región inicial (Continuar en España / Ir a Perú)
    private final String modalContinuarEspana = "//button[contains(., 'continuar en España')]";
    private final String modalIrAPeru = "//button[contains(., 'Perú') or contains(., 'Peru')]";

    // Botón alternativo para cerrar cookies si no existe "Rechazar" (p.ej. "Cerrar")
    private final String popUpCookiesCerrar = "//button[contains(., 'Cerrar') and ancestor::dialog]";

    public String getPopUpCoockies() {
        return popUpCoockies;
    }

    public String getBoxSearch() {
        return boxSearch;
    }

    public String getModalContinuarEspana() {
        return modalContinuarEspana;
    }

    public String getModalIrAPeru() {
        return modalIrAPeru;
    }

    public String getPopUpCookiesCerrar() {
        return popUpCookiesCerrar;
    }
}
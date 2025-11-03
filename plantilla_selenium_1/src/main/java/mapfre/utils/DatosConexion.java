package mapfre.utils;

public class DatosConexion {

    private String url;
    private Usuario usuarioEmisor;
    private Usuario usuarioSubscriptor;
    private Usuario usuarioSubscriptorCentral;
    private Usuario usuarioCambioClave;
    private Usuario usuarioEmisorBanca;

    public String getUrl() {
        return url;
    }

    public Usuario getUsuarioEmisor() {
        return usuarioEmisor;
    }

    public Usuario getUsuarioSubscriptor() {
        return usuarioSubscriptor;
    }

    public Usuario getUsuarioEmisorBanca() {
        return usuarioEmisorBanca;
    }

    public Usuario getUsuarioSubscriptorCentral() {
        return usuarioSubscriptorCentral;
    }
}
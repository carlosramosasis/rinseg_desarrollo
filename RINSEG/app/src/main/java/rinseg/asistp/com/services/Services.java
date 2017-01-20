package rinseg.asistp.com.services;

/**
 * Created by Carlos Ramos on 20/10/2016.
 * Clase que declara los endpoints.
 */

public class Services {

    private static final String PROTOCOL = "http://";
    //  public static final String IP_PUERTO = "192.168.2.36";
    private static final String IP_PUERTO = "190.81.47.196";
    private static final String PROY = "/rinseg/public/api/";
    private static final String SECURITY = "security/";
    private static final String ROPS = "rops/";

    public static final String URL = PROTOCOL + IP_PUERTO + PROY;
    public static final String URL_SECURITY = PROTOCOL + IP_PUERTO + PROY + SECURITY;
    public static final String URL_ROPS = PROTOCOL + IP_PUERTO + PROY + ROPS;

    //Métodos
    static final String LOGIN = "login";
    static final String LOGOUT = "logout";
    static final String SYNC = "sync";
    static final String ROP_CLOSE = "close";
    static final String ADD_IMAGEN = "add-image";
    static final String ROP_RECOVERY = "find";
    static final String FIX_INCIDENT = "close-incident";

}

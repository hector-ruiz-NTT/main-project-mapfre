package mapfre.general;

import java.util.Base64;

public class PassDecode {
    String passEncode;
    static String passEncodedString;

    public PassDecode(String passEncode) {
        this.passEncode = passEncode;
        byte[] encode = Base64.getDecoder().decode(passEncode.getBytes());
        passEncodedString = new String(encode);
    }

    public static String getPassDecode(){
        return passEncodedString;
    }

}

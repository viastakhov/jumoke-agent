package jumoke;

import java.io.UnsupportedEncodingException;
import java.util.Base64;


class String64 {

    public static String decode(String sText64) {
        byte[] bRes = sText64.getBytes();
        String sRes;

        try {
            sRes = new String(Base64.getDecoder().decode(bRes), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            sRes = new String(Base64.getDecoder().decode(bRes));
            e.printStackTrace();
        }
        return sRes;
    }


    public static String encode(String sText) {
        byte[] bRes = sText.getBytes();
        String sRes64 = Base64.getEncoder().encodeToString(bRes);
        return sRes64;
    }

}

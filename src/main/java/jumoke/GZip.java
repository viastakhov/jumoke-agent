package jumoke;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GZip {

    public static byte[] compress(String str) throws Exception {
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes());
        gzip.close();
        return obj.toByteArray();
    }


    public static String decompress(byte[] buf) throws Exception {
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(buf));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis));
        String outStr = "";
        String line;

        while ((line = bf.readLine()) != null) {
            outStr += line;
        }

        return outStr;
    }
}

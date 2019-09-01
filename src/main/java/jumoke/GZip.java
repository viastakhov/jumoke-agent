package jumoke;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZip {

	public static byte[] compress(String str) throws Exception {	 
		//System.out.println("String length : " + str.length());
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes());
		gzip.close();
		//System.out.println("Output String length : " + obj.size());
		return obj.toByteArray();
	}

	public static String decompress(byte[] buf) throws Exception {
		//System.out.println("Input String length : " + buf.length);
		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(buf));
		BufferedReader bf = new BufferedReader(new InputStreamReader(gis));
		String outStr = "";
		String line;
		while ((line = bf.readLine()) != null) {
			outStr += line;
		}
		//System.out.println("Output String lenght : " + outStr.length());
		return outStr;
	}
	
}

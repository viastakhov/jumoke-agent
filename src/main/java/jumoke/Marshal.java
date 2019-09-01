package jumoke;

import com.thoughtworks.xstream.XStream;


class Marshal {
	static XStream xs = new XStream(new com.thoughtworks.xstream.io.xml.StaxDriver());	

	public static String serialize(Object obj) {
		String xml = xs.toXML(obj);
	    return String64.encode(xml);
	}
	
	public static Object deserialize(String sText64) {
		String xml = String64.decode(sText64); 
		Object obj = xs.fromXML(xml);
	    return obj;
	}
	
	public static String serializeExt(Object obj) throws Exception {
		String serialized = serialize(obj);
		byte[] compressed = GZip.compress(serialized);
		String serializedExt = serialize(compressed);
	    return serializedExt;
	}
	
	
	public static Object deserializeExt(String sText64) throws Exception {
		byte[] deserialized = (byte[]) deserialize(sText64);
		String decompressed = GZip.decompress(deserialized);
		Object deserializedExt = deserialize(decompressed);
	    return deserializedExt;
	}
}

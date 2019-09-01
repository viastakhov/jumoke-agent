package jumoke;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Jdbc java class implementation
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 1.1
 */
public class Jdbc implements IJdbc {

	Jdbc() {

	}
 
	@Override
	public String executeQuery(String connectionString, String sqlStatement) throws ParserConfigurationException, TransformerException {
		Jumoke.log.info(">>");
		String connectionString_ = (String) Marshal.deserialize(connectionString);
		String sqlStatement_ = (String) Marshal.deserialize(sqlStatement);

		DocumentBuilderFactory xmlfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder xmlbuilder = xmlfactory.newDocumentBuilder();
		Document xmldoc = xmlbuilder.newDocument();
		Element xmlResultSet = xmldoc.createElement("Result");
		xmldoc.appendChild(xmlResultSet);

		String result;

		try {
			Connection conn = DriverManager.getConnection(connectionString_);
			Statement sta = conn.createStatement();
			ResultSet rs = sta.executeQuery(sqlStatement_);
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();

			while (rs.next()) {
				Element row = xmldoc.createElement("Row");
				xmlResultSet.appendChild(row);

				for (int i = 1; i <= colCount; i++) {
					String columnName = rsmd.getColumnName(i);
					Object value = rs.getObject(i);
					Element node = xmldoc.createElement(columnName);
					
					if (value == null) {
						value = "";						
					}
					
					node.appendChild(xmldoc.createTextNode(value.toString()));
					row.appendChild(node);
				}
			}

			conn.close();
			rs.close();

		} catch (Exception ex) {
			Jumoke.log.log(Level.SEVERE, ex.toString(), ex);
		}
		result = Marshal.serialize(xmldoc);
		Jumoke.log.info("<< executeQuery (<?>, " + sqlStatement_ + ") -> " + convertoDocumentToString(xmldoc));
		return result;
	}
	
	private String convertoDocumentToString(Document xmldoc) throws TransformerException {
		DOMSource domSource = new DOMSource(xmldoc);
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
	    StringWriter sw = new StringWriter();
	    StreamResult sr = new StreamResult(sw);
	    transformer.transform(domSource, sr);
	    return sw.toString();
	}
	
}

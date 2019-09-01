package jumoke;

import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * JDBC class implementation
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 1.0
 * 
 */
interface IJdbc {

	String executeQuery(String connectionString, String sqlStatement) throws SQLException, ParserConfigurationException, TransformerException;
}

package jumoke;

import java.io.IOException;
import org.sikuli.script.FindFailed;


/**
 * SikuliX interface
 *
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 2.1
 */
public interface ISikuli {
    public String getRegion(String x, String y, String w, String h);
    public String getScreen(String id);

    public String click(String rs, String target) throws FindFailed;
    public String exists(String rs, String target, String timeout) throws Exception;
    public String type(String rs, String target, String text) throws FindFailed;
    public String paste(String rs, String target, String text) throws FindFailed;
    public String wait(String rs, String target, String timeout) throws Exception;

    public String capture(String rs) throws IOException;
}

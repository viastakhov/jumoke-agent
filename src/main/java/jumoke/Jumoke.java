package jumoke;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcException;
import org.sikuli.script.FindFailed;


/**
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 2.2
 *
 */
public class Jumoke {
	private final static String PRODUCT = "Jumoke agent v.2.2";
	public static java.util.logging.Logger log;
	static int port = 80;
	static WebServer server = null;
	static boolean highlightMode = true;
	static int highlightDelay = 1000;
	static boolean isConsoleHandler = false;
	static boolean isFileHandler = false;
		
	public static void main(String[] args) throws XmlRpcException, IOException, FindFailed, ClassNotFoundException {
		int argCount = args.length;
		
		switch (argCount) {
			case 0:
				System.out.println(PRODUCT);
				System.out.println();
				System.out.println("Use command line: java -jar jumoke-agent /P:<port> [/H] [/T:<delay>] [/C]");
				System.out.println("Where:");
				System.out.println("	 /P: Set port number");
				System.out.println("	 /H: Set ON highlight mode");
				System.out.println("	 /T: Set delay in msec for highlight mode");
				System.out.println("	/CH: Enable ConsoleHandler");
				System.out.println("	/FH: Enable FileHandler");
				System.exit(1);
/*			case 1:	
				try {
					port = Integer.parseInt(args[0]);
				}
				catch (NumberFormatException e) {
			        System.err.println("Port " + args[0] + " must be an integer.");
			        System.exit(1);
			    }
				highlightMode = false;
				server = new WebServer(port);
				break;
			case 2:
				try {
					port = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
			        System.err.println("Port " + args[0] + " must be an integer.");
			        System.exit(1);
			    }
				
				if (args[1].toLowerCase().equals("/h")) {
					highlightMode = true;
				}
				
				if (args[1].toLowerCase().contains("/t:")) {
					highlightMode = true;
					highlightDelay = Integer.parseInt(args[1].toLowerCase().replace("/t:", ""));
				}
				
				server = new WebServer(port);
				break;*/
			default:
				for (int i = 0; i < argCount; i++) {
				
				if (args[i].toLowerCase().contains("/p:")) {
					String sPort = args[i].toLowerCase().replace("/p:", "");
					port = Integer.parseInt(sPort);
				}
				
				if (args[i].toLowerCase().contains("/t:")) {
					String sHighlightDelay = args[i].toLowerCase().replace("/t:", "");
					highlightDelay = Integer.parseInt(sHighlightDelay);
				}
				
				if (args[i].toLowerCase().equals("/h")) {
					highlightMode = true;
				}
				
				if (args[i].toLowerCase().equals("/ch")) {
					isConsoleHandler = true;
				}
				
				if (args[i].toLowerCase().equals("/fh")) {
					isFileHandler = true;
				}
		}
				server = new WebServer(port);
				break;
		}
		
		
		log = java.util.logging.Logger.getLogger(Agent.class.getName());
		
		try {
           LogManager.getLogManager().readConfiguration(Agent.class.getResourceAsStream("/jumoke.logging.properties"));
           
            if (isConsoleHandler) {
            	ConsoleHandler ch = new java.util.logging.ConsoleHandler();
                ch.setLevel(Level.ALL);
                log.addHandler(ch);
                log.setUseParentHandlers(false);
            }
           
            if (isFileHandler) {
            	FileHandler fh = new FileHandler();
                fh.setLevel(Level.ALL);
                log.addHandler(fh);
                log.setUseParentHandlers(false);
            }
            
        } catch (Exception e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
            e.printStackTrace();
        }
		
		
		AutoIt au3 = new AutoIt(highlightMode, highlightDelay);
		Sikuli sx = new Sikuli(highlightMode, (int)(Math.floor(highlightDelay/1000)));
		Jdbc dbc = new Jdbc();
		WinApi w32 = new WinApi();
		Agent ag = new Agent(au3, sx); 
			
		server.addHandler("Agent", ag);
		server.addHandler("AutoIT", au3);
		server.addHandler("Sikuli", sx);
		server.addHandler("Jdbc", dbc);
		server.addHandler("WinApi", w32);
		server.start();
				
		System.out.println(PRODUCT + " has been started.");
		System.out.println("Port: " + port);
		System.out.println("Highlight mode: " + (highlightMode ? "ON" : "OFF"));
		System.out.println("Highlight delay (msec): " + highlightDelay);
		System.out.println("ConsoleHandler: " + (isConsoleHandler ? "Enabled" : "Disabled"));
		System.out.println("FileHandler: " + (isFileHandler ? "Enabled" : "Disabled"));
		System.out.println();
		
		while(true) {
			log.info("Heartbeat...");
			try {
				Thread.sleep(3600000);
			} catch (InterruptedException e) {
				log.log(Level.SEVERE, e.toString(), e);
			}
		}
	}
	

}

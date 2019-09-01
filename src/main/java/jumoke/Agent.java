package jumoke;


/**
 * Agent class implementation
 * 
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 1.1
 */
public class Agent  {
	private AutoIt au3;
	private Sikuli sx;
	
	Agent(AutoIt au3, Sikuli sx) {
		this.au3 = au3;	
		this.sx = sx;	
	}
	
	public int handshake(String URL) {
		Jumoke.log.info(">> [" + URL + "]");
		String URL_ = (String) Marshal.deserialize(URL);
		Jumoke.log.info("<< Handshake with [" + URL_ + "] has been established.");
		return 1;
	}
	
	public int getHighlightDelay() {
		Jumoke.log.info(">>");
		int highlightDelay = this.au3.getHighlightDelay(); //sx is excluded due rounding value
		Jumoke.log.info("<< Highlight delay: " + highlightDelay + " msec.");
		return highlightDelay;
	}
		
	public int setHighlightDelay(String highlightDelay) {
		Jumoke.log.info(">>");
		int highlightDelay_ = (int) Marshal.deserialize(highlightDelay);
		this.au3.setHighlightDelay(highlightDelay_);
		this.sx.setHighlightDelay((int)(Math.floor(highlightDelay_/1000)));
		Jumoke.log.info("<< Highlight delay: " + highlightDelay_ + " msec.");
		return highlightDelay_;
	}
	
	public boolean getHighlightMode() {
		Jumoke.log.info(">>");
		boolean highlightMode = this.au3.getHighlightMode() && this.sx.getHighlightMode();
		Jumoke.log.info("<< Highlight mode: " + (highlightMode ? "ON" : "OFF"));
		return highlightMode;
	}
	
	public boolean setHighlightMode(String highlightMode) {
		Jumoke.log.info(">>");
		boolean highlightMode_ = (boolean) Marshal.deserialize(highlightMode);
		this.au3.setHighlightMode(highlightMode_);
		this.sx.setHighlightMode(highlightMode_);
		Jumoke.log.info("<< Highlight mode: " + (highlightMode_ ? "ON" : "OFF"));
		return highlightMode_;
	}

}

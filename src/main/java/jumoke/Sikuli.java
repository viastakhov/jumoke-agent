package jumoke;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.sikuli.script.*;


/**
 * SikuliX java class implementation
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 2.1
 */
public class Sikuli implements ISikuli{
	private boolean highlightMode;
	private int highlightDelay;

	public Sikuli (boolean highlightMode, int highlightDelay) {
		this.highlightMode = highlightMode;
		if (highlightDelay == 0) {
			this.highlightDelay = 1;	
		} else {
			this.highlightDelay = highlightDelay;
		}
	}

	public Sikuli() {
		this(false, 1);
	}

	public int getHighlightDelay() {
		return this.highlightDelay;
	}

	public void setHighlightDelay(int highlightDelay) {
		this.highlightDelay = highlightDelay;	
	}

	public void setHighlightMode(boolean highlightMode) {
		this.highlightMode = highlightMode;
	}

	public boolean getHighlightMode() {
		return this.highlightMode;
	}

	public void highlightControl() {
		//SikuliX debug settings
		org.sikuli.basics.Settings.DebugLogs = this.highlightMode;
		org.sikuli.basics.Settings.Highlight = this.highlightMode;
		org.sikuli.basics.Settings.DefaultHighlightTime = this.highlightDelay;
		org.sikuli.basics.Settings.WaitAfterHighlight = 0;

		//SikuliX OCR settings
		/*org.sikuli.basics.Settings.OcrTextSearch=true;
			org.sikuli.basics.Settings.OcrTextRead=true;
			org.sikuli.basics.Settings.OcrLanguage="ENG";
			TextRecognizer.reset();*/
	}

	public String getRegion(String x, String y, String w, String h) {
		Jumoke.log.info(">>");
		highlightControl();
		int x_ = (int) Marshal.deserialize(x);
		int y_ = (int) Marshal.deserialize(y);
		int w_ = (int) Marshal.deserialize(w);
		int h_ = (int) Marshal.deserialize(h);
		Region reg = Region.create(x_, y_, w_, h_);	
		String result = Marshal.serialize(reg);
		Jumoke.log.info("<< Region (" + x_ + ", " + y_ + ", " + w_ + ", " + h_ + ") -> " + reg.toString());
		return result;
	}

	public String getScreen(String id) {
		Jumoke.log.info(">>");
		highlightControl();
		int id_ = (int) Marshal.deserialize(id);
		Screen scr = new Screen(id_);
		String result = Marshal.serialize(scr);
		Jumoke.log.info("<< Screen (" + id_ + ") -> " + scr.toString());
		return result;
	}

	@Override
	public String capture(String rs) throws IOException {
		Jumoke.log.info(">>");
		Screen scr = (Screen) Marshal.deserialize(rs);
		ScreenImage scrimg = scr.capture();
		BufferedImage bufimg = scrimg.getImage();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufimg, "png", baos);
		byte[] pngBytes = baos.toByteArray();
		String result = Marshal.serialize(pngBytes);
		Jumoke.log.info("<< capture () -> " + bufimg.toString());
		return result;
	}
	
	@Override
	public String click(String rs, String target) throws FindFailed {
		Jumoke.log.info(">>");
		highlightControl();
		Object rs_ = Marshal.deserialize(rs);
		Object target_ = Marshal.deserialize(target);
		int ret = 0;

		if (rs_ instanceof Region) {
			if (target_ instanceof org.sikuli.script.Pattern) {
				ret = ((Region)rs_).click((org.sikuli.script.Pattern)target_);
			} else if (target_ instanceof String) {
				ret = ((Region)rs_).click((String)target_);
			} else if (target_ instanceof Pattern) {
				Pattern ptn = (Pattern) target_;
				org.sikuli.script.Pattern sxptn = new org.sikuli.script.Pattern();
				{
					sxptn.setBImage(ptn.getbImage());
					sxptn.similar(ptn.getSimilarity());
					sxptn.targetOffset(ptn.getOffset().x, ptn.getOffset().y);
				}
				ret = ((Region)rs_).click(sxptn);
			}
		} else {
			if (target_ instanceof org.sikuli.script.Pattern) {
				ret = ((Screen)rs_).click((org.sikuli.script.Pattern)target_);
			} else if (target_ instanceof String) {
				ret = ((Screen)rs_).click((String)target_);
			} else if (target_ instanceof Pattern) {
				Pattern ptn = (Pattern) target_;
				org.sikuli.script.Pattern sxptn = new org.sikuli.script.Pattern();
				{
					sxptn.setBImage(ptn.getbImage());
					sxptn.similar(ptn.getSimilarity());
					sxptn.targetOffset(ptn.getOffset().x, ptn.getOffset().y);
				}
				ret = ((Screen)rs_).click(sxptn);
			}
		}

		String result = Marshal.serialize(ret);
		Jumoke.log.info("<< click (" + target_ + ") -> " + ret);
		return result;
	}

	@Override
	public String exists(String rs, String target, String timeout) throws Exception {
		Jumoke.log.info(">>");
		highlightControl();
		Object rs_ = Marshal.deserialize(rs);
		Object target_ = Marshal.deserialize(target);
		double timeout_ = (double) Marshal.deserialize(timeout);
		Match ret = null;

		if (rs_ instanceof Region) {
			if (target_ instanceof org.sikuli.script.Pattern) {
				ret = ((Region)rs_).exists((org.sikuli.script.Pattern)target_, timeout_);
			} else if (target_ instanceof String) {
				ret = ((Region)rs_).exists((String)target_, timeout_);
			} else if (target_ instanceof Pattern) {
				Pattern ptn = (Pattern) target_;
				org.sikuli.script.Pattern sxptn = new org.sikuli.script.Pattern();
				{
					sxptn.setBImage(ptn.getbImage());
					sxptn.similar(ptn.getSimilarity());
					sxptn.targetOffset(ptn.getOffset().x, ptn.getOffset().y);
				}
				ret = ((Region)rs_).exists(sxptn, timeout_);
			}
		} else {
			if (target_ instanceof org.sikuli.script.Pattern) {
				ret = ((Screen)rs_).exists((org.sikuli.script.Pattern)target_, timeout_);
			} else if (target_ instanceof String) {
				ret = ((Screen)rs_).exists((String)target_, timeout_);
			} else if (target_ instanceof Pattern) {
				Pattern ptn = (Pattern) target_;
				org.sikuli.script.Pattern sxptn = new org.sikuli.script.Pattern();
				{
					sxptn.setBImage(ptn.getbImage());
					sxptn.similar(ptn.getSimilarity());
					sxptn.targetOffset(ptn.getOffset().x, ptn.getOffset().y);
				}
				ret = ((Screen)rs_).exists(sxptn, timeout_);
			}
		}

		String result;

		if (ret == null) {
			result = Marshal.serialize(false); 
		} else {
			result = Marshal.serialize(true); 
		}

		Jumoke.log.info("<< exists (" + target_ + ") -> " + ret);
		ret = null;
		System.gc();
		return result;
	}
	
	@Override
	public String wait(String rs, String target, String timeout) throws Exception {
		Jumoke.log.info(">>");
		highlightControl();
		Object rs_ = Marshal.deserialize(rs);
		Object target_ = Marshal.deserialize(target);
		double timeout_ = (double) Marshal.deserialize(timeout);

		Match ret = null;

		if (rs_ instanceof Region) {
			if (target_ instanceof org.sikuli.script.Pattern) {
				ret = ((Region)rs_).wait((org.sikuli.script.Pattern)target_, timeout_);
			} else if (target_ instanceof String) {
				ret = ((Region)rs_).wait((String)target_, timeout_);
			} else if (target_ instanceof Pattern) {
				Pattern ptn = (Pattern) target_;
				org.sikuli.script.Pattern sxptn = new org.sikuli.script.Pattern();
				{
					sxptn.setBImage(ptn.getbImage());
					sxptn.similar(ptn.getSimilarity());
					sxptn.targetOffset(ptn.getOffset().x, ptn.getOffset().y);
				}
				ret = ((Region)rs_).wait(sxptn, timeout_);
			}
		} else {
			if (target_ instanceof org.sikuli.script.Pattern) {
				ret = ((Screen)rs_).wait((org.sikuli.script.Pattern)target_, timeout_);
			} else if (target_ instanceof String) {
				ret = ((Screen)rs_).wait((String)target_, timeout_);
			} else if (target_ instanceof Pattern) {
				Pattern ptn = (Pattern) target_;
				org.sikuli.script.Pattern sxptn = new org.sikuli.script.Pattern();
				{
					sxptn.setBImage(ptn.getbImage());
					sxptn.similar(ptn.getSimilarity());
					sxptn.targetOffset(ptn.getOffset().x, ptn.getOffset().y);
				}
				ret = ((Screen)rs_).wait(sxptn, timeout_);
			}
		}

		String result;

		if (ret == null) {
			result = Marshal.serialize(false); 
		} else {
			result = Marshal.serialize(true); 
		}

		Jumoke.log.info("<< wait (" + target_ + ") -> " + ret);
		ret = null;
		System.gc();
		return result;
	}

	@Override
	public String paste(String rs, String target, String text) throws FindFailed {
		Jumoke.log.info(">>");
		highlightControl();
		Object rs_ = Marshal.deserialize(rs);
		Object target_ = Marshal.deserialize(target);
		String text_ = (String) Marshal.deserialize(text);

		int ret = 0;

		if (rs_ instanceof Region) {
			if (target_ instanceof org.sikuli.script.Pattern) {
				ret = ((Region)rs_).paste((org.sikuli.script.Pattern)target_, text_);
			} else if (target_ instanceof String) {
				ret = ((Region)rs_).paste((String)target_, text_);
			} else if (target_ instanceof Pattern) {
				Pattern ptn = (Pattern) target_;
				org.sikuli.script.Pattern sxptn = new org.sikuli.script.Pattern();
				{
					sxptn.setBImage(ptn.getbImage());
					sxptn.similar(ptn.getSimilarity());
					sxptn.targetOffset(ptn.getOffset().x, ptn.getOffset().y);
				}
				ret = ((Region)rs_).paste(sxptn, text_);
			}
		} else {
			if (target_ instanceof org.sikuli.script.Pattern) {
				ret = ((Screen)rs_).paste((org.sikuli.script.Pattern)target_, text_);
			} else if (target_ instanceof String) {
				ret = ((Screen)rs_).paste((String)target_, text_);
			} else if (target_ instanceof Pattern) {
				Pattern ptn = (Pattern) target_;
				org.sikuli.script.Pattern sxptn = new org.sikuli.script.Pattern();
				{
					sxptn.setBImage(ptn.getbImage());
					sxptn.similar(ptn.getSimilarity());
					sxptn.targetOffset(ptn.getOffset().x, ptn.getOffset().y);
				}
				ret = ((Screen)rs_).paste(sxptn, text_);
			}
		}

		String result = Marshal.serialize(ret);
		Jumoke.log.info("<< paste (" + target_ + ", " + text_ + ") -> " + ret);
		return result;
	}

	@Override
	public String type(String rs, String target, String text) throws FindFailed {
		Jumoke.log.info(">>");
		highlightControl();
		Object rs_ = Marshal.deserialize(rs);
		Object target_ = Marshal.deserialize(target);
		String text_ = (String) Marshal.deserialize(text);

		int ret = 0;

		if (rs_ instanceof Region) {
			if (target_ instanceof org.sikuli.script.Pattern) {
				ret = ((Region)rs_).type((org.sikuli.script.Pattern)target_, text_);
			} else if (target_ instanceof String) {
				ret = ((Region)rs_).type((String)target_, text_);
			} else if (target_ instanceof Pattern) {
				Pattern ptn = (Pattern) target_;
				org.sikuli.script.Pattern sxptn = new org.sikuli.script.Pattern();
				{
					sxptn.setBImage(ptn.getbImage());
					sxptn.similar(ptn.getSimilarity());
					sxptn.targetOffset(ptn.getOffset().x, ptn.getOffset().y);
				}
				ret = ((Region)rs_).type(sxptn, text_);
			}
		} else {
			if (target_ instanceof org.sikuli.script.Pattern) {
				ret = ((Screen)rs_).type((org.sikuli.script.Pattern)target_, text_);
			} else if (target_ instanceof String) {
				ret = ((Screen)rs_).type((String)target_, text_);
			} else if (target_ instanceof Pattern) {
				Pattern ptn = (Pattern) target_;
				org.sikuli.script.Pattern sxptn = new org.sikuli.script.Pattern();
				{
					sxptn.setBImage(ptn.getbImage());
					sxptn.similar(ptn.getSimilarity());
					sxptn.targetOffset(ptn.getOffset().x, ptn.getOffset().y);
				}
				ret = ((Screen)rs_).type(sxptn, text_);
			}
		}

		String result = Marshal.serialize(ret);
		Jumoke.log.info("<< type (" + target_ + ", " + text_ + ") -> " + ret);
		return result;
	}
}

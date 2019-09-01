package jumoke;

import java.awt.image.BufferedImage;


public class Pattern {
	private String imgpath = null;
	private BufferedImage bImage = null;
	private float similarity = 0.95f;
	private Location offset = new Location(0, 0);
	
	public String getImgpath() {
		return imgpath;
	}
	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}
	public BufferedImage getbImage() {
		return bImage;
	}
	public void setbImage(BufferedImage bImage) {
		this.bImage = bImage;
	}
	public float getSimilarity() {
		return similarity;
	}
	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}
	public Location getOffset() {
		return offset;
	}
	public void setOffset(Location offset) {
		this.offset = offset;
	}
}

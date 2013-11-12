package ch.unibe.sport.utils;

public class Dimension {
	public final int width;
	public final int height;
	public boolean filter = true;
	
	public Dimension(int width, int height){
		assert width > 0;
		assert height > 0;
		this.width = width;
		this.height = height;
	}
}

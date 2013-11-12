package ch.unibe.sport.utils;

public class Color {
	public final int r;
	public final int g;
	public final int b;
	public final int a;
	public final int hex;
	
	public Color(int hexColor){
		this.hex = hexColor;
		int[] rgba = hexColorToRgba(this.hex);
		a = rgba[0];
		r = rgba[1];
		g = rgba[2];
		b = rgba[3];
	}
	public Color(int r,int g, int b, int a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.hex = rgbaColorToHex(r,g,b,a);
	}
	/**
	 * rgba[0] - alpha</br>
	 * rgba[1] - red</br>
	 * rgba[2] - green</br>
	 * rgba[3] - blue</br>
	 * @return
	 */
	public static int[] hexColorToRgba(int hex){
		int[] rgba = new int[4];
		int h = hex;
		for (int i = 3; i >= 0 ; i--){
			rgba[i] = h & 255;
			h >>>= 8;
		}
	    return rgba;
	}
	public static int rgbaColorToHex(int r, int g, int b, int a){
		int hex = (r   << (8*3));
		hex += (g << (8*2));
		hex += (b << (8*1));
		hex +=  a;
		return hex;
	}
	public String toString(){
		return "("+r+","+g+","+b+","+a+")";
	}
}

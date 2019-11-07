package model;

public class PathologyROI {
	private int x, y, height, width, index; 
	private boolean isC, isOfInterest; 
	private String fileName; 
	private String classification; 
	private float likelihood; 
	
	PathologyROI(int xCoord, int yCoord, int w, int h, int i) {
		x = xCoord;
		y = yCoord;
		width = w;
		height = h; 
		index = i; 
		isC = false;
		isOfInterest = false;
		fileName = "";
		classification = "X"; 
		
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y; 
	}
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	public int getIndex() {
		return index;
	}
	public boolean _getIsC() {
		return isC;
	}
	public String getFileName() {
		return fileName;
	}
	public boolean _getIsOfInterest() {
		return isOfInterest;
	}
	public String getClassification() {
		return classification; 
	}
	public float getLikelihood() {
		return likelihood; 
	}
	
	public void setX(int xCoord) {
		x = xCoord;
	}
	public void setY(int yCoord) {
		y = yCoord;
	}
	public void setHeight(int h) {
		height = h;
	}
	public void setWidth(int w) {
		width = x;
	}
	public void _setIsC(boolean c) {
		isC = c; 
	}
	public void setFileName(String f) {
		fileName = f; 
	}
	public void _setIsOfInterest(boolean interesting) {
		isOfInterest = interesting; 
	}
	public void setClassification(String c) {
		classification = c;
		System.out.println(c); 
	}
	public void setLikelihood(float l) {
		likelihood = l; 
	}
}

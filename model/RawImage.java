package model;

import java.io.File;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "model.Project")

public class RawImage {
	private ImgStatus status; 
	private int id; 
	private File location;
	private int tiles;  
	
	public RawImage(File l, int i) {
		status = ImgStatus.RAW; 
		id = i; 
		location = l;
	}
	public RawImage() {
		status = ImgStatus.RAW; 
		id = -1; 
		location = null; 
	}
	
	// Getters
	public ImgStatus getStatus() {
		return status; 
	}
	public int getId() {
		return id;
	}
	public File getLocation() {
		return location; 
	}
	public int getTiles() {
		return tiles; 
	}

	
	// Setters
	public void setStatus(ImgStatus s) {
		status = s;
	}
	public void setId(int i) {
		id = i; 
	}
	public void setLocation(File loc) {
		location = loc; 
	}
	public void setTiles(int t) {
		tiles = t; 
	}

	
	
	public void incrementTiles() {
		tiles++; 
	}
}

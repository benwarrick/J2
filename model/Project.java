package model;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

// Define root element of project XML file. 
@XmlRootElement

public class Project { 
	private File projectFolder;
	private String projectName; 
	private ArrayList<RawImage> imageList = new ArrayList<RawImage>();
	public float diagnoseTimeLeft; 
	
	public void Project() {
		
	}
	
	// Getters
	public File getProjectFolder() {
		return projectFolder; 
	}
	public String getProjectName() {
		return projectName; 
	}
	public ArrayList<RawImage> getImageList() {
		return imageList; 
	}
	public RawImage getImageAtIndex(int index) {
		return imageList.get(index); 
	}
	
	// Setters
	@XmlElement
	public void setProjectFolder(File path) {
		projectFolder = path; 
	}
	@XmlElement
	public void setProjectName(String n) {
		// Name truncated to first 30 characters. 
		int maxLength = 30; 
		if (n.length() > maxLength) {
			projectName = n.substring(0, maxLength); 
		}
		else {
			projectName = n; 
		}
	}
	public void setImageList(ArrayList<RawImage> list) {
		imageList = list; 
	}
	public void addImage(RawImage image) {
		imageList.add(image); 
	}
	
	public static Project open(File source) {
		Project newProject = new Project(); 
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Project.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			File XMLFile = source; 
			newProject = (Project) jaxbUnmarshaller.unmarshal(XMLFile);
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
		return newProject; 
	}
	
	public void save() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Project.class); 
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// Format the output. 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			File newFile = new File(this.getProjectFolder() + File.separator + this.getProjectName() + ".proj");
			// Writing to XML file. 
			jaxbMarshaller.marshal(this, newFile);
			// Write to console. 
			jaxbMarshaller.marshal(this, System.out); 
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	public void imageListToTextPane(JTextPane tp) {
		tp.setEditable(true); 
		tp.setText(""); 
		
		for (int i=0; i<this.imageList.size(); i++) {
			if (this.imageList.get(i).getStatus() == ImgStatus.INPROC) {
				appendToPane(tp, "(" + i + ") " + this.imageList.get(i).getLocation().getName() + 
						System.lineSeparator(), Color.green);
			}
		}
		for (int i=0; i<this.imageList.size(); i++) {
			if (this.imageList.get(i).getStatus() == ImgStatus.RAW) {
				appendToPane(tp,  "(" + i + ") " + this.imageList.get(i).getLocation().getName() + 
						System.lineSeparator(), Color.BLUE);
			}
		}
		for (int i=0; i<this.imageList.size(); i++) {
			if (this.imageList.get(i).getStatus() == ImgStatus.NOTGRADED) {
				appendToPane(tp,  "(" + i + ") " + this.imageList.get(i).getLocation().getName() + 
						System.lineSeparator(), Color.ORANGE);
			}
		}
		for (int i=0; i<this.imageList.size(); i++) {
			if (this.imageList.get(i).getStatus() == ImgStatus.COMPLETE) {
				appendToPane(tp,  "(" + i + ") " + this.imageList.get(i).getLocation().getName() + 
						System.lineSeparator(), Color.BLACK);
			}
		}
		tp.setEditable(false);
	}
	
	private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
	
	public int getFreshImageIndex() {
		for (int i=0; i<this.imageList.size(); i++) {
			if (this.imageList.get(i).getStatus() == ImgStatus.RAW) {
				return i; 
			}
		}
		return -1; 
	}
	public int getImageIndexByStatus(ImgStatus s) {
		for (int i=0; i<this.imageList.size(); i++) {
			if (this.imageList.get(i).getStatus() == s) {
				return i; 
			}
		}
		return -1;
	}
	
	public void updateImageStatus(int index, ImgStatus s) {
		this.getImageAtIndex(index).setStatus(s);
	}
	public boolean isAllRaw() {
		int rawImages=0; 
		for (int i=0; i<this.imageList.size(); i++) {
			if (this.imageList.get(i).getStatus().equals(ImgStatus.RAW)) {
				rawImages++; 
				System.out.println(rawImages); 
			}
		}
		System.out.println(this.imageList.size());
		if (rawImages == this.imageList.size()) {
			return true;
		}
		else {
			return false; 
		}
	}
}


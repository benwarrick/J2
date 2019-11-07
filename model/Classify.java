package model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

public class Classify implements Runnable{
	int i; 
	Project project;
	Mat original; 
	int cii;
	PathologyROIList ROIList;
	int[] syncArray; 

	public Classify(int windowIndex, Project project, Mat original, int cii, PathologyROIList ROIList, int[] syncArray) {
		this.i = windowIndex;
		this.project = project;
		this.original = original; 
		this.cii = cii; 
		this.ROIList = ROIList;
		this.syncArray = syncArray; 
	}
	public void run() {
		String parentName = project.getImageAtIndex(cii).getLocation().getName();
			parentName = parentName.substring(0, parentName.lastIndexOf("."));
		String imageName = parentName + "_" + Integer.toString(i) + ".tif";
		String sigName = parentName + "_" + Integer.toString(i) + "_0_0.sig";			

		Path tmpDirectory = Paths.get(System.getProperty("user.dir"), "tmp");
		Path tmpImgName = tmpDirectory.resolve("tmp" + Integer.toString(cii) + "-" + Integer.toString(i) + ".tif");
		Path tmpFFName = tmpDirectory.resolve("tmp" + Integer.toString(cii) + "-" + Integer.toString(i) + "_0_0.sig");
		Path tmpTxtName = tmpDirectory.resolve("tmp" + Integer.toString(cii) + "-" + Integer.toString(i) + ".txt");
		Path tmpFitName = tmpDirectory.resolve("tmp" + Integer.toString(cii) + "-" + Integer.toString(i) + ".fit");
		Path cDirectory = Paths.get(project.getProjectFolder().toString(), "C");
		Path bDirectory = Paths.get(project.getProjectFolder().toString(), "B");
		
		String[] output = new String[2]; 
			
		// Create and save image. 
		Rect ROI = new Rect(ROIList.get(i).getX(), ROIList.get(i).getY(), ROIList.get(i).getWidth(), ROIList.get(i).getHeight());				
		Mat ROIimg = original.submat(ROI); 
		Imgcodecs.imwrite(tmpImgName.toString(), ROIimg);
		
		// Create and write the name of the image to a text file for wndchrm.
		try {
			PrintWriter writer = new PrintWriter(tmpTxtName.toString(), "UTF-8");
			writer.println(tmpImgName.toString() + "\t0");
			writer.close(); 
			
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Generate features for tmp image. 
		Wndchrm.featuresForSingleImage(tmpTxtName.toString(), tmpFitName.toString());
		
		// Wndchrm Classify and record to ROI object. 
		output = Wndchrm.classify(tmpImgName.toString(), project);
		ROIList.get(i).setClassification(output[0]);
		float likelihood = Float.parseFloat(output[1]);
		ROIList.get(i).setLikelihood(likelihood);
		//System.out.println("|" + this.get(i).getClassification().toString() + "|");
					
		// Save image and feature file to class folder to prevent from having to re-process. 
		if (ROIList.get(i).getClassification().trim().equals("C")) {			
			try {
				Files.move(tmpImgName, cDirectory.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
				Files.move(tmpFFName, cDirectory.resolve(sigName), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			try {
				Files.move(tmpImgName, bDirectory.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
				Files.move(tmpFFName, bDirectory.resolve(sigName), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		syncArray[i] = 1; 
	}
}

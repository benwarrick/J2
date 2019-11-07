package model;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class PathologyROIList extends ArrayList<PathologyROI> {	
	/*
	 * Creates a list of ROIs by tiling up an image.
	 * Optimized for even division of 2448x1920 image.
	 * 
	 * ToDo: Deal with images not even split by 64. 
	 */
	public static PathologyROIList ROIsFromImage(Mat image) {
		PathologyROIList ROIList= new PathologyROIList();
		int tilesX = 8;
		int tilesY = 8; 
		int windowIndex = 0; 
		int width = image.width() / tilesX; 
		int height = image.height() / tilesY; 
		System.out.println(image.width() + " / " + tilesX + " = " + width); 
		System.out.println(image.height() + " / " + tilesY + " = " + height); 
		for (int y=0; y<image.height(); y+=height) {
			for (int x=0; x<image.width(); x+=width) {
				ROIList.add(new PathologyROI(x, y, width, height, windowIndex++));
			}
		}
		return ROIList; 
	}
	/*
	 * Draw the ROIs.
	 * 
	 * Red: new Scalar(0,0,255,255)
	 * Blue: new Scalar (255,0,0,255)
	 */
	public Mat DrawROIs(Mat image) {
		Mat img = image.clone(); 
		Scalar color = new Scalar (255,255,0,255);
		for (int i=0; i<this.size(); i++) {
			PathologyROI roi = this.get(i); 
			if (this.get(i).getClassification().equals("C")) {
				color = new Scalar(0,0,255,255); 
			}
			else {
				color = new Scalar (255,0,0,255); 
			}
			Imgproc.rectangle(
					img, 
					new Point(this.get(i).getX()+1, this.get(i).getY()+1),
					new Point(this.get(i).getX() + this.get(i).getWidth()-1, this.get(i).getY() + this.get(i).getHeight()-1), 
					color,
					(!this.get(i).getClassification().equals("X")) ? 2 : 1);

			if (this.get(i).getClassification()!=null && this.get(i).getLikelihood()>0) {
				Imgproc.putText(img, this.get(i).getClassification() + ":" + this.get(i).getLikelihood(),
						new Point(this.get(i).getX()+10, this.get(i).getY()+this.get(i).getHeight()-10), 
						2, 1, new Scalar(255,0,0,255));
			}
		}
		
		return img; 
	}
	
	public void getClass(Mat original, Project project, int cii) {
		Path tmpDirectory = Paths.get(System.getProperty("user.dir"), "tmp");
		Path tmpImgName = tmpDirectory.resolve("tmp.tif");
		Path cDirectory = Paths.get(project.getProjectFolder().toString(), "C");
		Path bDirectory = Paths.get(project.getProjectFolder().toString(), "B");
		
		String[] output = new String[2];
		
		// For each window...
				//for (int i=0; i<this.size(); i++) {
				// DEBUG
		for (int i=0; i<4; i++) {
			
			String parentName = project.getImageAtIndex(cii).getLocation().getName();
			parentName = parentName.substring(0, parentName.lastIndexOf("."));
			String imageName = parentName + "_" + Integer.toString(i) + ".tif";
			String sigName = parentName + "_" + Integer.toString(i) + "_0_0.sig";
			
			if (Files.exists(cDirectory.resolve(imageName)) && Files.exists(cDirectory.resolve(sigName))) {
				// Wndchrm Classify and record to ROI object. 
				output = Wndchrm.classify(cDirectory.resolve(imageName).toString(), project);
				this.get(i).setClassification(output[0].trim());
				float likelihood = Float.parseFloat(output[1]);
				this.get(i).setLikelihood(likelihood);
			}
			else if (Files.exists(bDirectory.resolve(imageName)) && Files.exists(bDirectory.resolve(sigName))) {
				// Wndchrm Classify and record to ROI object. 
				output = Wndchrm.classify(bDirectory.resolve(imageName).toString(), project);
				this.get(i).setClassification(output[0].trim());
				float likelihood = Float.parseFloat(output[1]);
				this.get(i).setLikelihood(likelihood);
			}
			else {}
		}		
	}
	
	public void predictClass(Mat original, Project project, int cii) {
		Path tmpDirectory = Paths.get(System.getProperty("user.dir"), "tmp");
		Path tmpImgName = tmpDirectory.resolve("tmp.tif");
		Path cDirectory = Paths.get(project.getProjectFolder().toString(), "C");
		Path bDirectory = Paths.get(project.getProjectFolder().toString(), "B");
		
		String[] output = new String[2];  

		// For each window...
		//for (int i=0; i<this.size(); i++) {
		// DEBUG
		for (int i=0; i<4; i++) {
			
			String parentName = project.getImageAtIndex(cii).getLocation().getName();
			parentName = parentName.substring(0, parentName.lastIndexOf("."));
			String imageName = parentName + "_" + Integer.toString(i) + ".tif";
			String sigName = parentName + "_" + Integer.toString(i) + "_0_0.sig";			

				
			// Create and save image. 
			Rect ROI = new Rect(this.get(i).getX(), this.get(i).getY(), this.get(i).getWidth(), this.get(i).getHeight());				
			Mat ROIimg = original.submat(ROI); 
			Imgcodecs.imwrite(tmpImgName.toString(), ROIimg);
			
			// Create and write the name of the image to a text file for wndchrm.
			try {
				PrintWriter writer = new PrintWriter(tmpDirectory.resolve("tmp.txt").toString(), "UTF-8");
				writer.println(tmpDirectory.resolve("tmp.tif").toString() + "\t0");
				writer.close(); 
				
			} catch (FileNotFoundException | UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// Generate features for tmp image. 
			Wndchrm.featuresForOne();
			
			// Wndchrm Classify and record to ROI object. 
			output = Wndchrm.classify(tmpImgName.toString(), project);
			this.get(i).setClassification(output[0]);
			float likelihood = Float.parseFloat(output[1]);
			this.get(i).setLikelihood(likelihood);
			//System.out.println("|" + this.get(i).getClassification().toString() + "|");
						
			// Save image and feature file to class folder to prevent from having to re-process. 
			if (this.get(i).getClassification().trim().equals("C")) {			
				try {
					Files.move(tmpImgName, cDirectory.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
					Files.move(tmpDirectory.resolve("tmp_0_0.sig"), cDirectory.resolve(sigName), 
								StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				try {
					Files.move(tmpImgName, bDirectory.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
					Files.move(tmpDirectory.resolve("tmp_0_0.sig"), bDirectory.resolve(sigName), 
								StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
			System.out.println(this.get(i).getClassification());
			System.out.println(this.get(i).getLikelihood());
		}

		project.getImageAtIndex(cii).setStatus(ImgStatus.NOTGRADED);
		
	}
	/*
	 * Returns the window index containing the indicated point.
	 */
	public int locate(java.awt.Point coord) {
		int index = -1; 
		int i = 0; 
		while (index == -1) {
			if (coord.x >= this.get(i).getX() & 
				coord.x <= this.get(i).getX()+this.get(i).getWidth() &
				coord.y >= this.get(i).getY() &
				coord.y <= this.get(i).getY()+this.get(i).getHeight()) {
				index = i; 
			}
			else {
				i++; 
			}
		}
		return index;
	}
	/*
	 * Creates images of the windows (ROIs) and saves them to disc. 
	 */
	public int[] saveROIs(Mat original, Project project, int cii) {
		int Ccount = 0; 
		int Bcount = 0; 
		File Cfolder = new File(project.getProjectFolder() + File.separator + "C"); 
		File Bfolder = new File(project.getProjectFolder() + File.separator + "B");
		String currentFileName = project.getImageAtIndex(cii).getLocation().getName();
		currentFileName = currentFileName.substring(0, currentFileName.lastIndexOf("."));
		
		// Make sure directories exist.
		PathUtilities.createFolder(Cfolder); 
		PathUtilities.createFolder(Bfolder);
		
		for (int i=0; i<this.size(); i++) {
			String imageName = currentFileName + "_" + i + ".tif";
			String sigName = currentFileName + "_" + i + "_0_0.sig";
			
			if (this.get(i).getClassification().equals("C")) {
				Ccount++; 
				// Delete the file from opposite folder if it exists. 
				try {
					if (Files.deleteIfExists(Paths.get(Bfolder.getAbsolutePath(), imageName))) {
						System.out.println(Paths.get(Bfolder.getAbsolutePath(), imageName).toString() + " deleted");
					}
					if (Files.deleteIfExists(Paths.get(Bfolder.getAbsolutePath(), sigName))) {
						System.out.println(Paths.get(Bfolder.getAbsolutePath(), sigName).toString() + " deleted");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				PathologyROI po = this.get(i);
				Rect ROI = new Rect(po.getX(), po.getY(), po.getWidth(), po.getHeight());				
				Mat ROIimg = original.submat(ROI); 
				Imgcodecs.imwrite(Cfolder.getAbsolutePath() + File.separator + imageName, ROIimg);
			}
			else if (this.get(i).getClassification().equals("B")) {
				Bcount++; 
				// Delete the file from opposite folder if it exists. 
				try {
					if (Files.deleteIfExists(Paths.get(Cfolder.getAbsolutePath(), imageName))) {
						System.out.println(Paths.get(Cfolder.getAbsolutePath(), imageName).toString() + " deleted");
					}
					if (Files.deleteIfExists(Paths.get(Cfolder.getAbsolutePath(), sigName))) {
						System.out.println(Paths.get(Cfolder.getAbsolutePath(), sigName).toString() + " deleted");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				PathologyROI po = this.get(i);
				Rect ROI = new Rect(po.getX(), po.getY(), po.getWidth(), po.getHeight());				
				Mat ROIimg = original.submat(ROI); 
				Imgcodecs.imwrite(Bfolder.getAbsolutePath() + File.separator + imageName, ROIimg);
			}
			else {}
		}
		int[] r = {Ccount, Bcount}; 
		return null;
	}
	

}

package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Wndchrm implements Runnable{
	Project project;
	int testImageIndex; 
	static String wndchrm = System.getProperty("user.dir") + File.separator +
			"lib" + File.separator + "wndchrm" + File.separator + "wndchrm.exe";
	
	public Wndchrm(Project p, int imageIndex) {
		project = p;
		testImageIndex = imageIndex; 
	};
		
	public void run() {
		Runtime rt = Runtime.getRuntime(); 
		
		String imageFoldersString = project.getProjectFolder().getAbsolutePath().toString(); 
		String featureFileString = project.getProjectFolder().getAbsolutePath().toString() + 
				File.separator + "features.out";
		
		System.out.println(wndchrm); 
		
		try {
			Process pr = rt.exec(wndchrm + " train -m " +
					imageFoldersString + " " + featureFileString);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = null;
			while ((line=input.readLine()) != null) {
				System.out.println(line);
			}
			int exitVal  = pr.waitFor(); 
			project.updateImageStatus(testImageIndex, ImgStatus.COMPLETE);
			//System.out.println("Exit with errors code " + exitVal);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
	
	/*
	 * Attempts to classify the indicated image. 
	 * Returns array. [classification, likelihood] 
	 */
	public static String[] classify(String img, Project project) {
		String[] output = new String[2]; 
		Runtime rt = Runtime.getRuntime();
		try {
			String command = wndchrm + " classify " +
					project.getProjectFolder() + File.separator + "features.out" + " " + img;
			System.out.println(command); 
			Process pr = rt.exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = null;
			while ((line=input.readLine()) != null) {
				if (line.contains("The resulting class is:")) {
					//System.out.println(line);
					line = line.substring(line.indexOf(":")+2);
					output[0] = line.substring(0, line.indexOf("("));
					output[1] = line.substring(line.indexOf("(")+1, line.indexOf(")")-1);
					//System.out.println(output[0]);
					//System.out.println(output[1]);
				}				
			}
			int exitVal  = pr.waitFor(); 
			
			//project.updateImageStatus(testImageIndex, ImgStatus.COMPLETE);
			//System.out.println("Exit with errors code " + exitVal);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return output; 
	}
	public static void featuresForOne() {
		Runtime rt = Runtime.getRuntime(); 
		Path tmpDirectory = Paths.get(System.getProperty("user.dir"), "tmp");
		String txtFile = tmpDirectory.toString() + File.separator + "tmp.txt";
		String fitFile = tmpDirectory.toString() + File.separator + "tmp.fit";
		
		try {
			Process pr = rt.exec(wndchrm + " train -m " +
					txtFile + " " + fitFile);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = null;
			while ((line=input.readLine()) != null) {
				System.out.println(line);
			}
			int exitVal  = pr.waitFor(); 
			//System.out.println("Exit with errors code " + exitVal);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
	public static void featuresForSingleImage(String txtFile, String fitFile) {
		Runtime rt = Runtime.getRuntime(); 
//		Path tmpDirectory = Paths.get(System.getProperty("user.dir"), "tmp");
//		String txtFile = tmpDirectory.toString() + File.separator + "tmp.txt";
//		String fitFile = tmpDirectory.toString() + File.separator + "tmp.fit";
		
		try {
			Process pr = rt.exec(wndchrm + " train -m " +
					txtFile + " " + fitFile);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = null;
			while ((line=input.readLine()) != null) {
				System.out.println(line);
			}
			int exitVal  = pr.waitFor(); 
			//System.out.println("Exit with errors code " + exitVal);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
}

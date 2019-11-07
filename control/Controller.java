package control;

import view.*;
import model.*;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Arrays; 

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Controller {
	// Number of available cores assumed. 
	private int cores = 4; 
	
	private final PrimaryFrame frame = new PrimaryFrame();
	private NewProjectFrame npFrame = new NewProjectFrame();
	private DiagnoseWarnFrame dwFrame = new DiagnoseWarnFrame(); 
	
	private Project project = null; 
	private PathologyROIList ROIWindows = null;  
	// Current image index. 
	int cii = -1;  
	Mat originalImage = null; 
	Mat tmp = null; 
	int inProc = 0; 
	int procImit = 3; 
	
	// GUI actions
	private final Action newProjectAction = new NewProjectAction();
	private final Action SaveAction = new SaveAction(); 
	private final Action addImagesAction = new AddImagesAction();
	private final Action openProjectAction = new OpenProjectAction();
	//private final Action diagnoseImageAction = new DiagnoseImageAction(); 
	
	public Controller() {
		project = new Project(); 
		// New project frame.   
		npFrame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);
		npFrame.setModal(true);
		
		// Diagnose image frame, no used. 
		//dwFrame.setDefaultCloseOperation(dwFrame.HIDE_ON_CLOSE);
		//dwFrame.setModal(true);
		
		// Get the default images, insert into image pane. 
		BufferedImage img = null; 
		try {
			img = ImageIO.read(new File(System.getProperty("user.dir") + File.separator + "background.jpg"));
			frame.imgPane.setImage(img);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// MOUSE LISTENER
		frame.imgPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point panelPoint = e.getPoint();
                Point imgContext = frame.imgPane.toImageContext(panelPoint);
                
                int index = ROIWindows.locate(imgContext);
                if (ROIWindows.get(index).getClassification().equals("C")) {
                	ROIWindows.get(index).setClassification("X");
                }
                else if (ROIWindows.get(index).getClassification().equals("B")) {
                	ROIWindows.get(index).setClassification("C");
                }
                else {
                	ROIWindows.get(index).setClassification("B");;
                }                
                tmp = ROIWindows.DrawROIs(originalImage);
                frame.imgPane.setImage(PathUtilities.ImageUtils.toBufferedImage(tmp));
                frame.imgPane.repaint();                
                //frame.report.setText("You clicked at " + panelPoint + " which is relative to the image " + imgContext);
            }
        });
		// Attach actions to frame. 
		frame.setNewProjectAction(newProjectAction);
		frame.setSaveAction(SaveAction);
		frame.setAddImages(addImagesAction);
		frame.setOpenProjectAction(openProjectAction);
		//frame.setDiagnoseImageAction(diagnoseImageAction);
		
		// PROCESS BUTTON
		frame.add_processButton_ActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				// First image for this project.
				if (project.getImageAtIndex(cii).getStatus() == ImgStatus.RAW){					
					processImage(); 
				}
				// An ungraded image, having been graded, ready for final processing.  
				else if (project.getImageAtIndex(cii).getStatus() == ImgStatus.NOTGRADED){					
					processImage(); 
				}
				else {
					
				}			
			}
		});
		// This is just a button for debugging, testing things. 
		frame.add_utilityButton_ActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {			 
				System.out.println("cii = " + cii);
			}
		});
		// Button on the New Project frame for browsing file system. 
		npFrame.add_browseButton_ActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = fc.showOpenDialog(npFrame);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					npFrame.projectDirectoryTextField.setText(fc.getSelectedFile().toString());						
				}
				if (npFrame.projectDirectoryTextField.getText().equals("")) {
					npFrame.finishButton.setEnabled(false); 
				}
				else {
					npFrame.finishButton.setEnabled(true);
				}
			}
		});
		// Button no New Project frame to finish, create project. 
		npFrame.add_finishButton_ActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				project.setProjectFolder(new File(npFrame.getProjectDirectory()));
				project.setProjectName(npFrame.getName());
				//System.out.println("From frame: " + npFrame.getName());
				//System.out.println("From project: " + project.getProjectName());
				frame.report.setText("Project directory set to: " + project.getProjectFolder().toString() + 
						"  Add images to project. Project >> Add Images");
				npFrame.setVisible(false);
			}
		});
//		dwFrame.add_btnYes_ActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent evt) {
//				Wndchrm.classify("C:\\tmp\\Test2\\B\\bprostate11-100x0.tif", project);
//			}
//		});
//		dwFrame.add_btnCancel_ActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent evt) {
//				dwFrame.setVisible(false);
//			}
//		});
		
		// TIMER Listener. 
		ActionListener timerListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//frame.report.setText(new Date().toString());
            	System.out.println("cii: " + cii);
            	System.out.println("RAW: " + Integer.toString(project.getImageIndexByStatus(ImgStatus.NOTGRADED)));
            	           	
            	if (project.getProjectFolder() != null) {
            		project.imageListToTextPane(frame.imageReadout);
            		
            		// There is no image currently displayed and a NOTGRADED image is waiting. 
            		if (cii==-1 && project.getImageIndexByStatus(ImgStatus.NOTGRADED)>-1) {
            			// Get the ungraded image and display it, ready for evaluation. 
            			cii = project.getImageIndexByStatus(ImgStatus.NOTGRADED);
            			originalImage = Imgcodecs.imread(project.getImageList().get(cii).getLocation().getAbsolutePath());
        				ROIWindows = PathologyROIList.ROIsFromImage(originalImage);
        				//project.getImageAtIndex(cii).setStatus(ImgStatus.INPROC);
        				ROIWindows.getClass(originalImage, project, cii);
        				tmp = new Mat();
        				tmp = ROIWindows.DrawROIs(originalImage);
        				System.out.println("Get classifications:");
        				for (int j=0; j<4; j++) {
        					System.out.println(ROIWindows.get(j).getClassification());
        				}
        				BufferedImage img = PathUtilities.ImageUtils.toBufferedImage(tmp);
        				frame.imgPane.setImage(img);
        				frame.imgPane.repaint();
            		}
            		// There are no NOTGRADED images waiting nor images INPROC, so process a RAW into NOGRADED. 
            		if ((project.getImageIndexByStatus(ImgStatus.INPROC)==-1) && 
            				(project.getImageIndexByStatus(ImgStatus.NOTGRADED)==-1) &&
            				(project.getImageIndexByStatus(ImgStatus.COMPLETE)>-1)) {
            			// Grab a new RAW image and process a prediction. 
            			predictRawImage(); 
            		}
            	}
            	else {
            		
            	}
            }
        };
        Timer timer = new Timer(2000, timerListener);

        timer.start();

        //timer.stop();


	}
	/*
	 * Based on the pathologist's input, processes the image in the image pane making it COMPLETE. 
	 */
	private void processImage() {
        Log.l(3, "Controller.processImage()", "Finall processing of image " + project.getImageAtIndex(cii).getLocation().getName());
		String currentFileName = project.getImageAtIndex(cii).getLocation().getName();
		currentFileName = currentFileName.substring(0, currentFileName.lastIndexOf("."));
		ROIWindows.saveROIs(originalImage, project, cii);
		project.getImageAtIndex(cii).setStatus(ImgStatus.INPROC);
		ExecutorService threadExecutor = Executors.newCachedThreadPool(); 
		for (int i=0; i<cores; i++) {
			Thread thread = new Thread(new Wndchrm(project,cii));
			threadExecutor.execute(thread);
		}	
		threadExecutor.shutdown();
		cii = -1; 
		cycleImage();
	}
	/*
	 * Worker thread, predict RAW image  to create UNGRADED.  
	 */
	class Predict extends SwingWorker<Object, Object> {
		int windowIndex;
		Project project;
		Mat originalImage;
		int imageIndex;
		PathologyROIList ROIList;
		int test = 1; 
		
		public Predict(Project project, Mat originalImage, int imageIndex, PathologyROIList ROIList) {
			this.project = project;
			this.originalImage = originalImage;
			this.imageIndex = imageIndex;
			this.ROIList = ROIList; 
		}		
		protected Object doInBackground() throws Exception {
			// Setting number of windows to 4 for faster run during development
			//int numWindows = ROIList.size();
			int numWindows = 4;
			
			// Create the syncronization array. 
			int[] sync = new int[numWindows];
			Arrays.fill(sync, 0); 			
			
			ExecutorService threadExecutor = Executors.newCachedThreadPool(); 
			// For each window...
			for (int i=0; i<numWindows; i++) {
				Classify thread = new Classify(i, project, originalImage, imageIndex, ROIList, sync);
				threadExecutor.execute(thread);				
			}	
			
			// Wait while all the threads finish and update the scyn array to all 1's instead of 0's.
			while (intIndexOf(sync, 0)>-1) {
				Thread.sleep(500);
			}
			project.getImageAtIndex(this.imageIndex).setStatus(ImgStatus.NOTGRADED);
			threadExecutor.shutdown();
			return new Integer(test); 
		}
		protected void done() {
			//project.getImageAtIndex(this.imageIndex).setStatus(ImgStatus.NOTGRADED);
		}
	}
	/*
	 * Calls WORKER CLASS predict
	 */
	private void predictRawImage() {
		// If a RAW image is available. 
		if (project.getImageIndexByStatus(ImgStatus.RAW)>-1) {
			// Get the RAW image.
			int i = project.getImageIndexByStatus(ImgStatus.RAW);
			originalImage = Imgcodecs.imread(project.getImageList().get(i).getLocation().getAbsolutePath());
			// get windows. 
			ROIWindows = PathologyROIList.ROIsFromImage(originalImage);
			// Run worker thread... 
			project.getImageAtIndex(i).setStatus(ImgStatus.INPROC);
			//(new Predict(originalImage, project, i)).execute();
			// Project project, Mat original, int cii, PathologyROIList ROIList
			(new Predict(project, originalImage, i, ROIWindows)).execute();
		}
	}
	
	/* 
	 * Button actions
	 */
	private class NewProjectAction extends AbstractAction {
		public NewProjectAction() {
			putValue(NAME, "Project");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			npFrame.setLocationRelativeTo(frame);
			npFrame.setVisible(true);
		}
	}
	private class SaveAction extends AbstractAction {
		public SaveAction() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			frame.report.setText("Saving project");
			project.save(); 
		}
	}
	private class AddImagesAction extends AbstractAction {
		public AddImagesAction() {
			putValue(NAME, "Add Images");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			boolean empty = false; 
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Tiff file", "tif", "tiff"); 
			fc.setFileFilter(filter);
			fc.setMultiSelectionEnabled(true);
			fc.showOpenDialog(frame);
			File[] files = fc.getSelectedFiles();
			
			// If there are no images added yet, or all images have been processed. 
			if ((project.getImageList().size() < 1)) {
				empty = true; 
			}
			
			if (files.length > 0) {
				for (int i=0; i<files.length; i++) {
					System.out.println(files[i].toString());
					RawImage image = new RawImage(files[i], project.getImageList().size()+1);
					project.addImage(image);
				}
				project.imageListToTextPane(frame.imageReadout);
				
				if (empty) {
					cii = project.getImageIndexByStatus(ImgStatus.RAW);
					cycleImage(); 
					frame.report.setText("Click on the image. Red means cancer, blue benign.");
				}
			}
		}
	}
	private class OpenProjectAction extends AbstractAction {
		public OpenProjectAction() {
			putValue(NAME, "Open Project");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Project XML file", "proj", "proj");
			fc.setFileFilter(filter);
			int returnValue = fc.showOpenDialog(frame);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				project = Project.open(fc.getSelectedFile());
				project.imageListToTextPane(frame.imageReadout);
				if (project.isAllRaw()) {
					cii = project.getImageIndexByStatus(ImgStatus.RAW);
				}
				cycleImage(); 
			}
		}
	}
//	/* Diagnose Image Frame */
//	private class DiagnoseImageAction extends AbstractAction {
//		public DiagnoseImageAction() {
//			putValue(NAME, "Diagnose Image");
//			putValue(SHORT_DESCRIPTION, "Some short description");
//		}
//		public void actionPerformed(ActionEvent e) {
//			frame.report.setText("TO DO: Classify Image.");
//			dwFrame.setLocationRelativeTo(frame);
//			dwFrame.setVisible(true);
//			//frame.setEnabled(false);
//		}
//	}
	
	
	private void cycleImage() {
		
		if (cii > -1) {	
			originalImage = Imgcodecs.imread(project.getImageList().get(cii).getLocation().getAbsolutePath());
			ROIWindows = PathologyROIList.ROIsFromImage(originalImage);
			tmp = new Mat();
			tmp = ROIWindows.DrawROIs(originalImage);       
			BufferedImage img = PathUtilities.ImageUtils.toBufferedImage(tmp);
			frame.imgPane.setImage(img);
			frame.imgPane.repaint();
		}
		else {
			BufferedImage img = null; 
			try {
				img = ImageIO.read(new File("C:\\Users\\Benjamin\\workspace\\PathologyMVC\\background.jpg"));
				frame.imgPane.setImage(img);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		}
	}
	
//	private void recycleImage() {
//		if (originalImage != null) {
//			tmp = ROIWindows.DrawROIs(originalImage);
//	        frame.imgPane.setImage(PathUtilities.ImageUtils.toBufferedImage(tmp));
//	        frame.imgPane.repaint();
//		}
//	}
	
	/*
	 * Utility method
	 * returns first index of target in int array, or -1 if not present. 
	 */
	private int intIndexOf(int[] array, int target) {
		for (int i=0; i<array.length; i++) {
			if (array[i] == target) {
				return i; 
			}
		}
		return -1; 
	}

	public static void main(String[] args) {
		System.setProperty("java.library.path", System.getProperty("user.dir") + File.separator + "lib");
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible(true);
			try {
				fieldSysPath.set(null, null);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Controller app = new Controller();
					app.frame.setVisible(true);	
					app.frame.report.setText("Open a project or create a new project. [File >> New >> Project]  [Project >> Open Project]"); 					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}



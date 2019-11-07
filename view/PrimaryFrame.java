package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.Panel;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;

public class PrimaryFrame extends JFrame {

	public JPanel contentPane;


	/**
	 * Create the frame.
	 */
	public PrimaryFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 900);
		
		/* Menu */
		setJMenuBar(menuBar);	
		/*Add*/
		menuBar.add(mnFile);
			mnFile.add(mnOpen);
				mnOpen.add(mntmProject);				
			mnFile.add(mntmSaveItem);
		/*Project*/	
		mnProject.add(mntmOpenProject);
			menuBar.add(mnProject);
			mnProject.add(mntmAddImages);		
			mnProject.add(mntmOptions);
		/*Operations*/
		menuBar.add(mnOperations);
			//mnOperations.add(mntmDiagnoseImage);
			mnOperations.add(mntmTestAccuracy);
			
			//mnOpen.setActionCommand("New");
		//mntmProject.setAction(action);
				
		
		
				
		//mntmOpenProject.setAction(action_1);
				
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);		
		FlowLayout flowLayout = (FlowLayout) imgPane.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		imgPane.setAlignmentY(1.0f);
		imgPane.setAlignmentX(1.0f);
		imgPane.setBackground(new Color(235, 235, 235));
		imageReadout.setPreferredSize(new Dimension(200, 400));
		Border border = BorderFactory.createLineBorder(Color.darkGray);
		imageReadout.setBorder(border);
		imageReadout.setEditable(false);		
		
		

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
							.addGap(12)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addComponent(imageReadout, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGroup(gl_contentPane.createSequentialGroup()
										.addGap(44)
										.addComponent(processButton)))
								.addComponent(utilityButton)))
						.addComponent(report))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(processButton)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(imageReadout, GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
							.addComponent(utilityButton))
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE))
					.addGap(0)
					.addComponent(report))
		);
		
		contentPane.setLayout(gl_contentPane);
	}
	
	/*
	 * Form elements defined. 
	 */
	public JLabel report = new JLabel("...");
	public ImagePanel imgPane = new ImagePanel();
	JScrollPane scrollPane = new JScrollPane(imgPane);
	
	JButton processButton = new JButton("Process Image");
	public JTextPane imageReadout = new JTextPane(); 
	JButton utilityButton = new JButton("Utility");
	
	JMenuBar menuBar = new JMenuBar();
	JMenu mnFile = new JMenu("File");
	JMenu mnOpen = new JMenu("New");
	JMenuItem mntmProject = new JMenuItem("Project");
	JMenu mnProject = new JMenu("Project");
	JMenuItem mntmOpenProject = new JMenuItem("Open Project");
	JMenuItem mntmAddImages = new JMenuItem("Add Images");
	JMenuItem mntmOptions = new JMenuItem("Options");
	JMenuItem mntmSaveItem = new JMenuItem("Save");
	JMenu mnOperations = new JMenu("Operations");
	//JMenuItem mntmDiagnoseImage = new JMenuItem("Diagnose Image");	
	JMenuItem mntmTestAccuracy = new JMenuItem("Test Accuracy");
	
	
	/*
	 * Access objects. 
	 */
	
	
	/*
	 * Listeners. 
	 */
	public void add_processButton_ActionListener(ActionListener listener) {
		processButton.addActionListener(listener);
	}
	public void add_utilityButton_ActionListener(ActionListener listener) {
		utilityButton.addActionListener(listener);
	}

	public void setNewProjectAction(Action action) {
		mntmProject.setAction(action);
	}
	public void setSaveAction(Action action) {
		mntmSaveItem.setAction(action);
	}
	public void setAddImages(Action action) {
		mntmAddImages.setAction(action);
	}
	public void setOpenProjectAction(Action action) {
		mntmOpenProject.setAction(action);
	}
//	public void setDiagnoseImageAction(Action action) {
//		mntmDiagnoseImage.setAction(action);
//	}
	



}

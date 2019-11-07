package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NewProjectFrame extends JDialog {

	private JPanel contentPane;


	/**
	 * Create the frame.
	 */
	public NewProjectFrame() {
		nameField.setColumns(20);
		
		setTitle("Create New Project");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 481, 329);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);		
		
		finishButton.setEnabled(false);		
		
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.EAST;
		gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameLabel.gridx = 0;
		gbc_nameLabel.gridy = 1;
		contentPane.add(nameLabel, gbc_nameLabel);
		
		GridBagConstraints gbc_nameField = new GridBagConstraints();
		gbc_nameField.insets = new Insets(0, 0, 5, 5);
		gbc_nameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameField.gridx = 1;
		gbc_nameField.gridy = 1;
		contentPane.add(nameField, gbc_nameField);
		
		gbc_projectDirectoryLabel.insets = new Insets(0, 0, 5, 5);
		gbc_projectDirectoryLabel.anchor = GridBagConstraints.EAST;
		gbc_projectDirectoryLabel.gridx = 0;
		gbc_projectDirectoryLabel.gridy = 2;
		contentPane.add(projectDirectoryLabel, gbc_projectDirectoryLabel);		
		
		GridBagConstraints gbc_projectDirectoryTextField = new GridBagConstraints();
		gbc_projectDirectoryTextField.insets = new Insets(0, 0, 5, 5);
		gbc_projectDirectoryTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_projectDirectoryTextField.gridx = 1;
		gbc_projectDirectoryTextField.gridy = 2;
		contentPane.add(projectDirectoryTextField, gbc_projectDirectoryTextField);
		projectDirectoryTextField.setColumns(10);
		
		GridBagConstraints gbc_finishButton = new GridBagConstraints();
		gbc_finishButton.insets = new Insets(0, 0, 5, 0);
		gbc_finishButton.gridx = 2;
		gbc_finishButton.gridy = 2;
		contentPane.add(browseButton, gbc_finishButton);		
		
		GridBagConstraints gbc_fButton = new GridBagConstraints();
		gbc_fButton.gridx = 2;
		gbc_fButton.gridy = 8;
		contentPane.add(finishButton, gbc_fButton);
	}
	
	public JButton browseButton = new JButton("Broswe");
	public JButton finishButton = new JButton("Finish");
	private JLabel projectDirectoryLabel = new JLabel("Project Directory");
	GridBagConstraints gbc_projectDirectoryLabel = new GridBagConstraints();
	public JTextField projectDirectoryTextField = new JTextField();
	private final JLabel nameLabel = new JLabel("Project Name");
	private final JTextField nameField = new JTextField();
	
	public String getProjectDirectory() {
		return projectDirectoryTextField.getText(); 
	}
	public String getName() {
		return nameField.getText(); 
	}
	
	public void add_browseButton_ActionListener(ActionListener listener) {
		browseButton.addActionListener(listener);
	}	
	public void add_finishButton_ActionListener(ActionListener listener) {
		finishButton.addActionListener(listener);
	}

}

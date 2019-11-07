package view;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JDialog;

public class DiagnoseWarnFrame extends JDialog {
	JButton btnYes = new JButton("Yes");
	JButton btnCancel = new JButton("Cancel");
	
	public DiagnoseWarnFrame() {
		getContentPane().setLayout(null);
		this.setSize(385, 215);
		
		JLabel lblNewLabel = new JLabel("Diagnosing this image will likely require several minutes.", SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(12, 35, 348, 37);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Would you like to proceed.", SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(101, 76, 177, 16);
		getContentPane().add(lblNewLabel_1);		
		
		btnYes.setBounds(77, 121, 97, 25);
		getContentPane().add(btnYes);		
		
		btnCancel.setBounds(205, 121, 97, 25);
		getContentPane().add(btnCancel);
	}
	public void add_btnYes_ActionListener(ActionListener listener) {
		btnYes.addActionListener(listener);
	}
	public void add_btnCancel_ActionListener(ActionListener listener) {
		btnCancel.addActionListener(listener);
	}
}

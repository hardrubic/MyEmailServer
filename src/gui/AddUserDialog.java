package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

import server.bin.UserListManager;

public class AddUserDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField userNameText;
	private JPasswordField passwordText1;
	private JPasswordField passwordText2;
	
	private String userName;
	private String password;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddUserDialog dialog = new AddUserDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getPassword(){
		return password;
	}

	/**
	 * Create the dialog.
	 */
	public AddUserDialog() {
		setModal(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("\u6DFB\u52A0\u7528\u6237");
		setBounds(100, 100, 329, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{31, 0, 104, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel label = new JLabel("\u8D26\u53F7");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.SOUTH;
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 1;
			gbc_label.gridy = 0;
			contentPanel.add(label, gbc_label);
		}
		{
			userNameText = new JTextField();
			GridBagConstraints gbc_userNameText = new GridBagConstraints();
			gbc_userNameText.anchor = GridBagConstraints.SOUTH;
			gbc_userNameText.fill = GridBagConstraints.HORIZONTAL;
			gbc_userNameText.insets = new Insets(0, 0, 5, 5);
			gbc_userNameText.gridx = 2;
			gbc_userNameText.gridy = 0;
			contentPanel.add(userNameText, gbc_userNameText);
			userNameText.setColumns(10);
		}
		{
			JLabel lblNewLabel = new JLabel("\u5BC6\u7801");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 1;
			gbc_lblNewLabel.gridy = 1;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			passwordText1 = new JPasswordField();
			GridBagConstraints gbc_passwordText1 = new GridBagConstraints();
			gbc_passwordText1.insets = new Insets(0, 0, 5, 5);
			gbc_passwordText1.fill = GridBagConstraints.HORIZONTAL;
			gbc_passwordText1.gridx = 2;
			gbc_passwordText1.gridy = 1;
			contentPanel.add(passwordText1, gbc_passwordText1);
			passwordText1.setColumns(10);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("\u5BC6\u7801\u786E\u8BA4");
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.NORTH;
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 0, 5);
			gbc_lblNewLabel_1.gridx = 1;
			gbc_lblNewLabel_1.gridy = 2;
			contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		}
		{
			passwordText2 = new JPasswordField();
			GridBagConstraints gbc_passwordText2 = new GridBagConstraints();
			gbc_passwordText2.anchor = GridBagConstraints.NORTH;
			gbc_passwordText2.insets = new Insets(0, 0, 0, 5);
			gbc_passwordText2.fill = GridBagConstraints.HORIZONTAL;
			gbc_passwordText2.gridx = 2;
			gbc_passwordText2.gridy = 2;
			contentPanel.add(passwordText2, gbc_passwordText2);
			passwordText2.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String p1=new String(passwordText1.getPassword());
						String p2=new String(passwordText2.getPassword());
						if(!p1.equals(p2)){
							JOptionPane.showMessageDialog(null, "密码不匹配");
						}else {
//							UserManager userManager=new UserManagerJDBC();
							UserListManager userManager=UserListManager.getInstance();
							if(!userManager.checkUserExist(userNameText.getText().trim())){
								userName=userNameText.getText().trim();
								password=p1;
								//添加用户
								if(userManager.addUser(userName,password)){
									JOptionPane.showMessageDialog(null, "添加账号成功");
									dispose();
								}else {
									JOptionPane.showMessageDialog(null, "添加账号失败");
								}
							}else {
								JOptionPane.showMessageDialog(null,"账号已存在");
							}
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}

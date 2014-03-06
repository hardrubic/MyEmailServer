package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import server.bin.UserListManager;
import server.model.User;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField smtpPortText;
	private JTextField pop3PortText;
	private JTextField localDomainText;
	private JTextField localHostNameText;
	private JTextField connectLostTimeText;
	private JTextField connectResponseTimeText;
	private JTextField emailSizeText;

	private JButton startButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 读取设置
	 */
	private void loadProp() {
		ServerManager manager = ServerManager.getServerManager();
		smtpPortText.setText(manager.getPropertyValue("smtpPort"));
		pop3PortText.setText(manager.getPropertyValue("pop3Port"));
		localDomainText.setText(manager.getPropertyValue("localDomain"));
		localHostNameText.setText(manager.getPropertyValue("localHostName"));
		connectLostTimeText.setText(manager.getPropertyValue("connectLostTime"));
		connectResponseTimeText.setText(manager.getPropertyValue("connectResponseTime"));
		emailSizeText.setText(manager.getPropertyValue("emailSize"));
	}

	/**
	 * 保存设置
	 */
	private void saveProp() {
		ServerManager manager = ServerManager.getServerManager();
		String text = smtpPortText.getText();
		manager.savePropertyByKey("smtpPort", text);
		text = pop3PortText.getText();
		manager.savePropertyByKey("pop3Port", text);
		text = localDomainText.getText();
		manager.savePropertyByKey("localDomain", text);
		text = localHostNameText.getText();
		manager.savePropertyByKey("localHostName", text);
		text = connectLostTimeText.getText();
		manager.savePropertyByKey("connectLostTime", text);
		text = connectResponseTimeText.getText();
		manager.savePropertyByKey("connectResponseTime", text);
		text = emailSizeText.getText();
		manager.savePropertyByKey("emailSize", text);
		// 写入文件
		manager.saveProperty();

	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("\u90AE\u4EF6\u670D\u52A1\u5668");
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				// 读取配置文件
				loadProp();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 634, 561);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1, 0, 0, 0));

		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);

		JPanel tap1 = new JPanel();
		tabbedPane.addTab("\u57FA\u672C\u8BBE\u7F6E", null, tap1, null);
		GridBagLayout gbl_tap1 = new GridBagLayout();
		gbl_tap1.columnWidths = new int[] { 343, 0 };
		gbl_tap1.rowHeights = new int[] { 100, 100, 100, 100, 56, 0 };
		gbl_tap1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tap1.rowWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		tap1.setLayout(gbl_tap1);

		JPanel setPort = new JPanel();
		setPort.setBorder(new TitledBorder(null, "\u7AEF\u53E3\u8BBE\u7F6E", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		GridBagConstraints gbc_setPort = new GridBagConstraints();
		gbc_setPort.fill = GridBagConstraints.BOTH;
		gbc_setPort.insets = new Insets(0, 0, 5, 0);
		gbc_setPort.gridx = 0;
		gbc_setPort.gridy = 0;
		tap1.add(setPort, gbc_setPort);
		GridBagLayout gbl_setPort = new GridBagLayout();
		gbl_setPort.columnWidths = new int[] { 80, 293, 0 };
		gbl_setPort.rowHeights = new int[] { 41, 0, 0 };
		gbl_setPort.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_setPort.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setPort.setLayout(gbl_setPort);

		JLabel lblSmtp = new JLabel("SMTP\u7AEF\u53E3");
		GridBagConstraints gbc_lblSmtp = new GridBagConstraints();
		gbc_lblSmtp.insets = new Insets(0, 0, 5, 5);
		gbc_lblSmtp.gridx = 0;
		gbc_lblSmtp.gridy = 0;
		setPort.add(lblSmtp, gbc_lblSmtp);

		smtpPortText = new JTextField();
		GridBagConstraints gbc_smtpPortText = new GridBagConstraints();
		gbc_smtpPortText.fill = GridBagConstraints.HORIZONTAL;
		gbc_smtpPortText.insets = new Insets(0, 0, 5, 0);
		gbc_smtpPortText.gridx = 1;
		gbc_smtpPortText.gridy = 0;
		setPort.add(smtpPortText, gbc_smtpPortText);
		smtpPortText.setColumns(10);

		JLabel lblPop = new JLabel("POP3\u7AEF\u53E3");
		GridBagConstraints gbc_lblPop = new GridBagConstraints();
		gbc_lblPop.insets = new Insets(0, 0, 0, 5);
		gbc_lblPop.gridx = 0;
		gbc_lblPop.gridy = 1;
		setPort.add(lblPop, gbc_lblPop);

		pop3PortText = new JTextField();
		GridBagConstraints gbc_pop3PortText = new GridBagConstraints();
		gbc_pop3PortText.fill = GridBagConstraints.HORIZONTAL;
		gbc_pop3PortText.gridx = 1;
		gbc_pop3PortText.gridy = 1;
		setPort.add(pop3PortText, gbc_pop3PortText);
		pop3PortText.setColumns(10);

		JPanel setDomain = new JPanel();
		setDomain.setBorder(new TitledBorder(null, "\u57DF\u540D\u8BBE\u7F6E", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		GridBagConstraints gbc_setDomain = new GridBagConstraints();
		gbc_setDomain.fill = GridBagConstraints.BOTH;
		gbc_setDomain.insets = new Insets(0, 0, 5, 0);
		gbc_setDomain.gridx = 0;
		gbc_setDomain.gridy = 1;
		tap1.add(setDomain, gbc_setDomain);
		GridBagLayout gbl_setDomain = new GridBagLayout();
		gbl_setDomain.columnWidths = new int[] { 79, 0, 0 };
		gbl_setDomain.rowHeights = new int[] { 41, 0, 0 };
		gbl_setDomain.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_setDomain.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setDomain.setLayout(gbl_setDomain);

		JLabel lblNewLabel = new JLabel("\u670D\u52A1\u5668\u57DF\u540D");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		setDomain.add(lblNewLabel, gbc_lblNewLabel);

		localDomainText = new JTextField();
		GridBagConstraints gbc_localDomainText = new GridBagConstraints();
		gbc_localDomainText.insets = new Insets(0, 0, 5, 0);
		gbc_localDomainText.fill = GridBagConstraints.HORIZONTAL;
		gbc_localDomainText.gridx = 1;
		gbc_localDomainText.gridy = 0;
		setDomain.add(localDomainText, gbc_localDomainText);
		localDomainText.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("\u670D\u52A1\u5668\u4E3B\u673A\u540D");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		setDomain.add(lblNewLabel_1, gbc_lblNewLabel_1);

		localHostNameText = new JTextField();
		GridBagConstraints gbc_localHostNameText = new GridBagConstraints();
		gbc_localHostNameText.fill = GridBagConstraints.HORIZONTAL;
		gbc_localHostNameText.gridx = 1;
		gbc_localHostNameText.gridy = 1;
		setDomain.add(localHostNameText, gbc_localHostNameText);
		localHostNameText.setColumns(10);

		JPanel setConnect = new JPanel();
		setConnect.setBorder(new TitledBorder(null, "\u8FDE\u63A5\u8BBE\u7F6E", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		GridBagConstraints gbc_setConnect = new GridBagConstraints();
		gbc_setConnect.fill = GridBagConstraints.BOTH;
		gbc_setConnect.insets = new Insets(0, 0, 5, 0);
		gbc_setConnect.gridx = 0;
		gbc_setConnect.gridy = 2;
		tap1.add(setConnect, gbc_setConnect);
		GridBagLayout gbl_setConnect = new GridBagLayout();
		gbl_setConnect.columnWidths = new int[] { 81, 0, 0 };
		gbl_setConnect.rowHeights = new int[] { 46, 0, 0 };
		gbl_setConnect.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_setConnect.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setConnect.setLayout(gbl_setConnect);

		JLabel lblNewLabel_2 = new JLabel("\u7B49\u5F85\u8FDE\u63A5\u65F6\u95F4");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 0;
		setConnect.add(lblNewLabel_2, gbc_lblNewLabel_2);

		connectLostTimeText = new JTextField();
		GridBagConstraints gbc_connectLostTimeText = new GridBagConstraints();
		gbc_connectLostTimeText.insets = new Insets(0, 0, 5, 0);
		gbc_connectLostTimeText.fill = GridBagConstraints.HORIZONTAL;
		gbc_connectLostTimeText.gridx = 1;
		gbc_connectLostTimeText.gridy = 0;
		setConnect.add(connectLostTimeText, gbc_connectLostTimeText);
		connectLostTimeText.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("\u54CD\u5E94\u65F6\u95F4");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 1;
		setConnect.add(lblNewLabel_3, gbc_lblNewLabel_3);

		connectResponseTimeText = new JTextField();
		GridBagConstraints gbc_connectResponseTimeText = new GridBagConstraints();
		gbc_connectResponseTimeText.fill = GridBagConstraints.HORIZONTAL;
		gbc_connectResponseTimeText.gridx = 1;
		gbc_connectResponseTimeText.gridy = 1;
		setConnect.add(connectResponseTimeText, gbc_connectResponseTimeText);
		connectResponseTimeText.setColumns(10);

		JPanel setEmail = new JPanel();
		setEmail.setBorder(new TitledBorder(null, "\u90AE\u4EF6\u8BBE\u7F6E", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		GridBagConstraints gbc_setEmail = new GridBagConstraints();
		gbc_setEmail.fill = GridBagConstraints.BOTH;
		gbc_setEmail.insets = new Insets(0, 0, 5, 0);
		gbc_setEmail.gridx = 0;
		gbc_setEmail.gridy = 3;
		tap1.add(setEmail, gbc_setEmail);
		GridBagLayout gbl_setEmail = new GridBagLayout();
		gbl_setEmail.columnWidths = new int[] { 0, 0, 0 };
		gbl_setEmail.rowHeights = new int[] { 25, 0 };
		gbl_setEmail.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_setEmail.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setEmail.setLayout(gbl_setEmail);

		JLabel label = new JLabel("\u90AE\u4EF6\u5927\u5C0F\u9650\u5236");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 0, 5);
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		setEmail.add(label, gbc_label);

		emailSizeText = new JTextField();
		GridBagConstraints gbc_emailSizeText = new GridBagConstraints();
		gbc_emailSizeText.fill = GridBagConstraints.HORIZONTAL;
		gbc_emailSizeText.gridx = 1;
		gbc_emailSizeText.gridy = 0;
		setEmail.add(emailSizeText, gbc_emailSizeText);
		emailSizeText.setColumns(10);

		JPanel setButton = new JPanel();

		startButton = new JButton("\u542F\u52A8\u670D\u52A1\u5668");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveProp();
				ServerManager.getServerManager().startServer();
				startButton.setEnabled(false);
				// tabbedPane.setEnabledAt(2, true);
				tabbedPane.setEnabledAt(1, true);
			}
		});

		JButton savePropButton = new JButton("\u4FDD\u5B58\u8BBE\u7F6E");
		savePropButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveProp();
				JOptionPane.showMessageDialog(null, "保存成功");
			}
		});
		setButton.add(savePropButton);

		JButton readPropButton = new JButton("\u8BFB\u53D6\u8BBE\u7F6E");
		readPropButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadProp();
			}
		});
		setButton.add(readPropButton);
		setButton.add(startButton);
		GridBagConstraints gbc_setButton = new GridBagConstraints();
		gbc_setButton.anchor = GridBagConstraints.WEST;
		gbc_setButton.gridx = 0;
		gbc_setButton.gridy = 4;
		tap1.add(setButton, gbc_setButton);

		JPanel tap3 = new JPanel();
		tabbedPane.addTab("\u8D26\u53F7\u7BA1\u7406", null, tap3, null);
		tabbedPane.setEnabledAt(1, false);
		GridBagLayout gbl_tap3 = new GridBagLayout();
		gbl_tap3.columnWidths = new int[] { 306, 0 };
		gbl_tap3.rowHeights = new int[] { 1, 0, 0 };
		gbl_tap3.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tap3.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		tap3.setLayout(gbl_tap3);

		final JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.insets = new Insets(0, 0, 5, 0);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 0;
		tap3.add(textArea, gbc_textArea);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		tap3.add(panel_1, gbc_panel_1);

		JButton btnNewButton = new JButton("\u663E\u793A\u8D26\u53F7");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserListManager userListManager = UserListManager.getInstance();
				List<User> userList = userListManager.getUserList();
				textArea.setText("");
				for (User user : userList) {
					textArea.append("账号：" + user.getUserName() + " 邮件数量" + user.getEmailNumber()+"\n");
				}
			}
		});
		panel_1.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("\u6DFB\u52A0\u8D26\u53F7");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddUserDialog addUserDialog = new AddUserDialog();
				addUserDialog.setVisible(true);
			}
		});
		panel_1.add(btnNewButton_1);

		JPanel tap2 = new JPanel();
		tabbedPane.addTab("\u65E5\u5FD7", null, tap2, null);
		tabbedPane.setEnabledAt(2, true);
		GridBagLayout gbl_tap2 = new GridBagLayout();
		gbl_tap2.columnWidths = new int[] { 4, 0 };
		gbl_tap2.rowHeights = new int[] { 24, 0, 0 };
		gbl_tap2.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tap2.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		tap2.setLayout(gbl_tap2);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		tap2.add(scrollPane, gbc_scrollPane);

		final JTextArea logTextArea = new JTextArea();
		scrollPane.setViewportView(logTextArea);
		logTextArea.setLineWrap(true);
		logTextArea.setEditable(false);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		tap2.add(panel, gbc_panel);

		JButton btnNewButton_2 = new JButton("\u8BFB\u53D6log_debug.log");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logTextArea.setText("");
				BufferedReader reader = null;
				String content = null;
				try {
					File logFile = new File("src/server/log/log_debug.log");
					if (logFile.exists()) {
						reader = new BufferedReader(new FileReader(logFile));
						while ((content = reader.readLine()) != null) {
							logTextArea.append(content + "\n");
						}
					} else {
						logTextArea.setText("file not found!");
					}
				} catch (FileNotFoundException e3) {
					e3.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		panel.add(btnNewButton_2);
	}

}

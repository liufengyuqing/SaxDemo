package com.yonyou;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import demo.MultiSelectComboBox;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class UI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	// 逻辑处理对象
	private ContextReplace contextReplace = new ContextReplace();
	//文件选择对象
	private JFileChooser chooser = new JFileChooser();
	private JTextArea textArea;
	private JMenu mnNewMenu;
	private JMenu mnNewMenu_1;
	private JMenuItem mntmNewMenuItem;
	private JMenuItem mntmNewMenuItem_1;
	private JLabel lblCreateByLiuzh;
	//private JComboBox<String> comboBox1;
	private MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI frame = new UI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 530, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setTitle("用友-批处理替换pom.xml版本号");
		setResizable(false); // 禁止最大化
		setLocation(700, 100); // 设置窗体位置 默认是在屏幕左上角

		JButton btnNewButton = new JButton("浏览");

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseDirectory();
			}
		});
		btnNewButton.setBounds(10, 35, 65, 29);
		contentPane.add(btnNewButton);

		textField = new JTextField();
		textField.setBounds(93, 37, 407, 25);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setBounds(10, 99, 151, 21);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		textField_2 = new JTextField();
		textField_2.setBounds(171, 99, 137, 21);
		contentPane.add(textField_2);
		textField_2.setColumns(10);

		JButton btnNewButton_1 = new JButton("确认");

		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSome();
			}
		});

		btnNewButton_1.setBounds(318, 98, 75, 23);
		contentPane.add(btnNewButton_1);

		JLabel label = new JLabel("当前版本号");
		label.setBounds(45, 74, 85, 15);
		contentPane.add(label);

		JLabel label_1 = new JLabel("修改为：");
		label_1.setBounds(192, 74, 54, 15);
		contentPane.add(label_1);
		textArea = new JTextArea();

		// textArea.setBounds(10, 181, 504, 230);
		// textArea.setLineWrap(true); //激活自动换行功能
		// textArea.setWrapStyleWord(true);// 激活断行不断字功能
		// contentPane.add(new JScrollPane(textArea));

		// 设置滚动条
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(textArea);

		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 150, 490, 250);

		contentPane.add(scrollPane);

		JButton btnNewButton_2 = new JButton("清空日志");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});
		btnNewButton_2.setBounds(403, 98, 93, 23);
		contentPane.add(btnNewButton_2);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 524, 21);
		contentPane.add(menuBar);

		mnNewMenu = new JMenu("使用说明");
		menuBar.add(mnNewMenu);

		mntmNewMenuItem = new JMenuItem("ReadMe");
		mnNewMenu.add(mntmNewMenuItem);

		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == mntmNewMenuItem) {
					String msg = "该软件使用的JDK1.8，可能与您的版本不一致！\n" + "若你的版本不是1.8，请将随软件下载的jre文件和exe文件放在同一目录下\n"
							+ "若您的版本是1.8，需要配置JAVA_HOME目\n" + "以上都没问题的话，以下是怎么使用\n" + "找到项目的根目录-打开\n"
							+ "程序会找到当前目录下的所有的pom.xml\n" + "并匹配到所有的版本字段，打印的行数就每个文件代表匹配到几处";
					showMessage(msg);
				}
			}
		});

		mnNewMenu_1 = new JMenu("工具说明");
		menuBar.add(mnNewMenu_1);

		mntmNewMenuItem_1 = new JMenuItem("联系方式");
		mnNewMenu_1.add(mntmNewMenuItem_1);

		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == mntmNewMenuItem_1) {
					showMessage("如果使用过程中出现问题请联系我\n" + "Phone: 18392533494\n" + "Email: liuzhw1@yonyou.com\n"
							+ "还有不完善的地方，请指正\n");
				}
			}
		});
		// 添加底部标签
		lblCreateByLiuzh = new JLabel("Create By liuzhw1");
		lblCreateByLiuzh.setBounds(403, 406, 102, 15);
		contentPane.add(lblCreateByLiuzh);
		// 下拉框
		/*
		 * comboBox1 = new JComboBox<String>(); comboBox1.setBounds(88, 127, 75,
		 * 21); comboBox1.addItem("身份证"); comboBox1.addItem("驾驶证");
		 * comboBox1.addItem("军官证"); contentPane.add(comboBox1);
		 */
		JLabel label_2 = new JLabel("选择模块:");
		label_2.setBounds(14, 131, 63, 15);
		contentPane.add(label_2);

		// 多选下拉框

		comboBox.setPreferredSize(new Dimension(300, 26));
		comboBox.setBounds(100, 125, 400, 22);

		// 设置颜色
		comboBox.setForegroundAndToPopup(Color.BLACK);
		// comboBox.setBorder(BorderFactory.createLineBorder(Color.GREEN));
		// comboBox.setPopupBorder(BorderFactory.createLineBorder(Color.RED));
		comboBox.setPopupBackground(Color.WHITE);
		comboBox.setSelectionBackground(Color.lightGray);
		comboBox.setSelectionForeground(Color.RED);

		comboBox.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				System.out.println("选择的值：" + comboBox.getSelectedItemsString());
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});

		contentPane.add(comboBox);

		setVisible(true);
	}

	/*
	 * 显示信息
	 */

	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}

	// 选择文件目录
	private void chooseDirectory() {
		chooser.setCurrentDirectory(chooser.getSelectedFile());
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能选择目录
		chooser.setDialogTitle("选择项目根目录");

		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String filepath = file.getAbsolutePath();
			textField.setText(filepath);// 将文件路径设到JTextField
			File srcFile = new File(filepath);
			contextReplace.readDir(srcFile, null, "1");
			textField_1.setText(contextReplace.getDversion());
			textArea.setText(contextReplace.getFileMsg());
			contextReplace.setResultMsg("");
			contextReplace.setFileMsg("");

			int size = contextReplace.getMatched().size();
			contextReplace.getMatched().toArray(new String[size]);
			comboBox.removeAllItems();
			for (String s : contextReplace.getMatched()) {
				comboBox.addItem(s);
			}
			String [] str = filepath.split("\\\\");
			String string = str[str.length-1];
			comboBox.setSelectedItem(string);

			//System.out.println(contextReplace.getMatched().size());

			// System.out.println(filepath);
		}
	}

	private void doSome() {
		String filepath = textField.getText();
		// System.out.println(filepath);
		if (filepath.equals("")) {
			showMessage("请先选择项目目录！！！");
		} else {
			File srcFile = new File(filepath);
			String postStr = textField_2.getText();
			if (postStr.equals("")) {
				showMessage("请输入版本号！！！");
			} else {
				if (comboBox.getSelectedItemsString().equals("")) {
					showMessage("请选择模块！！！");
				} else {
					List<String> list = comboBox.getSelectedItems();
					contextReplace.readDir(srcFile, list, postStr);
					textField_1.setText(contextReplace.getDversion());
					
					textArea.setText(contextReplace.getResultMsg());
					contextReplace.setFileMsg("");
					contextReplace.setResultMsg("");
					
				}
			}
		}
	}
}

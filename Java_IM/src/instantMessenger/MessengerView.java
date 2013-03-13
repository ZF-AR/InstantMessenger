package instantMessenger;


import java.awt.*;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessengerView {

	// GUI layout
	private JFrame frame;
	private JPanel mainPanl;
	private JPanel memberPanl;

	// ... Main panel layout
	public JComboBox statusBox;
	private JTextField topicField;
	private static JTextArea chatHistoryArea;
	private JTextArea chatArea;
	private JScrollPane chatHistoryScroll;
	private JScrollPane chatScroll;
	public static JTextField editStatusField;
	private JCheckBox changeButton;
	private JMenu menuTelePointer;

	private int caretPos = 0;

	// ... Constants
	private static final int HOR_SIZE = 400-50;
	private static final int VER_SIZE = 150-50;

	public MessengerView() {
		createAndShowGUI();
	}
	
	public MessengerView(String title) {
		createAndShowGUI();
		setTitle(title);
	}

	private JPanel createMainPanel(Container pane, String pos) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		// (0,0) - Status
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("   Status:   "), c);

		// (1,0) - Status comboBox
		String[] statusStrings = { "  Idle", "  Busy" };
		statusBox = new JComboBox(statusStrings);
		statusBox.setSelectedIndex(0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		panel.add(statusBox, c);

		// (2,0) - Topic
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		panel.add(new JLabel("   Topic:   "), c);

		// (3,0) - Topic edit field
		topicField = new JTextField("");
		topicField.setPreferredSize(new Dimension(150, 23));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 0;
		panel.add(topicField, c);

		// (4,0) - Tele Pointer CheckBox
		// changeButton = new JCheckBox("TelePointer");
		// changeButton.setSelected(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 0;
		c.gridx = 4;
		c.gridy = 0;
		// panel.add(changeButton,c);

		JMenuBar menuBar = new JMenuBar();
//		menuTelePointer = new JMenu("TelePointer Menu");
		menuTelePointer = new JMenu("Menu");

		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem rbMenuItem;
//		rbMenuItem = new JRadioButtonMenuItem("None");
//		rbMenuItem.setSelected(true);
//		group.add(rbMenuItem);
//		menuTelePointer.add(rbMenuItem);

//		rbMenuItem = new JRadioButtonMenuItem("Single");
		rbMenuItem = new JRadioButtonMenuItem("TelepointerShow");
		rbMenuItem.setSelected(false);
		group.add(rbMenuItem);
		menuTelePointer.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("TelepointerHide");
		rbMenuItem.setSelected(false);
		group.add(rbMenuItem);
		menuTelePointer.add(rbMenuItem);

//		rbMenuItem = new JRadioButtonMenuItem("Multiple");
//		rbMenuItem.setSelected(false);
//		group.add(rbMenuItem);
//		menuTelePointer.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("JitterON");
		rbMenuItem.setSelected(false);
		group.add(rbMenuItem);
		menuTelePointer.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("JitterOFF");
		rbMenuItem.setSelected(false);
		group.add(rbMenuItem);
		menuTelePointer.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("JitterRecovery");
		rbMenuItem.setSelected(false);
		group.add(rbMenuItem);
		menuTelePointer.add(rbMenuItem);

		menuBar.add(menuTelePointer);
		panel.add(menuBar, c);

		// (0,1) - Chat history area
		chatHistoryArea = new JTextArea("");
		chatHistoryArea.setEditable(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(chatHistoryArea, c);

		chatHistoryScroll = new JScrollPane(chatHistoryArea);
		chatHistoryScroll
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatHistoryScroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatHistoryScroll.setMaximumSize(new java.awt.Dimension(HOR_SIZE,
				VER_SIZE));
		chatHistoryScroll.setMinimumSize(new java.awt.Dimension(HOR_SIZE,
				VER_SIZE));
		chatHistoryScroll.setPreferredSize(new java.awt.Dimension(HOR_SIZE,
				VER_SIZE));
		panel.add(chatHistoryScroll, c);

		// (0,2) - Chat editing area
		chatArea = new JTextArea("");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 0, 0); // top padding
		c.gridwidth = 5;
		c.gridx = 0;
		c.gridy = 2;
		panel.add(chatArea, c);

		chatScroll = new JScrollPane(chatArea);
		chatScroll
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatScroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatScroll.setMaximumSize(new java.awt.Dimension(HOR_SIZE, VER_SIZE));
		chatScroll.setMinimumSize(new java.awt.Dimension(HOR_SIZE, VER_SIZE-50));
		chatScroll.setPreferredSize(new java.awt.Dimension(HOR_SIZE, VER_SIZE-50));
		panel.add(chatScroll, c);

		// (0,3) - Editing status field
		editStatusField = new JTextField("Not entered any text");
		editStatusField.setEditable(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.gridx = 0;
		c.gridy = 3;
		panel.add(editStatusField, c);

		pane.add(panel, pos);

		return panel;
	}

	private void addComponentsToPane(Container pane) {
		mainPanl = createMainPanel(pane, BorderLayout.WEST);
		// memberPanl = createMemberListPanel(pane, BorderLayout.EAST);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		// Create and set up the window.
		frame = new JFrame("IM Client");
		// Set up the content pane.
		addComponentsToPane(frame.getContentPane());
		// Display the window.
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	void setTitle(String title) {
		frame.setTitle(title);
	}

	void setStatus(String status) {
		chatHistoryArea.append(status);
	}

	String getTopic() {
		return topicField.getText();
	}

	void setTopic(String topic) {
		topicField.setText(topic);
	}

	String getChatText() {
		return chatArea.getText();
	}

	void setChatText(String text) {
		chatArea.setText(text);
	}

	void appendHistory(String history) {
		chatHistoryArea.append(history);
	}
	
	void setHistory(String history) {
		chatHistoryArea.setText(history);
	}

	void setExitMessage(String mesg) {
		chatHistoryArea.append(mesg);
	}

	void setEditStatus(String editStatus) {
		editStatusField.setText(editStatus);
	}

	public int getCaretPos() {
		return caretPos;
	}

	public void setCaretPos(int caretPos) {
		this.caretPos = caretPos;
	}

	public JMenu getTelePointerMenu() {
		return menuTelePointer;
	}

	public JComboBox getStatusBox() {
		return statusBox;
	}

	public JFrame getMainFrame() {
		return frame;
	}

	void addStatusListener(ActionListener statusBoxListener) {
		statusBox.addActionListener(statusBoxListener);
	}

	void addTopicCaretListener(CaretListener topicCaretListener) {
		topicField.addCaretListener(topicCaretListener);
	}

	void addTopicKeyListener(KeyAdapter topicKeyListener) {
		topicField.addKeyListener(topicKeyListener);
	}

	void addChatListener(KeyListener chatListener) {
		chatArea.addKeyListener(chatListener);
	}

	void addFrameListener(WindowListener frameListener) {
		frame.addWindowListener(frameListener);
	}
}

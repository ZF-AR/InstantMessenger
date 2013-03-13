package instantMessenger;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import util.trace.TraceableListener;

public class ATraceableListener implements TraceableListener {

	String name;
	JTextArea logArea;

	public ATraceableListener(String name) {
		this.name = name;
		createAndShowGUI();
	}

	private void createAndShowGUI() {
		JFrame frame = new JFrame("Message Log: " + name);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		logArea = new JTextArea("");
		logArea.setEditable(false);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(logArea, c);

		int HOR_SIZE = 300;
		int VER_SIZE = 300;
		JScrollPane scrollPane = new JScrollPane(logArea);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setMaximumSize(new java.awt.Dimension(HOR_SIZE, VER_SIZE));
		scrollPane.setMinimumSize(new java.awt.Dimension(HOR_SIZE, VER_SIZE));
		scrollPane.setPreferredSize(new java.awt.Dimension(HOR_SIZE, VER_SIZE));
		panel.add(scrollPane, c);

		frame.getContentPane().add(panel);
		// Display the window.
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void newEvent(Exception arg0) {
		String msg = arg0.toString();
		if (msg.length() > 100) {
			msg = msg.substring(0, 100) + "...";
		}
		msg = msg + "\n";
		logArea.append(msg);
	}
	
	public void appendMsg(String msg) {
		logArea.append(msg+"\n");
	}
}

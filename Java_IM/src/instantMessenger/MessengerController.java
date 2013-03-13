package instantMessenger;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.Timer;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import util.models.PropertyListenerRegisterer;

public class MessengerController implements PropertyChangeListener {
	protected MessengerModel messengerModel;
	protected MessengerView messengerView;
	protected RMIClientInterface client;
	protected RMIServerInterface server;

	private String topic;
	private EdittingTimer task;
	private boolean statusChanged = false;
	private static int editStatus = 0; // 0 - not editing, 1 - editing, 2 -
										// editing paused
	private static final long editDelay = 5 * 1000; // 5 seconds delay

	public MessengerController(MessengerModel anIMmodel, MessengerView anIMgui) {
		messengerView = anIMgui;
		messengerModel = anIMmodel;

		// ... Add listeners to the view.
		try {
			messengerView.addChatListener(new ChatKeyListener());
			messengerView.addStatusListener(new StatusListener());
			messengerView.addTopicCaretListener(new TopicCaretListener());
			messengerView.addTopicKeyListener(new TopicKeyListener());
			messengerView.addFrameListener(new FrameListener());
		} catch (Exception ie) {
			System.out.println(ie.getMessage());
		}

		((PropertyListenerRegisterer) messengerModel)
				.addPropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent aPropertyChange) {
		// if (aPropertyChange.getPropertyName().equalsIgnoreCase("message")) {
		// System.out.println(aPropertyChange.getNewValue());
		// messengerView.setChatText((String) aPropertyChange.getNewValue());
		// //clientView.setHistory((String) aPropertyChange.getNewValue());
		// }
		// if (aPropertyChange.getPropertyName().equalsIgnoreCase("status")) {
		// System.out.println(aPropertyChange.getNewValue());
		// clientView.setHistory((String) aPropertyChange.getNewValue() + "\n");
		// }
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("topic")) {
//			System.out.println(aPropertyChange.getNewValue());
			String topicMsg = (String) aPropertyChange.getNewValue();

			if (topic == null || !topic.equals(topicMsg))
				messengerView.setTopic(topicMsg);
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("editStatus")) {
//			System.out.println(aPropertyChange.getNewValue());
			messengerView.setEditStatus((String) aPropertyChange.getNewValue());
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("history")) {
//			System.out.println(aPropertyChange.getNewValue());
			String history = (String) aPropertyChange.getNewValue();
			messengerView.appendHistory(history);
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("historyfull")) {
			//System.out.println(aPropertyChange.getNewValue());
			String history = (String) aPropertyChange.getNewValue();
			messengerView.setHistory(history);
		}
	}

	void setClient(RMIClientInterface client) {
		this.client = client;
		// try {
		// this.client.setClientModel(clientModel);
		// server.join(client);
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
	}

	MessengerController getThis() {
		return this;
	}

	public void setEditStatus(int statusIdx) {
		editStatus = statusIdx;
	}

	public void editStatusChange(int statusIdx) {
		String text = "";

		switch (statusIdx) {

		case 0:
			text = "Not entered any text";
			break;

		case 1:
			text = "Actively editing...";
			break;

		case 2:
			editStatus = 2;
			text = "Not actively editing";
			break;

		}
		messengerModel.setEditStatus(text);
	}

	// ////////////////////////////////////////inner class StatusListener
	class StatusListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
//			System.out.println("Command: " + actionEvent.getActionCommand());
			JComboBox cb = (JComboBox) actionEvent.getSource();
			String statusString = (String) cb.getSelectedItem();
			// String statusMsg = "You changed status:" + statusString;

			messengerModel.setStatus(statusString);
		}
	}// end inner class StatusListener

	// ////////////////////////////////////////inner class TopicCaretListener
	class TopicCaretListener implements CaretListener {
		public void caretUpdate(CaretEvent e) {
			int caretPos = e.getDot();
			messengerView.setCaretPos(caretPos);
//			System.out.println("Caret Position :" + caretPos);
		}
	}// end inner class TopicCaretListener

	// ////////////////////////////////////////inner class TopicKeyListener
	class TopicKeyListener extends KeyAdapter {
		public void keyTyped(KeyEvent evt) {
			//int index = evt.getKeyLocation();
			char c = evt.getKeyChar();
			String text = messengerView.getTopic();

			if (c != '\b') {
				text = Utilities.insertChar(text, c,
						messengerView.getCaretPos());
			}

//			System.out.println("Topic edited: " + text);

			text = Utilities.removeChar(text, '\n');

			topic = text;
			//messengerModel.setTopic(topic);
			
			Operation op = new Operation("insert", messengerView.getCaretPos(), String.valueOf(c), false, new OperationTimeStamp());
			
			messengerModel.setTopicOperation(topic, op);
		}
	}// end inner class TopicKeyListener

	// ////////////////////////////////////////inner class ChatKeyListener
	class ChatKeyListener implements KeyListener {
		public void keyTyped(KeyEvent evt) {
			char c = evt.getKeyChar();
			String textString;

			// Need to listen to caretPos
			// clientModel.setMessage(clientView.getChatText());
			if (c == '\n') { // just finished a line
				textString = messengerView.getChatText();
				textString = Utilities.removeChar(textString, '\n');

//				System.out.println("****Just pressed enter");
				messengerView.setChatText("");
				messengerModel.setMessage(textString);

				if (task != null && task.getScheduled())
					task.cancelTask(); // cancel

				if (editStatus != 0) // just finished a line
					statusChanged = true;
				else
					statusChanged = false;

				editStatus = 0;

			} else { // either editing or paused
				if (editStatus != 1) // editing
					statusChanged = true;
				else
					statusChanged = false;

				editStatus = 1;

				if (task != null && task.getScheduled())
					task.cancelTask(); // cancel

				task = new EdittingTimer(getThis());
				Timer timer = new Timer();

				timer.schedule(task, editDelay);
				task.setScheduled();
			}

			if (statusChanged)
				editStatusChange(editStatus);
		}

		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
		}
	}// end inner class ChatKeyListener

	class FrameListener implements WindowListener {

		public void windowClosing(WindowEvent e) {
			JFrame frame = (JFrame) e.getSource();

			int result = JOptionPane.showConfirmDialog(frame,
					"Are you sure you want to leave the chat?",
					"Exit Application", JOptionPane.YES_NO_OPTION);

			if (result == JOptionPane.YES_OPTION) {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				try {
					server.leave(client);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		public void windowOpened(WindowEvent e) {
		}

		public void windowClosed(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowActivated(WindowEvent e) {
		}

		public void windowDeactivated(WindowEvent e) {
		}

	}
}
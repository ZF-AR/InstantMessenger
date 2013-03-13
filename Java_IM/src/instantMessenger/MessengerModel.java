package instantMessenger;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.ArrayList;

import util.models.*;

public class MessengerModel implements PropertyListenerRegisterer {

	public String name = "";
	public String topic = "";
	public String status = "";
	public String message = "";
	public String history = "";
	public String editStatus = "";
	public int delaySeconds = 0;
	
	// For operational transformation: history
	//public ArrayList<String> msgList = new ArrayList<String>();
	public Operation topicOperation = new Operation();

	public Operation getTopicOperation() {
		return topicOperation;
	}

	public void setTopicOperation(Operation topicOperation) {
		this.topicOperation = topicOperation;
	}

	public PortDescription portDescription = new PortDescription("RelayServer",
			"Relay");

	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	public MessengerModel() {
	}

	public MessengerModel(String name) {
		setName(name);
	}

	public void addPropertyChangeListener(PropertyChangeListener aListener) {
		propertyChangeSupport.addPropertyChangeListener(aListener);
	}

	public int getDelaySeconds() {
		return delaySeconds;
	}

	public void setDelaySeconds(int delaySeconds) {
		this.delaySeconds = delaySeconds;
		propertyChangeSupport.firePropertyChange("delay", true,
				this.delaySeconds);
	}

	public PortDescription getPortDescription() {
		return portDescription;
	}

	public void setPortDescription(PortDescription portDescription) {
		this.portDescription = portDescription;
		propertyChangeSupport.firePropertyChange("portDescription", true,
				this.portDescription);
	}

	public void setPortDescription(String name, String type) {
		this.portDescription.setName(name);
		this.portDescription.setMode(type);
		propertyChangeSupport.firePropertyChange("portDescription", true,
				this.portDescription);
	}

	public void setPortDescription1(PortDescription portDescription) {
		this.portDescription = portDescription;
		propertyChangeSupport.firePropertyChange("portDescription", false,
				this.portDescription);
	}

	public void setPortDescription1(String name, String type) {
		this.portDescription.setName(name);
		this.portDescription.setMode(type);
		propertyChangeSupport.firePropertyChange("portDescription", false,
				this.portDescription);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		// String oldTopic = this.topic;
		this.topic = topic;

		propertyChangeSupport.firePropertyChange("topic", true, this.topic);
	}
	
	public void setTopicOperation(String topic, Operation op) {
		this.topic = topic;
		this.topicOperation = op;
		propertyChangeSupport.firePropertyChange("topicOperation", true, op);
	}

	public void setTopic1(String topic) {
		// String oldTopic = this.topic;
		this.topic = topic;

		propertyChangeSupport.firePropertyChange("topic", false, this.topic);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		String oldStatus = this.status;
		this.status = status;
		setHistory("You changed status:" + status + "\n"); // 11/27
		propertyChangeSupport.firePropertyChange("status", oldStatus,
				this.status);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
//		setHistory("You : " + message + "\n"); // 11/27
		setHistory(message + "\n");
		propertyChangeSupport.firePropertyChange("message", true, this.message);
	}

	public void setMessage1(String message) {
		this.message = message;
		propertyChangeSupport
				.firePropertyChange("message", false, this.message);
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		// String oldHistory = this.history;
		this.history = history;
		propertyChangeSupport.firePropertyChange("history", null, this.history);
	}
	
	public void setHistory1(String history) {
		// String oldHistory = this.history;
		this.history = history;
		propertyChangeSupport.firePropertyChange("historyFull", null, this.history);
	}

	public String getEditStatus() {
		return editStatus;
	}

	public void setEditStatus(String editStatus) {
		String oldEditStatus = this.editStatus;
		this.editStatus = editStatus;

		propertyChangeSupport.firePropertyChange("editStatus", oldEditStatus,
				this.editStatus);
	}
}

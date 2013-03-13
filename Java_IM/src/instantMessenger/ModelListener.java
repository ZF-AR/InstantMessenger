package instantMessenger;


import java.awt.Color;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

import util.models.PropertyListenerRegisterer;

public class ModelListener implements PropertyChangeListener {

	protected MessengerModel clientModel;
	protected TelepointerModel telepointerModel;
	protected RMIClientInterface client;
	protected RMIServerInterface server;
	protected OutCoupler outCoupler;
	protected OperationalTransformation topicOT;
	protected OperationalTransformation msgOT;
	
	public ModelListener(MessengerModel anClient, TelepointerModel aTelepointer) {
		this.clientModel = anClient;
		this.telepointerModel = aTelepointer;

		((PropertyListenerRegisterer) telepointerModel)
				.addPropertyChangeListener(this);
		((PropertyListenerRegisterer) clientModel)
				.addPropertyChangeListener(this);
	}

	public OperationalTransformation getMsgOT() {
		return msgOT;
	}

	public void setMsgOT(OperationalTransformation msgOT) {
		this.msgOT = msgOT;
	}

	public OperationalTransformation getTopicOT() {
		return topicOT;
	}	
	
	public void setTopicOT(OperationalTransformation topicOT) {
		this.topicOT = topicOT;
	}
	
	public void setTopicOTandMsgOT(OperationalTransformation topicOT, OperationalTransformation msgOT) {
		setTopicOT(topicOT);
		setMsgOT(msgOT);
	}

	public void setOutCoupler(OutCoupler outCoupler) {
		this.outCoupler = outCoupler;
	}

	public void setClient(RMIClientInterface client) {
		this.client = client;
	}

	public void setClientAndServer(RMIClientInterface client,
			RMIServerInterface server) {
		this.client = client;
		this.server = server;
	}

	public void createNewMessage(Object msg, String type) {
		ARMIMessageWithSource msgWithSource = null;
		try {
			msgWithSource = new ARMIMessageWithSource(client.getName(), type,
					msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		outCoupler.sendMessage(msgWithSource);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		String propertyName = evt.getPropertyName();
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();
		switch (propertyName.toLowerCase()) {
		case "message":
			if ((boolean) oldValue) {
				String chatMsg = (String) newValue;
				//outCoupler.sendMessage(chatMsg, "send");
				Operation op = new Operation("insert", 0, chatMsg, false, new OperationTimeStamp());
				op = msgOT.addOperation(op).deepCopy();
				outCoupler.sendOperation(op, "send");
			}
			break;
		case "topic":
			if ((boolean) oldValue) {
				String topicMsg = (String) newValue;
//				outCoupler.sendMessage(topicMsg, "topic");
			}
			break;
		case "topicoperation":
			if ((boolean) oldValue) {
				Operation op = (Operation) newValue;
				//outCoupler.sendMessage(topicMsg, "topic");
				Operation op1 = topicOT.addOperation(op).deepCopy();
				outCoupler.sendOperation(op1, "topic");
			}
			break;
		case "status":
			String statusMsg = (String) newValue;
//			outCoupler.sendMessage(statusMsg, "status");
			Operation op = new Operation("insert", 0, statusMsg, false, new OperationTimeStamp());
			op = msgOT.addOperation(op).deepCopy();
			outCoupler.sendOperation(op, "status");
			break;
		case "point":
			if ((boolean) oldValue) {
				Point point = (Point) newValue;
				outCoupler.sendMessage(point, "point");
			}
			break;
		case "color":
			Color color = (Color) newValue;
			outCoupler.sendMessage(color, "color");
			// createNewMessage(color,"color");
			break;
		case "width":
			if ((boolean) oldValue) {
				int width = (int) newValue;
				outCoupler.sendMessage(width, "pointwidth");
			}
			break;
		case "height":
			if ((boolean) oldValue) {
				int height = (int) newValue;
				outCoupler.sendMessage(height, "pointheight");
			}
			break;
		case "portdescription":
			if ((boolean) oldValue) {
				PortDescription portDescription = (PortDescription) newValue;
				outCoupler.setCurrentPort(portDescription);
				// createNewMessage(portDescription,"portDescription");
			}
			break;
		case "delay":
			outCoupler.setDelay((int) newValue);
			break;
		default:
			System.out.println("Unknown property change: " + propertyName);
		}
	}

}

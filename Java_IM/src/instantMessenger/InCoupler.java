package instantMessenger;


import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import util.models.PropertyListenerRegisterer;

public class InCoupler extends UnicastRemoteObject implements
		RMIClientInterface {

	protected MessengerModel messengerModel;
	protected TelepointerModel telepointerModel;
	protected OperationalTransformation topicOT;
	protected OperationalTransformation msgOT;
	protected long lastMsgDelay = 0;
	protected boolean isJitter = false;

	protected InCoupler() throws RemoteException {
	}

	public InCoupler(MessengerModel messengerModel) throws RemoteException {
		this.messengerModel = messengerModel;
	}
	
	public InCoupler(MessengerModel messengerModel, TelepointerModel telepointerModel) throws RemoteException {
		this.messengerModel = messengerModel;
		this.telepointerModel = telepointerModel;
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

	public void joinMessage(String name) throws RemoteException {
		try {
			String msg;
			if (!name.equals(messengerModel.getName()))
				msg = name + " has joined.\n";
			else
				msg = "You has joined.\n";
			messengerModel.setHistory(msg);
		} catch (Exception e) {
			System.out.println("Message Failure: join");
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(String name, String message) throws RemoteException {
//		try {
//			String msg;
//			if (!name.equals(messengerModel.getName())) {
//				msg = name + " : " + message + "\n";
//				messengerModel.setHistory(msg);
//			} else {
//				// msg = "You : " + message + "\n";
//				messengerModel.setMessage1("");
//			}
//			// clientModel.setHistory(msg);
//		} catch (Exception e) {
//			System.out.println("Message Failure: message");
//			e.printStackTrace();
//		}
		messengerModel.setHistory1(message);
	}

	public void exitMessage(String name) throws RemoteException {
		try {
			String msg;
			if (!name.equals(messengerModel.getName()))
				msg = name + " has left the conversation.\n";
			else
				msg = "You has left the conversation.\n";
			messengerModel.setHistory(msg);
		} catch (Exception e) {
			System.out.println("Message Failure: exit");
			e.printStackTrace();
		}
	}

	public void topicMessage(String name, String message)
			throws RemoteException {
		try {
			if (!name.equals(messengerModel.getName()))
				messengerModel.setTopic1(message);
		} catch (Exception e) {
			System.out.println("Message Failure: topic");
		}
	}

	public void statusMessage(String name, String message)
			throws RemoteException {
		try {
			String msg;
			if (!name.equals(messengerModel.getName())) {
				msg = name + " changed status:" + message + "\n";
			} else {
				msg = "You changed status:" + message + "\n";
				messengerModel.setStatus(message);
			}
			messengerModel.setHistory(msg);
		} catch (Exception e) {
			System.out.println("Message Failure: status");
		}
	}

	public void pointerMessage(String name, Point point) throws RemoteException {
		try {
			if (!name.equals(messengerModel.getName())) {
				telepointerModel.setJitter(isJitter);
				telepointerModel.setPoint1(point);
			}
		} catch (Exception e) {
			System.out.println("Message Failure: pointer");
			e.printStackTrace();
		}
	}

	public void pointerMessage(String name, TelepointerModel pointer)
			throws RemoteException {
		try {
			if (!name.equals(messengerModel.getName()))
				telepointerModel.setThis(pointer);
		} catch (Exception e) {
			System.out.println("Message Failure: telePointer");
		}
	}

	public void pointerWidth(String name, int width) throws RemoteException {
		try {
			if (!name.equals(messengerModel.getName()))
				telepointerModel.setWidth1(width);
		} catch (Exception e) {
			System.out.println("Message Failure: telePointer");
		}
	}

	public void pointerHeight(String name, int height) throws RemoteException {
		try {
			if (!name.equals(messengerModel.getName()))
				telepointerModel.setHeight1(height);
		} catch (Exception e) {
			System.out.println("Message Failure: telePointer");
		}
	}

	public void pointerMultipleMessage(String name,
			Collection<GlasspaneInteractor> telePointerList)
			throws RemoteException {
	}

	public String getName() throws RemoteException {
		return messengerModel.getName();
	}

	public String getStatus() throws RemoteException {
		return null;
	}

	public GlasspaneInteractor getTelePointer() throws RemoteException {
		return null;
	}

	public void setName(String name) throws RemoteException {
	}

	public void setStatus(String status) throws RemoteException {
	}

	public void setTelePointer(TelepointerModel telePointer)
			throws RemoteException {
		this.telepointerModel = telePointer;
	}

	public void setClientModel(MessengerModel clientModel)
			throws RemoteException {
		this.messengerModel = clientModel;
	}
	
	private boolean jitterDetection(long time1, long time2) {
		int thresholdMin = 2;
		int thresholdMax = 250;
		
		boolean isJitter = false;
		if(Math.abs(time1-time2) > thresholdMax)
			isJitter = true;
//		if(Math.abs(time1-time2) < thresholdMin)
//			isJitter = true;
		
		return isJitter;
	}

	public void newMessage(RMIMessageWithSource msg) throws RemoteException {

		long curMsgDelay = System.currentTimeMillis() - msg.getTimeStamp();
		
		if(lastMsgDelay > 0)
			isJitter = jitterDetection(lastMsgDelay, curMsgDelay);
		
		lastMsgDelay = curMsgDelay;
		
		switch (msg.getType().toLowerCase()) {
		case "join":
			this.joinMessage(msg.getSource());
			break;
		case "exit":
			this.exitMessage(msg.getSource());
			break;
		case "send":
			AMessageWithSource localMsg1 = new AMessageWithSource(msg);
			msgOT.transform(localMsg1.getOperation(true));
			this.sendMessage(msg.getSource(), msgOT.getHistory());
//			this.sendMessage(msg.getSource(), (String) msg.getMessage());
			break;
		case "topic":
			AMessageWithSource localMsg = new AMessageWithSource(msg);
			topicOT.transform(localMsg.getOperation(true));
			String topic = topicOT.getResult();
			this.topicMessage(msg.getSource(), topic);
			//this.topicMessage(msg.getSource(), (String) msg.getMessage());
			break;
		case "status":
			this.statusMessage(msg.getSource(), (String) msg.getMessage());
			break;
		case "point":
			this.pointerMessage(msg.getSource(), (Point) msg.getMessage());
			break;
		case "pointwidth":
			this.pointerWidth(msg.getSource(), (int) msg.getMessage());
			break;
		case "pointheight":
			this.pointerHeight(msg.getSource(), (int) msg.getMessage());
			break;
		default:
			System.out.println("RMIClient: Invalid message type: "
					+ msg.getType());
			break;
		}
	}
}

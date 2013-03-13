package instantMessenger;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ARMIMessageWithSource extends UnicastRemoteObject implements
		RMIMessageWithSource {
	private String userName;
	private String type;
	private Object message;
	private long timeStamp;
	private Operation op;

	public ARMIMessageWithSource() throws RemoteException {

	}

	public ARMIMessageWithSource(String userName, String type, Object message)
			throws RemoteException {
		this.userName = userName;
		this.type = type;
		this.message = message;
	}

	public String getSource() throws RemoteException {
		return userName;
	}

	public void setSource(String userName) throws RemoteException {
		this.userName = userName;
	}

	public String getType() throws RemoteException {
		return type;
	}

	public void setType(String type) throws RemoteException {
		this.type = type;
	}

	public Object getMessage() throws RemoteException {
		return message;
	}

	public void setMessage(Object message) throws RemoteException {
		this.message = message;
	}

	public void setMessageWithSource(String userName, Object message,
			String type) throws RemoteException {
		this.userName = userName;
		this.type = type;
		this.message = message;
	}

	@Override
	public long getTimeStamp() throws RemoteException {
		return timeStamp;
	}

	@Override
	public void setTimeStamp(long timeStamp) throws RemoteException {
		this.timeStamp = timeStamp;
	}

	@Override
	public void setMessageWithSource(AMessageWithSource msg)
			throws RemoteException {
		this.userName = msg.getSource();
		this.type = msg.getType();
		this.message = msg.getMessage();
		this.timeStamp = msg.getTimestamp();
		this.op = msg.getOperation(false);
	}

	public void setOperation(Operation op) throws RemoteException {
		this.op = op;
	}

	public Operation getOperation(boolean isClone) throws RemoteException {
		if(!isClone)
			return this.op;
		else {
			Operation op1 = new Operation();
			
			op1.index = op.index;
			op1.isServer = op.isServer;
			op1.msg = op.msg;
			op1.type = op.type;
			op1.timeStamp = op.timeStamp;
			
			return op1;
		}
	}

	@Override
	public void setMessageWithSource(RMIMessageWithSource msg)
			throws RemoteException {
		this.userName = msg.getSource();
		this.type = msg.getType();
		this.message = msg.getMessage();
		this.timeStamp = msg.getTimeStamp();
		this.op = msg.getOperation(false);
	}
}

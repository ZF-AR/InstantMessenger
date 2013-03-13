package instantMessenger;


import java.rmi.RemoteException;

public class AMessageWithSource {
	private String userName;
	private String type;
	private Object message;
	private long timestamp;
	private Operation op;

	public AMessageWithSource() {

	}
	
	public AMessageWithSource(RMIMessageWithSource rmiMsg) {
		setMessageWithRMIMsg(rmiMsg);
	}

	public AMessageWithSource(String userName, String type, Object message) {
		this.userName = userName;
		this.type = type;
		this.message = message;
		timestamp = System.currentTimeMillis();
	}
	
	public AMessageWithSource deepCopy() {
		AMessageWithSource msg = new AMessageWithSource();
		
		msg.message = this.message;
		msg.op = this.op;
		msg.timestamp = this.timestamp;
		msg.type = this.type;
		msg.userName = this.userName;
		
		return msg;
	}

	public String getSource() {
		return userName;
	}

	public void setSource(String userName) {
		this.userName = userName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}
	
	public void setMessageWithRMIMsg(RMIMessageWithSource rmiMsg) {
		try {
			this.userName = rmiMsg.getSource();
			this.type = rmiMsg.getType();
			this.message = rmiMsg.getMessage();
			this.timestamp = rmiMsg.getTimeStamp();
			this.op = rmiMsg.getOperation(false);
		} catch (RemoteException e) {
			System.out.println("Error in initialization with RMIMessageWithSource");
			e.printStackTrace();
		}
	}
	
	public void setMessageWithSource(AMessageWithSource msg) {
		this.userName = msg.getSource();
		this.type = msg.getType();
		this.message = msg.getMessage();
		this.timestamp = msg.getTimestamp();
	}

	public void setMessageWithSource(String userName, Object message,
			String type) {
		this.userName = userName;
		this.type = type;
		this.message = message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void generateTimestamp() {
		timestamp = System.currentTimeMillis() / 1000;
	}
	
	public void setOperation(Operation op) {
		this.op = op;
	}

	public Operation getOperation(boolean isClone) {
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
}

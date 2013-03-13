package instantMessenger;


import java.rmi.RemoteException;
import java.sql.Timestamp;

public class MessageWithTimeStamp extends ARMIMessageWithSource {

	long timestamp;

	public MessageWithTimeStamp() throws RemoteException {
		super();
	}

	public MessageWithTimeStamp(String userName, String type, Object message)
			throws RemoteException {
		super(userName, type, message);
		timestamp = System.currentTimeMillis();
	}

	public MessageWithTimeStamp(String userName, String type, Object message,
			int timestamp) throws RemoteException {
		super(userName, type, message);
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		try {
			super.setTimeStamp(timestamp);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		this.timestamp = timestamp;
	}

	public void generateTimestamp() {
		timestamp = System.currentTimeMillis();
	}
}

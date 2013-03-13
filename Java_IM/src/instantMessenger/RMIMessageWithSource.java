package instantMessenger;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIMessageWithSource extends Remote {
	public void setMessageWithSource(String userName, Object message,
			String type) throws RemoteException;

	public String getSource() throws RemoteException;

	public void setSource(String userName) throws RemoteException;

	public String getType() throws RemoteException;

	public void setType(String type) throws RemoteException;

	public Object getMessage() throws RemoteException;
	
	public void setMessageWithSource(AMessageWithSource msg) throws RemoteException;
	
	public void setMessageWithSource(RMIMessageWithSource msg)  throws RemoteException;

	public void setMessage(Object message) throws RemoteException;

	public long getTimeStamp() throws RemoteException;

	public void setTimeStamp(long timeStamp) throws RemoteException;
	
	public void setOperation(Operation op) throws RemoteException;
	
	public Operation getOperation(boolean isClone) throws RemoteException;
}

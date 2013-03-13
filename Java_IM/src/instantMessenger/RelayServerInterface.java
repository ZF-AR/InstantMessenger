package instantMessenger;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface RelayServerInterface extends Remote {
	public void setDelay(int delay) throws RemoteException;

	public void relayToAll(RMIMessageWithSource msg) throws RemoteException;

	public void relayToOthers(RMIMessageWithSource msg) throws RemoteException;
	
	public void relayToLateComer(RMIClientInterface lateClient) throws RemoteException;

	public void relay(String dest, RMIMessageWithSource msg)
			throws RemoteException;

	public void relay(Set<String> dest, RMIMessageWithSource msg)
			throws RemoteException;

	public void setConnectionStatus(boolean connected) throws RemoteException;

	public boolean getConnectionStatus() throws RemoteException;
}

package instantMessenger;


import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface ClientRMIInterface extends Remote {

	public void joinMessage(String name) throws RemoteException;

	public void sendMessage(String name, String message) throws RemoteException;

	public void exitMessage(String name) throws RemoteException;

	public void topicMessage(String name, String message)
			throws RemoteException;

	public void statusMessage(String name, String message)
			throws RemoteException;

	public void pointerMessage(String name, Point point) throws RemoteException;

	public void pointerMultipleMessage(String name,
			Collection<GlasspaneInteractor> telePointerList)
			throws RemoteException;

	public String getName() throws RemoteException;

	public String getStatus() throws RemoteException;

	public GlasspaneInteractor getTelePointer() throws RemoteException;

	// Not called remotely
	public void setName(String name) throws RemoteException;

	public void setStatus(String status) throws RemoteException;

	public void setTelePointer(GlasspaneInteractor telePointer)
			throws RemoteException;
}

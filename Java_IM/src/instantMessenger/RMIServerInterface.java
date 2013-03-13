package instantMessenger;


import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface RMIServerInterface extends Remote {

	public void join(RMIClientInterface n) throws RemoteException;

	public void talk(RMIClientInterface n, String s) throws RemoteException;

	public void leave(RMIClientInterface n) throws RemoteException;

	public void editTopic(RMIClientInterface n, String s)
			throws RemoteException;

	public void changeStatus(RMIClientInterface n, String s)
			throws RemoteException;

	public void repaintPointer(RMIClientInterface n, Point point)
			throws RemoteException;

	public void setPointerWidth(RMIClientInterface n, int width)
			throws RemoteException;

	public void setPointerHeight(RMIClientInterface n, int height)
			throws RemoteException;

	public void repaintTelePointer(RMIClientInterface n,
			TelepointerModel pointer) throws RemoteException;

	public void repaintPointerMultiple(RMIClientInterface n,
			Collection<GlasspaneInteractor> telePointerList)
			throws RemoteException;
}

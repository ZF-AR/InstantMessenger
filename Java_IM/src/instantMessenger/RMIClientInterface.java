package instantMessenger;


import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface RMIClientInterface extends Remote {

	public void newMessage(RMIMessageWithSource msg) throws RemoteException;

	// public void newMessage(MessageWithTimeStamp msg) throws RemoteException;

	public void joinMessage(String name) throws RemoteException;

	public void sendMessage(String name, String message) throws RemoteException;

	public void exitMessage(String name) throws RemoteException;

	public void topicMessage(String name, String message)
			throws RemoteException;

	public void statusMessage(String name, String message)
			throws RemoteException;

	public void pointerMessage(String name, Point point) throws RemoteException;

	public void pointerMessage(String name, TelepointerModel pointer)
			throws RemoteException;

	public void pointerWidth(String name, int width) throws RemoteException;

	public void pointerHeight(String name, int height) throws RemoteException;

	public void pointerMultipleMessage(String name,
			Collection<GlasspaneInteractor> telePointerList)
			throws RemoteException;

	public String getName() throws RemoteException;

	public String getStatus() throws RemoteException;

	public GlasspaneInteractor getTelePointer() throws RemoteException;
	
	public void setTopicOTandMsgOT(OperationalTransformation topicOT, OperationalTransformation msgOT) throws RemoteException;
	public void setTopicOT(OperationalTransformation topicOT) throws RemoteException;
	public void setMsgOT(OperationalTransformation msgOT) throws RemoteException;

	// Not called remotely
	public void setName(String name) throws RemoteException;

	public void setStatus(String status) throws RemoteException;

	public void setTelePointer(TelepointerModel telePointer)
			throws RemoteException;

	public void setClientModel(MessengerModel clientModel)
			throws RemoteException;
}

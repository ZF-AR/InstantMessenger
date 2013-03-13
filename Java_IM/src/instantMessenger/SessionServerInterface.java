package instantMessenger;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface SessionServerInterface extends Remote {
	public void joinAsClient(RMIClientInterface clientProxy)
			throws RemoteException;

	public void joinAsServer(RelayServerInterface relayServerProxy)
			throws RemoteException;

	public void leave(String name) throws RemoteException;

	public ArrayList<PortDescription> list() throws RemoteException;

	public ArrayList<RMIClientInterface> listClients() throws RemoteException;

	public RelayServerInterface getRelayServer() throws RemoteException;

	public RMIClientInterface getClientProxy(String name)
			throws RemoteException;

	public PortDescription getPortDescription(String name)
			throws RemoteException;
}

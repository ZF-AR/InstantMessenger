package instantMessenger;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SessionServer extends UnicastRemoteObject implements
		SessionServerInterface {
	private ArrayList<PortDescription> memberList = new ArrayList<PortDescription>();
	private ArrayList<RMIClientInterface> clientList = new ArrayList<RMIClientInterface>();
	private RelayServerInterface relayServerProxy;
	public RMIMessageWithSource aMessageWithSource = new ARMIMessageWithSource();

	public SessionServer() throws RemoteException {

	}

	public void joinAsClient(RMIClientInterface clientProxy)
			throws RemoteException {
		clientList.add(clientProxy);
		System.out.println("New member: " + clientProxy.getName());
		if (relayServerProxy != null
				&& relayServerProxy.getConnectionStatus() == true) {
			relayServerProxy.relayToLateComer(clientProxy);
		}
	}

	public void joinAsServer(RelayServerInterface relayServerProxy)
			throws RemoteException {
		this.relayServerProxy = relayServerProxy;
		this.relayServerProxy.setConnectionStatus(true);
		System.out.println("New member: RelayServer");
	}

	public void leave(String name) throws RemoteException {
		for (Iterator i = memberList.iterator(); i.hasNext();) {
			PortDescription member = (PortDescription) i.next();
			if (member.getName().equals(name)) {
				memberList.remove(member);
				break;
			}
		}
	}

	public ArrayList<RMIClientInterface> listClients() throws RemoteException {
		return clientList;
	}

	public RMIClientInterface getClientProxy(String name)
			throws RemoteException {
		for (Iterator i = clientList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			if (client.getName().equals(name)) {
				return client;
			}
		}
		return null;
	}

	public ArrayList<PortDescription> list() throws RemoteException {
		return memberList;
	}

	public PortDescription getPortDescription(String name)
			throws RemoteException {
		for (Iterator i = memberList.iterator(); i.hasNext();) {
			PortDescription member = (PortDescription) i.next();
			if (member.getName().equals(name)) {
				return member;
			}
		}
		return null;
	}

	public RelayServerInterface getRelayServer() throws RemoteException {
		return relayServerProxy;
	}

	public static void main(String[] args) {
		try {
			SessionServer sessionServer = new SessionServer();
			Naming.rebind("SessionServer", sessionServer);
			System.out.println("SessionServer started.");

			Naming.rebind("SessionServerMessage",
					sessionServer.aMessageWithSource);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}

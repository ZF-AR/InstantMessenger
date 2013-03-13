package instantMessenger;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import bus.uigen.ObjectEditor;
import bus.uigen.trace.TraceableDisplayAndWaitManagerFactory;

public class RelayServer extends UnicastRemoteObject implements
		RelayServerInterface {

	boolean connected = false;
	SessionServerInterface sessionServer;
	protected int delaySeconds = 0;
	private ArrayList<AMessageWithSource> msgHistory = new ArrayList<AMessageWithSource>();
	public RMIMessageWithSource aMessageWithSource = new ARMIMessageWithSource();
	private ArrayList<OperationalTransformation> listTopicOT = new ArrayList<OperationalTransformation>();
	private ArrayList<OperationalTransformation> listMsgOT = new ArrayList<OperationalTransformation>();
	private static ATraceableListener aListener;
	
	public RelayServer(SessionServerInterface sessionServer)
			throws RemoteException {
		this.sessionServer = sessionServer;
	}
	
	private void addToHistory(RMIMessageWithSource rmiMsg) {
		AMessageWithSource msg = new AMessageWithSource(rmiMsg);
		if( !msgHistory.isEmpty() && msg.getType().toLowerCase().equals("point")
		 && msgHistory.get(0).getType().toLowerCase().equals("point")) {
			msgHistory.get(0).setMessageWithSource(msg);
//			System.out.println("Point msg updated");
		}
		else {
			if(msg.getType().toLowerCase().equals("point"))
				msgHistory.add(0, msg);
			else
				msgHistory.add(msg);
		}
	}
	
	public synchronized void relayToLateComer(RMIClientInterface lateClient) throws RemoteException {
		
		for (Iterator i = msgHistory.iterator(); i.hasNext();) {
			aMessageWithSource.setMessageWithSource((AMessageWithSource)i.next());
			lateClient.newMessage(aMessageWithSource);
		}
		
//		aMessageWithSource.setMessageWithSource(lateClient.getName(), "", "join");
//		relayToAll(aMessageWithSource);
		
		OperationalTransformation otTopic = new OperationalTransformation(lateClient.getName(), true, true);
		listTopicOT.add(otTopic);
//		ObjectEditor.edit(listTopicOT.get(listTopicOT.size()-1));
		
		OperationalTransformation otMsg = new OperationalTransformation(lateClient.getName(), true, false);
		listMsgOT.add(otMsg);
//		ObjectEditor.edit(listMsgOT.get(listMsgOT.size()-1));
	}

	public synchronized void relayToAll(RMIMessageWithSource msg)
			throws RemoteException {
//		TracerInfo.newInfo("relayToAll", this);
		aListener.appendMsg("relayToAll" + " From: " + msg.getSource());
		
		ArrayList<RMIClientInterface> clientList = sessionServer.listClients();

		System.out.println("From: " + msg.getSource());

		for (Iterator i = clientList.iterator(); i.hasNext();) {

			RMIClientInterface client = (RMIClientInterface) i.next();

//			TracerInfo.newInfo("Relay to: " + client.getName() + "  Message: "
//					+ message, this);
//			aListener.appendMsg("Relay to: " + client.getName() + "  Message: "	+ message);

			client.newMessage(msg);
		}
		
		addToHistory(msg);
	}
	
	private String composeTracerMsg(RMIMessageWithSource msg, RMIClientInterface client) throws RemoteException {
		String message;

		if (msg.getType().toLowerCase().equals("point"))
			message = "point";
		else
			message = (String) msg.getMessage();

		if (msg.getType().toLowerCase().equals("join"))
			message = msg.getSource() + " join";
		
		return new String("Relay to: " + client.getName() + "  Message: " + message);
	}
	
	public boolean clearOTBuffer() {
		boolean isClear = false;
		
		int opSum = listTopicOT.get(0).getTimeStamp().sum();
		int i=1;
		for(i=1;i<listTopicOT.size();i++) {
			if(opSum != listTopicOT.get(i).getTimeStamp().sum())
				break;
		}
		if(i == listTopicOT.size())
			isClear = true;
		
		if(isClear) {
			for(i=1;i<listTopicOT.size();i++)
				listTopicOT.get(i).clearLocalBuffer();
		}
		
		return isClear;
	}
	
	public void operationTransformation(RMIMessageWithSource msg, ArrayList<OperationalTransformation> otList) throws RemoteException {
		Operation op = msg.getOperation(false).deepCopy();
		op.swapTimeStamp();
		for (int i = 0; i < otList.size(); i++) {
			if(otList.get(i).getName().equals(msg.getSource())) {
				System.out.println("***************");
				System.out.println("******" + otList.get(i).getName() + "******");
				op = otList.get(i).transform(op);
			}
		}
		op.isServer = true;
		for (int i = 0; i < otList.size(); i++) {
			if(!otList.get(i).getName().equals(msg.getSource())) {
				System.out.println("***************");
				System.out.println("****** " + otList.get(i).getName() + " ******");
				otList.get(i).addOperation(op);
			}
		}
	}

	public synchronized void relayToOthers(RMIMessageWithSource msg)
			throws RemoteException {
//		TracerInfo.newInfo("relayToOthers", this);
//		TracerInfo.newInfo("From: " + msg.getSource(), this);
		if(!msg.getType().equals("point"))
			aListener.appendMsg("relayToOthers" + " From: " + msg.getSource() + " " + msg.getType() + ":" + " " + msg.getMessage());
		
		new AMessageWithSource(msg);
		
		ArrayList<OperationalTransformation> otList = null;
		if(msg.getType().toLowerCase().equals("topic")) {
			otList = listTopicOT;
		}
		else if(!msg.getType().toLowerCase().equals("point")) {
			otList = listMsgOT;
		}
	
		if(otList != null)
			operationTransformation(msg,otList);
		
		int index = 0;
		ArrayList<RMIClientInterface> clientList = sessionServer.listClients();
		for (Iterator i = clientList.iterator(); i.hasNext();) {

			RMIClientInterface client = (RMIClientInterface) i.next();

			if (!client.getName().equals(msg.getSource())) {
				
				if (!msg.getType().toLowerCase().equals("point")) {
					Operation op = otList.get(index).getNewOperation(true);
					aMessageWithSource.setMessageWithSource(msg);
					aMessageWithSource.setOperation(op);
					client.newMessage(aMessageWithSource);
				}
				else
					client.newMessage(msg);
			}
			index++;
		}
		
		addToHistory(msg);
	}

	public synchronized void relay(String dest, RMIMessageWithSource msg)
			throws RemoteException {
		ArrayList<PortDescription> portList = sessionServer.list();
		for (Iterator i = portList.iterator(); i.hasNext();) {
			PortDescription member = (PortDescription) i.next();
			if (member.getName().equals(dest)) {
				RMIClientInterface client = (RMIClientInterface) member;
				client.newMessage(msg);
				break;
			}
		}
	}

	public synchronized void relay(Set<String> dest, RMIMessageWithSource msg)
			throws RemoteException {
		// to be implemented
	}

	public void setConnectionStatus(boolean connected) throws RemoteException {
		this.connected = connected;
		if (this.connected)
			System.out.println("RelayServer connected to SessionServer.");
	}

	public boolean getConnectionStatus() throws RemoteException {
		return connected;
	}

	public static void main(String[] args) {
		try {
			Remote remoteObject = Naming.lookup("SessionServer");
			if (remoteObject instanceof SessionServerInterface) {
				SessionServerInterface sessionServerProxy = (SessionServerInterface) remoteObject;
				RelayServer relayServer = new RelayServer(sessionServerProxy);
				Naming.rebind("RelayServer", relayServer);
				
				Naming.rebind("RelayServerMessage",
						relayServer.aMessageWithSource);

				RelayServerInterface relayServerProxy = (RelayServerInterface) Naming
						.lookup("RelayServer");

				sessionServerProxy.joinAsServer(relayServerProxy);

//				 ObjectEditor.edit(TraceableDisplayAndWaitManagerFactory
//				 .getTraceableDisplayAndPrintManager());
				 aListener = new ATraceableListener(
				 "RelayServer");
//				 TracerInfo.addListener(aListener);
				 
//				 TracerInfo.newInfo("RelayServer Started.", relayServer);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (NotBoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void setDelay(int delay) throws RemoteException {
		this.delaySeconds = delay;
	}
}

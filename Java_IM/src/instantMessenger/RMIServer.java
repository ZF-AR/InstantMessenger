package instantMessenger;


import java.awt.Point;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import util.models.PropertyListenerRegisterer;

public class RMIServer extends UnicastRemoteObject implements
		RMIServerInterface {

	private Collection<RMIClientInterface> threadList = new ArrayList<RMIClientInterface>();

	private String topic;
	private Point point;
	private Collection<GlasspaneInteractor> telePointerList = new ArrayList<GlasspaneInteractor>();
	private String telePointerMode = "None";
	private TelepointerModel pointer;
	private int width, height;

	public RMIServer() throws RemoteException {
	}

	public synchronized void join(RMIClientInterface n) throws RemoteException {
		threadList.add(n);
		for (Iterator i = threadList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			client.joinMessage(n.getName());
		}

		if (topic != null) {
			System.out.println(topic);
			n.topicMessage("", topic);
		}

		if (telePointerMode.equals("Single")) {
			if (point != null) {
				n.pointerMessage("", point);
				if (height != 0)
					n.pointerHeight("", height);
				if (width != 0)
					n.pointerWidth("", width);
			}
		} else if (telePointerMode.equals("Multiple")) {
			if (!telePointerList.isEmpty()) {
				n.pointerMultipleMessage("", telePointerList);
			}
		}
	}

	public synchronized void talk(RMIClientInterface n, String s)
			throws RemoteException {
		for (Iterator i = threadList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			client.sendMessage(n.getName(), s);
		}
	}

	public synchronized void leave(RMIClientInterface n) throws RemoteException {
		threadList.remove(n);
		for (Iterator i = threadList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			client.exitMessage(n.getName());
		}
	}

	@Override
	public synchronized void editTopic(RMIClientInterface n, String s)
			throws RemoteException {
		for (Iterator i = threadList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			client.topicMessage(n.getName(), s);
		}

		topic = s;
	}

	@Override
	public synchronized void changeStatus(RMIClientInterface n, String s)
			throws RemoteException {
		for (Iterator i = threadList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			client.statusMessage(n.getName(), s);
		}
	}

	@Override
	public synchronized void repaintPointer(RMIClientInterface n, Point point)
			throws RemoteException {
		for (Iterator i = threadList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			client.pointerMessage(n.getName(), point);
		}

		this.telePointerMode = "Single";
		this.point = point;
	}

	@Override
	public synchronized void setPointerWidth(RMIClientInterface n, int width)
			throws RemoteException {
		for (Iterator i = threadList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			client.pointerWidth(n.getName(), width);
		}
		this.width = width;
	}

	@Override
	public synchronized void setPointerHeight(RMIClientInterface n, int height)
			throws RemoteException {
		for (Iterator i = threadList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			client.pointerHeight(n.getName(), height);
		}
		this.height = height;
	}

	@Override
	public synchronized void repaintTelePointer(RMIClientInterface n,
			TelepointerModel pointer) throws RemoteException {
		for (Iterator i = threadList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			client.pointerMessage(n.getName(), pointer);
		}

		this.telePointerMode = "Single";
		this.pointer = pointer;
	}

	@Override
	public synchronized void repaintPointerMultiple(RMIClientInterface n,
			Collection<GlasspaneInteractor> telePointerList)
			throws RemoteException {
		for (Iterator i = threadList.iterator(); i.hasNext();) {
			RMIClientInterface client = (RMIClientInterface) i.next();
			client.pointerMultipleMessage(n.getName(), telePointerList);
		}

		this.telePointerMode = "Multiple";
		this.telePointerList = telePointerList;
	}

	public static void main(String[] args) {
		try {

			RMIServer server = new RMIServer();

			Naming.rebind("RMIServer", server);

			System.out.println("Server started.");
		} catch (java.net.MalformedURLException e) {
			System.out.println("Malformed URL for MessageServer name "
					+ e.toString());
		} catch (RemoteException e) {
			System.out.println("Communication error " + e.toString());
		}
	}

}

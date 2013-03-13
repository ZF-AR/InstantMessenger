package instantMessenger;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;

import util.trace.Tracer;

public class OutCoupler {
	private final BlockingQueue<AMessageWithSource> msgDelayQueue;
	private final BlockingQueue<DelayLength> delayQueue;
	private final BlockingQueue<AMessageWithSource> msgJitterQueue;

	protected String name;
	protected RelayServerInterface relayServerProxy;
	protected RMIClientInterface clientProxy;
	protected RMIClientInterface localProxy;
	protected SessionServerInterface sessionServerProxy;
	protected boolean isRelay = true;
	protected MessageWithTimeStamp aMessageWithSource;
	protected int delaySeconds = 0;
	protected boolean jitterMode = false;


	public OutCoupler(String name, SessionServerInterface sessionServerProxy,
			BlockingQueue<AMessageWithSource> msgQueue,
			BlockingQueue<DelayLength> delayQueue,
			BlockingQueue<AMessageWithSource> msgJitterQueue) {
		this.name = name;
		this.sessionServerProxy = sessionServerProxy;
		setRelayServerProxy();
		setLocalProxy();

		try {
			aMessageWithSource = new MessageWithTimeStamp();
			Naming.rebind(this.name + "Message", aMessageWithSource);
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}

		this.msgDelayQueue = msgQueue;
		this.delayQueue = delayQueue;
		this.msgJitterQueue = msgJitterQueue;
	}

	public void setDelay(int delay) {
		delaySeconds = delay;
	}

	public void setRelayServerProxy() {
		try {
			relayServerProxy = sessionServerProxy.getRelayServer();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void setLocalProxy() {
		try {
			localProxy = sessionServerProxy.getClientProxy(name);
			if (localProxy == null)
				System.out.println("Local proxy not found.");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(MessageWithTimeStamp msg) {

		try {
			if (isRelay) {
				relayServerProxy.setDelay(delaySeconds);
				relayServerProxy.relayToOthers(aMessageWithSource);
//				System.out.println("**Relay mode");
//				System.out.println("Message: " + msg.getMessage().toString());
			} else {
				clientProxy.newMessage(aMessageWithSource);
//				System.out.println("**P2P mode");
//				System.out.println("Message: " + msg.getMessage().toString());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void jitterMode(boolean isJitterMode) {
		this.jitterMode = isJitterMode;
	}
	
	public void sendOperation(Operation op, String type) {
		AMessageWithSource msgWithTimestamp = new AMessageWithSource(name,
				type, op.msg);
		msgWithTimestamp.setOperation(op.deepCopy());

		if (!jitterMode)
			sendMessage(msgWithTimestamp); // no delay and jitter
		else {
			try {
				msgDelayQueue.put(msgWithTimestamp);
				delayQueue.put(new DelayLength(delaySeconds*1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessage(Object msg, String type) {

		AMessageWithSource msgWithTimestamp = new AMessageWithSource(name,
				type, msg);

		if (!jitterMode)
			sendMessage(msgWithTimestamp); // no delay and jitter
		else {
			try {
				msgDelayQueue.put(msgWithTimestamp);
				delayQueue.put(new DelayLength(true));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addJitter(AMessageWithSource msg) {
		try {
			msgJitterQueue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(AMessageWithSource msg) {
		try {
			aMessageWithSource.setMessageWithSource(msg);

			// TracerInfo.newInfo("sendMessage: " + (String) msg, this);

			if (isRelay) {
				relayServerProxy.relayToOthers(aMessageWithSource);
//				System.out.println("**Relay mode");
//				System.out.println("Message: " + msg.getMessage().toString());
			} else {
				clientProxy.newMessage(aMessageWithSource);
//				System.out.println("**P2P mode");
//				System.out.println("Message: " + msg.getMessage().toString());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void sendMessage(RMIMessageWithSource msg) {
		if (isRelay) {
			try {
				relayServerProxy.relayToOthers(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			try {
				clientProxy.newMessage(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void setCurrentPort(PortDescription portDescription) {

		switch (portDescription.getMode().toLowerCase()) {
		case "relay":
			// default: use relayToOthers()
			isRelay = true;
			break;
		case "p2p":
			try {
				clientProxy = sessionServerProxy.getClientProxy(portDescription
						.getName());
				if (clientProxy != null) {
					isRelay = false;
				} else {
					System.out.println("P2P port not found.");
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
}

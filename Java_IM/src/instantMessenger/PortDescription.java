package instantMessenger;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PortDescription {

	String name;
	String mode;

	public PortDescription() {
	}

	public PortDescription(String name, String mode) {
		this.name = name;
		this.mode = mode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String type) {
		this.mode = type;
	}

}

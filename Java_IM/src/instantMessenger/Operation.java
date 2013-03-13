package instantMessenger;
import java.io.Serializable;

public class Operation implements Serializable {
	public String type = "";
	public int index = 0;
	public String msg = "";
	public boolean isServer = false;
	public OperationTimeStamp timeStamp = new OperationTimeStamp();
	
	public Operation() {
		
	}
	
	public Operation(String type, int index, String msg, boolean isServer, OperationTimeStamp timeStamp) {
		this.type = type;
		this.index = index;
		this.msg = msg;
		this.isServer = isServer;
		this.timeStamp = timeStamp;
	}
	
	public Operation deepCopy() {
		Operation op = new Operation();
		op.type = this.type;
		op.index = this.index;
		op.msg = this.msg;
		op.isServer = this.isServer;
		op.timeStamp = this.timeStamp.deepCopy();
		
		return op;
	}
	
	public void swapTimeStamp() {
		int tmp = this.timeStamp.local;
		this.timeStamp.local = this.timeStamp.remote;
		this.timeStamp.remote = tmp;
	}
}

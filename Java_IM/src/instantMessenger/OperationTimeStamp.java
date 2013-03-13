package instantMessenger;
import java.io.Serializable;


public class OperationTimeStamp implements Serializable {
	public int local = 0;
	public int remote = 0;
	
	public OperationTimeStamp deepCopy() {
		OperationTimeStamp ts =  new OperationTimeStamp();
		ts.local = this.local;
		ts.remote = this.remote;
		return ts;
	}
	
	public int sum() {
		return (local + remote);
	}
}

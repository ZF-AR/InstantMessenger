package instantMessenger;
import java.util.ArrayList;

public class OperationalTransformation {

	private OperationTimeStamp timeStamp;
	private ArrayList<Operation> localBuffer;
	private String result = "";
	private ArrayList<String> msgList;
	private String name = "";
	private boolean isServer;
	private boolean isTopic;
	
	
	public OperationalTransformation() {
		
	}

	public boolean isTopic() {
		return isTopic;
	}

	public void setTopic(boolean isTopic) {
		this.isTopic = isTopic;
	}

	public OperationalTransformation(String name, boolean isServer,
			boolean isTopic) {
		timeStamp = new OperationTimeStamp();
		localBuffer = new ArrayList<Operation>();
		msgList = new ArrayList<String>();
		this.name = name;
		this.isServer = isServer;
		this.isTopic = isTopic;
	}

	public boolean isServer() {
		return isServer;
	}

	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getHistory() {
		String str = "";

		for (int i = 0; i < msgList.size(); i++)
			str = str + msgList.get(i) + "\n";

		return str;
	}

	public OperationTimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(OperationTimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public ArrayList<Operation> getLocalBuffer() {
		return localBuffer;
	}

	public void setLocalBuffer(ArrayList<Operation> localBuffer) {
		this.localBuffer = localBuffer;
	}

	public ArrayList<String> getMsgList() {
		return msgList;
	}

	public String msgListToString() {
		String result = "";

		for (int i = msgList.size() - 1; i >= 0; i--) {
			result += msgList.get(i) + "\n";
		}

		return result;
	}

	public void setMsgList(ArrayList<String> msgList) {
		this.msgList = msgList;
	}

	public Operation getNewOperation(boolean isSwap) {
		Operation op = this.localBuffer.get(this.localBuffer.size() - 1)
				.deepCopy();
		if (isSwap)
			op.swapTimeStamp();
		return op;
	}

	public Operation addOperation(Operation op) {
		Operation op1 = op.deepCopy();
		localBuffer.add(op1);

		if (!isServer && !isTopic)
			op1.index = timeStamp.sum();

		timeStamp.local++;
		// attach local timeStamp
		op1.timeStamp.local = timeStamp.local;
		op1.timeStamp.remote = timeStamp.remote;

		if (isTopic) {
			if (result.equals(""))
				result = op1.msg;
			else
				result = Utilities.insertString(result, op1.msg, op1.index);
		} else {
			if (msgList.isEmpty())
				msgList.add(op1.msg);
			else
				msgList.add(op1.index, op1.msg);
		}
		
		echo();

		return op1.deepCopy();
	}
	
	private void echo() {
		System.out.println("***************");
		System.out.println("SiteTimeStamp: (" + timeStamp.local + ", " + timeStamp.remote + ")");
		if(localBuffer.isEmpty()) {
			System.out.println("LocalBuffer: (empty)");
			return;
		}
		System.out.println("LocalBuffer: ");
		for(int i=0; i<localBuffer.size(); i++) {
			Operation L = localBuffer.get(i);
			System.out.println("   (" + L.msg + ", " + L.index + ") " + 
					"(" + L.timeStamp.local + ", " + L.timeStamp.remote + ")");
		}
	}

	public void clearLocalBuffer() {
		if (!localBuffer.isEmpty())
			localBuffer.clear();
	}

	public void removeOperation(int pos) {
		localBuffer.remove(pos);
	}

	public Operation transform(Operation R) {
		
		System.out.println("***************");
		System.out.println("Remote operation: " + "(" + R.msg + ", " + R.index + ") " + 
				"(" + R.timeStamp.local + ", " + R.timeStamp.remote + ")");

		for (int i = 0; i < localBuffer.size(); i++) {
			if (R.timeStamp.local >= localBuffer.get(i).timeStamp.local) {
				localBuffer.remove(i);
				i--;
				continue;
			}

			// Do the operation transformation
			Operation L = localBuffer.get(i); // not reference

			R = transformInsert(R, L);
			L = transformInsert(L, R);
			L.timeStamp.remote = R.timeStamp.remote;

			// update (possibly transformed) local operation
			localBuffer.set(i, L); 
		}

		timeStamp.remote = R.timeStamp.remote;

		// execute R
		if (isTopic)
			result = Utilities.insertString(result, R.msg, R.index);
		else
			msgList.add(R.index, R.msg);
		
		echo();

		return R;
	}

	private Operation transformInsert(Operation R, Operation L) {
		Operation R1 = R.deepCopy();

		if ((R.index > L.index) || (R.index == L.index && !R.isServer))
			R1.index = R.index + 1;

		return R1;
	}
}

package instantMessenger;


import java.util.concurrent.BlockingQueue;

public class JitterAdder implements Runnable {
	private final BlockingQueue<AMessageWithSource> msgJitterQueue;
	private static final long SLEEPTIME = 400; // milliseconds
	private OutCoupler outCoupler;

	public JitterAdder(OutCoupler outCoupler,
			BlockingQueue<AMessageWithSource> msgJitterQueue) {
		this.outCoupler = outCoupler;
		this.msgJitterQueue = msgJitterQueue;
	}

	public void run() {
		AMessageWithSource msg = null;
		while (true) {
			try {
				Thread.sleep(SLEEPTIME);

				int size = msgJitterQueue.size();
				for (int i = 0; i < size; i++) {
					msg = msgJitterQueue.take();
					outCoupler.sendMessage(msg); // release message
					//outCoupler.sendOperation(op, type)
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}

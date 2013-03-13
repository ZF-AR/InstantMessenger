package instantMessenger;


import java.util.concurrent.BlockingQueue;

public class Delayer implements Runnable {

	private final BlockingQueue<AMessageWithSource> msgQueue;
	private final BlockingQueue<DelayLength> delayQueue;
	private OutCoupler outCoupler;

	public Delayer(OutCoupler outCoupler,
			BlockingQueue<AMessageWithSource> msgQueue,
			BlockingQueue<DelayLength> delayQueue) {
		this.outCoupler = outCoupler;
		this.msgQueue = msgQueue;
		this.delayQueue = delayQueue;
	}

	public void addMessage(AMessageWithSource msg, DelayLength delay) {
//		System.out.println("**Delayer: message added");
//		System.out.println("Message: " + msg.getMessage().toString());

		try {
			msgQueue.put(msg);
			delayQueue.put(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		AMessageWithSource msg = null;
		DelayLength delayLen = null;
//		System.out.println("**Delayer running...");

		while (true) {
			try {
				msg = msgQueue.take();
				delayLen = delayQueue.take();

//				long msgTS = msg.getTimestamp();
				long msgDelay = delayLen.getDelay();
//				long curTime = System.currentTimeMillis();
//				int delay = (int) (msgTS + msgDelay - curTime);

//				System.out.println("**Delayer: " + msgDelay);

				try {
					Thread.sleep(msgDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
//				System.out.println("**Delayer: " + delay);	
//				if (delay > 0) {
//					try {
//						Thread.sleep(delay);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}

				// outCoupler.sendMessage(msg); // release message
				outCoupler.addJitter(msg);

//				System.out.println("Take: " + msg.getMessage().toString());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

}

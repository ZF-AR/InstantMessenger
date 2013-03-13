package instantMessenger;


public class DelayLength {
	private long delay = 0;
	private static final long MIN = 10; // ms
	private static final long MAX = 20;

	public DelayLength() {

	}

	public DelayLength(long delay) {
		this.delay = delay;
	}

	public DelayLength(boolean random) {
		if (random) {
			randomDelay();
		}
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public void randomDelay() {
		delay = MIN + (long) (Math.random() * (MAX - MIN));
	}
}

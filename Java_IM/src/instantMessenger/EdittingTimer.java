package instantMessenger;


import java.util.TimerTask;

public class EdittingTimer extends TimerTask {
	private boolean scheduled = false;
	private boolean canceled = false;
	private MessengerController clientController;

	public EdittingTimer(MessengerController aController) {
		clientController = aController;
	}

	public void setScheduled() {
		scheduled = true;
		System.out.println("TimerTask scheduled.");
	}

	public boolean getScheduled() {
		return scheduled;
	}

	public void cancelTask() {
		canceled = true;
		scheduled = false;

		this.cancel();
		System.out.println("TimerTask canceled.");
	}

	/**
	 * When the timer executes, this code is run.
	 */
	public void run() {
		System.out.println("TimerTask excecuted.");

		if (!canceled) {
			clientController.editStatusChange(2);
		}

		this.cancel();
		scheduled = false;
		canceled = true;
	}
}

package instantMessenger;


import java.util.TimerTask;

public class PointCollectorTimer extends TimerTask {
	private boolean scheduled = false;
	private boolean canceled = false;
	private GlasspaneInteractor glassPaneInteractor;
	private PointCollector pointCollector;
	
	public PointCollectorTimer(GlasspaneInteractor glassPaneInteractor) {
		this.glassPaneInteractor = glassPaneInteractor;
	}
	
	public PointCollectorTimer(PointCollector pointCollector) {
		this.pointCollector = pointCollector;
	}
	
	public void setScheduled() {
		scheduled = true;
		System.out.println("**Start collecting points...");
	}
	
	public boolean getScheduled() {
		return scheduled;
	}

	public void cancelTask() {
		canceled = true;
		scheduled = false;

		this.cancel();
		//System.out.println("TimerTask canceled.");
	}
	
	public void run() {
		if (!canceled) {
			System.out.println("**End collecting points...");
			if(glassPaneInteractor!=null)
				glassPaneInteractor.changePointList();
			else
				pointCollector.changePointList();
		}

		this.cancel();
		scheduled = false;
		canceled = true;
	}
}

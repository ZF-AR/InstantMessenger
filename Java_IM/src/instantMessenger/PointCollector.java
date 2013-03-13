package instantMessenger;


import java.awt.Color;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PointCollector implements PropertyChangeListener, Runnable {

	protected PointCollectorTimer task;
	private volatile boolean isPointQueue = true;
	private BlockingQueue<Point> curQueue;
	private final BlockingQueue<Point> pointQueue;
	private final BlockingQueue<Point> pointBufferQueue;

	public PointCollector(BlockingQueue<Point> pointQueue, 
			BlockingQueue<Point> pointBufferQueue) {
		this.pointQueue = pointQueue;
		this.pointBufferQueue = pointBufferQueue;
	}
	
	public void run() {
		System.out.println("**PointCollector Thread started.");
		while (true) {
			
		}
	}
	
	public boolean getQueue() {
		return isPointQueue;
	}

	public void propertyChange(PropertyChangeEvent aPropertyChange) {
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("point")) {
			System.out.println(aPropertyChange.getNewValue());
			Point point = (Point) aPropertyChange.getNewValue();

			if (task == null || !task.getScheduled()) {
				task = new PointCollectorTimer(this);
				Timer timer = new Timer();
				timer.schedule(task, 2 * 1000);
				task.setScheduled();
			}

			try {
				curQueue.put(point);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void changePointList() {
		isPointQueue = !isPointQueue;
		if(isPointQueue)
			curQueue = pointQueue;
		else
			curQueue = pointBufferQueue;
	}

}

package instantMessenger;


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import util.models.PropertyListenerRegisterer;

public class GlasspaneInteractor extends JComponent implements
		PropertyChangeListener, ActionListener {

	protected TelepointerModel telepointerModel;
	protected MessengerView clientView;
	protected RMIClientInterface client;
	protected RMIServerInterface server;
	protected PointCollectorTimer task;
	private static PointerTrail pointerTrail; // Pointer trail

	int width;
	int height;
	Point point;
	boolean filled;
	Color color;
	String mode = "None";
	
	boolean isBufferArray = false;
	Point [] line = new Point[2];
	ArrayList<Point> curDrawArray;
	ArrayList<Point> curPointCollectionArray;
	ArrayList<Point> pointArray = new ArrayList<Point>();
	ArrayList<Point> pointBufferArray = new ArrayList<Point>();
	
	PointCollector pointCollector;
	private BlockingQueue<Point> curQueue;
	private final BlockingQueue<Point> pointQueue = new 
			ArrayBlockingQueue<Point>(100);
	private final BlockingQueue<Point> pointBufferQueue = new 
			ArrayBlockingQueue<Point>(100);
	
	OutCoupler outCoupler;

	public GlasspaneInteractor(TelepointerModel anTPmodel, MessengerView anIMgui) {
		
		telepointerModel = anTPmodel;
		clientView = anIMgui;

		JFrame frame = clientView.getMainFrame();
		JMenu menu = clientView.getTelePointerMenu();
		JComboBox comboBox = clientView.getStatusBox();

		JRadioButtonMenuItem rbMenuItem;
		for (int i = 0; i < menu.getMenuComponentCount(); i++) {
			rbMenuItem = (JRadioButtonMenuItem) menu.getMenuComponent(i);
			rbMenuItem.addActionListener(this);
		}

		frame.setGlassPane(this);
		CBListenerInner listener = new CBListenerInner(menu, comboBox, this,
				frame.getContentPane());
		addMouseListener(listener);
		addMouseMotionListener(listener);

		((PropertyListenerRegisterer) telepointerModel)
				.addPropertyChangeListener(this);

		initParameters();
		
		curPointCollectionArray = pointArray;
		
		point = new Point(telepointerModel.getX(),
				telepointerModel.getY());
		pointerTrail = new PointerTrail(50);
		pointerTrail.leaveTrail(new Point(telepointerModel.getX(),
				telepointerModel.getY()));

	}
	
	void setOutCouper(OutCoupler outCoupler) {
		this.outCoupler = outCoupler;
	}

	public void initParameters() {
		width = telepointerModel.getWidth();
		height = telepointerModel.getHeight();
		color = telepointerModel.getColor();
		filled = telepointerModel.isFilled();
	}

	public void setPoint(Point p) {
		telepointerModel.setPoint(p);
	}
	
	public void changePointList() {
		
		curDrawArray = curPointCollectionArray;
		
		isBufferArray = !isBufferArray;
		if(!isBufferArray) {
			curPointCollectionArray = pointArray;
		}
		else {
			curPointCollectionArray = pointBufferArray;
		}
		
		int j=0;
		for(int i=0; i<3*curDrawArray.size(); i++) {
			
			if(i<curDrawArray.size())
				j=i;		
			pointerTrail.leaveTrail(curDrawArray.get(j));
			repaint();
			
			try {
				Thread.sleep(20);
			}
			catch(Exception e) {
				return;
			}
		}
		curDrawArray.clear();
	}

	protected void paintComponent(Graphics g) {

		if (mode.equals("Single") || mode.equals("JitterMode")) {
			if (point == null) {
				point = new Point(telepointerModel.getX(),
						telepointerModel.getY());
			}
			if (point != null) {
				g.setColor(color);
				if (filled)
					g.fillOval(point.x - width / 2, point.y - height / 2,
							width, height);
				else
					g.drawOval(point.x - width / 2, point.y - height / 2,
							width, height);
			}
		} else if (mode.equals("Multiple")) {
			paintMultipleComponent(g);
		} else if (mode.equals("JitterRecovery")) {	
//			super.paintComponent(g);
			pointerTrail.draw((Graphics2D) g);
		}
	}
	

	protected void paintMultipleComponent(Graphics g) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());

		boolean needRepaint = !mode.equals(source.getText());

		mode = source.getText();
		switch (mode) {
		case "None":
			setVisible(false);
			break;
		case "Single":
			outCoupler.jitterMode(false);
			setVisible(true);
			break;
		case "Multiple":
			setVisible(true);
			break;
		case "JitterMode":	
			outCoupler.jitterMode(true);
			setVisible(true);
			break;
		case "JitterRecovery":
			outCoupler.jitterMode(true);
			setVisible(true);
//			drawThread.run();
			break;
		}

		if (needRepaint) {
			// Point point = new Point(width/2, height/2);
			// setPoint(point);
			repaint();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent aPropertyChange) {
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("point")) {
			System.out.println(aPropertyChange.getNewValue());
			point = (Point) aPropertyChange.getNewValue();
			if (mode.equals("Single") || mode.equals("JitterMode")) {
				repaint();
			} 
			else if (mode.equals("JitterRecovery")) {
				if (task == null || !task.getScheduled()) {
					task = new PointCollectorTimer(this);
					Timer timer = new Timer();
					timer.schedule(task, 2*1000);
					task.setScheduled();
				}
				
				curPointCollectionArray.add(point);
			}
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("color")) {
			System.out.println(aPropertyChange.getNewValue());
			color = (Color) aPropertyChange.getNewValue();
			repaint();
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("width")) {
			System.out.println(aPropertyChange.getNewValue());
			width = (int) aPropertyChange.getNewValue();
			repaint();
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("height")) {
			System.out.println(aPropertyChange.getNewValue());
			height = (int) aPropertyChange.getNewValue();
			repaint();
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("x")) {
			System.out.println(aPropertyChange.getNewValue());
			int x = (int) aPropertyChange.getNewValue();
			point.x = x;
			repaint();
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("y")) {
			System.out.println(aPropertyChange.getNewValue());
			int y = (int) aPropertyChange.getNewValue();
			point.y = y;
			repaint();
		}
	}
}

/**
 * Listen for all events that our check box is likely to be interested in.
 * Redispatch them to the check box.
 */
class CBListenerInner extends MouseInputAdapter {
	Toolkit toolkit;
	Component liveButton;
	Component liveCombobox;
	GlasspaneInteractor telePointer;
	Container contentPane;

	public CBListenerInner(Component liveButton, GlasspaneInteractor telePointer,
			Container contentPane) {
		toolkit = Toolkit.getDefaultToolkit();
		this.liveButton = liveButton;
		this.telePointer = telePointer;
		this.contentPane = contentPane;
	}

	public CBListenerInner(Component liveButton, Component liveCombobox,
			GlasspaneInteractor telePointer, Container contentPane) {
		toolkit = Toolkit.getDefaultToolkit();
		this.liveButton = liveButton;
		this.liveCombobox = liveCombobox;
		this.telePointer = telePointer;
		this.contentPane = contentPane;
	}

	public void mouseMoved(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mouseDragged(MouseEvent e) {
		redispatchMouseEvent(e, true);
	}

	public void mouseClicked(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mouseEntered(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mouseExited(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mousePressed(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mouseReleased(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	// Returns just the class name -- no package info.
	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex + 1);
	}

	// A basic implementation of redispatching events.
	private void redispatchMouseEvent(MouseEvent e, boolean repaint) {
		Point glassPanePoint = e.getPoint();
		Container container = contentPane;
		Point containerPoint = SwingUtilities.convertPoint(telePointer,
				glassPanePoint, contentPane);
		if (containerPoint.y < 0) { // we're not in the content pane
			// if (containerPoint.y + menuBar.getHeight() >= 0) {
			// //The mouse event is over the menu bar.
			// //Could handle specially.
			// } else {
			// //The mouse event is over non-system window
			// //decorations, such as the ones provided by
			// //the Java look and feel.
			// //Could handle specially.
			// }
		} else {
			// The mouse event is probably over the content pane.
			// Find out exactly which component it's over.
			Component component = SwingUtilities.getDeepestComponentAt(
					container, containerPoint.x, containerPoint.y);

			// System.out.println(getClassName(component));

			if ((component != null)
					&& (component.equals(liveButton)
							|| getClassName(component).equals(
									"AquaComboBoxButton") || getClassName(
								component).equals("JTextField"))) {

				// Forward events over the check box.
				Point componentPoint = SwingUtilities.convertPoint(telePointer,
						glassPanePoint, component);
				component
						.dispatchEvent(new MouseEvent(component, e.getID(), e
								.getWhen(), e.getModifiers(), componentPoint.x,
								componentPoint.y, e.getClickCount(), e
										.isPopupTrigger()));
			}
		}

		// Update the glass pane if requested.
		if (repaint) {
			telePointer.setPoint(glassPanePoint);
			// telePointer.setPoint(glassPanePoint, false);
			telePointer.repaint();
		}
	}
}

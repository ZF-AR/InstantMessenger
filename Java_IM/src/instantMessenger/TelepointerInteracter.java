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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import util.models.PropertyListenerRegisterer;

public class TelepointerInteracter implements PropertyChangeListener,
		ActionListener {

	protected TelepointerModel telepointerModel;
	protected MessengerView clientView;
	protected PointerTrail pointerTrail = new PointerTrail(200);
	protected DrawPanel drawPanel;

	int width;
	int height;
	Point point;
	boolean filled;
	Color color;
	String mode = "None";
	boolean isJitter = false;

	OutCoupler outCoupler;

	public TelepointerInteracter(TelepointerModel anTPmodel,
			MessengerView anIMgui) {

		telepointerModel = anTPmodel;
		clientView = anIMgui;

		initParameters();

		((PropertyListenerRegisterer) telepointerModel)
		.addPropertyChangeListener(this);
		
		JFrame frame = clientView.getMainFrame();
		JMenu menu = clientView.getTelePointerMenu();
		JComboBox comboBox = clientView.getStatusBox();

		JRadioButtonMenuItem rbMenuItem;
		for (int i = 0; i < menu.getMenuComponentCount(); i++) {
			rbMenuItem = (JRadioButtonMenuItem) menu.getMenuComponent(i);
			rbMenuItem.addActionListener(this);
		}
		
		drawPanel = new DrawPanel(frame, menu, comboBox);
	}

	void setOutCouper(OutCoupler outCoupler) {
		this.outCoupler = outCoupler;
	}

	public void initParameters() {
		point = new Point(telepointerModel.getX(), telepointerModel.getY());
		width = telepointerModel.getWidth();
		height = telepointerModel.getHeight();
		color = telepointerModel.getColor();
		filled = telepointerModel.isFilled();
	}

	public void setPoint(Point p) {
		telepointerModel.setPoint(p);
	}
	
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());

		boolean needRepaint = !mode.equals(source.getText());

		mode = source.getText();
		switch (mode) {
//		case "None":
//			drawPanel.setVisible(false);
//			break;
//		case "Single":
//			outCoupler.jitterMode(false);
//			drawPanel.setVisible(true);
//			break;
//		case "Multiple":
//			drawPanel.setVisible(true);
//			break;
		case "TelepointerShow":
//			outCoupler.jitterMode(false);
			drawPanel.setVisible(true);
			break;
		case "TelepointerHide":
			drawPanel.setVisible(false);
			break;
		case "JitterON":
			outCoupler.jitterMode(true);
//			drawPanel.setVisible(true);
			break;
		case "JitterOFF":
			outCoupler.jitterMode(false);
//			drawPanel.setVisible(true);
			break;
		case "JitterRecovery":
			outCoupler.jitterMode(true);
//			drawPanel.setVisible(true);
			break;
		}

		if (needRepaint) {
			drawPanel.repaint();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent aPropertyChange) {
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("point")) {
//			System.out.println(aPropertyChange.getNewValue());
			point = (Point) aPropertyChange.getNewValue();
			if (mode.equals("Single") || mode.equals("JitterMode")) {
				drawPanel.repaint();
			}
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("jitter")) {
			System.out.println("isJitter: "+ aPropertyChange.getNewValue());
			boolean newValue = (boolean) aPropertyChange.getNewValue();
			if(pointerTrail.arePtsAllSame())
				isJitter = newValue;
			drawPanel.repaint();
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("color")) {
			System.out.println(aPropertyChange.getNewValue());
			color = (Color) aPropertyChange.getNewValue();
			drawPanel.repaint();
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("width")) {
			System.out.println(aPropertyChange.getNewValue());
			width = (int) aPropertyChange.getNewValue();
			drawPanel.repaint();
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("height")) {
			System.out.println(aPropertyChange.getNewValue());
			height = (int) aPropertyChange.getNewValue();
			drawPanel.repaint();
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("x")) {
			System.out.println(aPropertyChange.getNewValue());
			int x = (int) aPropertyChange.getNewValue();
			point.x = x;
			drawPanel.repaint();
		}
		if (aPropertyChange.getPropertyName().equalsIgnoreCase("y")) {
			System.out.println(aPropertyChange.getNewValue());
			int y = (int) aPropertyChange.getNewValue();
			point.y = y;
			drawPanel.repaint();
		}
	}

	public class DrawPanel extends JComponent {
		private DrawThread drawThread = new DrawThread();
		//private PointerTrail pointerTrail = new PointerTrail(200);

		public DrawPanel(JFrame frame, Component liveButton, Component liveCombobox) {
			frame.setGlassPane(this);
			CBListener listener = new CBListener(liveButton, liveCombobox, this,
					frame.getContentPane());
			addMouseListener(listener);
			addMouseMotionListener(listener);
			drawThread.start();
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			if (mode.equals("Single") || mode.equals("JitterMode") || !isJitter) {
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
				// paintMultipleComponent(g);
			} else if (mode.equals("JitterRecovery") && isJitter) {
				pointerTrail.draw((Graphics2D) g);
			}
		}

		private class DrawThread extends Thread {
			public void run() {
				while (true) {
					pointerTrail.leaveTrail(new Point(point.x, point.y));
					repaint();

					try {
						Thread.sleep(4);
					} catch (Exception e) {
						return;
					}
				}
			}
		}
	}
	
	class CBListener extends MouseInputAdapter {
		Toolkit toolkit;
		Component liveButton;
		Component liveCombobox;
		DrawPanel telePointer;
		Container contentPane;

		public CBListener(Component liveButton, Component liveCombobox,
				DrawPanel drawPanel, Container contentPane) {
			toolkit = Toolkit.getDefaultToolkit();
			this.liveButton = liveButton;
			this.liveCombobox = liveCombobox;
			this.telePointer = drawPanel;
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
			Point containerPoint = SwingUtilities.convertPoint(drawPanel,
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
					Point componentPoint = SwingUtilities.convertPoint(drawPanel,
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
				setPoint(glassPanePoint);
				drawPanel.repaint();
			}
		}
	}
}

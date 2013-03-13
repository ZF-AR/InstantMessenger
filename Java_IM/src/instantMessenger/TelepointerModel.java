package instantMessenger;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import util.annotations.StructurePattern;
import util.annotations.StructurePatternNames;
import util.annotations.StructurePattern;
import util.models.PropertyListenerRegisterer;
import bus.uigen.shapes.*;

public class TelepointerModel implements Oval, PropertyListenerRegisterer {

	private int x;
	private int y;
	private int width;
	private int height;
	private Point point;
	private boolean filled;
	private Color color;
	private boolean isJitter;

	public boolean isJitter() {
		return isJitter;
	}

	public void setJitter(boolean isJitter) {
		this.isJitter = isJitter;
		propertyChangeSupport.firePropertyChange("jitter", null, isJitter);
	}

	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	public TelepointerModel() {
	}

	public TelepointerModel(int initX, int initY, int initWidth, int initHeight) {
		x = initX;
		y = initY;
		width = initWidth;
		height = initHeight;
	}

	public void addPropertyChangeListener(PropertyChangeListener aListener) {
		propertyChangeSupport.addPropertyChangeListener(aListener);
	}

	public void setThis(TelepointerModel pointer) {
		setPoint(pointer.getPoint());
		setColor(pointer.getColor());
		setWidth(pointer.getWidth());
		setHeight(pointer.getHeight());
		setFilled(pointer.isFilled());
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		int oldX = x;
		this.x = x;
		propertyChangeSupport.firePropertyChange("x", oldX, this.x);
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		int oldY = y;
		this.y = y;
		propertyChangeSupport.firePropertyChange("y", oldY, this.y);
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {

		if (this.point == null)
			this.point = new Point(x, y);

		Point oldPoint = this.point;
		this.point = point;

		if (point != null) {
			setX(point.x);
			setY(point.y);
		}

		propertyChangeSupport.firePropertyChange("point", true, this.point);
	}

	public void setPoint1(Point point) {

		if (this.point == null)
			this.point = new Point(x, y);

		Point oldPoint = this.point;
		this.point = point;

		if (point != null) {
			setX(point.x);
			setY(point.y);
		}

		propertyChangeSupport.firePropertyChange("point", false, this.point);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		// int oldWidth = this.width;
		this.width = width;
		propertyChangeSupport.firePropertyChange("width", true, this.width);
	}

	public void setWidth1(int width) {
		// int oldWidth = this.width;
		this.width = width;
		propertyChangeSupport.firePropertyChange("width", false, this.width);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		// int oldHeight = this.height;
		this.height = height;
		propertyChangeSupport.firePropertyChange("height", true, this.height);
	}

	public void setHeight1(int height) {
		// int oldHeight = this.height;
		this.height = height;
		propertyChangeSupport.firePropertyChange("height", false, this.height);
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean filled) {
		boolean oldFilled = this.filled;
		this.filled = filled;
		propertyChangeSupport.firePropertyChange("filled", oldFilled,
				this.filled);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		Color oldColor = this.color;
		this.color = color;
		propertyChangeSupport.firePropertyChange("color", oldColor, this.color);
	}
}

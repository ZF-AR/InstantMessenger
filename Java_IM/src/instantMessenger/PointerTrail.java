package instantMessenger;

import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;

class PointerTrail
{
	private ArrayList pointList; // List of points in the trail
	private int length; // Maximum number of points allowed in the arraylist
	private boolean arePtsAllSame = true;
	private int animationCount = 0;
	
	public PointerTrail(int length)
	{
		// Create the list
		pointList = new ArrayList();
		this.length = length;
	}
	
	public void leaveTrail(Point newPoint)
	{
		// Add a new point to be left behind
		pointList.add(newPoint);
		// If it is too long, remove the last one
		if(pointList.size() >= length)
			pointList.remove(0);
	}
	
	public boolean arePtsAllSame() {
		return arePtsAllSame;
	}
	
	public void draw(Graphics2D g)
	{
		if(pointList.size() < 2)
			return;
		// Calculate each individual alpha step
		double alphaStep = 127/(double)(pointList.size());
		// Draw the trail
		for(int i = 0;i < pointList.size()-1;i++)
		{
			// Get the current alpha
			int alpha = (int)(alphaStep*i)+127;
			if(alpha > 255)
				alpha = 255;
			// Set the current color and fade
			g.setColor(new Color(255,0,0,alpha));
			// Get the points to draw the line with
			Point from = (Point)(pointList.get(i));
			Point to = (Point)(pointList.get(i+1));
			// Draw the line
			g.drawLine(from.x,from.y,to.x,to.y);
			
			if(i==pointList.size()-2) {
				g.setColor(Color.RED);
				g.fillOval(to.x - 20 / 2, to.y - 20 / 2,
					20, 20);
			}
		}
		
		int idx;		
		for(idx = 0;idx < pointList.size()-1;idx++)
		{
			Point from = (Point)(pointList.get(idx));
			Point to = (Point)(pointList.get(idx+1));

			if(from.x != to.x || from.y != to.y) {
				arePtsAllSame = false;
				break;
			}
		}
		if(idx==pointList.size()-1) {
			arePtsAllSame = true;
		}
	}
}
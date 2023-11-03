


import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import processing.core.PVector;

public abstract class SimulationObject {
	protected PVector pos;
	protected Dimension dim;
	protected float size;
	protected Area outline;
	
	public SimulationObject(float x, float y, int w, int h, float size) {
		this.pos = new PVector(x,y);
		this.dim = new Dimension(w,h);
		this.size = size;
		setShapeAttributes();
		setOutline();
	}
	
	public Rectangle2D getBoundingBox() {
		return getOutline().getBounds2D();
	}
	
	public float getSize() {
		return size;
	}
	
	public PVector getPos() {
		return pos;
	}
	
	protected abstract boolean isColliding(Creature creature);
	public abstract void draw(Graphics2D g2);
	public abstract void drawInfo(Graphics2D g2);
	protected abstract void setShapeAttributes();
	protected abstract void setOutline();
	protected abstract Shape getOutline();
	public abstract void checkCollision(ArrayList<Creature> fishArr);


}

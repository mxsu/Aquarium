import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import processing.core.PVector;

public class Food {
	
	private PVector pos;
	private double scale = 1;
	private Arc2D.Double shrimp;
	private Ellipse2D.Double head;
	private int size = 10;
	private Area outline;
	private Color c = new Color(255,145,164);

	public Food(PVector pos, int size) {
//		pos = new PVector(
//				(float) (pos.x),
//				(float) (pos.y));
		this.pos = pos;
		shrimp = new Arc2D.Double();
		head = new Ellipse2D.Double();
		setAttributes(size);
		size = this.size;
		
	}
	
	
	//Drawing stuff
	public void setAttributes(int size) {
		shrimp.setArc(-size/2, -5, size, size,0,90, Arc2D.OPEN);;
		head.setFrame(-size/4,size/4-size/2-2,size/4,size/4);
		outline = new Area(head);
		outline = new Area(shrimp);
	}
	
	public void drawGeomShrimp(Graphics2D g2) {
		AffineTransform trans = g2.getTransform();
		g2.translate(pos.x+2,pos.y+2);
		g2.setColor(getCol());
		g2.setStroke(new BasicStroke((int) (size*0.6)));
		g2.draw(shrimp);
		//g2.fill(shrimp);
		g2.setColor(Color.red);
		g2.draw(head);
		g2.fill(head);
		g2.setTransform(trans);
	}

	public Shape getBoundary() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		at.scale(scale, scale);
		return at.createTransformedShape(outline);
	}
	
	public void enlarge() {
		scale *= 1.1;
	}
	
	public PVector getPos() {
		return pos;
	}

	public double getScale() {
		// TODO Auto-generated method stub
		return scale;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Color getCol() {
		return c;
	}

	public void setCol(Color c) {
		
		this.c = c;
	}


	


}

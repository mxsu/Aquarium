import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import processing.core.PVector;



/* Draw, move, are different from the superclass so it
 * is in the subclass. The Predator does most of the same 
 * movements as the Creature superclass but the draw is in
 * the subclass because it has a different look. It also 
 * inherits a lot of the same movements but different 
 * from the prey when it finds a target. Since the target
 * is not food, but prey.
 * 
 * chasePrey is a new method and not in the superclass
 * because only the predator does this action, I can use 
 * instanceof and cast typing to call this method
 * 
 * the fields int and chasing are in the predator class
 * because the objects hold the timer for chasing the fish
 */
public class Predator extends Creature{
	int timer;
	boolean chasing = false;
	// GEOM STUFF
	protected Line2D.Double tongue;
	protected Arc2D.Double body, tail, tail2, fin, fin2;
	protected Ellipse2D.Double eye;
	
	
	public Predator(PVector pos, int w, int h, double sc) {
		super(pos, w, h, sc);
		
		maxSpeed = 2;
		
		this.dimension = new Dimension(75,50);
		float sight = (float) (dimension.width * 2 * .75f);
		fov = new Arc2D.Double(-sight,-sight,sight*2,sight*2, 0, 360, Arc2D.PIE);
		
		double velX = (Math.random() * 1) - 1;
		double velY = (Math.random() * 1) - 1;
		setVel(new PVector((float) velX, (float) velY));

		// MAYBE I SHOULD MAKE A UTIL CLASS :)
		int r, g, b;
		r = (int) (Math.random() * 255);
		g = (int) (Math.random() * 255);
		b = (int) (Math.random() * 255);
		setCol(new Color(r, g, b));
		tailLength = (int) (Math.random() * (h - h / 2) + h / 2);

		// GEOM STUFF
		tongue = new Line2D.Double();
		body = new Arc2D.Double();
		tail = new Arc2D.Double();
		tail2 = new Arc2D.Double();
		fin = new Arc2D.Double();
		fin2 = new Arc2D.Double();
		eye = new Ellipse2D.Double();

		setAttributes();
	}
	
	

	protected void setAttributes() {
		tongue.setLine(-bodyW / 2, 0, bodyW / 2, 0);
		body.setArc(-bodyW / 2, -bodyH / 2, bodyW * 1.1, bodyH, 30 - mouthAnim, (300 + mouthAnim * 2), 2);
		tail.setArc(-bodyW / 2 - tailLength / 2 - mouthAnim / 2, -tailLength / 2, tailLength / 2 + mouthAnim / 2,
				tailLength, 90, -90, Arc2D.OPEN);
		tail2.setArc(-bodyW / 2 - tailLength / 2 - mouthAnim / 2, -tailLength / 2, tailLength / 2 + mouthAnim / 2,
				tailLength, 0, -90, Arc2D.OPEN);
		fin.setArc(-tailLength, -tailLength, tailLength + mouthAnim / 2, tailLength * 2, 90, -90, Arc2D.OPEN);
		fin2.setArc(-tailLength, -tailLength, tailLength + mouthAnim / 2, tailLength * 2, 0, -90, Arc2D.OPEN);
		eye.setFrame(5 * scale / 2, -20 * scale / 2, 5 * scale, 5 * scale);

		outline = new Area(body);
		outline.add(new Area(tongue));
		outline.add(new Area(tail));
		outline.add(new Area(tail2));
		outline.add(new Area(fin));
		outline.add(new Area(fin2));

		mouthAnim += mouthAngle;
		if (mouthAnim > 30 || mouthAnim < 0) {
			mouthAngle *= -1;
		}
	}

	public void drawMe(Graphics2D geom) {
		setAttributes();
		AffineTransform trans = geom.getTransform();
		geom.translate(getPos().x, getPos().y);
		geom.scale(getScale(), getScale());
		
		float angle = getVel().heading();
		geom.rotate(angle);
		
		geom.setColor(Color.PINK);
		geom.draw(tongue);
		geom.fill(tongue);
		
		geom.setColor(Color.BLACK);
		geom.draw(tail);
		geom.fill(tail);
		geom.draw(tail2);
		geom.fill(tail2);

		geom.draw(fin);
		geom.fill(fin);
		geom.draw(fin2);
		geom.fill(fin2);
		
		geom.setColor(getCol());
		geom.draw(body);
		geom.fill(body);
		
		geom.setColor(Color.BLACK);
		if(state == SICK)
			geom.setColor(Color.GREEN);
		geom.draw(eye);
		geom.fill(eye);
		

		//FOV AND OUTLINE STUFF
		geom.setColor(Color.red);
		geom.draw(fov);
		geom.draw(outline);
		
		geom.setTransform(trans);
	}
	
	public void move() {
		if(state != FULL)
			super.move();
			super.checkCollision();
			vel.normalize().mult(maxSpeed);
			pos.add(vel);
			energy -= engLossRatio*scale*vel.mag()/5;
		 if (!chasing) {
		    
			if ( Math.random()*32 < 1) {
			      dir *= -1;
			    }
			angle += 0.04 * dir; 
		    
		    vel.add((float) (1* Math.cos(angle)), (float) (1* Math.sin(angle)));
		    maxSpeed = 2;
		} else {
			maxSpeed = 4;
			timer--;
			if(timer < 0) chasing = false;	
		}
	}
	
	public void chasePrey(Creature target) {
		float coef = .2f;
		chasing = true;
		PVector direction = PVector.sub(target.getPos(), getPos()).normalize();
		PVector acceleration = PVector.mult(direction, maxSpeed*coef);
		getVel().add(acceleration);
		vel.limit(maxSpeed);
	}

	public boolean collides(Creature fish) {
		super.collides(fish);
		
		return (getBoundary().intersects(fish.getBoundary().getBounds2D())
				&& fish.getBoundary().intersects(getBoundary().getBounds2D()));
	}
	
	public boolean checkHeadOn(Creature otherFish) {
		if (vel.x * otherFish.vel.x < 0 && vel.y * otherFish.vel.y < 0) {
			return true;
		}
		return false;
	}
}

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.Timer;

import processing.core.PVector;

/* Move is different from the superclass so it
 * is in the subclass as it has a different action when
 * colliding with the predator class
 * 
 * runFromPred is a new method and not in the superclass
 * because only the prey does this action and can be 
 * called with instanceof and typecasting
 * 
 * the fields int and chasing are in the prey class
 * because the objects hold the timer for running away
 * from the predator object
 */
public class Prey extends Creature implements ActionListener {

	int timer;
	boolean beingChased = false;
	// GEOM STUFF
	protected Line2D.Double tongue;
	protected Arc2D.Double body, tail, tail2, fin, fin2;
	protected Ellipse2D.Double eye;

	public Prey(PVector pos, int w, int h, double sc) {
		super(pos, w, h, sc);
		this.setPos(pos);
		maxSpeed = 4;
		setVel(new PVector(1, 1));

		// GEOM STUFF
		tongue = new Line2D.Double();
		body = new Arc2D.Double();
		tail = new Arc2D.Double();
		tail2 = new Arc2D.Double();
		fin = new Arc2D.Double();
		fin2 = new Arc2D.Double();
		eye = new Ellipse2D.Double();

		// MAYBE I SHOULD MAKE A UTIL CLASS :)
		int r, g, b;
		r = (int) (Math.random() * 255);
		g = (int) (Math.random() * 255);
		b = (int) (Math.random() * 255);
		setCol(new Color(r, g, b));
		tailLength = (int) (Math.random() * (h - h / 2 * scale) + h / 2 * scale);

		// COLLISION THINGY
		this.dimension = new Dimension(75, 50);
		float sight = (float) (dimension.width * 2 * .75f);
		fov = new Arc2D.Double(-sight, -sight, sight * 2, sight * 2, -55, 110, Arc2D.PIE);

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

		geom.setColor(Color.red);
		geom.draw(tongue);
		geom.fill(tongue);

		geom.setColor(getCol());
		geom.draw(body);
		geom.fill(body);

		geom.draw(tail);
		geom.fill(tail);
		geom.draw(tail2);
		geom.fill(tail2);

		geom.draw(fin);
		geom.fill(fin);
		geom.draw(fin2);
		geom.fill(fin2);

		geom.setColor(Color.white);
		if(state == SICK || state == DEATH)
			geom.setColor(Color.GREEN
					);
		geom.draw(eye);
		geom.fill(eye);

		// geom.setColor(Color.red);
		// geom.draw(fov);
		// geom.draw(outline);

		geom.setTransform(trans);
	}

	public void move() {
		checkCollision();
		energy -= engLossRatio*scale*vel.mag()/5;
		vel.normalize().mult(maxSpeed);
		pos.add(vel);
		//super.move();
		if (!beingChased) {
			maxSpeed = 2;
		} else {
			maxSpeed = 4;
			timer--;
			if (timer < 0)
				beingChased = false;
		}
		
		
	}

	public void runFromPred(Creature pred) {
		super.runFromPred(pred);
		float coef = 0.2f;
		beingChased = true;
		timer = 120;
		PVector direction = PVector.sub(getPos(), pred.getPos()).normalize();
		PVector acceleration = PVector.mult(direction, maxSpeed * coef);
		getVel().add(acceleration);
		vel.limit(maxSpeed * 2);
	}

	public int traceClosestFood(ArrayList<Food> foodArr) {
		int afcInd = 0;
		if (foodArr.size() > 0) {

			double biggestAfc = -1;
			for (int i = 0; i < foodArr.size(); i++) {

				double distance = PVector.dist(foodArr.get(i).getPos(), pos);
				double afc = foodArr.get(i).getSize() / distance;

				if (afc >= biggestAfc) {
					biggestAfc = afc;
					afcInd = i;
				}
			}
			this.attractedBy(foodArr.get(afcInd));
		}
		return afcInd;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	// FOV
	// ESCAPE
	// ADD MOVESPEED SHEEEESH
}

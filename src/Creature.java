import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import processing.core.PVector;

public abstract class Creature {

	// MOVEMENT STUFF
	protected PVector pos;
	protected PVector vel;
	protected float maxSpeed;

	// DRAWING SIZE STUFF
	protected int bodyW;
	protected int bodyH;
	protected double scale;
	protected int dir = 1;
	protected double angle;
	protected Color c = new Color(255, 145, 164);

	// ANIMATION STUFF
	protected int tailLength;
	protected int mouthAnim = 0;
	protected double mouthAngle = 1;

	protected Dimension dimension;
	protected Area outline;
	protected Arc2D.Double fov;
	// FSM
	protected static final int DEATH = -1;
	protected static final int SICK = 0;
	protected static final int HUNGRY = 1;
	protected static final int QUART_FULL = 2;
	protected static final int HALF_FULL = 3;
	protected static final int FULL = 4;
	protected static final int OVER_FULL = 5;
	protected static final int MAX_ENERGY = 1000;
	protected float engGainRatio = 100;
	protected float engLossRatio = 1000 / (30 * 15);
	protected int state = HUNGRY;
	protected float energy = MAX_ENERGY / 2;
	protected int timer = 90;
	protected float sizeGrowRatio = 0.0001f;

	// Constructor
	public Creature(PVector pos, int w, int h, double sc) {
		this.setPos(pos);
		bodyW = w;
		bodyH = h;
		setScale(sc);
		maxSpeed = (float) (scale * 4);
	}

	protected abstract void setAttributes();

	public abstract void drawMe(Graphics2D geom);

	public void attractedBy(Food target) {
		float coef = .2f;
		PVector direction = PVector.sub(target.getPos(), getPos()).normalize();
		PVector acceleration = PVector.mult(direction, maxSpeed * coef);
		getVel().add(acceleration);
		vel.limit(maxSpeed);
	}

	public boolean collides(Food food) {
		return (getBoundary().intersects(food.getBoundary().getBounds2D())
				&& food.getBoundary().intersects(getBoundary().getBounds2D()));
	}

	public boolean collides(Creature fish) {
		return (getBoundary().intersects(fish.getBoundary().getBounds2D())
				&& fish.getBoundary().intersects(getBoundary().getBounds2D()));
	}

	protected PVector wallPushForce() {
		PVector force = new PVector(0, 0);
		float wallCoeff = 50.0f;

		double distance = 0;
		// WHY DOES MY CODE BREAK IF THE FISH POS IS IN THE MIDDLE????
		// SO I ADDED THESE IF STATEMENTS AJSDLJASKLDA:L
		if (pos.x < AquaPanel.OCEAN_X + 2 * bodyW * scale) {
			distance = Environment.leftEdge.ptLineDist(pos.x, pos.y) - (double) bodyW * (double) scale;
			force.add(new PVector((float) (+wallCoeff / Math.pow(distance, 2)), 0.0f));
		}
		if (pos.x > AquaPanel.OCEAN_W + AquaPanel.OCEAN_X - 2 * bodyW * scale) {
			distance = Environment.rightEdge.ptLineDist(pos.x, pos.y) - (double) bodyW * (double) scale;
			force.add(new PVector((float) (-wallCoeff / Math.pow(distance, 2)), 0.0f));
		}
		if (pos.y < AquaPanel.OCEAN_Y + 2 * bodyH * scale) {
			distance = Environment.topEdge.ptLineDist(pos.x, pos.y) - (double) bodyH * (double) scale;
			force.add(new PVector(0.0f, (float) (+wallCoeff / Math.pow(distance, 2))));
		}
		if (pos.y > AquaPanel.OCEAN_H + AquaPanel.OCEAN_Y - 2 * bodyH * scale) {
			distance = Environment.bottomEdge.ptLineDist(pos.x, pos.y) - (double) bodyH * (double) scale;
			force.add(new PVector(0.0f, (float) (-wallCoeff / Math.pow(distance, 2))));
		}
		return force;

	}

	public void move() {
		// make it walk randomly

		/*
		 * angle += 0.04 * dir; if ( Math.random()*32 < 1) { dir *= -1; }
		 * vel.set((float) (maxSpeed* Math.cos(angle)), (float) (maxSpeed*
		 * Math.sin(angle)));
		 */
		if (state == SICK || state == DEATH) 
			maxSpeed = 2;
		else
			maxSpeed = 4;
		checkCollision();
		vel.normalize().mult(maxSpeed);
		pos.add(vel);
		

		energy -= engLossRatio*scale*vel.mag()/5;
	}

	public void update(ArrayList<Creature> objList) {

		if (energy > MAX_ENERGY)
			state = OVER_FULL;
		else if (energy > MAX_ENERGY*0.75)
			state = FULL;
		else if (energy > MAX_ENERGY / 2)
			state = HALF_FULL;
		else if (energy > MAX_ENERGY / 4)
			state = QUART_FULL;
		else if (energy > MAX_ENERGY / 10)
			state = HUNGRY;
		else if (energy > 0) {
			state = SICK;
			timer = 80;
		}
		else
			state = DEATH;
		
		if (state == DEATH) {
			timer--;
			if (timer < 0)
				objList.remove(this);
			return;
		}
		
		if(state == OVER_FULL) {
			float extra = energy - MAX_ENERGY;
			energy = MAX_ENERGY;
			scale += extra*sizeGrowRatio*scale;
		}

	}

	protected void checkCollision() {
		PVector wallSteerAccel = new PVector(0, 0);
		wallSteerAccel = wallPushForce().div((float) scale);
		float speedValue = vel.mag();
		vel.add(wallSteerAccel);
		vel.normalize().mult(speedValue);

	}

	public Shape getBoundary() {
		AffineTransform at = new AffineTransform();
		at.translate(getPos().x, getPos().y);
		at.scale(getScale(), getScale());
		return at.createTransformedShape(outline);
	}

	public Shape getFOV() {
		AffineTransform at = new AffineTransform();
		at.translate(getPos().x, getPos().y);
		at.rotate(getVel().heading());
		at.scale(getScale(), getScale());
		return at.createTransformedShape(fov);
	}

	public boolean collidesFOV(Creature fish2) {
		return (getFOV().intersects(fish2.getBoundary().getBounds2D()));
	}

	public void runAway(Creature fish2) {
		float coef = 0.2f;

		if (scale < fish2.scale) {
			PVector accel = new PVector(0, 0);
			if (pos.x < fish2.getPos().x)
				accel.add(-1, 0);
			if (pos.x > fish2.getPos().x)
				accel.add(1, 0);
			if (pos.y < fish2.getPos().y)
				accel.add(0, -1);
			if (pos.y > fish2.getPos().y)
				accel.add(0, 1);
			accel.mult(coef * maxSpeed);
			vel.add(accel);
			vel.limit(maxSpeed);
			vel.normalize().mult(maxSpeed);
		}
	}

	public void runFromPred(Creature pred) {
		float coef = .2f;
		PVector direction = PVector.add(pred.getPos(), getPos()).normalize();
		PVector accel = PVector.mult(direction, maxSpeed * coef);
		getVel().add(accel);
		vel.limit(maxSpeed);
	}

	// collision thing
	public void pushAway(Creature fish2) {
		float coef = 2.f;
		if (scale < fish2.scale) {
			PVector direction = PVector.sub(pos, fish2.getPos()).normalize();
			PVector accel = PVector.mult(direction, maxSpeed * coef);
			vel.add(accel);
			vel.limit(maxSpeed);
		}
	}

	public void drawInfo(Graphics2D g) {
		AffineTransform at = g.getTransform();
		g.translate(pos.x, pos.y);

		String st1 = "Size     : " + String.format("%.2f", scale);
		String st2 = "Speed  : " + String.format("%.2f", vel.mag());
		String st3 = state +" Energy : "  + String.format("%.2f", energy/10);

		Font f = new Font("Courier", Font.PLAIN, 12);
		FontMetrics metrics = g.getFontMetrics(f);

		float textWidth = metrics.stringWidth(st3);
		float textHeight = metrics.getHeight();
		float margin = 12, spacing = 6;

		g.setColor(new Color(255, 255, 255, 60));
		g.fillRect((int) (-textWidth / 2 - margin),
				(int) (-bodyH * scale * .75f - textHeight * 5f - spacing * 4f - margin * 2f),
				(int) (textWidth + margin * 2f), (int) (textHeight * 5f + spacing * 4f + margin * 2f));

		g.setColor(Color.blue.darker());
		g.drawString(animalType(), -metrics.stringWidth(this.animalType()) / 2,
				(float) (-bodyH * scale * .75f - margin - (textHeight + spacing) * 4f));
		g.drawString(st1, -textWidth / 2, (float) (-bodyH * scale * .75f - margin - (textHeight + spacing) * 2f));
		g.drawString(st2, -textWidth / 2, (float) (-bodyH * scale * .75f - margin - (textHeight + spacing) * 1f));
		if (state == SICK)
			g.setColor(Color.red);
		g.drawString(st3, -textWidth / 2, (float) (-bodyH * scale * .75f - margin));
		g.setTransform(at);
	}

	private String animalType() {
		String type = ";";
		if (this instanceof Prey)
			type = "Prey";
		if (this instanceof Predator)
			type = "Predator";
		return type;
	}

	public PVector getPos() {
		return pos;
	}

	public void setPos(PVector pos) {
		this.pos = pos;
	}

	public Color getCol() {
		return c;
	}

	public void setCol(Color c) {
		this.c = c;
	}

	public PVector getVel() {
		return vel;
	}

	public void setVel(PVector vel) {
		this.vel = vel;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	private void changeCol() {
		int r = (int) (Math.random() * 255);
		int gr = (int) (Math.random() * 255);
		int b = (int) (Math.random() * 255);
		setCol(new Color(r, gr, b));
	}

	private void changeTail() {
		tailLength = (int) (Math.random() * (25 - 15) + 15);
	}
}

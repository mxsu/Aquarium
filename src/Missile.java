
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import java.util.ArrayList;

import processing.core.PVector;

public class Missile extends SimulationObject implements Mover {

	private PVector speed;
	private Ellipse2D missile;
	protected boolean hit = false;
	protected boolean out = false;
	
	public Missile(float x, float y, float speedx, float speedy) {
		super(x, y, 10, 5, 1f);
		speed = new PVector(speedx, speedy);
	}

	@Override
	public void draw(Graphics2D g) {
		AffineTransform at = g.getTransform();
		g.translate(pos.x, pos.y);
		g.scale(size, size);
		g.rotate(speed.heading());
		g.setColor(Color.BLACK);
		g.fill(missile);
		g.setTransform(at);
	}

	@Override
	public void drawInfo(Graphics2D g2) {
		// nothing to do here
	}

	@Override
	public void update(ArrayList<Creature> fishArr) {
		move();
		checkCollision(fishArr);
		if (outOfBounds()) {
			out = true;
		}
	}

	@Override
	protected void setShapeAttributes() {
		missile = new Ellipse2D.Double(-dim.width / 2, -dim.height / 2, dim.width, dim.height);
	}

	@Override
	protected void setOutline() {
		outline = new Area(missile);
	}

	@Override
	protected Shape getOutline() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		at.scale(size, size);
		at.rotate(speed.heading());
		return at.createTransformedShape(outline);
	}

	@Override
	public void move() {
		// System.out.println(pos.x + " " + pos.y + " " + speed.x + " " + speed.y);
		approach(AquaPanel.fishArr);
		pos.add(speed);
	}

	@Override
	public void approach(ArrayList<Creature> fishArr) {
		int ind = -1;
		float biggestNRG = 0;
		for (int i = 0; i < fishArr.size(); i++) {
			if (fishArr.get(i) instanceof Predator) {
				if (fishArr.get(i).energy > biggestNRG) {
					biggestNRG = fishArr.get(i).energy;
					ind = i;
				}
			}
		}
		if (ind != -1) {
			speed.x = fishArr.get(ind).pos.x - pos.x;
			speed.y = fishArr.get(ind).pos.y - pos.y;
			speed.limit(10f);
		}
	}

	@Override
	public void checkCollision(ArrayList<Creature> fishArr) {
		for (int i = 0; i < fishArr.size(); i++) {
			if (fishArr.get(i) instanceof Predator) {
				if (isColliding(fishArr.get(i))) {
					fishArr.remove(i);
					hit = true;
					break;
				}
			}
		}
	}

	protected boolean outOfBounds() {
		if(pos.x > 1280+69+69 || pos.x < 0) return true;
		if(pos.y > 800+69+69 || pos.y < 0) return true;
		return false;
	}
	
	

	protected boolean isColliding(Creature creature) {
		return (getOutline().intersects(creature.getBoundary().getBounds2D())
				&& creature.getBoundary().getBounds2D().intersects(getBoundingBox()));
	}

}
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;

import processing.core.PVector;

public class Hunter extends Creature {

	private Ellipse2D glass, window1,window2;
	private Rectangle2D body, head;
	private Arc2D tail, scope;

	int width, height;
	double scale;

	private PVector pos;
	private PVector vel = new PVector(0, 5);

	private ArrayList<Missile> missileList = new ArrayList<Missile>();

	private boolean up = false;
	protected int timer = 0;
	
	public Hunter(PVector pos, int w, int h, double sc, ArrayList<SimulationObject> animList, AquaPanel pnlSize) {
		super(pos, w, h, sc);
		this.pos = pos;
		width = w;
		height = h;
		scale = sc;

		// panel = pnlSize;
		glass = new Ellipse2D.Double();
		body = new Rectangle2D.Double();
		head = new Rectangle2D.Double();
		scope = new Arc2D.Double();
		tail = new Arc2D.Double();
		window1 = new Ellipse2D.Double();
		window2 = new Ellipse2D.Double();
		
		setAttributes();
	}

	public void drawMe(Graphics2D g) {
		AffineTransform at = g.getTransform();
		g.translate(pos.x, pos.y-20);
		g.scale(scale, scale);
		
		g.setColor(Color.blue);
		g.draw(glass);
		g.fill(glass);
		
		g.setColor(Color.black);
		g.draw(body);
		g.fill(body);
		g.draw(head);
		g.fill(head);
		
		g.setColor(Color.gray);
		g.draw(scope);
		g.draw(tail);
		
		g.setColor(Color.white);
		g.draw(window1);
		g.fill(window1);
		g.draw(window2);
		g.fill(window2);
		
		g.setTransform(at);
		for (Missile m : missileList)
			m.draw(g);
	}

	@Override
	protected void setAttributes() {
		glass.setFrame(pos.x+width/2, pos.y, width, height);
		body.setFrame(pos.x, pos.y, width, height);
		head.setFrame(pos.x+width/3, pos.y-height/2+5, width/2, height/2);
		scope.setArc(pos.x+width/2,pos.y-height/1.5,width/2,height/2,90,90, Arc2D.OPEN);
		tail.setArc(pos.x-width,pos.y,width,height,90,-180, Arc2D.OPEN);
		window1.setFrame(pos.x+width/4, pos.y+height/4, width/6, height/6);
		window2.setFrame(pos.x+width/1.5, pos.y+height/4, width/6, height/6);
	}

	public void fire() {
		// create speed for the missile that goes along the same direction as the player
		PVector mSpeed = PVector.fromAngle(vel.heading()).mult(maxSpeed * 3);

		// Add a missile object to the list for shooting
		missileList.add(new Missile(pos.x+width*2, pos.y+100, mSpeed.x, mSpeed.y));
	}

	public void up() {
		//System.out.println("up");
		vel.y = -5;
		pos.add(vel);
	}

	public void down() {
		vel.y = 5;
		pos.add(vel);
	}

	public void move() {
		if(pos.y > 700)
			up = true;
		if(pos.y < 100)
			up = false;
		if(up == true) {
			vel.y = -5;
		}
		else 
			vel.y = 5;
		pos.add(vel);
	}

	public void update() {
		timer--;
		move(); // move the player
		for (Missile m : missileList) {
			m.update(AquaPanel.fishArr);
			if (m.hit == true || m.out == true) {
				missileList.remove(m);
				break;
			}
		}
	}
	
	public void drawInfo(Graphics2D g) {
		AffineTransform at = g.getTransform();
		g.translate(pos.x+50, pos.y+50);

		String st1 = "Size     : " + String.format("%.2f", scale);
		String st2 = "Speed  : " + String.format("%.2f", vel.mag());

		Font f = new Font("Courier", Font.PLAIN, 12);
		FontMetrics metrics = g.getFontMetrics(f);

		float textWidth = metrics.stringWidth(st2);
		float textHeight = metrics.getHeight();
		float margin = 12, spacing = 6;

		g.setColor(new Color(255, 255, 255, 60));
		g.fillRect((int) (-textWidth / 2 - margin),
				(int) (-bodyH * scale * .75f - textHeight * 4f - spacing * 4f - margin * 2f),
				(int) (textWidth + margin * 2f), (int) (textHeight * 5f + spacing * 4f + margin * 2f));

		g.setColor(Color.blue.darker());
		g.drawString("Hunter", 0,
				(float) (-bodyH * scale * .75f - margin - (textHeight + spacing) * 3f));
		g.drawString(st1, -textWidth / 2, (float) (-bodyH * scale * .75f - margin - (textHeight + spacing) * 1f));
		g.drawString(st2, -textWidth / 2, (float) (-bodyH * scale * .75f - margin));
		g.setTransform(at);
	}
}

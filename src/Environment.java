import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Environment {

	private int seaweedAnimation = 0;
	private int animate = 1;
	private int bubbleFloat = 300;
	
	private Line2D.Double seaweed;
	private Arc2D.Double seaweedL, seaweedR;
	private Ellipse2D.Double bubble;
	private Rectangle2D.Double ocean;
	
	
	public static Line2D.Double rightEdge;
	public static Line2D.Double leftEdge;
	public static Line2D.Double topEdge;
	public static Line2D.Double bottomEdge;
	
	Environment(){
		seaweed = new Line2D.Double();
		seaweedL = new Arc2D.Double();
		seaweedR = new Arc2D.Double();
		bubble = new Ellipse2D.Double();
		ocean = new Rectangle2D.Double();
		

		rightEdge = new Line2D.Double(AquaPanel.OCEAN_X + AquaPanel.OCEAN_W, AquaPanel.OCEAN_Y, AquaPanel.OCEAN_X+AquaPanel.OCEAN_W, AquaPanel.OCEAN_Y + AquaPanel.OCEAN_H);
		leftEdge = new Line2D.Double(AquaPanel.OCEAN_X, AquaPanel.OCEAN_Y, AquaPanel.OCEAN_X, AquaPanel.OCEAN_Y+AquaPanel.OCEAN_H);
		topEdge = new Line2D.Double(AquaPanel.OCEAN_X, AquaPanel.OCEAN_Y, AquaPanel.OCEAN_X+AquaPanel.OCEAN_W, AquaPanel.OCEAN_Y);
		bottomEdge = new Line2D.Double(AquaPanel.OCEAN_X, AquaPanel.OCEAN_Y+AquaPanel.OCEAN_H, AquaPanel.OCEAN_X+AquaPanel.OCEAN_W, AquaPanel.OCEAN_Y + AquaPanel.OCEAN_H);

		
	}
	
	private void setAttributes() {
		seaweed.setLine(100, 0, 100, 50);
		seaweedL.setArc(0, 0, 100, 100, 0, 60+seaweedAnimation/5,0);
		seaweedR.setArc(100, 0, 100, 100, 180, -60+seaweedAnimation/5,0);
		bubble.setFrame(0, 0, 10, 10);
		ocean.setFrame(AquaPanel.OCEAN_X, AquaPanel.OCEAN_Y, AquaPanel.OCEAN_W, AquaPanel.OCEAN_H);
	}
	
	public void drawGeomEnvironment(Graphics2D g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		setAttributes();
		
		drawGeomOcean(g2);
		drawGeomSeaweed(g2,AquaPanel.OCEAN_W*1/3,AquaPanel.OCEAN_H);
		drawGeomSeaweed(g2,AquaPanel.OCEAN_W*2/3,AquaPanel.OCEAN_H);
		drawGeomBubbles(g2,(int)(AquaPanel.OCEAN_W*0.9),(int)(bubbleFloat));
		drawGeomBubbles(g2,(int)(AquaPanel.OCEAN_W*0.6),(int)(bubbleFloat));
		drawGeomBubbles(g2,(int)(AquaPanel.OCEAN_W*0.3),(int)(bubbleFloat));
		
		g2.setColor(new Color(255, 0, 0));
		
//		g2.draw(rightEdge);
//		g2.draw(leftEdge);
//		g2.draw(topEdge);
//		g2.draw(bottomEdge);
		
		seaweedAnimation+=animate;
		if(seaweedAnimation > 50 || seaweedAnimation < -50) animate*=-1;
		
		bubbleFloat-=1;
		if(bubbleFloat < AquaPanel.OCEAN_Y) bubbleFloat = AquaPanel.OCEAN_H;
	}
	
	private void drawGeomOcean(Graphics2D g2) {
		g2.setColor(new Color(135, 206, 235));
		g2.fill(ocean);
		g2.draw(ocean);
	}
	
	private void drawGeomSeaweed(Graphics2D g2,int xPos, int yPos) {
		AffineTransform trans = g2.getTransform();
		g2.translate(xPos-50,yPos-5);
		g2.setColor(new Color(84, 118, 75));
		g2.setStroke(new BasicStroke(5));
		g2.draw(seaweed);
		g2.fill(seaweed);
		g2.draw(seaweedL);
		//g2.fill(seaweedL);
		g2.draw(seaweedR);
		//g2.fill(seaweedR);
		g2.setTransform(trans);
	}
	
	private void drawGeomBubbles(Graphics2D g2, int xPos, int yPos) {
		AffineTransform trans = g2.getTransform();
		g2.translate(xPos,yPos);
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(2));
		g2.draw(bubble);
		//g2.fill(bubble);
		g2.setTransform(trans);
	}
	
	
}

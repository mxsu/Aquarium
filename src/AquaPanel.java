/* Aquatic Environment Panel that controls
 * the drawing for the program
 * Program created for IAT265 Spring 2022
 * 
 * Author: Michael Su 301371124
 * Lab: D102
 * Date: Jan, 31, 2022
 * 
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import processing.core.PVector;

public class AquaPanel extends JPanel implements ActionListener {

	// CLASS STUFF
	private Timer timer;
	private Environment oceanEnv;
	// Environment Stuff
	public final static int OCEAN_X = 69;
	public final static int OCEAN_Y = 69;
	public final static int OCEAN_W = 1280;
	public final static int OCEAN_H = 800;
	// Creature Class
	ArrayList<Food> foodArr = new ArrayList<>();
	public static ArrayList<Creature> fishArr = new ArrayList<>();
	private int predatorArrSize = 6;
	private int preyArrSize = 12;
	private int foodArrSize = 30;

	private Hunter hunter;
	public ArrayList<SimulationObject> missiles;

	public Dimension pnlSize = new Dimension(1280, 800);

	private boolean fire;
	private boolean showInfo = true;
	private boolean hunterSpawn = false;
	private int respawnTimer = 999;
	private String status = "status...";

	public static void main(String[] args) {
		JFrame frame = new JFrame("Aquatic Ecosystem");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		AquaPanel aquaPane = new AquaPanel();
		frame.add(aquaPane);
		frame.pack();
		frame.setVisible(true);

	}

	public AquaPanel() {
		pnlSize = (new Dimension(OCEAN_W + 2 * OCEAN_X, OCEAN_H + 2 * OCEAN_Y));
		setPreferredSize(pnlSize);
		timer = new Timer(33, this);
		timer.start();
		setFocusable(true);
		addKeyListener(new MyKeyAdapter());

		for (int i = 0; i < predatorArrSize; i++) {
			double scale = Math.random() * 0.4 + 0.8;
			PVector pos = new PVector((int) (Math.random() * (OCEAN_W - OCEAN_X * 3)) + OCEAN_X * 3,
					(int) (Math.random() * (OCEAN_H - OCEAN_Y * 3)) + OCEAN_Y * 3);
			fishArr.add(new Predator(pos, 45, 40, scale));
		}
		for (int i = 0; i < preyArrSize; i++) {
			double scale = Math.random() * 0.3 + 0.6;
			PVector pos = new PVector((int) (Math.random() * (OCEAN_W - OCEAN_X * 2)) + OCEAN_X * 2,
					(int) (Math.random() * (OCEAN_H - OCEAN_Y * 2)) + OCEAN_Y * 2);
			fishArr.add(new Prey(pos, 45, 40, scale));
		}

		for (int i = 0; i < foodArrSize; i++) {
			int foodSize = (int) (Math.random() * (40 - 15) + 15);
			PVector pos = new PVector((int) (Math.random() * (OCEAN_W - OCEAN_X * 2)) + OCEAN_X * 2,
					(int) (Math.random() * (OCEAN_H - OCEAN_Y * 2)) + OCEAN_Y * 2);
			foodArr.add(new Food(pos, foodSize));
		}
		PVector hunterPos = new PVector(12, 100);
		hunter = new Hunter(hunterPos, 40, 40, 1, missiles, this);
		oceanEnv = new Environment();

		// this.addMouseListener(new MyMouseAdapter());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		oceanEnv.drawGeomEnvironment(g2);
		for (int i = 0; i < fishArr.size(); i++) {
			fishArr.get(i).drawMe(g2);
			if (showInfo)
				fishArr.get(i).drawInfo(g2);
		}

		if (foodArr.size() > 0)
			for (Food food : foodArr)
				food.drawGeomShrimp(g2);

		if (hunterSpawn == true) {
			hunter.drawMe(g2);
			if (showInfo)
				hunter.drawInfo(g2);
		}
		drawStatusBar(g2);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		int ind = -1;
		int indFood = -1;

		for (int i = 0; i < foodArr.size(); i++) {
			foodArr.get(i).setCol(new Color(255, 145, 164));
		}
		if (fishArr.size() > 0)
			for (int k = 0; k < fishArr.size(); k++) {
				for (int j = 0; j < fishArr.size(); j++) {
					if (fishArr.get(k) != fishArr.get(j)) {
						// PRED
						// prey within fov of pred
						if (fishArr.get(k) instanceof Predator && fishArr.get(j) instanceof Prey) {
							if (fishArr.get(k).collidesFOV(fishArr.get(j))) {
								((Predator) fishArr.get(k)).chasePrey(fishArr.get(j));
								fishArr.get(j).runFromPred(fishArr.get(k));
							}

							if (fishArr.get(k).collides(fishArr.get(j))) {
								if (ind == -1)
									ind = j;
								((Predator) fishArr.get(k)).chasing = false;
								((Predator) fishArr.get(k)).timer = 0;
								((Predator) fishArr.get(k)).energy += 10 * fishArr.get(j).scale * fishArr.get(j).bodyW;
							}
						}
						// prey colliding with prey
						if (fishArr.get(k).collides(fishArr.get(j))) {
							fishArr.get(k).pushAway(fishArr.get(j));
							fishArr.get(j).runAway(fishArr.get(k));
						}
						// Food interactions
						if (fishArr.get(k) instanceof Prey && ((Prey) fishArr.get(k)).beingChased == false) {
							if (foodArr.size() > 0) {
								int foodInd = ((Prey) fishArr.get(k)).traceClosestFood(foodArr);
								foodArr.get(foodInd).setCol(fishArr.get(k).getCol());
							}
							// resolve collision
							for (int i = 0; i < foodArr.size(); i++) {
								if (fishArr.get(k).collides(foodArr.get(i))) {
									fishArr.get(k).energy += foodArr.get(i).getSize() * 2;
									if (indFood == -1)
										indFood = i;
								}
							}
						}
					}
				}
				fishArr.get(k).move();
				fishArr.get(k).update(fishArr);
			}

		// EATING RESOLVE
		if (ind != -1) {
			fishArr.remove(ind);
			ind = -1;
		}

		if (indFood != -1) {
			foodArr.remove(indFood);
			ind = -1;
			int foodSize = (int) (Math.random() * (40 - 15) + 15);
			PVector pos = new PVector((int) (Math.random() * (OCEAN_W - OCEAN_X * 2)) + OCEAN_X * 2,
					(int) (Math.random() * (OCEAN_H - OCEAN_Y * 2)) + OCEAN_Y * 2);
			foodArr.add(new Food(pos, foodSize));
		}
		// HUNTER STUFF
		// HUNTER IS SPAWNED
		int numOfPred = 0;
		int numOfPrey = 0;
		for (Creature fish : fishArr) {
			if (fish instanceof Predator) {
				numOfPred += 1;
			}
		}
		
		for (Creature fish : fishArr) {
			if (fish instanceof Prey) {
				numOfPrey += 1;
			}
		}

		if (numOfPrey <= 6 && numOfPred > 3) {
			hunterSpawn = true;
		}

		

		if (hunterSpawn == true) {
			status = numOfPred-3 + " predators to kill";
			if (fire) {
				if (hunter.timer <= 0) {
					hunter.fire();
					hunter.timer = 15;
				}
			}
			hunter.update();
			// COUNT HOW MANY PRED ARE ALIVE
		}

		if (hunterSpawn == false) {
			status = "hunter coming when " + (numOfPrey-6) + " preys are killed";
			respawnTimer--;
			//System.out.println(respawnTimer);
		}
		
		if (numOfPred <= 3 && numOfPrey <= 6) {
			if (hunterSpawn == true)
				respawnTimer = 5 * 30;
			hunterSpawn = false;
			status = "hunter has disappeared. area respawning in " + (respawnTimer/30%5 +1);
			// IF HALF OF PRED IS DED THEN HUNTER DISAPPEARS
			// TIME OF 5 SECS
		}

		// 5 SEC RESPAWN THING ADD FISH INTO THE ARRAY
		if (respawnTimer < 0) {
			// REMOVE FROM ARRAY
			while (fishArr.size() > 0)
				fishArr.remove(0);
			//hunterSpawn = true;

			for (int i = 0; i < predatorArrSize; i++) {
				double scale = Math.random() * 0.4 + 0.8;
				PVector pos = new PVector((int) (Math.random() * (OCEAN_W - OCEAN_X * 3)) + OCEAN_X * 3,
						(int) (Math.random() * (OCEAN_H - OCEAN_Y * 3)) + OCEAN_Y * 3);
				fishArr.add(new Predator(pos, 45, 40, scale));
			}
			for (int i = 0; i < preyArrSize; i++) {
				double scale = Math.random() * 0.3 + 0.6;
				PVector pos = new PVector((int) (Math.random() * (OCEAN_W - OCEAN_X * 2)) + OCEAN_X * 2,
						(int) (Math.random() * (OCEAN_H - OCEAN_Y * 2)) + OCEAN_Y * 2);
				fishArr.add(new Prey(pos, 45, 40, scale));
			}
			respawnTimer = 999;
		}

		repaint();
		
	}
	
	private void drawStatusBar(Graphics2D g) {
		Font f = new Font("Arial", Font.BOLD, 12);
		g.setFont(f);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, getSize().height - 24, getSize().width, 24);
		g.setColor(Color.BLACK);
		
		g.drawString(status, 12, getSize().height - 8);
	}

	private class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE)
				fire = true;

			if (e.getKeyCode() == KeyEvent.VK_D) {
				// System.out.println(showInfo);
				if (showInfo)
					showInfo = false;
				else
					showInfo = true;
			}
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE)
				fire = false;
		}
	}
}

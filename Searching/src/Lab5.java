/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.LCD;
import lejos.nxt.Button;

/* this is a driver that instantaties a Robot and makes it do stuff */
class Lab5 {
	static final int COMMAND_DELAY = 200;

	public static void main(String args[]) {
		Robot robot = new Robot();

		LCD.clear();
		System.out.println("Status: "+robot.getStatus());
		while(robot.getStatus() != Robot.Status.PLOTTING) {
			try { Thread.sleep(COMMAND_DELAY); } catch (InterruptedException e) { };
		}
		LCD.drawString("Goto (-3,-3)", 0,0);
		robot.travelTo(-3f,-3f);
		LCD.clear();
		LCD.drawString("Turn to 45", 0,0);
		robot.turnTo(45f);
		LCD.clear();
		LCD.drawString("Press", 0,0);
		Button.waitForAnyPress();
	}
}

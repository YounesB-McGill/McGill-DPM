/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.LCD;
import lejos.nxt.Button;
/* that's a bummer! import java.util.concurrent.Executors; */

/* this is a driver that instantaties a Robot and makes it do stuff */
class Lab5 {
	static final int COMMAND_DELAY = 200;

	public static void main(String args[]) {
		Robot robot = new Robot();

		new Thread(robot).start();
		System.out.println(""+robot.getStatus());
		Button.waitForAnyPress();

		System.out.println("Goto (-3,-3)");
		robot.travelTo(-3f,-3f);
		System.out.println(""+robot.getStatus());
		/* wait for it to travel */
		while(robot.getStatus() != Robot.Status.PLOTTING) {
			try { Thread.sleep(COMMAND_DELAY); } catch (InterruptedException e) { };
		}
		System.out.println(""+robot.getStatus());
		Button.waitForAnyPress();

		System.out.println("Turn to 45");
		robot.turnTo(45f);
		System.out.println(""+robot.getStatus());
		/* wait for it to travel */
		while(robot.getStatus() != Robot.Status.PLOTTING) {
			try { Thread.sleep(COMMAND_DELAY); } catch (InterruptedException e) { };
		}
		System.out.println(""+robot.getStatus());
		Button.waitForAnyPress();
		
		robot.shutdown();
		System.out.println("Press");
		Button.waitForAnyPress();
	}
}

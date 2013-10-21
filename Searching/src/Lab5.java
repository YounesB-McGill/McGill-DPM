/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.LCD;
import lejos.nxt.Button;
/* that's a bummer! import java.util.concurrent.Executors; */

/* this is a driver that instantaties a Robot and makes it do stuff */
class Lab5 {
	static final int COMMAND_DELAY = 200;

	public static void main(String args[]) {
		Robot robot = new Robot();
		Thread rt = new Thread(robot, Robot.NAME);

		rt.start();

		/* turning test (haha, get it) */
		robot.turnTo(-45f);
		System.out.println(""+robot.getStatus());
		/* wait for it to travel */
		while(robot.getStatus() != Robot.Status.PLOTTING) {
			try {
				Thread.sleep(COMMAND_DELAY);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		System.out.println("Press");
		Button.waitForAnyPress();

		robot.travelTo(10f,0f);
		System.out.println(""+robot.getStatus());
		/* wait for it to travel */
		while(robot.getStatus() != Robot.Status.PLOTTING) {
			try {
				Thread.sleep(COMMAND_DELAY);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		robot.shutdown();
		try {
			rt.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		System.out.println("Press");
		Button.waitForAnyPress();
	}
}

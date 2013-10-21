/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.LCD;
import lejos.nxt.Button;
/* that's a bummer! import java.util.concurrent.Executors; */

private static final SCAN_THRESHOLD = 200;
private static final DISTANCE_THRESHOLD = 30;
private static final DESTINATION_X = 30;
private static final DESTINATION_Y = 90;

/* this is a driver that instantaties a Robot and makes it do stuff */
class Lab5 {
	static final int COMMAND_DELAY = 200;

	public static void main(String args[]) {
		Robot robot = new Robot();
		Thread rt = new Thread(robot, Robot.NAME);

		rt.start();

		//find blocks from corner
      robot.turnConstantlyTo(90f);
		/* while turning */
      float targetTheta = -1;
      float smallestPing = 254;
		while(robot.getStatus() != Robot.Status.PLOTTING) {
         int tStart = (int)System.currentTimeMillis();
         float ping = robot.ping();
         if(smallestPing > ping) {
            smallestPing = ping;
            targetTheta = robot.getPosition().theta;
         }
         //pause thread
         int tEnd = (int)System.currentTimeMillis();
         int deltaT = tEnd - tStart;
			try {
				Thread.sleep(COMMAND_DELAY - deltaT);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
      if(targetTheta < SCAN_THRESHOLD) {
         robot.turnTo(targetTheta);
         float targetX = robot.getPosition().x + smallestPing*Math.cos(targetTheta);
         float targetY = robot.getPosition().y + smallestPing*Math.sin(targetTheta);
         robot.travelTo(targetX,targetY);
         float distance = robot.ping();
         while(distance > TRAVEL_THRESHOLD) {
            distance = robot.ping();
         }
         robot.stop();
         //if styroform go to destination!
         if(robot.getColor() == robot.getColor().values.styrofoam) {
            //travel with avoidance
            robot.travelTo(DESTINATION_X,DESTINATION_Y);
            while(distance > TRAVEL_THRESHOLD) {
               
            }
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

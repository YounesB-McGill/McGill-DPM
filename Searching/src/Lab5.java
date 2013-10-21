/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.LCD;
import lejos.nxt.Button;
/* that's a bummer! import java.util.concurrent.Executors; */


/* this is a driver that instantaties a Robot and makes it do stuff */
class Lab5 {
	static final int COMMAND_DELAY = 200;
   private static final float SCAN_THRESHOLD = 200;
   private static final float TRAVEL_THRESHOLD = 30;
   private static final float DESTINATION_X = 30;
   private static final float DESTINATION_Y = 90;

	public static void main(String args[]) {
		Robot robot = new Robot();
		Thread rt = new Thread(robot, Robot.NAME);
      LCDInfo lcd = new LCDInfo(robot);

		rt.start();

		//find blocks from corner
      robot.turnConstantlyTo(90f);
		/* while turning */
      float targetTheta = -1;
      float smallestPing = 254;
      //while turning get smallest ping value and corresponding theta
		while(robot.getStatus() != Robot.Status.PLOTTING) {
         int tStart = (int)System.currentTimeMillis();
         float ping = robot.pingSonar();
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
      //if there is a block...
      if(targetTheta < SCAN_THRESHOLD) {
         robot.turnTo(targetTheta);
         float targetX = robot.getPosition().x + smallestPing*(float)Math.cos(targetTheta);
         float targetY = robot.getPosition().y + smallestPing*(float)Math.sin(targetTheta);
         robot.travelTo(targetX,targetY);
         float distance = robot.pingSonar();
         //move toward it
         while(distance > TRAVEL_THRESHOLD && robot.getStatus() != Robot.Status.PLOTTING) {
            distance = robot.pingSonar();
         }
         robot.stop();
         //if styroform go to destination!
         if(robot.getColour() == Robot.Colour.Value.STYROFOAM) {
            //travel with avoidance
            robot.travelTo(DESTINATION_X,DESTINATION_Y);
            while(distance > TRAVEL_THRESHOLD && robot.getStatus() != Robot.Status.PLOTTING) {
               distance = robot.pingSonar();
            }
         }
      }

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

/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.LCD;
import lejos.nxt.Button;
/* that's a bummer! import java.util.concurrent.Executors; */


/* this is a driver that instantaties a Robot and makes it do stuff */
class Lab5 {
	private static final int COMMAND_DELAY     = 200;
   private static final float SCAN_THRESHOLD   = 100;
   private static final float TRAVEL_THRESHOLD = 30;
   private static final float DESTINATION_X = 75;
   private static final float DESTINATION_Y = 195;

	public static void main(String args[]) {
		Robot robot = new Robot();
		Thread rt = new Thread(robot, Robot.NAME);
      LCDInfo lcd = new LCDInfo(robot);

		Button.setKeyClickLength(500);
		Button.setKeyClickTone(Button.ID_ENTER, 500000);
		Button.setKeyClickVolume(10);

		rt.start();
		/*for(int i = 0; i < 10; i++) {
			robot.driveLeg(0.48f);
			robot.driveLeg(0.48f);
			robot.driveLeg(0.48f);
			robot.driveLeg(0.48f);
		}*/
		//while((Button.waitForAnyPress() & Button.ID_ENTER) != 0);

		//find blocks from corner
      robot.turnConstantlyTo(90f,100);
		// while turning
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
		}
      lcd.setText("TT: " + targetTheta);
      //if there is a block...
      if(smallestPing < SCAN_THRESHOLD) {
         float targetX = robot.getPosition().x + (smallestPing-5)*(float)Math.cos(Math.toRadians(targetTheta+3));
         float targetY = robot.getPosition().y + (smallestPing-5)*(float)Math.sin(Math.toRadians(targetTheta+3)); //small adjustments...
         robot.travelTo(targetX,targetY);
         //if styroform go to destination!
         if(robot.getColour() == Colour.Value.STYROFOAM) {
            //travel with avoidance
            robot.travelTo(DESTINATION_X,DESTINATION_Y);
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

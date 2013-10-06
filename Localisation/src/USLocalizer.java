import lejos.nxt.UltrasonicSensor;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;

	private final static int granularity = 36;
	private final static int distanceThreshold = 36;
	private final static double /* fixme */ speed = 50;
	
	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		int us;
		
		/* this is stupid */
		if (locType == LocalizationType.FALLING_EDGE) {
			robot.setRotationSpeed(speed);
			// rotate the robot until it sees no wall
			LCD.drawString("...clear.", 0,1);
			while((us = getFilteredData()) < distanceThreshold) {
				LCD.drawString("US: "+us+"  ", 0, 0);
			}
			try { Thread.sleep(1000); } catch (InterruptedException e) {}
			// keep rotating until the robot sees a wall, then latch the angle
			LCD.drawString("...hit.   ", 0,1);
			while((us = getFilteredData()) > distanceThreshold) {
				LCD.drawString("US: "+us+"  ", 0, 0);
			}
			float a = odo.getTheta();
			LCD.drawString("a "+a, 0,1);
			// switch direction and wait until it sees no wall
			robot.setRotationSpeed(-speed);
			try { Thread.sleep(1000); } catch (InterruptedException e) {}

			// keep rotating until the robot sees a wall, then latch the angle
			while((us = getFilteredData()) > distanceThreshold) {
				LCD.drawString("US: "+us+"  ", 0, 0);
			}
			float b = odo.getTheta();
			LCD.drawString("b "+b, 0,2);
			//robot.setRotationSpeed(0);
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			/* when a and b are the same branch cut, this is so, otherwise
			 incorrect, fix it */
			if(a < b) b += 360f;
			/* that's really scetchy and not at all standard */
			/* the robot is at point b -- but that's not important, since we add
			 to the current value
			 eg a = 105; b = 252 when facing away => 178.5
			 eg a = 300; b = 85+360 when facing towards the wall => 192.5, 372.5
			 */
			float correction = 45 - (a + b) * 0.5f;
			// update the odometer position (example to follow:)
			/* the angle, but not the position because we conviently forgot */
			//odo.setPosition(0f, 0f, 0f);
			odo.correctTheta(correction);
			LCD.drawString("t "+odo.getTheta()+"  ", 0,3);
			LCD.drawString("Finding 0.", 0,5);
			robot.setRotationSpeed(speed);
			for( ; ; ) {
				float theta = odo.getTheta();
				/* this is why you should never branch cut dead in the middle of
				 your operating range :[ . . . in fact, screw this . . . */
				if(theta > 180f) theta -= 360f;
				if(theta >= 0f && theta < 20f) break;
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
			}
			robot.stop();
			LCD.drawString(" found.", 9,5);
			LCD.drawString("t "+odo.getTheta()+"  ", 0,3);
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			//
			// FILL THIS IN
			//
		}
	}

	/* fixme: get FILTED data */
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
				
		return distance;
	}

}

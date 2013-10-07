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
		
		if (locType == LocalizationType.FALLING_EDGE) {
			/* this is stupid, it should store a buffer of us data, then get the
			 linear intercept instead of approximating to the nearest
			 measurement */
			robot.setRotationSpeed(speed);
			// rotate the robot until it sees no wall
			LCD.drawString("wait clear  ", 0,1);
			while((us = getFilteredData()) < distanceThreshold) {
				LCD.drawString("US: "+us+"  ", 0, 0);
			}
			try { Thread.sleep(1000); } catch (InterruptedException e) {}
			// keep rotating until the robot sees a wall, then latch the angle
			LCD.drawString("wait hit   ", 0,1);
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
			float correction = 45 - (a + b) * 0.5f;
			// update the odometer position (example to follow:)
			/* the angle, but not the position because we conviently forgot */
			//odo.setPosition(0f, 0f, 0f);
			odo.correctTheta(correction);
			LCD.drawString("t "+odo.getTheta()+"  ", 0,3);
			LCD.drawString("finding 0  ", 0,5);
			robot.setRotationSpeed(10); /* slow enough */
			for( ; ; ) {
				float theta = odo.getTheta();
				/* this is why you should never branch cut dead in the middle of
				 your operating range :[ . . . in fact, screw this . . . */
				if(theta > 180f) theta -= 360f;
				if(theta >= 0f && theta < 20f) break;
				try { Thread.sleep(100); } catch (InterruptedException e) {}
			}
			/* stop; don't use setRotationSpeed(0) because it completly fubars
			 it, don't ask me why, it's not my code */
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
			/* this is much better because we can get a distance to @ wall, and
			 then set x, y, and theta; we don't do this, though, but we could
			 (takes a maximum of 3 times longer) */
			if((us = getFilteredData()) > distanceThreshold) {
				robot.setRotationSpeed(-speed);
				LCD.drawString("wait hit  ", 0,1);
				while((us = getFilteredData()) > distanceThreshold) {
					LCD.drawString("US: "+us+"  ", 0, 0);
				}
				try { Thread.sleep(200); } catch (InterruptedException e) {}
			}
			robot.setRotationSpeed(speed);
			LCD.drawString("wait clear  ", 0,1);
			while((us = getFilteredData()) < distanceThreshold) {
				LCD.drawString("US: "+us+"  ", 0, 0);
			}
			float a = odo.getTheta();
			LCD.drawString("a "+a, 0,1);
			robot.setRotationSpeed(-speed);
			try { Thread.sleep(1000); } catch (InterruptedException e) {}
			while((us = getFilteredData()) < distanceThreshold) {
				LCD.drawString("US: "+us+"  ", 0, 0);
			}
			float b = odo.getTheta();
			LCD.drawString("b "+b, 0,2);
			if(a < b) b += 360f;
			/* remember, theta's degrees are backwards and start at y */
			float correction = 225 - (a + b) * 0.5f;
			odo.correctTheta(correction);
			LCD.drawString("t "+odo.getTheta()+"  ", 0,3);
			try { Thread.sleep(2000); } catch (InterruptedException e) {}
			LCD.drawString("finding 0  ", 0,5);
			robot.setRotationSpeed(-10); /* slow enough */
			for( ; ; ) {
				float theta = odo.getTheta();
				/* branch on a different, more useful, cut */
				if(theta > 180f) theta -= 360f;
				if(theta <= 0f && theta > -20f) break;
				try { Thread.sleep(100); } catch (InterruptedException e) {}
			}
			robot.stop();
			LCD.drawString(" found.", 9,5);
			LCD.drawString("t "+odo.getTheta()+"  ", 0,3);
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

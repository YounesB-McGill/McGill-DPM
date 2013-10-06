import lejos.nxt.UltrasonicSensor;
import lejos.nxt.LCD;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;

	private final static int granularity = 36;
	
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
		
		/* this is stupid */
		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall
			robot.setRotationSpeed(10);
			// keep rotating until the robot sees a wall, then latch the angle
			int us;
			while((us = getFilteredData()) > 40){
				LCD.drawString("US: "+us+"  ", 0, 0);
			}
			float a = odo.getTheta();
			LCD.drawString("t "+a, 0,0);
			robot.setRotationSpeed(0);
			// switch direction and wait until it sees no wall
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			// update the odometer position (example to follow:)
			odo.setPosition(0f, 0f, 0f); /* stupid */
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

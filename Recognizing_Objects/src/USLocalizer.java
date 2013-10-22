/* Lab 4, Group 51 -- Alex Bhandari-Young and Neil Edelman */
/*
This method is from lab4:Localization.
It localizes using the ultrasonic sensor.
*/
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;

	private final static int granularity       = 36;
	private final static int distanceThreshold = 36;
	/* fixme: double */
	private final static double speed          = 50;
	private final static boolean isMeasure     = false;
	/* really 3, but the sensor is reads higher values than is acutally the
	 case at close distances */
	private final static float cmToUltrasound  = 1f;
	private final static float cmSquare        = 30f;

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
			/* fixme: it should store a buffer of us data, then get the
			 linear intercept instead of approximating to the nearest
			 measurement */
			robot.setRotationSpeed(speed);
			// rotate the robot until it sees no wall
			LCD.drawString("wait clear  ", 0,1);
			while((us = getFilteredData()) < distanceThreshold) {
				LCD.drawString("US: "+us+"  ", 0, 0);
			}
			/* get clear of the wall */
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
			/* get clear of the region where the distance is indeterminate */
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
			/* that's really scetchy and not at all standard, but as long as
			 it's consistent */
			float correction = 45 - (a + b) * 0.5f;
			// update the odometer position (example to follow:)
			//odo.setPosition(0f, 0f, 0f);
			/* to complicated, wrote a new method */
			odo.correctTheta(correction);
			LCD.drawString("t "+odo.getTheta()+"  ", 0,3);
			/* this is code for the measurements */
			if(isMeasure) {
				LCD.drawString("finding 0  ", 0,5);
				robot.setRotationSpeed(10); /* slow enough */
				for( ; ; ) {
					float theta = odo.getTheta();
					/* this is why you should never branch cut dead in the middle of
					 your operating range :[ . . . */
					if(theta > 180f) theta -= 360f;
					if(theta >= 0f && theta < 20f) break;
					try { Thread.sleep(100); } catch (InterruptedException e) {}
				}
				/* don't setRotationSpeed(0) because it's completely fubar */
				robot.stop();
				LCD.drawString(" found.", 9,5);
			}
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
			if(isMeasure) {
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
			}
			LCD.drawString("t "+odo.getTheta()+"  ", 0,3);
		}
		/* approx distance from x, y (we conviently forgot :[ ) */
		if(!isMeasure) {
			/* fixme: we should just store the data and get it from there */
			float error, theta;
			for( ; ; ) {
				theta = odo.getTheta();
				if(theta <= 45f) theta += 360f; /* facepalm, [-Pi,Pi] really */
				error = odo.getTheta() - 270;
				if(error < 0.5) break;
				/* hack: p-control, the i (integral) is replaced with a constant
				 which is not correct, but the robot is going clockwise and it
				 needs to get there not at a snail's pace */
				robot.setRotationSpeed(-error * 1f - 5f);
				try { Thread.sleep(50); } catch (InterruptedException e) {}
			}
			robot.stop();
			float x = us = getFilteredData();
			LCD.clear();
			LCD.drawString("X "+us, 0,0);
			for( ; ; ) {
				theta = odo.getTheta();
				error = odo.getTheta() - 180;
				if(error < 0.5) break;
				robot.setRotationSpeed(-error * 1f - 5f);
				try { Thread.sleep(50); } catch (InterruptedException e) {}
			}
			robot.stop();
			float y = us = getFilteredData();
			LCD.drawString("Y "+us, 0,1);
			/* so (x,y) are not at the centre of rotation, fix this */
			x -= cmToUltrasound;
			y -= cmToUltrasound;
			/* set the odometer, confusingly (0,0) is after the first square,
			 so (0,0) is on the first line,  */
			odo.setX(-cmSquare + x);
			odo.setY(-cmSquare + y);
			x = odo.getX();
			y = odo.getY();
			float t = odo.getTheta();
			LCD.drawString("X "+x+"\nY: "+y+"\nT: "+t+" ", 0,0);
		}
	}

	/* fixme: get FILTED data; this is especially critical when two robots are
	 getting sound distances at the same time at the same frequency */
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

/*
 * Odometer.java
 */
import lejos.nxt.*;

public class Odometer extends Thread {
   
	/* constants */
	private final float deg2rad = (float)Math.PI / 180f;
	private final float rad2deg = 180f / (float)Math.PI;
	private final NXTRegulatedMotor leftMotor = Motor.A , rightMotor = Motor.B;
	private final float robotWidth     = 16f;
	private final float normaliseWidth = 2f / robotWidth;
	/* wheel * rad / deg */
	private final float wheelRadiusL   = /* 5.6 cm / 2 */ 2.8f * deg2rad;
	private final float wheelRadiusR   = /* 5.6 cm / 2 */ 2.8f * deg2rad;
	
	/* independent tachometer values; fixme: numerically unstable */
	private int previousLTacho, previousRTacho;

   // robot position
	/* fixme: int */
	private float x, y, theta;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 0f;
		y = 0f;
		theta = 0f;
		lock = new Object();

      previousLTacho = leftMotor.getTachoCount();
      previousRTacho = rightMotor.getTachoCount();
	}

	// run method (required for Thread)
	public void run() {
		int updateStart, updateEnd;

		while (true) {
			updateStart = (int)System.currentTimeMillis();
			
			/* compute displacment in degrees */
			int lTacho = leftMotor.getTachoCount();
			int rTacho = rightMotor.getTachoCount();
			int deltaL = lTacho - previousLTacho;
			int deltaR = rTacho - previousRTacho;
			/* update previous class variable for next update */
			previousLTacho = lTacho;
			previousRTacho = rTacho;

			/* calculate */

			float distanceL  = (float)deltaL * wheelRadiusL;
			float distanceR  = (float)deltaR * wheelRadiusR;
			float deltaArc   = (distanceL + distanceR) * 0.5f;
			float deltaTheta = (distanceL - distanceR) * normaliseWidth * rad2deg;

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				//update x,y,theta values using displacment vector
				double thetaIntemediate = theta + deltaTheta * 0.5f;
				/* fixme: sin, cos approx, Java doesn't have fmath? */
				x     += deltaArc * Math.cos(thetaIntemediate * Math.PI / 180.0);
				y     += deltaArc * Math.sin(thetaIntemediate * Math.PI / 180.0);
				theta += deltaTheta;
				if(     theta > 180f)  theta -= 180f;
				else if(theta < -180f) theta += 180f;
			}

			// this ensures that the odometer only runs once every period
			updateEnd = (int)System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(float[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(float x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(float y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(float theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}

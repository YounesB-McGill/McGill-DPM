/*
 * Odometer.java
 */
import lejos.nxt.*;

public class Odometer extends Thread {
   
	/* constants */
	private final NXTRegulatedMotor leftMotor = Motor.A , rightMotor = Motor.B;
	private final float robotWidth     = 16f;
	private final float normaliseWidth = 2f / robotWidth;
	private final float wheelRadiusL   = /* 5.6 cm / 2 */ 2.8f;
	private final float wheelRadiusR   = /* 5.6 cm / 2 */ 2.8f;
	
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

			float deltaArc   = ((float)deltaL * wheelRadiusL + (float)deltaR * wheelRadiusR) * 0.5f;
			float deltaTheta = ((float)deltaL * wheelRadiusL - (float)deltaR * wheelRadiusR) * normaliseWidth;

			//displacment vector
         //angleComponent = T+dT/2;
         //magnitudeComponent = dC * ( (2/dT) * Math.sin(dT/2) );

         // put (some of) your odometer code here

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				//update x,y,theta values using displacment vector
				float thetaIntemediate = theta + deltaTheta * 0.5f;
				/* fixme: sin, cos approx, Java doesn't have fmath? */
				x     += deltaArc * Math.cos((double)thetaIntemediate * Math.PI / 180.0);
				y     += deltaArc * Math.sin((double)thetaIntemediate * Math.PI / 180.0);
				theta += deltaTheta;
				/* fixme */
				if(theta > 180f) theta -= 180f;
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

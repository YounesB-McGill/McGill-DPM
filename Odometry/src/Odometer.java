/*
 * Odometer.java
 */
import lejos.nxt.*;

public class Odometer extends Thread {
   
	/* constants */

	private final NXTRegulatedMotor leftMotor = Motor.A , rightMotor = Motor.B;

	private final float toDegrees   = 180f / (float)Math.PI; /* [deg]/[rad] */
	private final float fromDegrees = (float)Math.PI / 180f; /* [rad]/[deg] */
	private final float robotWidth     = 16f; /* [cm] */
	private final float normaliseWidth = 1f / robotWidth; /* [cm^{-1}] */
	/* 5.6 cm / 2; fixme: get more accurate */
	private final float wheelRadiusL   = 2.8f * fromDegrees; /* [cm][rad]/[deg] */
	private final float wheelRadiusR   = 2.8f * fromDegrees; /* [cm][rad]/[deg] */
	
	/* independent tachometer values; fixme: numerically unstable */
	private int previousLTacho, previousRTacho;

	// robot position
	private float x, y, theta;

	// odometer update period, in ms
	private static final int ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 15.24f;
		y = -15.24f;
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
			
			/* compute displacment in [cm][deg]/[rad] */
			int lTacho = leftMotor.getTachoCount();
			int rTacho = rightMotor.getTachoCount();
			int deltaL = lTacho - previousLTacho;
			int deltaR = rTacho - previousRTacho;
			previousLTacho = lTacho;
			previousRTacho = rTacho;

			/* [cm][deg]/[rad] * [cm][rad]/[deg] = [cm] (are orthoganal) */
			float distanceL  = (float)deltaL * wheelRadiusL; /* [cm] */
			float distanceR  = (float)deltaR * wheelRadiusR; /* [cm] */
			float deltaArc   = (distanceR + distanceL) * 0.5f; /* [cm] */

			/* [cm] / [cm] [deg]/[rad] = [deg] (unitless) */
			float deltaTheta = (distanceR - distanceL) * normaliseWidth * toDegrees;

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				//update x,y,theta values using displacment vector
				float thetaIntemediate = (theta + deltaTheta * 0.5f); /* [deg] */
				theta += deltaTheta; /* [deg] */
				/* numerical stability (assert -180 < deltaTheta < 180) */
				if(     theta > 180f)  theta -= 360f;
				else if(theta < -180f) theta += 360f;
				/* convert to normal radians for sin/cos */
				thetaIntemediate *= fromDegrees; /* [rad] */
				x     += deltaArc * Math.cos(thetaIntemediate); /* [cm] */
				y     += deltaArc * Math.sin(thetaIntemediate); /* [cm] */
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

	public float getX() {
		float result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public float getY() {
		float result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public float getTheta() {
		float result;

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

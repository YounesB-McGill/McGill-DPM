/* Group 51 Alex Bhandari-Young and Neil Edelman

 The internal representation is the robot points along the x-axis, with
 standard coordinates. The units are in cm, except the int tachometer values
 which are in cm deg/rad. */

/* must be linked with lejos */
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;
import java.lang.String;

public class Odometer extends Thread {

	/* constants */

	private final float toDegrees   = 180f / (float)Math.PI; /* [deg]/[rad] */
	private final float fromDegrees = (float)Math.PI / 180f; /* [rad]/[deg] */

	private static final int period = 25; /* ms */

	private final float wheelRadius = 2.78f/*2.95f*/; /* cm */
	private final float wheelBase   = 16.2f; /* cm */
	private final float normBase    = 1 / wheelBase; /* cm^{-1} */
	private final float angularCorrection = wheelBase * 0.5f / wheelRadius;

	private final NXTRegulatedMotor lMotor = Motor.A, rMotor= Motor.B;

	/* independent tachometer values; fixme: numerically unstable */
	private int lLastTach, rLastTach;

	/* robot position */
	private float x, y, theta;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x     = 0f;
		y     = 0f;
		theta = 0f;
		lock = new Object();
		lLastTach = lMotor.getTachoCount();
		rLastTach = rMotor.getTachoCount();
	}

	// run method (required for Thread)
	public void run() {
		int updateStart, updateEnd;

		while (true) {
			updateStart = (int)System.currentTimeMillis();

			/* compute displacment in [cm][deg]/[rad] */
			int lTach    = lMotor.getTachoCount();
			int rTach    = rMotor.getTachoCount();
			int lDelta   = lTach - lLastTach;
			int rDelta   = rTach - rLastTach;
			lLastTach = lTach;
			rLastTach = rTach;

			/* [cm][deg]/[rad] * [cm][rad]/[deg] = [cm] (are orthoganal) */
			float lDistance = (float)lDelta * wheelRadius; /* [cm] */
			float rDistance = (float)rDelta * wheelRadius; /* [cm] */
			float deltaArc  = (rDistance + lDistance) * 0.5f; /* [cm] */

			/* [cm] / [cm] [deg]/[rad] = [deg] (unitless) */
			float thetaDelta = (rDistance - lDistance) * normBase * toDegrees;

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				//update x,y,theta values using displacment vector
				float thetaIntemediate = (theta + thetaDelta * 0.5f); /* [deg] */
				theta += thetaDelta; /* [deg] */
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
			if (updateEnd - updateStart < period) {
				try {
					Thread.sleep(period - (updateEnd - updateStart));
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

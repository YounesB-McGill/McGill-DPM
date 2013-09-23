/*
 * Odometer.java
 */
import lejos.nxt.*;

public class Odometer extends Thread {
   
	private final NXTRegulatedMotor leftMotor = Motor.A , rightMotor = Motor.B;
   private final double robotWidth = 100; //robot width in idk yet
   private final double rL = 10; //left wheel radius
   private final double rR = 10; //right wheel radius
   private double previousLTacho, previousRTacho;
	
   // robot position
	private double x, y, theta;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();

      previousLTacho = leftMotor.getTachoCount();
      previousRTacho = rightMotor.getTachoCount();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
         //compute displacment
         double lTacho = leftMotor.getTachoCount(); //get tacho count
         double rTacho = rightMotor.getTachoCount();//get tacho count
         double dL = lTacho - previousLTacho; //tacho change
         double dR = rTacho - previousRTacho; //tacho change
         previousLTacho = lTacho; //update previous for next update
         previousRTacho = rTacho; //update previous for next update
         double dC = (dL*rL+dR*rR)/2; //calculated change in arc length
         double dT = (dR*rR-dL*rL)/robotWidth; //calculated change in theta
         //displacment vector
         //angleComponent = T+dT/2;
         //magnitudeComponent = dC * ( (2/dT) * Math.sin(dT/2) );

         // put (some of) your odometer code here

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
//				theta = -0.7376;

            //update x,y,theta values using displacment vector
            x=x+dC*Math.cos(theta+dT/2);
            y=y+dC*Math.sin(theta+dT/2);
            theta=theta+dT;
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
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
	public void setPosition(double[] position, boolean[] update) {
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

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}

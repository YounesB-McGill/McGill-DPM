/* Lab 3
 Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* must be linked with lejos */
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;
import java.lang.String;

/* " . . . robot to an absolute position on the field while avoiding obstacles,
 by use of the odometer and an ultrasonic sensor */

class Navigator extends Thread /*implements Runnable*/ {
	private final float toDegrees   = 180f / (float)Math.PI; /* [deg]/[rad] */
	private final int period = 400;

	private final float wheelRadius = 2.78f; /* cm */
	private final float wheelBase   = 16.2f; /* cm */
	private final float angle       = wheelBase * 0.5f / wheelRadius;
	private final float distError = 3; /* cm^{-1/2} */
	private final NXTRegulatedMotor leftMotor = Motor.A , rightMotor = Motor.B;
	private Odometer odometer = new Odometer();

	//running when true
	boolean isNavigating;
	String navMessage = "stopped";

	/** constructor */
	public Navigator() {
		odometer.run();
	}

	/** to string, may be useful */
	public String toString() {
		return navMessage;
	}
	
	/** implements all the navigation */
	public void run() {
		for( ; ; ) {
			LCD.drawString("Nav: " + navMessage, 0, 0);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	/** "This method causes the robot to travel to the absolute field
	 location (x, y). This method should continuously call turnTo(double theta)
	 and then set the motor speed to forward(straight). This will make sure
	 that your heading is updated until you reach your exact goal.
	 (This method will poll the odometer for information)" */
	void travelTo(float xTarget, float yTarget) {
		float xCurrent, yCurrent, x, y;
		float tCurrent, theta;

		isNavigating = true;
		navMessage = "to " + xTarget + ", " + yTarget;
		for( ; ; ) {
			xCurrent = odometer.getX();
			yCurrent = odometer.getY();
			tCurrent = odometer.getTheta();
			x = xTarget - xCurrent;
			y = yTarget - yCurrent;
			if(x*x + y*y > distError) break;
			theta = (float)Math.atan2(y, x)*toDegrees - tCurrent;
			if(     theta >  180f) theta -= 180f;
			else if(theta < -180f) theta += 180f;
			if(theta > 20f) {
				this.turnTo(theta);
			} else {
				leftMotor.setSpeed(200);
				rightMotor.setSpeed(200);
				leftMotor.forward();
				rightMotor.forward();
			}
			try {
				Thread.sleep(period);
			} catch(InterruptedException e) {
			}
		}
		isNavigating = false;
	}

	/** "This method causes the robot to turn (on point) to the absolute
	 heading theta. This method should turn a MINIMAL angle to it's target."
	 in "degrees" */
	void turnTo(float theta) {
		int rotate = (int)(theta * angle);
		leftMotor.rotate((int)-rotate, true);
		rightMotor.rotate((int)rotate, true);
	}
	
	/** "This method returns true if another thread has called travelTo() or
	 turnTo() and the method has yet to return; false otherwise."
	 fixme: not implemented */
	boolean isNavigating() {
		return isNavigating;
	}
}

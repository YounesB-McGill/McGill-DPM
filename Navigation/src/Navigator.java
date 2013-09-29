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
	private final float fromDegrees = (float)Math.PI / 180f; /* [rad]/[deg] */
	private final float wheelRadius = 2.78f/*2.95f*/;
	private final float wheelBase   = 16.2f;
	private final float angularCorrection = wheelBase * 0.5f / wheelRadius;
	private final NXTRegulatedMotor leftMotor = Motor.A , rightMotor = Motor.B;

	boolean isNavigating;
	String navMessage = "stopped";
	/* the actual x, y, \theta at which the robot thinks */
	float x, y, t;

	/** constructor */
	public Navigator() {
		x = 0f;
		y = 0f;
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
	void travelTo(float x, float y) {
		navMessage = "to " + x + ", " + y;
		/* fixme: hahahahahahahaha more complcated system */
		this.turnTo((float)Math.atan2(y, x) * toDegrees);
	}

	/** "This method causes the robot to turn (on point) to the absolute
	 heading theta. This method should turn a MINIMAL angle to it's target."
	 in "degrees" */
	void turnTo(float theta) {
		navMessage = "to " + theta;
		int rotate = (int)(theta * angularCorrection);
		leftMotor.rotate((int)-rotate, true);
		rightMotor.rotate((int)rotate, false);
	}
	
	/** "This method returns true if another thread has called travelTo() or
	 turnTo() and the method has yet to return; false otherwise."
	 fixme: not implemented */
	boolean isNavigating() {
		return isNavigating;
	}
}

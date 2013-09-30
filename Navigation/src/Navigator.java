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

	private Odometer odometer;
	private float xTarget, yTarget;

	//running when true
	boolean isNavigating;
	String navMessage = "running";

	/** constructor */
	public Navigator(Odometer odometer) {
		this.odometer = odometer;
	}

	/** to string, may be useful */
	public String toString() {
		return navMessage;
	}
	
	/** implements all the navigation */
	public void run() {
		float xCurrent, yCurrent, x, y;
		float tCurrent, theta;

		for( ; ; ) {
			/* get values */
			//LCD.drawString("Nav: " + navMessage, 0, 0);
			xCurrent = odometer.getX();
			yCurrent = odometer.getY();
			tCurrent = odometer.getTheta();
			x = xTarget - xCurrent;
			y = yTarget - yCurrent;
			theta = (float)Math.atan2(y, x)*toDegrees - tCurrent;
			if(     theta >  180f) theta -= 180f;
			else if(theta < -180f) theta += 180f;
			/* print */
			LCD.drawString("Cur: x "+xCurrent+"\n     y "+yCurrent+"\n     t "+tCurrent, 0, 0);
			LCD.drawString("Tar: x "+xTarget+"\n     y "+yTarget, 0, 3);
			LCD.drawString("Del: x "+x+"\n     y "+y, 0, 5);
			LCD.drawString("     t "+theta, 0, 7);
			/* react */
			if(/*x*x + y*y < distError*/theta > -2f && theta < 2f) {
				isNavigating = false;
				leftMotor.stop();
				rightMotor.stop();
				navMessage = "stopped";
				LCD.drawString("(stopped)", 0, 0);
			} else {
				isNavigating = true;
				//LCD.drawString("     t "+theta, 0, 7);
				//LCD.drawString("Dif:(\n"+x+"\n,"+y+":\n"+theta+")    ", 0, 1);
				//this.turnTo(theta);
				float rotate = theta * angle;
				if(theta > 0) rotate = 10;
				else          rotate = -10;
				leftMotor.setSpeed(-rotate); /* fixme: I cannot get it to revese; stupid robot it's fucking midnight I give up */
				rightMotor.setSpeed(rotate);

/*				if(theta > 20f) {
					navMessage = "turning";
					this.turnTo(theta);
				} else {
					navMessage = "forward";
					leftMotor.setSpeed(50);
					rightMotor.setSpeed(50);
					leftMotor.forward();
					rightMotor.forward();
				}*/
				leftMotor.forward();
				rightMotor.forward();
			}
			try {
				Thread.sleep(period);
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
		/* fixme!!! bounds checking, please */
		xTarget = x;
		yTarget = y;
	}

	/** "This method causes the robot to turn (on point) to the absolute
	 heading theta. This method should turn a MINIMAL angle to it's target."
	 in "degrees" */
	void turnTo(float theta) {
		int rotate = (int)(theta * angle);

		/*leftMotor.setSpeed((int)-rotate);
		rightMotor.setSpeed((int)rotate);*/
		/*leftMotor.rotate((int)-rotate, true);
		rightMotor.rotate((int)rotate, true);*/
	}
	
	/** "This method returns true if another thread has called travelTo() or
	 turnTo() and the method has yet to return; false otherwise."
	 fixme: not implemented */
	boolean isNavigating() {
		return isNavigating;
	}
}

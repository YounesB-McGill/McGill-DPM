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
	private final int period = 400;

	private final float wheelRadius = 2.78f; /* cm */
	private final float wheelBase   = 16.2f; /* cm */
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
	
	/** "This method causes the robot to travel to the absolute field
	 location (x, y). This method should continuously call turnTo(double theta)
	 and then set the motor speed to forward(straight). This will make sure
	 that your heading is updated until you reach your exact goal.
	 (This method will poll the odometer for information)" */
	void travelTo(float x, float y) {
		float xCurrent, yCurrent, tCurrent;
      this.isNavigating = true;
		/* fixme!!! bounds checking, please */
		xTarget = x;
		yTarget = y;
		/* get values */
      xCurrent = odometer.getX();
      yCurrent = odometer.getY();
      tCurrent = odometer.getTheta();
      x = xTarget - xCurrent;
      y = yTarget - yCurrent;
      theta = normTheta((float)Math.atan2(y, x) - tCurrent);
      
		/* print */
		LCD.drawString("Cur: x "+xCurrent+"\n     y "+yCurrent+"\n     t "+tCurrent, 0, 0);
		LCD.drawString("Tar: x "+xTarget+"\n     y "+yTarget, 0, 3);
		LCD.drawString("Del: x "+x+"\n     y "+y, 0, 5);
		LCD.drawString("     t "+theta, 0, 7);
		/* react */
		float dist2 = x*x + y*y;
		if(dist2 < distError) {
			isNavigating = false;
			leftMotor.stop();
			rightMotor.stop();
			navMessage = "stopped";
			LCD.drawString("(stopped)", 0, 0);
		} else {
			isNavigating = true;
         //adjust angle to object
			this.turnTo(theta*wheelBase/2);
         //move forward
			float dist = Math.sqrt(dist2);
			leftMotor.rotate(motorDegrees(dist), true);
			rightMotor.rotate(motorDegrees(dist), false);
		}
	}

	/** "This method causes the robot to turn (on point) to the absolute
	 heading theta. This method should turn a MINIMAL angle to it's target."
	 in "degrees" */
	void turnTo(float theta) {
      turnLeft(theta);
	}
  
  
   float toDegrees(radians) {
      return radians * (180f/(float)Math.PI); /* [rad] times [deg]/[rad] */
   }
   /** "Use with motor.rotate() Converts radian distance to degree rotations.*/
   private int motorDegrees(float radianDist) {
      return (int) ((180.0 * radianDist) / (Math.PI * wheelRadius));
   }
   private void turnLeft(float rad) {
      leftMotor.rotate(motorDegrees(-rad),true);
      rightMotor.rotate(motorDegrees(rad),false);
   }
   private void turnRight(float rad) {
      leftMotor.rotate(motorDegrees(rad),true);
      rightMotor.rotate(motorDegrees(-rad),false);
   }
   private float normTheta(theta) {
      if(     theta >  Math.PI) theta -= Math.PI;
      else if(theta < -Math.PI) theta += Math.PIi;
      return theta;
   }
	
	/** "This method returns true if another thread has called travelTo() or
	 turnTo() and the method has yet to return; false otherwise."
	 fixme: not implemented */
	boolean isNavigating() {
		return isNavigating;
	}
}

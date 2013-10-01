/* Lab 3
 Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* must be linked with lejos */
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;

/* " . . . robot to an absolute position on the field while avoiding obstacles,
 by use of the odometer and an ultrasonic sensor */

class Navigator extends Thread /*implements Runnable*/ {
	private final int period = 40;

	private final float wheelRadius = 2.78f; /* cm */
	private final float wheelBase   = 16.2f; /* cm */
	private final float distError = 3; /* cm^{-1/2} */
	private final NXTRegulatedMotor leftMotor = Motor.A , rightMotor = Motor.B;

	private Odometer odometer;
	//running when true
	private boolean isNavigating;
   private float dx,dy,xTarget,yTarget;

	/** constructor */
	public Navigator(Odometer odometer) {
		this.odometer = odometer;
      this.isNavigating = false;
      this.dx = 0;
      this.dy = 0;
      this.xTarget = 0;
      this.yTarget = 0;
	}

	/** "This method causes the robot to travel to the absolute field
	 location (x, y). This method should continuously call turnTo(double theta)
	 and then set the motor speed to forward(straight). This will make sure
	 that your heading is updated until you reach your exact goal.
	 (This method will poll the odometer for information)" */
	void travelTo(float tx, float ty) {
      this.isNavigating = true;
      xTarget = tx;
      yTarget = ty;
      //compute delta x,y
      dx = xTarget - odometer.getX();
      dy = yTarget - odometer.getY();
      float theta = normTheta((float)Math.atan2(dy,dx) - odometer.getTheta()*(float)Math.PI/180f);
      
		/* react */
		float dist = (float)Math.sqrt(dx*dx + dy*dy);
		if(dist < distError) {
			isNavigating = false;
			leftMotor.stop();
			rightMotor.stop();
//			LCD.drawString("(stopped)", 0, 0);
		} else {
			isNavigating = true;
         //adjust angle to object
			this.turnTo(theta*wheelBase/2);
         //move forward
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
  
  
   float toDegrees(float radians) {
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
   private float normTheta(float theta) {
      if(     theta >  Math.PI) theta -= Math.PI;
      else if(theta < -Math.PI) theta += Math.PI;
      return theta;
   }
   int getPeriod() {
      return this.period;
   }
   float xTarget() {
      return this.xTarget;
   }
   float yTarget() {
      return this.yTarget;
   }
  
	
	/** "This method returns true if another thread has called travelTo() or
	 turnTo() and the method has yet to return; false otherwise."*/
	boolean isNavigating() {
		return isNavigating;
	}
}

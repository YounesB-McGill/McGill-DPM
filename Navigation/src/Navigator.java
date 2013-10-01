/* Lab 3
 Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* must be linked with lejos */
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;
import lejos.nxt.LCD;

/* " . . . robot to an absolute position on the field while avoiding obstacles,
 by use of the odometer and an ultrasonic sensor */

class Navigator extends Thread /*implements Runnable*/ {
	private final float toDegrees = 180f / (float)Math.PI;
	private final int period = 100;
	private final float wheelRadius = 2.78f; /* cm */
	private final float wheelBase   = 16.2f; /* cm */
	private final float dist2Error  = 1; /* cm^{-1/2} */
	private final NXTRegulatedMotor leftMotor = Motor.A , rightMotor = Motor.B;

	private Odometer odometer;
	//running when true
	private boolean isNavigating;

	/** constructor */
	public Navigator(Odometer odometer) {
		this.odometer = odometer;
		this.isNavigating = false;
		/*this.xDelta = 0;
		this.yDelta = 0;
		this.xTarget = 0;
		this.yTarget = 0;*/
	}

	/** "This method causes the robot to travel to the absolute field
	 location (x, y). This method should continuously call turnTo(double theta)
	 and then set the motor speed to forward(straight). This will make sure
	 that your heading is updated until you reach your exact goal.
	 (This method will poll the odometer for information)" */
	void travelTo(float x, float y, String name) {
		float xOdo, yOdo, tOdo;
		float xDelta, yDelta, tDelta;
		float xTarget, yTarget;
		float left, right;

		this.isNavigating = true;

		/* fixme: do some filtering */
		xTarget = x;
		yTarget = y;

		/* turn to target */
		//this.turnTo((float)Math.atan2(yDelta,xDelta)*toDegrees);

		/* loop until it gets there;
		 fixme: blocking (this is what we want for this lab, but in general) */
		for( ; ; ) {
			xOdo = odometer.getX();
			yOdo = odometer.getY();
			tOdo = odometer.getTheta();
			xDelta = xTarget - xOdo;
			yDelta = yTarget - yOdo;
			float dist2 = xDelta*xDelta + yDelta*yDelta;
			float dist = (float)Math.sqrt(dist2);
			/* if it's gotten there */
			if(dist2 < dist2Error) {
				isNavigating = false;
				leftMotor.stop();
				rightMotor.stop();
				LCD.drawString("target " + name, 0, 0);
				break;
			}
			/* calculate deviation */
			tDelta = (float)Math.atan2(yDelta, xDelta) * toDegrees - tOdo;
			/* do some magic */
			left  = 300f - tDelta * 6f;
			right = 300f + tDelta * 6f;
			/* set speed */
			leftMotor.setSpeed(left);
			rightMotor.setSpeed(right);
			/* why nxj? */
			if(left < 0) {
				leftMotor.backward();
			} else {
				leftMotor.forward();
			}
			if(right < 0) {
				rightMotor.backward();
			} else {
				rightMotor.forward();
			}
			/* sleep */
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				/* don't care */
			}
		}
	}

	/** "This method causes the robot to turn (on point) to the absolute
	 heading theta. This method should turn a MINIMAL angle to it's target."
	 in "degrees" */
	void turnTo(float theta) {
		float tDelta = theta - odometer.getTheta();
		leftMotor.rotate(-(int)tDelta, true);
		rightMotor.rotate((int)tDelta, false);
	}
	/*void turnTo(float theta) {
      turnLeft(theta);
	}*/
  
  
   /*float toDegrees(float radians) {
      return radians * (180f/(float)Math.PI); [rad] times [deg]/[rad]
   }*/
   /** "Use with motor.rotate() Converts radian distance to degree rotations.*/
   /*private int motorDegrees(float radianDist) {
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
   }*/
   int getPeriod() {
      return this.period;
   }
   /*float xTarget() {
      return this.xTarget;
   }
   float yTarget() {
      return this.yTarget;
   }*/

	/** "This method returns true if another thread has called travelTo() or
	 turnTo() and the method has yet to return; false otherwise."*/
	boolean isNavigating() {
		return isNavigating;
	}
}

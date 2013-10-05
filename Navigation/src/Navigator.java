/* Lab 3
 Group 51 -- Alex Bhandari-Young and Neil Edelman 
The navigation class is the crux of this lab. It contains the travelTo method,
which is passed a coordinate in (float x,float y) form. Our coordinate system
was explained in the last lab in more depth. It is left handed, with x-positive
in the direction of theta=0. Theta is measured counter clockwise from the positive
x-axis, and ranges from -180 to 180 degrees. More info in Odometer.java
The travelTo method calles the turnTo method, which points the robot toward the
target point. It then checks the ultrasonic sensor. If there is an obstruction,
it turns left and navigates in a partial arc around it. This sequence is contained
in a while loop, which runs while the robot is farther than the tolerated distance
from the target. This is the basic theory. Additional checks are added to smooth
the robot's operation and reduce error. The turnTo() method caused the robot to
stop and turn towards the target, which creates a jolty motion. We expreimented
with difference ways to handle this, including adding a theshold theta to correct
to, which was rejected because it decreased accuracy. We ended up only adjusting theta
with turnTo every tenth cycle. This minimizes the amount of jolting while also keeping
the robot's direction correct. Additionally, when the robot navigates around an object,
it changes turnCount (which counts cycles for turning) to -1, which is updated to 0 so
that turnTo runs after the navigation and corrects its direction.
 */

//for motors
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;
//for ultrasonic sensor
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.SensorPort;

/* " . . . robot to an absolute position on the field while avoiding obstacles,
 by use of the odometer and an ultrasonic sensor */

class Navigator extends Thread /*implements Runnable*/ {
	private final int period = 40;
   
   //constants for robot attributes
	private final float wheelRadius = 2.78f; /* cm */
	private final float wheelBase   = 16.2f; /* cm */
	private final float distError = 3; /* cm^{-1/2} */
	private final NXTRegulatedMotor leftMotor = Motor.A , rightMotor = Motor.B;
   private final float uDistThreshold = 25;
   private final int sleepPeriod = 200;
   private final float thetaThreshold = (float)Math.PI/60f; //keep theta within 180/60=3 degrees

   private static final SensorPort usPort = SensorPort.S1;
   UltrasonicSensor ultrasonic = new UltrasonicSensor(usPort);
	private Odometer odometer;
	//running when true
	private boolean isNavigating;
   private float dx,dy,xTarget,yTarget;
   private float uDist;
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
      //initialize delta x and y before the while loop
      dx = xTarget - odometer.getX();
      dy = yTarget - odometer.getY();
		
      /* react */
		float dist = (float)Math.sqrt(dx*dx + dy*dy);
      boolean turn = true;
      int turnCount = 0; //adjusts angle every 5 cycles, test ultrasonic every cycle
	   while(dist > distError) {
         if(turnCount == 10) {turnCount = 0;}
	      //update delta x,y,theta
         dx = xTarget - odometer.getX();
         dy = yTarget - odometer.getY();
         //this is the theta that we want to turn to
         float theta = normTheta((float)Math.atan2(dy,dx) - odometer.getTheta()*(float)Math.PI/180f);
		   //update distance
         dist = (float)Math.sqrt(dx*dx + dy*dy);
         //get ultrasonic sensor distance
         uDist=ultrasonic.getDistance();
         //adjust angle to object every ten cycles of the while loop
         if( (turnCount == 0 ))
			   this.turnTo(theta*wheelBase/2);
         //if the robot is close to an obsticle, it navigates in an arc around it
         if(uDist < uDistThreshold) {
            turnLeft((float)Math.PI*wheelBase/4);
            leftMotor.setSpeed(200);
			   rightMotor.setSpeed(150);
            leftMotor.forward();
            rightMotor.forward();
  			   try {
			   	Thread.sleep(4000);
			   } catch (Exception e) {
			   	System.out.println("Error: " + e.getMessage());
			   }
            turnRight((float)Math.PI*wheelBase/6);
            leftMotor.setSpeed(250);
			   rightMotor.setSpeed(250);
            leftMotor.forward();
            rightMotor.forward();
  			   try {
			   	Thread.sleep(2000);
			   } catch (Exception e) {
			   	System.out.println("Error: " + e.getMessage());
			   }
            turnCount = -1; //this is updated to 0 below causing turnTo to run and fix the robots direction after the correction around the object
            turn = false; //prevents robot from turning again on the loop back
         } else {turn = true;}
         //move forward
			leftMotor.setSpeed(200);
			rightMotor.setSpeed(200);
         leftMotor.forward();
         rightMotor.forward();
 			try {
				Thread.sleep(sleepPeriod);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
         turnCount++;

		}
      leftMotor.stop();
      rightMotor.stop();
      isNavigating = false;
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
   //turn methods, speed is adjusted to improve accuracy
   private void turnLeft(float rad) {
      leftMotor.setSpeed(100);
      rightMotor.setSpeed(100);
      leftMotor.rotate(motorDegrees(-rad),true);
      rightMotor.rotate(motorDegrees(rad),false);
      leftMotor.setSpeed(200);
      rightMotor.setSpeed(200);
   }
   private void turnRight(float rad) {
      leftMotor.setSpeed(100);
      rightMotor.setSpeed(100);
      leftMotor.rotate(motorDegrees(rad),true);
      rightMotor.rotate(motorDegrees(-rad),false);
      leftMotor.setSpeed(200);
      rightMotor.setSpeed(200);
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

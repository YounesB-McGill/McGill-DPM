/*
Wall Follower Lab
Group 51
Alex Bhandari-Young and Neil Edelman
*/
import lejos.nxt.*;

public class PController implements UltrasonicController {

   //attribute variables
	private final int walldist, tolerance;
	private final int motorStraight = 200, FILTER_OUT = 100;
   private final int DELTA = 25;
   private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;	
	private int distance;
	private int currentLeftSpeed;
	private int filtercontrol = 0;
   private int nrCount = 0;

	
	public PController(int walldist, int tolerance) {
		//Default Constructor
//      walldist = 30;
//      tolerance = 5;
		this.walldist = walldist;
		this.tolerance = tolerance;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
	}
	
	@Override
	public void processUSData(int distance) {
      //added to restart motors if stopped or in reverse
      //needed for this code
      leftMotor.forward();
      rightMotor.forward();
	   //update distance and error variables
      this.distance = distance;
		int error = distance-walldist;

// TODO: process a movement based on the us distance passed in (P style)

      if(distance==255)
      {
         if(nrCount<60)
            error=0;
         else
            error=error/10;
         nrCount++;
      }
      else
      {
         nrCount=0;
      }
      if(Math.abs(error)<=tolerance) //within tolerance
      {
         leftMotor.setSpeed(motorStraight);
         rightMotor.setSpeed(motorStraight);
      }

         leftMotor.setSpeed(motorStraight-20*error);
         rightMotor.setSpeed(motorStraight+20*error);
         if(motorStraight-DELTA*error<0)
            leftMotor.setSpeed(50);
         if(motorStraight+DELTA*error<0)
            rightMotor.setSpeed(50);

	}
	
	@Override
	public int readUSDistance() {
		return this.distance;
	}
	@Override
   public int leftMotorSpeed() {
      return leftMotor.getSpeed();
   }
	@Override
   public int rightMotorSpeed() {
      return rightMotor.getSpeed();
   } 
}

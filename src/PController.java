/*
Wall Follower Lab
Group 51
Alex Bhandari-Young and Neil Edelman
*/
import lejos.nxt.*;

public class PController implements UltrasonicController {

   //attribute variables
	private final int walldist, tolerance;
	private final int motorStraight = 200;
//   private final int FILTER_OUT = 100; //FILTER_OUT is replaced with nrCount
   private final int DELTA = 20; //multiplies error to determine speed change
   private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;	
	private int distance;
	private int currentLeftSpeed; //not used
//	private int filtercontrol = 0; //not used
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

      //filter out 255 values until 60 consecutive 255 readings
      if(distance==255)
      {
         if(nrCount<60)
            error=0;
         else
            error=error/10;
         nrCount++;
      }
      //resets 255 count if not consecutive
      else
      {
         nrCount=0;
      }

      //logic to determine motor speed and orientation
      if(Math.abs(error)<=tolerance) //within tolerance
      {
         leftMotor.setSpeed(motorStraight);
         rightMotor.setSpeed(motorStraight);
      }
      //in this method we allowed error to be negative, eliminating the need
      //for an else-if else statment checking the turn direction
      leftMotor.setSpeed(motorStraight-DELTA*error);
      rightMotor.setSpeed(motorStraight+DELTA*error);
      //in this method we also added a special case because a large error
      //will result in a negative number, which requires the reverse method
      //to be implemented correctly. We decieded not to implement this method.
      if(motorStraight-DELTA*error<0)
         leftMotor.setSpeed(0);
      if(motorStraight+DELTA*error<0)
         rightMotor.setSpeed(0);

	}
	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

   //print motor speed added for debugging
	@Override
   public int leftMotorSpeed() {
      return leftMotor.getSpeed();
   }
	@Override
   public int rightMotorSpeed() {
      return rightMotor.getSpeed();
   } 
}

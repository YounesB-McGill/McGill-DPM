/*
Wall Follower Lab
Group 51
Alex Bhandari-Young and Neil Edelman
*/
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{

	//attribute variables
   private final int walldist;
   private final int tolerance; 
   private final int motorLow, motorHigh; //not used
	private final int motorStraight = 200;
   private final int DELTA = 175;  //multiplies error to determine speed change
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	private int distance;
	private int currentLeftSpeed; //not used
   private int nrCount = 0; //no read (255) count for filter control

	public BangBangController(int walldist, int tolerance, int motorLow, int motorHigh) {
		//Default Constructor
		this.walldist = walldist;
		this.tolerance = tolerance;
      this.motorLow = motorLow;
      this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
	}
	
	@Override
	public void processUSData(int distance) {
      //added to restart motors if stopped or in reverse
      //not currently needed as motors never go below speed=50
		leftMotor.forward();
      rightMotor.forward();
      //update distance and error variables
      this.distance = distance;
		int error = distance-walldist;

		// TODO: process a movement based on the us distance passed in (BANG-BANG style)

      //255 distance Filter implemented for the bangbang controller as well as p to improve smoothness
      if(distance==255)
      {
         if(nrCount<6)
            error=0;
         nrCount++;
      }
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
      else if(error < 0) //too close, turn right
      {
         leftMotor.setSpeed(motorStraight+DELTA/2);
         rightMotor.setSpeed(motorStraight-DELTA*2);
      }
      else //too far, turn left
      {
        leftMotor.setSpeed(motorStraight-DELTA*2);
        rightMotor.setSpeed(motorStraight+DELTA/2);
      }
      

      
      
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

   //print motor speed methods added for debugging
	@Override
   public int leftMotorSpeed() {
      return leftMotor.getSpeed();
   }
	@Override
   public int rightMotorSpeed() {
      return rightMotor.getSpeed();
   }
}

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class PController implements UltrasonicController {
	
	private final int walldist, tolerance;
	private final int motorStraight = 200, FILTER_OUT = 30;
   private final int DELTA = 25;
   private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;	
	private int distance;
	private int currentLeftSpeed;
	private int filtercontrol = 0;
   private int nrCount = 0;

	
	public PController(int walldist, int tolerance) {
		//Default Constructor
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
	   
      this.distance = distance;
		int error = distance-walldist;
	
//		// rudimentary filter
//		if (distance == 255 && filterControl < FILTER_OUT) {
//			// bad value, do not set the distance var, however do increment the filter value
//			filterControl ++;
//		} else if (distance == 255){
//			// true 255, therefore set distance to 255
//			this.distance = distance;
//		} else {
//			// distance went below 255, therefore reset everything.
//			filterControl = 0;
//			this.distance = distance;
//		}

// TODO: process a movement based on the us distance passed in (P style)

      //255 count(no read)

//      if(distance==255)
//      {
//         if(filtercontrol<FILTER_OUT)
//            error=0;
//         filtercontrol++;
//      }
//      else
//      {
//         filtercontrol=0;
//      }
//      if(error == 0) //check for debug
//      {
//         leftMotor.setSpeed(motorStraight);
//         rightMotor.setSpeed(motorStraight);
//      }
//      if(Math.abs(error)<=tolerance) //within tolerance
//      {
//         leftMotor.setSpeed(motorStraight);
//         rightMotor.setSpeed(motorStraight);
//      }
//      else if(error < 0) //too close, turn right
//      {
//         leftMotor.setSpeed(motorStraight+DELTA*scale);
//         rightMotor.setSpeed(motorStraight-DELTA*scale);
//      }
//      else //too far, turn left
//      {
//        leftMotor.setSpeed(motorStraight-DELTA*scale);
//        rightMotor.setSpeed(motorStraight+DELTA*scale);
//      }
      leftMotor.forward();
      rightMotor.forward();
      if(distance==255)
      {
         if(nrCount<30)
            error=0;
         else
            error=error/4;
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
//      else if(error < 0) //too close, turn right
//      {
//         leftMotor.setSpeed(motorStraight+75+scale);
//         rightMotor.setSpeed(motorStraight-75-scale);
//        
//      }
//      else //too far, turn left
//      {
         leftMotor.setSpeed(motorStraight-15*error);
         rightMotor.setSpeed(motorStraight+15*error);
         if(motorStraight-DELTA*error<0)
            leftMotor.setSpeed(50);
         if(motorStraight+DELTA*error<0)
            rightMotor.setSpeed(50);

//      }
      

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

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith; //wall dist, tolerance
	private final int motorLow, motorHigh;
	private final int motorStraight = 200;
   private final int DELTA = 50;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	private int distance;
	private int currentLeftSpeed;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
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
		this.distance = distance;
		int error = distance-bandCenter;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
      int nrCount = 0; //255 count(no read)
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
      if(Math.abs(error)<=bandwith) //within tolerance
      {
         leftMotor.setSpeed(motorStraight);
         rightMotor.setSpeed(motorStraight);
      }
      else if(error < 0) //too close, turn right
      {
         leftMotor.setSpeed(motorStraight+DELTA);
         rightMotor.setSpeed(motorStraight-DELTA);
      }
      else //too far, turn left
      {
        leftMotor.setSpeed(motorStraight-DELTA);
        rightMotor.setSpeed(motorStraight+DELTA);
      }
      


      
      
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}

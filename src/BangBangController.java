import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 200;
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
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
      if(this.bandCenter > this.distance)
      {
         rightMotor.setSpeed(motorLow);
         leftMotor.setSpeed(motorStraight);;
      }
      else if(this.bandCenter < this.distance)
      {
         leftMotor.setSpeed(motorLow);
         rightMotor.setSpeed(motorStraight);
      }
      
      leftMotor.forward();
      rightMotor.forward();
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}

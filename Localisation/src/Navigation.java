//for ultrasonic sensor
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.SensorPort;

public class Navigation {
	// put your navigation code here 
	
	private Odometer odometer;
	private TwoWheeledRobot robot;
   //constants for robot attributes
	private final double distError = 10; /* cm^{-1/2} */
//   private final float uDistThreshold = 25;
   private final int sleepPeriod = 200;
   private final double thetaThreshold = 3; //keep theta within 3 degrees

//   private static final SensorPort usPort = SensorPort.S1;
//   UltrasonicSensor ultrasonic = new UltrasonicSensor(usPort);
	//running when true
   private double[] position;
   private double dx,dy,xTarget,yTarget,dist;
//   private float uDist;

	public Navigation(Odometer odo) {
		this.odometer = odo;
		this.robot = odo.getTwoWheeledRobot();
	   this.dx = 0;
      this.dy = 0;
      this.xTarget = 0;
      this.yTarget = 0;
      this.position = new double[3];
}
	
	public void travelTo(double tx, double ty) {
		// USE THE FUNCTIONS setForwardSpeed and setRotationSpeed from TwoWheeledRobot!
	   xTarget = tx;
      yTarget = ty;
      odometer.getPosition(position);
      //initialize delta x and y before the while loop
      dx = xTarget - position[0];
      dy = yTarget - position[1];
		dist = Math.sqrt(dx*dx + dy*dy);
//      int turnCount = 0; //adjusts angle every 5 cycles, test ultrasonic every cycle
	   while(dist > distError) {
//         if(turnCount == 10) {turnCount = 0;}
	      //update delta x,y,theta
         odometer.getPosition(position);
         dx = xTarget - position[0];
         dy = yTarget - position[1];
         //this is the theta that we want to turn to
         double theta = Math.toDegrees(Math.atan2(dy,dx)) - position[2];
		   //update distance
         dist = Math.sqrt(dx*dx + dy*dy);
         //get ultrasonic sensor distance
//         uDist=ultrasonic.getDistance();
         //adjust angle to object every ten cycles of the while loop
//         if( (turnCount == 0 ))
			this.turnTo(theta);
         //if the robot is close to an obsticle, it navigates in an arc around it
//         if(uDist < uDistThreshold) {
//            turnLeft((float)Math.PI*wheelBase/4);
//            leftMotor.setSpeed(200);
//			   rightMotor.setSpeed(150);
//            leftMotor.forward();
//            rightMotor.forward();
//  			   try {
//			   	Thread.sleep(4000);
//			   } catch (Exception e) {
//			   	System.out.println("Error: " + e.getMessage());
//			   }
//            turnRight((float)Math.PI*wheelBase/6);
//            leftMotor.setSpeed(250);
//			   rightMotor.setSpeed(250);
//            leftMotor.forward();
//            rightMotor.forward();
//  			   try {
//			   	Thread.sleep(2000);
//			   } catch (Exception e) {
//			   	System.out.println("Error: " + e.getMessage());
//			   }
//            turnCount = -1; //this is updated to 0 below causing turnTo to run and fix the robots direction after the correction around the object
//         }
         //move forward
			robot.setForwardSpeed(200);
 			try {
				Thread.sleep(sleepPeriod);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
//         turnCount++;

		}
	   robot.setForwardSpeed(0);
	
	}
	
      // USE THE FUNCTIONS setForwardSpeed and setRotationSpeed from TwoWheeledRobot!
	public void turnTo(double angle) {
      robot.setRotationSpeed(200);
      if(angle > 180) { //rotate left
         robot.setRotationSpeed(-200);
         while(position[2] >  angle){odometer.getPosition(position);}
      }
      else { //rotate right
         robot.setRotationSpeed(200);
         while(position[2] <  angle){odometer.getPosition(position);}
      }
      robot.setRotationSpeed(0);
	}

//   float toDegrees(float radians) {
//      return radians * (180f/(float)Math.PI); /* [rad] times [deg]/[rad] */
//   }
//   /** "Use with motor.rotate() Converts radian distance to degree rotations.*/
//   private int motorDegrees(float radianDist) {
//      return (int) ((180.0 * radianDist) / (Math.PI * wheelRadius));
//   }
   //turn methods, speed is adjusted to improve accuracy
//   private void turnLeft(float rad) {
//      robot.setRotationSpeed(150);
//      leftMotor.setSpeed(100);
//      rightMotor.setSpeed(100);
//      leftMotor.rotate(motorDegrees(-rad),true);
//      rightMotor.rotate(motorDegrees(rad),false);
//      leftMotor.setSpeed(200);
//      rightMotor.setSpeed(200);
//   }
//   private void turnRight(float rad) {
//      leftMotor.setSpeed(100);
//      rightMotor.setSpeed(100);
//      leftMotor.rotate(motorDegrees(rad),true);
//      rightMotor.rotate(motorDegrees(-rad),false);
//      leftMotor.setSpeed(200);
//      rightMotor.setSpeed(200);
//  }
//   private float normTheta(float theta) {
//      if(     theta >  Math.PI) theta -= Math.PI;
//      else if(theta < -Math.PI) theta += Math.PI;
//      return theta;
//   }
   double xTarget() {
      return this.xTarget;
   }
   double yTarget() {
      return this.yTarget;
   }
   double dx() {return dx;}
   double dy() {return dy;}
   double dist() {return dist;}
}

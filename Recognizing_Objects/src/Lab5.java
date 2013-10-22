//Lab 5 -- Neil and Alex
/*
This file contains the algorithm for scanning the entire field for objects. The robot localizes in the corner of the field at 0,0.
The "search point" is a point that the robot chooses to scan for blocks. The first search point is at 0,0 and the robot scans ninty degrees,
starting at theta=0 (parallel with the right hand wall equation: x = -30) to theta=90 (parallel with the left hand wall equation: y=-30).
The remaining search points start at 30,30 and increaement y by 30 so: (30,30) (30,60) (30,90), and the robot scans 180 degrees. This allows
it to cover the entire field. When it finds a block it drives near but not into it and uses the color sensor to verify its type. If it is styrofoam
that robot drives to the destination and exits the loop. If not, the robot backs up and researches for remaining blocks, and then drives to the next
search point. This allows the entire algorithm to be implemented in the single while loop.

*/
import lejos.nxt.Button;
//import Ulitrasonic and Color sensor
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.ColorSensor;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Motor;
import lejos.util.Timer;
import lejos.nxt.Sound;

public class Lab5 {

   private static final int SCAN_THRESHOLD = 70;
   private static final float DESTINATION_X = 75;
   private static final float DESTINATION_Y = 195;

	public static void main(String args[]) {

      UltrasonicSensor uSensor = new UltrasonicSensor(SensorPort.S4);
      Colour colour = new Colour();
      TwoWheeledRobot robot = new TwoWheeledRobot(Motor.A,Motor.B);
      Odometer odometer = new Odometer(robot,true);
      UltrasonicListener ultrasonicListener = new UltrasonicListener(uSensor,odometer);
      Timer ultrasonicTimer = new Timer(10/*round-up to int 9.375*/,ultrasonicListener); //timeout value in ms
      //usListener times out every 9ms, assuming 75ms for 8 pings
      //15 pings in 140.625ms. Distance recalculated on every 8th and 15th ping,
      //meaning every 75ms then ~66ms repeating after 140ms for the first value.
      //Get distance calls or returns will need to be timed. Might make getDistance blocking.
      LCDInfo lcd = new LCDInfo(odometer,ultrasonicListener);

      //Localize
      Sound.setVolume(50);
      USLocalizer usl = new USLocalizer(odometer, new UltrasonicSensor(SensorPort.S4), USLocalizer.LocalizationType./*RISING_EDGE*/FALLING_EDGE);
      usl.doLocalization();
      odometer.travelTo(-3f,-3f);
      odometer.turnTo(45f);
      LightLocalizer lsl = new LightLocalizer(odometer, new LightSensor(SensorPort.S1));
      lsl.doLocalization();
      Sound.setVolume(100);

      //start timer
      ultrasonicTimer.start();
      //spawn thread for exit
   	(new Thread() {
         public void run() {
            if (Button.waitForAnyPress() == Button.ID_ESCAPE)
   	         System.exit(0);
         }
      }).start();

      //setup variables for searching
      boolean searching = true;
      float adjust_x = 0; //designates the point on the field to be searched from
      float adjust_y = 0; //values are (0,0);(30,30);(30,60);(30,90)...etc
      boolean first = true; //adjust_x is incremented only on the first run to set the second search point
      while(searching) { //runs until the robot find a blue block
         
         //update x and y
         if(first) {
            adjust_x += 30f;
         }
         adjust_y += 30f;

	   	//find blocks from search point (starts in corner and progresses along board)
         //on the first iteration the robot is in the corner and only turns ninty degrees
         odometer.turnTo(90f);
         ultrasonicListener.scan();
         odometer.turnConstantlyTo(0f); //turn constantly disables the p error correction so the robot can ping at a constant speed
         //if it is not the first iteration, the robot has moved to the center of the field and now turns the full 180 degrees
         //to search for blocks
         if(!first)
            odometer.turnConstantlyTo(-90f);
         float smallestPing = ultrasonicListener.getSmallestPing(); //stops scan
         float targetTheta = ultrasonicListener.getTargetTheta(); 
         lcd.setText("SP: "+smallestPing);
         //lcd.setText("TT: " + targetTheta);
         //if there is a block...
         if(smallestPing < SCAN_THRESHOLD) {
            //when the robot encounters a block, the position of the block is calculated using the measured distance
            //the robot then move near that position and get a colour reading of the block
            float targetX = odometer.cmGetX() + (smallestPing-2)*(float)Math.cos(Math.toRadians(targetTheta+3));
            float targetY = odometer.cmGetY() + (smallestPing-2)*(float)Math.sin(Math.toRadians(targetTheta+3));
            odometer.travelTo(targetX,targetY); //move near test object
            //if styroform go to destination!
            if(colour.getColourValue() == Colour.Value.STYROFOAM) { //is styrofoam, grab and move
               Sound.beep();
               searching = false;
               //travel with avoidance
               odometer.travelTo(DESTINATION_X,DESTINATION_Y); //move forward
            }
            else { //is wood move on
               Sound.buzz();
               odometer.backup(); //move back
//             odometer.travelTo(targetX-15,targetY-15); using backup instead
               odometer.travelTo(adjust_x,adjust_y); //move to next scan point
            }
         }
         else
            Sound.buzz(); //no blocks found
         first = false; //false after the first iteration of the loop
      }
   }   
}

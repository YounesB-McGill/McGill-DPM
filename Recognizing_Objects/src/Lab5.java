//Lab 5 -- Neil and Alex
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

      Sound.setVolume(50);
      USLocalizer usl = new USLocalizer(odometer, new UltrasonicSensor(SensorPort.S4), USLocalizer.LocalizationType./*RISING_EDGE*/FALLING_EDGE);
      usl.doLocalization();
      odometer.travelTo(-3f,-3f);
      odometer.turnTo(45f);
//      LightLocalizer lsl = new LightLocalizer(odometer, new LightSensor(SensorPort.S1));
//      lsl.doLocalization();
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

      boolean searching = true;
      float adjust_x = 0;
      float adjust_y = 0;
      boolean first = true;
      while(searching) {
	   	//find blocks from corner
         odometer.turnTo(90f);
         ultrasonicListener.scan();
         odometer.turnConstantlyTo(0f);
         if(!first)
            odometer.turnConstantlyTo(-90f);
         float smallestPing = ultrasonicListener.getSmallestPing(); //stops scan
         float targetTheta = ultrasonicListener.getTargetTheta(); 
         lcd.setText("SP: "+smallestPing);
         //lcd.setText("TT: " + targetTheta);
         //if there is a block...
         if(smallestPing < SCAN_THRESHOLD) {
            float targetX = odometer.cmGetX() + (smallestPing-2)*(float)Math.cos(Math.toRadians(targetTheta+3));
            float targetY = odometer.cmGetY() + (smallestPing-2)*(float)Math.sin(Math.toRadians(targetTheta+3)); //small adjustments...
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
//               odometer.travelTo(targetX-15,targetY-15); //move back
               odometer.travelTo(adjust_x,adjust_y); //move to next scan point
            }
         }
         Sound.buzz();
         if(first) {
            first = false;
            adjust_x += 30f;
         }
         adjust_y += 30f;
      }
   }   
}

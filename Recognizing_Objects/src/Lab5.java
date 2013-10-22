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
      LCDInfo ldc = new LCDInfo(odometer,ultrasonicListener);

//      Sound.setVolume(50);
//      USLocalizer usl = new USLocalizer(odometer, new UltrasonicSensor(SensorPort.S4), USLocalizer.LocalizationType./*RISING_EDGE*/FALLING_EDGE);
//      usl.doLocalization();
//      odometer.travelTo(-3f,-3f);
//      odometer.turnTo(45f);
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

		//find blocks from corner
      ultrasonicListener.scan();
      odometer.turnConstantlyTo(90f);
      float smallestPing = ultrasonicListener.getSmallestPing(); //stops scan
      float targetTheta = ultrasonicListener.getTargetTheta(); 
//      lcd.setText("TT: " + targetTheta);
      //if there is a block...
      if(smallestPing < SCAN_THRESHOLD) {
         float targetX = odometer.cmGetX() + (smallestPing-5)*(float)Math.cos(Math.toRadians(targetTheta+3));
         float targetY = odometer.cmGetY() + (smallestPing-5)*(float)Math.sin(Math.toRadians(targetTheta+3)); //small adjustments...
         odometer.travelTo(targetX,targetY);
         //if styroform go to destination!
         if(colour.getColourValue() == Colour.Value.STYROFOAM) {
            Sound.beep();
            //travel with avoidance
            odometer.travelTo(DESTINATION_X,DESTINATION_Y);
         }
      }
      else {
         Sound.buzz();
      }





      //Detection methods called for lab stuff
      //Odometer can be used to move robot here or Dectection
      odometer.turnTo(90);
      odometer.travelTo(45f,10f);
      Sound.beep();
		/*System.out.println("Done!");*/
   }   
}

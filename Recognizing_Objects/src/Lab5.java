//Lab 5 -alex
//see diectory src in this folder (Recognizing_Objects/src/src) it has all the old code, I liked having it separate, but you could move it up into src
//if you need to, to get it to compile. Cool.
import lejos.nxt.Button;
//import Ulitrasonic and Color sensor
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Motor;
import lejos.util.Timer;
import lejos.nxt.Sound;
public class Lab5 {



	public static void main(String args[]) {

      UltrasonicSensor uSensor = new UltrasonicSensor(SensorPort.S4);
      Colour colour = new Colour();
      TwoWheeledRobot robot = new TwoWheeledRobot(Motor.A,Motor.B);
      Odometer odometer = new Odometer(robot,true);
      UltrasonicListener ultrasonicListener = new UltrasonicListener(uSensor);
      Timer ultrasonicTimer = new Timer(10/*round-up to int 9.375*/,ultrasonicListener); //timeout value in ms
      //usListener times out every 9ms, assuming 75ms for 8 pings
      //15 pings in 140.625ms. Distance recalculated on every 8th and 15th ping,
      //meaning every 75ms then ~66ms repeating after 140ms for the first value.
      //Get distance calls or returns will need to be timed. Might make getDistance blocking.
      LCDInfo ldc = new LCDInfo(odometer,ultrasonicListener);
      
      //start timer
      ultrasonicTimer.start();
      //spawn thread for exit
   	(new Thread() {
         public void run() {
            if (Button.waitForAnyPress() == Button.ID_ESCAPE)
   	         System.exit(0);
         }
      }).start();
   
      //Detection methods called for lab stuff
      //Odometer can be used to move robot here or Dectection
      odometer.turnTo(90);
      odometer.travelTo(45f,10f);
      Sound.beep();
		/*System.out.println("Done!");*/
   }   
}

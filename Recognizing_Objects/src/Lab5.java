//Lab 5 -alex
//see diectory src in this folder (Recognizing_Objects/src/src) it has all the old code, I liked having it separate, but you could move it up into src
//if you need to, to get it to compile. Cool.
import lejos.nxt.Buttons;
//import Ulitrasonic and Color sensor
public class Lab5 {



	public static void main(String args[]) {

      UltrasonicSensor uSensor = new UltrasonicSensor(/*port*/);
      ColorSensor cSensor = new ColorSensor(/*port*/);

      Odometer odometer = new Odometer();
      Detection detection = new Detection(odometer,uSensor,cSensor); //still need to create uSensor and cSensor above
   
      //spawn thread for exit
   	(new Thread() {
         public void run() {
            if (Button.waitForAnyPress() == Button.ID_ESCAPE)
   	         System.exit(0);
         }
      }).start();
   
      //Detection methods called for lab stuff
      //Odometer can be used to move robot here or Dectection
      //chill swag
   }   
}

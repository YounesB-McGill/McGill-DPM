//Lab5
//Alex Bhandari-Young and Neil Edelman

import lejos.nxt.UltrasonicSensor;
import lejos.nxt.ColorSensor;

public class Detection {
   
   private Odometer odometer;
   private UltrasonicSensor uSensor;
   private ColorSensor cSensor;

   public Detection(Odometer odometer, UltrasonicSensor uSensor, ColorSensor cSensor) {
      this.odometer = odometer; //controlled with travelTo and turnTo as is... more methods could be added if you think we need them
      this.uSensor = uSensor;
      this.cSensor = cSensor; //light vs. color not exacly sure which is better suited. Color seems to be better if we can get it accurate.
   }

   //implemect lab stuff here -alex
}   

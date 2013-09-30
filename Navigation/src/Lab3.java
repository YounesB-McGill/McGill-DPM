/* Lab3.java
 Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.Button; /* must be linked with lejos */
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor; 
/* this is the entry point */

public class Lab3 {

   //variables for ultrasonic
	private static final SensorPort usPort = SensorPort.S1;
	private static final int walldist = 35, tolerance = 3;
	
   public static void main(String args[]) {
		Odometer odo = new Odometer();
		Navigator nav = new Navigator(odo);
      Display display = new Display(odo,nav);

      //setup ultrasonic objects
      PController p = new PController(walldist, tolerance);
		UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
		UltrasonicPoller usPoller = new UltrasonicPoller(usSensor, p, nav);
		
      /* start odometer, navigation, display, and ultrasonic poller threads*/
		odo.start();
      nav.start();
      display.start();
		usPoller.start();
      //start thread for exit
		(new Thread() {
         public void run() {
            if (Button.waitForAnyPress() == Button.ID_ESCAPE)
		         System.exit(0);
         }
      }).start();


		//part a
		/* random coordinates as specified in the assignment */
		nav.travelTo(30, -60);
		nav.travelTo(30, -30);
		nav.travelTo(60, -30);
		nav.travelTo(0,  -60);
      //done:
      //wait for escape button press
      while(Button.waitForAnyPress() == Button.ID_ESCAPE){}
		System.exit(0);
	}
//   private static void wait(Navigator nav) {
//      while(nav.isNavigating()) {
// 			try {
//				Thread.sleep(nav.getPeriod());
//			} catch (Exception e) {
//				System.out.println("Error: " + e.getMessage());
//			}
//      }
//   }
}

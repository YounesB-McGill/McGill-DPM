/* Lab3.java
 Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.Button; /* must be linked with lejos */
import lejos.nxt.LCD;

/* this is the entry point */

public class Lab3 {
	/** press any key to stop */
	public static void main(String args[]) {
		Odometer odo = new Odometer();
		Navigator nav = new Navigator(odo);
      Display display = new Display(odo,nav);

		/* start odometer and display*/
		odo.start();
      nav.start();
      display.start();

      //spawn thread for exit
		(new Thread() {
         public void run() {
            if (Button.waitForAnyPress() == Button.ID_ESCAPE)
		         System.exit(0);
         }
      }).start();


		//part a
		/* random coordinates as specified in the assignment */
		nav.travelTo(60, -30);
      wait(nav);
		nav.travelTo(30, -30);
      wait(nav);
		nav.travelTo(30, -60);
      wait(nav);
		nav.travelTo(60,   0);
      wait(nav);

		/* press any key to exit */
      LCD.clear();
		LCD.drawString("Press any key.", 0, 1);
		Button.waitForAnyPress();
		System.exit(0);
	}
   private static void wait(Navigator nav) {
      while(nav.isNavigating()) {
 			try {
				Thread.sleep(nav.getPeriod());
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
      }
   }
}

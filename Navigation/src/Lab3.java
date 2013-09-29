/* Lab3.java
 Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.Button; /* must be linked with lejos */
import lejos.nxt.LCD;

/* this is the entry point */

public class Lab3 {
	/** press any key to stop */
	public static void main(String args[]) {
		Navigator nav = new Navigator();

		/* clear screen */
		LCD.clear();

		/* nav start */
		nav.start();

		/* random coordinates as specified in the assignment */
		nav.travelTo(60, -30);
		nav.travelTo(30, -30);
		nav.travelTo(30, -60);
		nav.travelTo(60,   0);

		/* press any key to exit */
		LCD.drawString("Press any key.", 0, 1);
		Button.waitForAnyPress();
		System.exit(0);
	}
}

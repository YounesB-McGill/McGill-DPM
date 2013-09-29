/* Lab3.java
 Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.Button; /* must be linked with lejos */
import lejos.nxt.LCD;

/* this is the entry point */

public class Lab3 {
	/** press any key to stop */
	public static void main(String args[]) {
		Navigator nav = new Navigator();

		/* nav start */
		nav.start();

		/* press any key to exit */
		LCD.clear();
		LCD.drawString("Press any key.", 0, 0);
		Button.waitForAnyPress();
		System.exit(0);
	}
}

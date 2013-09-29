/* Lab3.java
 Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.Button; /* must be linked with lejos */
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;

/* this is the entry point */

public class Lab3 {
	/** press any key to stop */
	public static void main(String args[]) {
		ButtonListener stop = new PressToStop();
		Button.ESCAPE.addButtonListener(stop);
		LCD.clear();
		LCD.drawString("Press any key to stop.", 0, 0);
		for( ; ; );
	}
}

/** press to stop button listener; this just stops when you press it */
class PressToStop implements ButtonListener {
	public void buttonPressed(Button b) {
		LCD.drawString("Pressed.", 0, 1);
	}
	public void buttonReleased(Button b) {
		LCD.drawString("Released.", 0, 1);
	}
}

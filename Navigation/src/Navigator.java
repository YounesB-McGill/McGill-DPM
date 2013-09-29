/* Lab 3
 Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* must be linked with lejos */
import lejos.nxt.LCD;
import java.lang.String;

/* " . . . robot to an absolute position on the field while avoiding obstacles,
 by use of the odometer and an ultrasonic sensor */

class Navigator extends Thread /*implements Runnable*/ {
	boolean isNavigating;
	String navMessage = "stopped";

	/** constructor */
	public Navigator() {
	}

	/** to string, may be useful */
	public String toString() {
		return navMessage;
	}
	
	/** implements all the navigation */
	public void run() {
		for( ; ; ) {
			LCD.drawString("Nav: " + navMessage, 0, 0);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	/** "This method causes the robot to travel to the absolute field
	 location (x, y). This method should continuously call turnTo(double theta)
	 and then set the motor speed to forward(straight). This will make sure
	 that your heading is updated until you reach your exact goal.
	 (This method will poll the odometer for information)" */
	void travelTo(double x, double y) {
		navMessage = "to " + x + ", " + y;
	}
	
	/** "This method causes the robot to turn (on point) to the absolute
	 heading theta. This method should turn a MINIMAL angle to it's target." */
	void turnTo(double theta) {
		
	}
	
	/** "This method returns true if another thread has called travelTo() or
	 turnTo() and the method has yet to return; false otherwise." */
	boolean isNavigating() {
		return isNavigating;
	}
}

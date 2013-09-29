/* Lab 3
 Group 51 -- Alex Bhandari-Young and Neil Edelman */

//import lejos.nxt.*; /* must be linked with lejos */

/* " . . . robot to an absolute position on the field while avoiding obstacles,
 by use of the odometer and an ultrasonic sensor */

class Navigator extends Thread /*implements Runnable*/ {
	int var;

	public Navigator() {
		this.var = 7;
	}

	public String toString() {
		String j = "";
		return j;
	}
	
	/** implements all the navigation */
	public void run() {
		for( ; ; ) {
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	void travelTo(double x, double y) {
		
	}
	
	void turnTo(double theta) {
		
	}
	
	boolean isNavigating() {
		return false;
	}
}

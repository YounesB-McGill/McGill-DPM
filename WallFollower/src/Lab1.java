/*
Wall Follower Lab
Group 51
Alex Bhandari-Young and Neil Edelman
*/

import lejos.nxt.*;


public class Lab1 {
	
	private static final SensorPort usPort = SensorPort.S1;
	//private static final SensorPort lightPort = SensorPort.S2;
	
   //attributes passed to bang bang and p classes
	private static final int walldist = 35, tolerance = 3;

	private static final int motorLow = 100, motorHigh = 400; //not used
	
	
	public static void main(String [] args) {
		/*
		 * Wait for startup button press
		 * Button.ID_LEFT = BangBang Type
		 * Button.ID_RIGHT = P Type
		 */
		
		int option = 0;
		Printer.printMainMenu();
		while (option == 0)
			option = Button.waitForAnyPress();
		
		// Setup controller objects
		BangBangController bangbang = new BangBangController(walldist, tolerance, motorLow, motorHigh);
		PController p = new PController(walldist, tolerance);
		
		// Setup ultrasonic sensor
		UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
		
		// Setup Printer
		Printer printer = null;
		
		// Setup Ultrasonic Poller
		UltrasonicPoller usPoller = null;
		
		switch(option) {
		case Button.ID_LEFT:
			usPoller = new UltrasonicPoller(usSensor, bangbang);
			printer = new Printer(option, bangbang);
			break;
		case Button.ID_RIGHT:
			usPoller = new UltrasonicPoller(usSensor, p);
			printer = new Printer(option, p);
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
		
		usPoller.start();
		printer.start();
		
		//Wait for another button press to exit
		Button.waitForAnyPress();
		System.exit(0);
		
	}
}

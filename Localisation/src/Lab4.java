/* Lab 4, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.*;

public class Lab4 {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		/* resume boring code -- true? okay sure */
		Odometer odo = new Odometer(patBot, true);
		// /* breaks the drawtext */ LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
		LightSensor ls = new LightSensor(SensorPort.S4);
//		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType./*RISING_EDGE*/FALLING_EDGE);
		LCD.drawString("US localisation", 0,4);
		usl.doLocalization();
		
		LCD.drawString("Press", 0,5);
//		Button.waitForAnyPress();
		LCD.drawString("     ", 0,5);
		
		LCD.clear();
		/* experimentally determined */
		LCD.drawString("Goto (-3,-3)", 0,0);
		odo.travelTo(-3f,-3f);
		LCD.clear();
		/* make sure that the 'south' line is hit first */
		LCD.drawString("Turn to 45", 0,0);
		odo.turnTo(45f);

		// perform the light sensor localization
		LCD.drawString("Light localisation", 0,4);
		LightLocalizer lsl = new LightLocalizer(odo, ls);
	   LCDInfo lcd = new LCDInfo(odo,lsl);
      lsl.doLocalization();			
		
		LCD.drawString("Press", 0,5);
		Button.waitForAnyPress();

	}

}

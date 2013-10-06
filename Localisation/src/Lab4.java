import lejos.nxt.*;

public class Lab4 {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		/* blade of death startup */
		Motor.C.setSpeed(1000);
		Motor.C.forward();
		/* resume boring code */
		Odometer odo = new Odometer(patBot, true);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		LightSensor ls = new LightSensor(SensorPort.S1);
		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
		LCD.drawString("US localisation", 0,0);
		usl.doLocalization();
		
		LCD.drawString("Press", 0,1);
		Button.waitForAnyPress();
		LCD.drawString("     ", 0,1);

		// perform the light sensor localization
		LCD.drawString("Light localisation", 0,0);
		LightLocalizer lsl = new LightLocalizer(odo, ls);
		lsl.doLocalization();			
		
		Button.waitForAnyPress();
	}

}

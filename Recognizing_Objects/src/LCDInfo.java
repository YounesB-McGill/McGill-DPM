/* Lab 4, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 10/*temp: swich back to 100 later*/;
	private Odometer odo;
	private Timer lcdTimer;
   private LightLocalizer light;
   private UltrasonicListener uListener;
   private String drawText = "Nothing";
	
	// arrays for displaying data
	private float [] pos;
	
	public LCDInfo(Odometer odo,UltrasonicListener uListener) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
      this.light = light;
      this.uListener = uListener;
		
		// initialise the arrays for displaying data
		pos = new float[3]; /* confusing, why? */
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		LCD.clear();
		LCD.drawString("X value: ", 0, 0);
		LCD.drawString("Y value: ", 0, 1);
		LCD.drawString("Theta value: ", 0, 2);
		LCD.drawString("Distance: ", 0, 3);
      LCD.drawString(drawText, 0, 7);
		LCD.drawInt((int)(odo.cmGetX()), 13, 0);
		LCD.drawInt((int)(odo.cmGetY()), 13, 1);
		LCD.drawInt((int)(odo.getTheta()), 13, 2);
      LCD.drawInt(uListener.getDistance(), 13, 3);
	}
   public void setText(String text) {
      this.drawText = text; 
   }

}

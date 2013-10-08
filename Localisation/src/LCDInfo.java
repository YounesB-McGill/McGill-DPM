import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
   private LightLocalizer light;
	
	// arrays for displaying data
	private float [] pos;
	
	public LCDInfo(Odometer odo,LightLocalizer light) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
      this.light = light;
		
		// initialise the arrays for displaying data
		pos = new float[3]; /* worst idea ever */
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString("L: ", 0, 2);
		LCD.drawString("C: ", 0, 2);
		LCD.drawInt((int)(pos[0] * 10), 3, 0);
		LCD.drawInt((int)(pos[1] * 10), 3, 1);
		LCD.drawInt((int)pos[2], 3, 2);
      LCD.drawInt(light.getLS(),3,3);
      LCD.drawInt(light.getCount(),3,4);
	}
}

/* 
 * OdometryCorrection.java
*/
import lejos.nxt.*;

public class OdometryCorrection extends Thread {

	private static final int CORRECTION_PERIOD = 25;
   private static final float d = 5.4f; //distance lightsensor proceeds the center of the robot
	private Odometer odometer;
   private LightSensor lightsensor = new LightSensor(SensorPort.S4,true);
   private float countX,countY;
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
      Sound.setVolume(100);
      countX = 0;
      countY = 0;
	}

	// run method (required for Thread)
	public void run() {
		int correctionStart, correctionEnd;
		while (true) {
			correctionStart = (int)System.currentTimeMillis();
         float Ox = odometer.getX();
         float Oy = odometer.getY();
         float Ot = odometer.getTheta();
			// put your correction code here
         if(lightsensor.readValue() < 45)
         {
             Sound.beep();
             if(Math.abs(Ot)<10) //theta at 0 within threshold (points north)
             {
                countX++;
                float error = Ox+d-30.48f*countX;
                odometer.setX(odometer.getX()-error);
             }
             else if(Math.abs(Ot+90)<10) //theta at -90 within threshold (points east)
             {
                countY--;
                float error = Oy-d+30.48f*Math.abs(countY);
                odometer.setY(odometer.getY()-error);
             }
             else if(Math.abs(Math.abs(Ot)-180)<10) //theta at 180 within threshold (points south)
             {
                float error = Ox-d-30.48f*countX;
                odometer.setX(odometer.getX()-error);
                countX--;
             }
             else if(Math.abs(Ot-90)<10) //theta at 90 within threshold (points west)
		       {
                float error = Oy+d+30.48f*Math.abs(countY);
                odometer.setY(odometer.getY()-error);
                countY++;
             }
             else //not supported (something's wrong so do nothing)
             {
               Sound.buzz();
             }
         }
         // this ensure the odometry correction occurs only once every period
			correctionEnd = (int)System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}

   public float getCountX()
   {
      return countX;
   }

   public float getCountY()
   {
      return countY;
   }

}

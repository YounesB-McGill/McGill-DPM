/* 
 * OdometryCorrection.java
   Group 51
   Alex Bhandari-Young and Neil Edelman
*/
import lejos.nxt.*;

public class OdometryCorrection extends Thread {

	private static final int CORRECTION_PERIOD = 25;
   private static final float d = 5.4f; //distance lightsensor proceeds the center of the robot
	private Odometer odometer;
   private LightSensor lightsensor = new LightSensor(SensorPort.S4,true);
   private float lineX,lineY; //simply to count lines and display for debugging, not used in correction.
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
      Sound.setVolume(100);
      lineX = 0;
      lineY = 0;
	}

	// run method (required for Thread)
	public void run() {
		int correctionStart, correctionEnd;
		while (true) {
			correctionStart = (int)System.currentTimeMillis();
         //integer division of odometer reading by thirty partitions the grid into intervals of 30,
         //centered at the middle of each tile, with tile 0,0 as the origin. One is added so that
         //the first line the robot hits is line #1 not #0 and continues snapping at intervals of 30.
         float snapX = ((int)Math.abs(odometer.getX()))/30+1; //x=snapX*15+15 line to snap to
         float snapY = ((int)Math.abs(odometer.getY()))/30+1; //y=snapY*15+15 line to snap to
         float Ot = odometer.getTheta(); //get theta value to determine orientation
			// put your correction code here
         if(lightsensor.readValue() < 45)
         {
             //This code is a little longer than it needs to be. The up and down and the right and left directions do the same thing and could
             //be combined, but it is left like this to make debugging easier.
             //GOING UP
             if(Math.abs(Ot)<10) //theta at 0 within threshold (points north)
             {
                Sound.beep();
                odometer.setX((snapX-1)*30+15-d);
             }
             //GOING RIGHT 
             else if(Math.abs(Ot+90)<10) //theta at -90 within threshold (points east)
             {
                Sound.beep();
                odometer.setY(-((snapY-1)*30+15-d));
             }
             //GOING DOWN 
             else if(Math.abs(Math.abs(Ot)-180)<10) //theta at 180 within threshold (points south)
             {
                Sound.beep();
                odometer.setX((snapX-1)*30+15+d);
             }
             //GOING LEFT 
             else if(Math.abs(Ot-90)<10) //theta at 90 within threshold (points west)
		       {
                Sound.beep();
                odometer.setY(-((snapY-1)*30+15+d));
             }
             else //not supported (something's wrong so do nothing)
             {
               Sound.buzz();
             }
         }
         lineX = snapX; //for debugging, written to screen
         lineY = snapY;
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

   public float getLineX()
   {
      return lineX;
   }

   public float getLineY()
   {
      return lineY;
   }

}

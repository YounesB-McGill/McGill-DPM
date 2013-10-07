import lejos.nxt.LCD;

/* this should not be in a separete file from Odometry,
 @Nav has 1 Odo, @Odo has 1 Nav, makes no sense */

public class Navigation {
	// put your navigation code here 
	
	private final static float dist2Tolerance = 5f; /* cm^{1/2} */
	private final static float pTheta = 3.0f; /* proportional theta in degrees */
	private final static float pDist  = 3.0f; /* proportional distance in cm */

	private Odometer odo;
	private TwoWheeledRobot robot;
	
	public Navigation(Odometer odo) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
	}
	
	public void travelTo(final float xTarget, final float yTarget) {
		float xCurrent, yCurrent, tCurrent, tTarget, x, y, t, dist2, dist;
		float l, r, speed;
		// USE THE FUNCTIONS setForwardSpeed and setRotationalSpeed from TwoWheeledRobot!
		/* yeah, that's good if you want to get a robot from the '70s, but let's
		 try and get a smoother motion (fixme: i, d) */
		for( ; ; ) {
			xCurrent = odo.getX();
			yCurrent = odo.getY();
			x = xTarget - xCurrent;
			y = yTarget - yCurrent;
			dist2 = x*x + y*y;
			if(dist2 < dist2Tolerance) break;
			tCurrent = odo.getTheta();
			/* tCurrent branch cut is useless, change */
			if(tCurrent > 180f) tCurrent -= 360f;
			tTarget = (float)Math.toDegrees(Math.atan2(y, x));
			t = tTarget - tCurrent;
			//LCD.drawString("x "+x+"\ny "+y+"\ntt "+tTarget+"\ntc "+tCurrent, 0,1);
			LCD.drawString("x "+xCurrent+"\ny "+yCurrent+"\nt "+tCurrent, 0,1);
			/* the theta */
			l =  t * pTheta;
			r = -t * pTheta;
			/* the dist */
			dist = (float)Math.sqrt(dist2);
			LCD.drawString("d "+dist, 0,5);
			speed = dist * pDist * (float)Math.cos(Math.toRadians(t));
			l += speed;
			r += speed;
			/* go */
			robot.setLeftSpeed(l);
			robot.setRightSpeed(r);
			/* wait */
			try { Thread.sleep(1000); } catch (InterruptedException e) { }
		}
	}
	
	public void turnTo(float angle) {
		// USE THE FUNCTIONS setForwardSpeed and setRotationalSpeed from TwoWheeledRobot!
	}
}

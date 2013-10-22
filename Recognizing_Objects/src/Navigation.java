/* Lab 4, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.LCD;

/* this should not be in a separete file from Odometry,
 @Nav has 1 Odo, @Odo has 1 Nav, makes no sense */

public class Navigation {
	// put your navigation code here 
	
	private final static float dist2Tolerance = 0.5f;  /* cm^{1/2} */
	private final static float angleTolerance = 2f;    /* deg */
	private final static float pTheta         = 5.0f;  /* proportional theta in degrees */
	private final static float pDist          = 15.0f; /* proportional distance in cm */

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
		/* that's good if you want to get a robot from the '70s, but let's
		 try and get a smoother motion (fixme: integal, derivative, would make
		 a faster, more stable ride to the setpoint) */
		for( ; ; ) {
			/* good on paper, but when uploaded to robot, gets close, then goes off
			 in a random direction (-90) at unstable and incresing speed for
			 NO REASON; fixed by converting crazy coordinates to standard */
			xCurrent = odo.getX();
			yCurrent = odo.getY();
			x = xTarget - xCurrent;
			y = yTarget - yCurrent;
			dist2 = x*x + y*y;
			if(dist2 < dist2Tolerance) break;
			tCurrent = odo.getTheta();
			/* this is why you programme in standard coordinates; this took so
			 long to debug :[ */
			tCurrent = -tCurrent + 90f;
			if(tCurrent < 180f) tCurrent += 360f;
			tTarget = (float)Math.toDegrees(Math.atan2(y, x));
			t = tTarget - tCurrent;
			if(t < -180f)     t += 360f;
			else if(t > 180f) t -= 360f;
			LCD.drawString("x "+x+"\ny "+y+"\nt "+t, 0,1);
			/* the theta */
			l = -t * pTheta;
			r =  t * pTheta;
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
			try { Thread.sleep(100); } catch (InterruptedException e) { }
		}
		robot.stop();
	}

   public void turnConstantlyTo(final float angle) {
		float tTarget, tCurrent, t, l, r;

		tTarget = angle % 360f;
		if(tTarget > 180f) tTarget -= 360f;
		for( ; ; ) {
			tCurrent = odo.getTheta();
			if(tCurrent > 180f) tCurrent -= 360f;
			t = tTarget - tCurrent;
			if(t < -180f)     t += 360f;
			else if(t > 180f) t -= 360f;
			if(t < angleTolerance && t > -angleTolerance) break;
			LCD.drawString("t "+t, 0,2);
			/* reversed */
			l =  t/Math.abs(t) * 100; //turns at a constant speed
			r = -t/Math.abs(t) * 100;
			robot.setLeftSpeed(l);
			robot.setRightSpeed(r);
		}
		robot.stop();
      
   }
	
	public void turnTo(final float angle) {
		float tTarget, tCurrent, t, l, r;

		tTarget = angle % 360f;
		if(tTarget > 180f) tTarget -= 360f;
		for( ; ; ) {
			tCurrent = odo.getTheta();
			if(tCurrent > 180f) tCurrent -= 360f;
			t = tTarget - tCurrent;
			if(t < -180f)     t += 360f;
			else if(t > 180f) t -= 360f;
			if(t < angleTolerance && t > -angleTolerance) break;
			LCD.drawString("t "+t, 0,2);
			/* reversed */
			l =  t * pTheta;
			r = -t * pTheta;
			robot.setLeftSpeed(l);
			robot.setRightSpeed(r);
		}
		robot.stop();
	}

   public 
}

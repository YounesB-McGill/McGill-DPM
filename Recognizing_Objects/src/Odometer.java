/* Lab 4, Group 51 -- Alex Bhandari-Young and Neil Edelman */

/*
--------------------------------------------------------------
The following comments are impotant:

Odometer uses degrees: 0<=theta<360
Operates in millimeters (no)
Methods cmSet[theta,X,Y](int) used to circumvent this issue
--------------------------------------------------------------
*/



import lejos.util.Timer;
import lejos.util.TimerListener;

public class Odometer implements TimerListener {
	/* FIXME: have the time be a fn of the speed */
	public static final int DEFAULT_PERIOD = 25;
	private TwoWheeledRobot robot;
	private Timer odometerTimer;
	private Navigation nav;
	// position data
	private Object lock;
	private float x, y, theta;
	private double [] oldDH, dDH; 	
	
   public Odometer(TwoWheeledRobot robot, int period, boolean start) {
		// initialise variables
		this.robot = robot;
		this.nav = new Navigation(this);
		odometerTimer = new Timer(period, this);
		x = 0f;
		y = 0f;
		theta = 0f;
		oldDH = new double[2];
		dDH = new double[2];
      lock = new Object(); 
		
		// start the odometer immediately, if necessary
		if (start)
			odometerTimer.start();
	}
	
	public Odometer(TwoWheeledRobot robot) {
		this(robot, DEFAULT_PERIOD, false);
	}
	
	public Odometer(TwoWheeledRobot robot, boolean start) {
		this(robot, DEFAULT_PERIOD, start);
	}
	
	public Odometer(TwoWheeledRobot robot, int period) {
		this(robot, period, false);
	}
	
	public void timedOut() {
		robot.getDisplacementAndHeading(dDH);
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];
		
		// update the position in a critical region
		synchronized (lock) {
			theta += dDH[1];
			theta = (float)fixDegAngle(theta);
			
			x += dDH[0] * Math.sin(Math.toRadians(theta));
			y += dDH[0] * Math.cos(Math.toRadians(theta));
		}
		
		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}
	
	// accessors
	public void getPosition(float [] pos) {
		synchronized (lock) {
			pos[0] = x;
			pos[1] = y;
			pos[2] = theta;
		}
	}
	
	public float getX() {
		synchronized(lock) {
			return x;
		}
	}
	public float getY() {
		synchronized(lock) {
			return y;
		}
	}
	public float getTheta() {
		synchronized(lock) {
			return theta;
		}
	}	
	
	public TwoWheeledRobot getTwoWheeledRobot() {
		return robot;
	}
	
	public Navigation getNavigation() {
		return this.nav;
	}
	
	// mutators
	public void setPosition(float[] position) {
		synchronized (lock) {
			this.x = position[0];
			this.y = position[1];
			this.theta = position[2];
		}
	}
	public void setX(final float x) {
		synchronized (lock) {
			this.x = x;
		}
	}
	public void setY(final float y) {
		synchronized (lock) {
			this.y = y;
		}
	}
	/** fixme: use signed fixed point 0:32 for the entire circle, braching on
	 [-Pi, Pi) */
	public void correctTheta(final float theta) {
		synchronized (lock) {
			this.theta += theta;
			/* fixme: lazy */
			while(this.theta < 0f)   this.theta += 360f;
			while(this.theta > 360f) this.theta -= 360f;
		}
	}
	
	// static 'helper' methods
	/* aaauuuugh */
	public static float fixDegAngle(float angle) {
		if (angle < 0.0)
			angle = 360.0f + (angle % 360.0f);
		
		return angle % 360.0f; /* facepalm */
	}
	public static float minimumAngleFromTo(float a, float b) {
		float d = fixDegAngle(b - a);
		
		if (d < 180.0f)
			return d;
		else
			return d - 360.0f;
	}

	/** we need to draw a class diagram and modify classes, this is not optimal */
	public void travelTo(final float x, final float y) {
		nav.travelTo(x, y);
	}
	public void turnTo(final float a) {
		nav.turnTo(a);
	}
   public void turnConstantlyTo(final float a) {
      nav.turnConstantlyTo(a);
   }

//USE THESE!
//I think I implemented them correctly, but if I made an error and it doesn't work feel free to fix it ;)
   public void setTheta(float theta) {
      synchronized(lock) {
         this.theta=theta;
      }
   }
   public void cmSetX(float x) {
      synchronized(lock) {
         this.x = x*10;
      }
   }
   public void cmSetY(float y) {
      synchronized(lock) {
         this.y = y*10;
      }
   }

   public float cmGetX() {
      synchronized(lock) {
         return this.x;
      }
   }
   public float cmGetY() {
      synchronized(lock) {
         return this.y;
      }
   }
}

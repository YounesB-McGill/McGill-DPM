import lejos.util.Timer;
import lejos.util.TimerListener;

public class Odometer implements TimerListener {
	/* hello! we don't need doubles, float is overkill, should be using fixed
	 point; doubles don't exist on nxj anyway */
	public static final int DEFAULT_PERIOD = 25;
	private TwoWheeledRobot robot;
	private Timer odometerTimer;
	private Navigation nav;
	// position data
	private Object lock;
	private float x, y, theta;
	private double [] oldDH, dDH; /* I don't know what these do; writing code
								   that fixes an array of special values is
								   what object-orientism is specifically
								   designed to avoid */
	
	public Odometer(TwoWheeledRobot robot, int period, boolean start) {
		// initialise variables
		this.robot = robot;
		/* why is Nav under Odo? this is crap */
		this.nav = new Navigation(this);
		odometerTimer = new Timer(period, this);
		x = 0f;
		y = 0f;
		theta = 0f;
		oldDH = new double[2];
		dDH = new double[2]; /* when you do this, god kills a virgin kitten */
		lock = new Object(); /* why not just use "this", instead of creating
							  unnecessary objects? */
		
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
	
	/* stupid getPosition, why? */
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
	
	/* why? */
	public TwoWheeledRobot getTwoWheeledRobot() {
		return robot;
	}
	
	public Navigation getNavigation() {
		return this.nav;
	}
	
	// mutators; update is studid, this is how to write a setter --Neil */
	public void setPosition(final float x, final float y, final float theta) {
		synchronized (lock) {
			this.x = x;
			this.y = y;
			this.theta = theta;
		}
	}
	public void setDeltaTheta(final float theta) {
		synchronized (lock) {
			this.theta -= theta;
		}
	}
	
	// static 'helper' methods
	/* they are only called one time? thus they are useless */
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
}

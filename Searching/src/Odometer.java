/* Lab 4, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.NXTRegulatedMotor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/* this is very basic */
public class Odometer implements TimerListener {
	static final float FROM_DEGREES   = (float)Math.PI / 180f;
	/* FIXME: have the time be a fn of the speed */
	static final int ODOMETER_DELAY = 25;
	static final float RADIUS       = 2.75f;
	/* static final float WIDTH     = 15.8f; */
	/* experiment: rotated by 10 000 went 4.75, 10000/360/4.75 */
	static final float MUL_WIDTH    = 0.171f;

	final NXTRegulatedMotor leftMotor, rightMotor;

	Timer timer = new Timer(ODOMETER_DELAY, this);

	int displacement, heading;
	Position p = new Position();

	/** constructor */
	public Odometer(final NXTRegulatedMotor leftMotor, final NXTRegulatedMotor rightMotor) {
		this.leftMotor  = leftMotor;
		this.rightMotor = rightMotor;
		timer.start();
	}

	/** TimerListener function */
	public void timedOut() {
		/* get tach values */
		int  left = leftMotor.getTachoCount();
		int right = rightMotor.getTachoCount();

		/* reset tach */
		/*leftMotor.resetTachoCount();
		rightMotor.resetTachoCount(); <- results in 0, I think this would
		 ignore overflows anyway */

		int displacement = right + left - this.displacement;
		int heading      = right - left - this.heading;

		/* cm */
		float d = displacement * RADIUS * 0.5f * FROM_DEGREES;
		/* radians */
		float t = heading * MUL_WIDTH;

		float x = d * (float)Math.cos(t);
		float y = d * (float)Math.sin(t);

		synchronized(this) {
			p.x += x;
			p.y += y;
			p.theta += t;
			if(p.theta < -180f) p.theta += 360f;
			if(180f <= p.theta) p.theta -= 360f;
		}

		this.displacement += displacement;
		this.heading      += heading;
	}

	/** accessors */
	public float getTheta() {
		synchronized(this) {
			return p.theta;
		}
	}
	public float getX() {
		synchronized(this) {
			return p.x;
		}
	}
	public float getY() {
		synchronized(this) {
			return p.y;
		}
	}

	public String toString() {
		return "O" + p;
	}
}

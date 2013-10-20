/* Lab 4, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.NXTRegulatedMotor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/* this is very basic */
public class Odometer implements TimerListener {
	/* FIXME: have the time be a fn of the speed */
	static final int ODOMETER_DELAY = 25;
	static final float LEFT_RADIUS  = 2.75f;
	static final float RIGHT_RADIUS = 2.75f;
	static final float WIDTH        = 15.8f;
	static final float ONEOVERWIDTH = 1f /*1f / WIDTH*/;

	final NXTRegulatedMotor leftMotor, rightMotor;

	Timer timer = new Timer(ODOMETER_DELAY, this);

	float displacement;
	float angle;
	
	float x, y, theta;

	/** constructor */
	public Odometer(final NXTRegulatedMotor leftMotor, final NXTRegulatedMotor rightMotor) {
		this.leftMotor  = leftMotor;
		this.rightMotor = rightMotor;
		timer.start();
	}

	/** TimerListener function */
	public void timedOut() {
		float left  = leftMotor.getTachoCount()  * LEFT_RADIUS;
		float right = rightMotor.getTachoCount() * RIGHT_RADIUS;

		/* cm (converts cm / deg) */
		float newDisplacement = (left + right) * 0.5f * ((float)Math.PI / 180f);
		/* radians */
		float newAngle        = (left - right) * ONEOVERWIDTH;

		newDisplacement -= displacement;
		newAngle        -= angle;

		synchronized(this) {
			theta += newAngle;
			x += newDisplacement * Math.sin(theta);
			y += newDisplacement * Math.cos(theta);
		}

		displacement += newDisplacement;
		angle        += newAngle;
	}

	public String toString() {
		return "O("+(int)x+","+(int)y+";"+(int)theta+")";
	}
}

/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/* Robot */

/* "In most cases, the Runnable interface should be used if you are only
 planning to override the run() method and no other Thread methods." */

class Robot implements Runnable {
	static final int   NAV_DELAY = 100; /* ms */
	static final int SONAR_DELAY = 50;  /* ms */
	static final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;

	enum Status { PLOTTING, EVADING, EXPLORING, NAVIGATING, PUSHING };

	Status status;

	UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
	LightSensor      ls = new LightSensor(SensorPort.S4);
	Position   position = new Position();
	/* this is the actal values */
	final int angleTolerance = Position.fromDegrees(2f), angleP = 5;
	/* this is what we're moving towards */
	Controller<Integer>  angle = new Controller<Integer>(1, 1, 1, 1);
	Controller<Float> distance = new Controller<Float>(1f, 1f, 1f, 0.5f);

	public Robot() {
		status = Status.PLOTTING;
	}

	public Status getStatus() {
		return status;
	}

	public void run() {
		
	}

	public void travelTo(final float x, final float y) {
		
	}

	/** this turns to a [0,360) degree */
	public void turnTo(final float degrees) {
		this.turnTo(Position.fromDegrees(degrees));
	}
	/** this turns to a {0,32} fixed point angle */
	public void turnTo(final int theta) {
		/*angle.setSetpoint(theta);
		while(angle.next()) {
			try { Thread.sleep(NAV_DELAY); } catch (InterruptedException e) { }
		}*/
		int t;
		int r, l;

		for( ; ; ) {
			t = theta - position.theta;
			if(t > -angleTolerance && t < angleTolerance) break;
			l =  t * angleP;
			r = -t * angleP;
			this.setLeftSpeed(l);
			this.setRightSpeed(r);
			try { Thread.sleep(NAV_DELAY); } catch (InterruptedException e) { }
		}
		this.stop();
	}

	/** set r/l speeds indepedently is good for pid-control */
	private void setLeftSpeed(final int s) {
		leftMotor.setSpeed(s);
		if(s > 0) {
			leftMotor.forward();
		} else if(s < 0) {
			leftMotor.backward();
		} else {
			leftMotor.stop();
		}
	}
	private void setRightSpeed(final int s) {
		rightMotor.setSpeed(s);
		if(s > 0) {
			rightMotor.forward();
		} else if(s < 0) {
			rightMotor.backward();
		} else {
			rightMotor.stop();
		}
	}
	private void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}
	
	/** fixme: get FILTED data; this is especially critical when two robots are
	 getting sound distances at the same time at the same frequency */
	private int pingSonar() {
		us.ping();
		try { Thread.sleep(SONAR_DELAY); } catch (InterruptedException e) { }
		return us.getDistance();
	}	

	public String toString() {
		return "Robot"+this.hashCode();
	}
}

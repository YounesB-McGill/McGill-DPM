/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

import lejos.nxt.LCD;
import lejos.nxt.Button;

/* Robot */

/* "In most cases, the Runnable interface should be used if you are only
 planning to override the run() method and no other Thread methods." */

class Robot implements Runnable {
	static final int   NAV_DELAY = 100; /* ms */
	static final int SONAR_DELAY = 50;  /* ms */
	static final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;

	enum Status { PLOTTING, SUCCESS, ROTATING, TRAVELLING, EVADING, EXPLORING, PUSHING };

	Status status;

	UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
	LightSensor      ls = new LightSensor(SensorPort.S4);
	Odometer   odometer = new Odometer(leftMotor, rightMotor);
	Position   position = new Position();
	Position     target = new Position();
	/* this is the actal values */
	final float/*int*/ angleTolerance = Position.fromDegrees(2f), angleP = 5;
	/* this is what we're moving towards */
	Controller<Integer>  angle = new Controller<Integer>(1, 1, 1, 1);
	Controller<Float> distance = new Controller<Float>(1f, 1f, 1f, 0.5f);

	/** the constructor */
	public Robot() {
		status = Status.PLOTTING;
	}

	public Status getStatus() {
		return status;
	}

	/** this acts as the control */
	public void run() {
		for( ; ; ) {
			switch(status) {
				case PLOTTING:
					break;
				case SUCCESS:
					return;
				case ROTATING:
					this.rotate();
					LCD.drawString(""+odometer, 0, 0);
					break;
				case TRAVELLING:
					status = Status.PLOTTING;
					break;
			}
			try { Thread.sleep(NAV_DELAY); } catch (InterruptedException e) { }
		}
	}

	public void shutdown() {
		status = Status.SUCCESS;
		return;
	}

	public void travelTo(final float x, final float y) {
		status = Status.TRAVELLING;
	}

	/** this sets the target to a [0,360) degree */
	public void turnTo(final float theta/*degrees*/) {
		/*this.turnTo(Position.fromDegrees(degrees));*/
		target.theta = theta;
		status = Status.ROTATING;
	}
	/** this sets the target to a {0,32} fixed point angle; flag rotating */
	/*public void turnTo(final int theta) {
		target.theta = theta;
		status = Status.ROTATING;
	}*/

	/** this implements a rotation by parts */
	void rotate() {
		float/*int*/ t;
		int r, l;

		/*t = target.theta - position.theta;
		if(t > -angleTolerance && t < angleTolerance) {
			this.stop();
			status = Status.PLOTTING;
			return;
		}
		l = (int)( t * angleP);
		r = (int)(-t * angleP);*/
		if((odometer.getTheta() < 360) /*&& (!Button.ENTER.isDown())*/) {
			l = -100;
			r = 100;
			this.setLeftSpeed(l);
			this.setRightSpeed(r);
		} else {
			this.stop();
			status = Status.PLOTTING;
		}
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

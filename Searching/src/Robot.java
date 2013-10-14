/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.LightSensor;

/* Robot */

class Robot {
	static final int NAV_DELAY = 100; /* ms */

	UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
	LightSensor      ls = new LightSensor(SensorPort.S4);
	Position   position = new Position();
	final int angleTolerance = Position.fromDegrees(2f), angleP = 5;
	Controller    angle = new Controller(1f, 1f, 1f, 0.5f);
	Controller distance = new Controller(1f, 1f, 1f, 0.5f);

	public Robot() {
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

	/** set r/l speeds indepedently is good for pid-control;
	 eg stays to to left, it increases the speed of the right wheel without
	 stopping to correct; we use p-control */
	public void setLeftSpeed(final int s) {
		leftMotor.setSpeed(s);
		if(s > 0) {
			leftMotor.forward();
		} else {
			leftMotor.backward();
		}
	}
	public void setRightSpeed(final int s) {
		rightMotor.setSpeed(s);
		if(s > 0) {
			rightMotor.forward();
		} else {
			rightMotor.backward();
		}
	}

	public String toString() {
		return "Robot"+this.hashCode();
	}
}

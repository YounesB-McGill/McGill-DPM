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

	enum Status { PLOTTING, SUCCESS, ROTATING, TRAVELLING, EVADING, EXPLORING, PUSHING };
	
	public final static String NAME = "Sex Robot";
	static final int   NAV_DELAY    = 100; /* ms */
	static final int SONAR_DELAY    = 50;  /* ms */
	static final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;

	final float/*int*/ angleTolerance = Position.fromDegrees(2f), angleP = 5f;
	/* this is what we're moving towards */
	Controller<Integer>  angle = new Controller<Integer>(1, 1, 1, 1);
	Controller<Float> distance = new Controller<Float>(1f, 1f, 1f, 0.5f);

	Status status = Status.PLOTTING;

	UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
	LightSensor      ls = new LightSensor(SensorPort.S4);
	Odometer   odometer = new Odometer(leftMotor, rightMotor);
	Position   position;
	Position     target = new Position();

	/** the constructor */
	public Robot() {
		System.out.println(NAME);
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
					LCD.drawString(""+odometer+"  ", 0, 0);
					break;
				case TRAVELLING:
					this.travel();
					LCD.drawString(""+odometer+"  ", 0, 0);
					break;
			}
			try {
				Thread.sleep(NAV_DELAY);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

	public void shutdown() {
		odometer.shutdown();
		status = Status.SUCCESS;
	}

	public void travelTo(final float x, final float y) {
		System.out.println("Goto("+(int)x+","+(int)y+")");
		target.x = x;
		target.y = y;
		status = Status.TRAVELLING;
	}

	/** this sets the target to a [0,360) degree */
	public void turnTo(final float theta/*degrees*/) {
		/*this.turnTo(Position.fromDegrees(degrees));*/
		System.out.println("Turn("+(int)theta+")");
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
		float r, l;

		position = odometer.getPosition();
		t = target.theta - position.theta;
		LCD.drawString("> "+t+"  ", 0, 1);
		if((t > -angleTolerance) && (t < angleTolerance)) {
			System.out.println(""+(-angleTolerance)+"<"+t+"<"+angleTolerance);
			this.stop();
			status = Status.PLOTTING;
			return;
		}
		l = -t * angleP;
		r =  t * angleP;
		this.setLeftSpeed(l);
		this.setRightSpeed(r);
//		if((odometer.getTheta() < 90f) /*&& (!Button.ENTER.isDown())*/) {
/*			l = -100;
			r = 100;
			this.setLeftSpeed(l);
			this.setRightSpeed(r);
		} else {
			this.stop();
			status = Status.PLOTTING;
		}*/
	}
	
	void travel() {
		if(true) {
			this.stop();
			status = Status.PLOTTING;
		}
	}

	/** set r/l speeds indepedently is good for pid-control */
	private void setLeftSpeed(final /*int*/float s) {
		leftMotor.setSpeed(s);
		if(s > 0) {
			leftMotor.forward();
		} else if(s < 0) {
			leftMotor.backward();
		} else {
			leftMotor.stop();
		}
	}
	private void setRightSpeed(final /*int*/float s) {
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
		try {
			Thread.sleep(SONAR_DELAY);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return us.getDistance();
	}	

	public String toString() {
		return NAME+/*this.hashCode()+*/" is "+status;
	}
}

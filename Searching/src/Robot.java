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

	Controller/*<Integer>*/  angle = new Controller/*<Integer>*/(6f, 1f, 1f);
	Controller/*<Float>*/ distance = new Controller/*<Float>*/(15f, 1f, 1f);
	final float     angleTolerance = Position.fromDegrees(0.5f);
	final float  distanceTolerance = 0.5f;

	Status       status = Status.PLOTTING;
	UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
	LightSensor      ls = new LightSensor(SensorPort.S4);
	Odometer   odometer = new Odometer(leftMotor, rightMotor);
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
					LCD.drawString("R:R "+odometer+";", 0, 0);
					break;
				case TRAVELLING:
					this.travel();
					LCD.drawString("R:T "+odometer+";", 0, 0);
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

	/** this sets the target to a [0,360) degree */
	public void turnTo(final float theta/*degrees*/) {
		/*this.turnTo(Position.fromDegrees(degrees));*/
		System.out.println("Turn("+(int)theta+")");
		target.theta = theta;
		/*angle.setSetpoint(theta);*/
		status = Status.ROTATING;
	}
	/** this sets the target to a {0,32} fixed point angle; flag rotating */
	/*public void turnTo(final int theta)*/

	public void travelTo(final float x, final float y) {
		System.out.println("Goto("+(int)x+","+(int)y+")");
		target.x = x;
		target.y = y;
		//status = Status.TRAVELLING;

		float dx, dy, dt=0, t, dist;
		float r, l, speed;
		Position p;
		for( ; ; ) {
			p    = odometer.getPositionCopy();
			dx   = target.x - p.x;
			dy   = target.y - p.y;
			dist = (float)Math.sqrt(dx*dx + dy*dy);
			if(dist < 0.5f) break;
			t    = (float)Math.toDegrees(Math.atan2(dy, dx));
			dt   = t - p.theta;
			if(dt >= 180f)      dt -= 360f;
			else if(dt < -180f) dt += 360f;
			//angle.next(dt);
			l = -dt * 4f;
			r =  dt * 4f;
			speed = dist * 4f /* * (float)Math.cos(Math.toRadians(dt))*/;
			if(speed > 200) break; /* fuck you!! */
			speed = 100;
			l += speed;
			r += speed;
			/*if(angle.isWithin(distanceTolerance)) break;*/
			this.setLeftSpeed(l);
			this.setRightSpeed(r);
			try { Thread.sleep(100); } catch (InterruptedException e) { } /* bad */
			//break;
		}
		this.stop();
		status = Status.PLOTTING;
		
		System.out.println("tt "+dist+":"+dt);

		/*for( ; ; ) {
			float right = angle.next(p.theta);
			LCD.drawString("angle"+angle+";", 0, 1);
			if(angle.isWithin(angleTolerance)) break;
		}*/

		/*float tCurrent, tTarget, dx, dy, dt, dist;
		float l, r, speed;
		Position current;
		for( ; ; ) {
			current = odometer.getPositionCopy();
			dx = xTarget - current.x;
			dy = yTarget - current.y;
			dist = (float)Math.sqrt(dx*dx + dy*dy);
			if(dist < 0.5f) break;
			tTarget = (float)Math.toDegrees(Math.atan2(dy, dx));
			dt = tTarget - current.theta;
			if(dt < -180f)     dt += 360f;
			else if(dt > 180f) dt -= 360f;
			LCD.drawString("T("+(int)xTarget+","+(int)yTarget+":"+(int)tTarget+")\nC"+current+"\ndd"+(int)dist+":dt"+(int)dt+";", 0,0);
			l = -dt * 5f;
			r =  dt * 5f;
			speed = dist * 5f * (float)Math.cos(Math.toRadians(dt));
			l += speed;
			r += speed;
			this.setLeftSpeed(l);
			this.setRightSpeed(r);
			try { Thread.sleep(100); } catch (InterruptedException e) { }
		}
		this.stop();
		
		status = Status.PLOTTING;*/
	}

	/** this implements a rotation by the angle controller */
	void rotate() {
		Position  p = odometer.getPositionCopy();
		float    dt = target.theta - p.theta;
		if(dt < -180f)     dt += 360f;
		else if(dt > 180f) dt -= 360f;
		float right = angle.next(dt);

		LCD.drawString("r:a "+(int)dt+" "+angle+"", 0, 1);
		if(angle.isWithin(angleTolerance)) {
			this.stop();
			status = Status.PLOTTING;
			return;
		}
		this.setLeftSpeed(-right);
		this.setRightSpeed(right);
	}

	/** travels to a certain position */
	void travel() {
		Position p = odometer.getPositionCopy();
		float x = target.x - p.x;
		float y = target.y - p.y;
		float dist = (float)Math.sqrt(x*x + y*y);

		/*(float)Math.toDegrees(Math.atan2(y, x))*/
		
		float r = angle.next(p.theta);
		float l = -r;

		float d = distance.next(-dist);// * Math.cos(Math.toRadians(p.theta));
		r += d;
		l += d;

		LCD.drawString("Angle "+angle+"  ", 0, 1);
		LCD.drawString("Dist "+distance+"  ", 0, 2);

		if(distance.isWithin(distanceTolerance)) {
			this.stop();
			status = Status.PLOTTING;
			return;
		}
		this.setLeftSpeed(l);
		this.setRightSpeed(r);
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

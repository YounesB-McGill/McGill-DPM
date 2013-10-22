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

	enum Status { PLOTTING, SUCCESS, LOCALISING, ROTATING, TRAVELLING, EVADING, EXPLORING, PUSHING };
	
	public final static String NAME = "Sex Robot";
	static final int   NAV_DELAY    = 100; /* ms */
	static final int SONAR_DELAY    = 10;  /* ms */
	static final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;

	Controller/*<Integer>*/  angle = new Controller/*<Integer>*/(5f, 1f, 1f);
	Controller/*<Float>*/ distance = new Controller/*<Float>*/(10f, 1f, 1f);
	final float     angleTolerance = Position.fromDegrees(0.5f);
	final float  distanceTolerance = 3f;

	Status       status = Status.PLOTTING;
	UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
	LightSensor      ls = new LightSensor(SensorPort.S1);
	Odometer   odometer = new Odometer(leftMotor, rightMotor);
	Position     target = new Position(), d = new Position();
	Colour       colour = new Colour();
	int lastDistance;

	/** the constructor */
	public Robot() {
//		System.out.println(NAME);
		/*leftMotor.setAccelertion(3000);
		rightotor.setAccelertion(3000);*/
	}

	public int getLastDistance() {
		synchronized(this) {
			return lastDistance;
		}
	}
	
	public Position getPosition() {
		synchronized(this) {
			return odometer.getPositionCopy();
		}
	}

	public Status getStatus() {
		return status;
	}

   public Position getTarget() {
      synchronized(this) {
         return target;
      }
   }

	public Colour.Value getColour() {
		return colour.getColourValue();
	}

	/** this acts as the control */
	public void run() {
		int distance;

		for( ; ; ) {
			/* do this */
			lastDistance = us.getDistance();
			if(lastDistance < 15) {
//				status = Status.EVADING;
//				System.out.println("EVADING! TODO");
			}
			/* what is it doing? */
			switch(status) {
				case PLOTTING:
					/* muhahahaha */
					break;
				case LOCALISING:
					localise();
					break;
				case SUCCESS:
					return;
				case ROTATING:
					this.rotate();
//					LCD.drawString("R:R "+odometer+";", 0, 0);
					break;
				case TRAVELLING:
					this.travel();
//					LCD.drawString("R:T "+odometer+";", 0, 0);
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
   float angleTo;
	/** this is used in localisation */
	public void turnConstantlyTo(final float angleTo,final int speed) {
		status = Status.LOCALISING;
		this.setLeftSpeed(-speed);
		this.setRightSpeed(speed);
      this.angleTo = angleTo;
	}
	void localise() {
		Position p = odometer.getPositionCopy();
		if(p.theta < angleTo) return;
		this.stop();
		status = Status.PLOTTING;
	}

	/** this sets the target to a [0,360) degree */
	public void turnTo(final float theta/*degrees*/) {
		angle.reset();
		/*this.turnTo(Position.fromDegrees(degrees));*/
//		System.out.println("Turn("+(int)theta+")");
		target.theta = theta;
		/*angle.setSetpoint(theta);*/
		status = Status.ROTATING;
	}
	/** this sets the target to a {0,32} fixed point angle; flag rotating */
	/*public void turnTo(final int theta)*/

	public void travelTo(final float x, final float y) {
//		System.out.println("Goto("+(int)x+","+(int)y+")");

		distance.reset();
		angle.reset();

		target.x = x;
		target.y = y;
		//status = Status.TRAVELLING;

		/* fixme: ghetto */

		Position p;
		float dx, dy, dt;
		float right, dist, speed;

		for( ; ; ) {
			p = odometer.getPositionCopy();
			dx                      = target.x - p.x;
			dy                      = target.y - p.y;
			target.theta            = (float)Math.toDegrees(Math.atan2(dy, dx));
			dt                      = target.theta - p.theta;
			if(dt < -180f)      dt += 360f;
			else if(dt >= 180f) dt -= 360f;
//			LCD.drawString(""+(int)dx+","+(int)dy+":"+(int)dt+";", 0,0);
			right = angle.next(dt);
			if(angle.isWithin(angleTolerance)) break;
			this.setLeftSpeed(-right);
			this.setRightSpeed(right);
			try { Thread.sleep(50); } catch (InterruptedException e) { }
		}
		float targetDist = (float)Math.sqrt(dx*dx + dy*dy) * 0.2f;
		for( ; ; ) {
			p = odometer.getPositionCopy();
			dx   = target.x - p.x;
			dy   = target.y - p.y;
			dist = (float)Math.sqrt(dx*dx + dy*dy);
			if(dist < targetDist) break;
			if(dist < 1.5f) break;
			//speed = dist * 5f;
			speed = distance.next(dist - targetDist);
         this.setLeftSpeed(speed);
			this.setRightSpeed(speed);
			try { Thread.sleep(50); } catch (InterruptedException e) { }
		}
		this.stop();
		status = Status.PLOTTING;
	}

	/** this implements a rotation by the angle controller */
	void rotate() {
		Position  p = odometer.getPositionCopy();
		float    dt = target.theta - p.theta;
		if(dt < -180f)     dt += 360f;
		else if(dt > 180f) dt -= 360f;
		float right = angle.next(dt);

//		LCD.drawString("r:a "+(int)dt+" "+angle+"", 0, 1);
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
		d.x     = target.x - p.x;
		d.y     = target.y - p.y;
		d.theta = (float)Math.toDegrees(Math.atan2(d.y, d.x)) - p.theta;
		if(d.theta < -180f)      d.theta += 360f;
		else if(d.theta >= 180f) d.theta -= 360f;
		float dist = (float)Math.sqrt(d.x*d.x + d.y*d.y);

		float l = angle.next(d.theta);
		float r = -l;

		float d = distance.next(dist);// * Math.cos(Math.toRadians(p.theta));
		r += d;
		l += d;

		if(distance.isWithin(distanceTolerance)) {
			this.stop();
			status = Status.PLOTTING;
			return;
		}
		this.setLeftSpeed(l);
		this.setRightSpeed(r);

//		LCD.drawString("t"+d+"  ", 0, 0);
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
	public void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}	

	/** fixme: get FILTED data; this is especially critical when two robots are
	 getting sound distances at the same time at the same frequency */
	public int pingSonar() {
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

	/** this is for calibrating; 3 squares 91.44 -> 30.48 cm / tile */
	public void driveLeg(final float cm) {
		final int FORWARD_SPEED = 250;
		final int ROTATE_SPEED = 150;

		/* forward */
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate((int)((180.0 * cm) / (Math.PI * Odometer.RADIUS)), true);
		rightMotor.rotate((int)((180.0 * cm) / (Math.PI * Odometer.RADIUS)), false);

		/* turn 90 */
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate((int)-((180.0 * Math.PI * Odometer.WIDTH * 90.0 / 360.0) / (Math.PI * Odometer.RADIUS)), true);
		rightMotor.rotate((int)((180.0 * Math.PI * Odometer.WIDTH * 90.0 / 360.0) / (Math.PI * Odometer.RADIUS)), false);
	}
}

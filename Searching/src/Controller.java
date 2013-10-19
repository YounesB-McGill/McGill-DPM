/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* Controller: implements PID control */

public class Controller<N extends Number> {
	N p, i, d, tolerance;
	N setpoint;
	N x;

	public Controller(final N p, final N i, final N d, final N tolerance) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.tolerance = tolerance;
		/* uuumm don't know how to do this . . . (tolerance.intValue() <= 0) ? (???) : tolerance; */
	}

	/** next step; returns true if within tolerance value of setpoint */
	public boolean next() {
		return false;
	}

	public void setSetpoint(final N setpoint) {
		this.setpoint = setpoint;
	}

	public String toString() {
		return "Controller"+this.hashCode()+" with pid "+p+", "+i+", "+d+" at setpoint "+setpoint+" is at "+x+"";
	}
}

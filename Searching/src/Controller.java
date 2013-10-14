/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* Controller: implements PID control */

class Controller {
	float p, i, d, tolerance;
	float setpoint;
	float x;

	public Controller(final float p, final float i, final float d, final float tolerance) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.tolerance = (tolerance <= 0f) ? 1f : tolerance;
	}

	/** next step; returns true if within tolerance value of setpoint */
	public boolean next() {
		return false;
	}

	public void setSetpoint(final float setpoint) {
		this.setpoint = setpoint;
	}

	public String toString() {
		return "Controller"+this.hashCode()+" with pid "+p+", "+i+", "+d+" at setpoint "+setpoint+" is at "+x+"";
	}
}

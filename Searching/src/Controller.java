/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* Controller: implements PID control */

public class Controller/*<N extends Number> was so cool but aritmetic operations can't be applied to Number */ {
	/* fixme: all int! */
	/*N*/float kp, ki, kd;
	/*N*/float sp, pv, e;

	public Controller(final /*N*/float p, final /*N*/float i, final /*N*/float d) {
		kp = p;
		ki = i;
		kd = d;
	}

	/** returns the next step */
	public /*N*/float next(final /*N*/float presentValue) {
		pv = presentValue;
		e = sp - pv;
		return kp * e /* + ki * (int e) + kd * (d/dt e) */;
	}

	public boolean isWithin(final /*N*/float tolerance) {
		return (e > -tolerance) && (e < tolerance);
	}

	public void setSetpoint(final /*N*/float setpoint) {
		sp = setpoint;
	}

	public String toString() {
		return "C("+e+")";
		//return "Controller"+this.hashCode()+" with pid "+kp+", "+ki+", "+kd+" at setpoint "+sp+" is at "+pv+"";
	}
}

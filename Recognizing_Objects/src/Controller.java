/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* THIS IS NOT USED IN YOUR CODE but could be -Neil */

/* Controller: implements PID control with 0 as the setpoint */

import java.lang.IllegalArgumentException;

public class Controller/*<N extends Number> was so cool but aritmetic operations can't be applied to Number */ {
	/* fixme: all int! */
	/*N*/float kp, ki, kd; /* proportional, intergal, derivative */
	/*N*/float /*sp, pv,*/ e;  /* setpoint, current value, error */
	float min, max;
	boolean isLimit;

	//public Controller(final /*N*/float p, final /*N*/float i, final /*N*/float d) {
	//	kp = p;
	//	ki = i;
	//	kd = d;
	//}

	public Controller(final float p, final float min, final float max) {
		if(min > max || p <= 0) throw new IllegalArgumentException();
		kp = p;
		ki = 0;
		kd = 0;
		this.min = min;
		this.max = max;
	}

	/** returns the next step fixme id */
	public /*N*/float next(final /*N*/float error/*presentValue*/) {
		/*pv = presentValue;
		e = sp - pv;*/
		e = error;
      float r = kp * e;
      /* hack, make variable */
      if(r > 200f) r = 200f;
      else if(r < -200f) r = -200f;
		return r /*kp * e*/ /* + ki * (int e) + kd * (d/dt e) */;
	}

	public boolean isWithin(final /*N*/float tolerance) {
		return (e > -tolerance) && (e < tolerance);
	}

	//public void setSetpoint(final /*N*/float setpoint) {
	//	sp = setpoint;
	//}
	
	public void reset() {
		e = 0f;
	}

	public String toString() {
		return "("+(int)e/*pv+":"+(int)sp*/+")";
		//return "Controller"+this.hashCode()+" with pid "+kp+", "+ki+", "+kd+" at setpoint "+sp+" is at "+pv+"";
	}
}

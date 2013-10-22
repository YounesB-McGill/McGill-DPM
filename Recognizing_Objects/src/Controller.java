/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* THIS IS NOT USED IN YOUR CODE but could be -Neil */

/* Controller: implements PID control with 0 as the setpoint */

import java.lang.IllegalArgumentException;

public class Controller/*<N extends Number> was so cool but aritmetic operations can't be applied to Number */ {
	/* fixme: all int! */
	/*N*/float kp, ki, kd; /* proportional, intergal, derivative */
	/*N*/float /*sp, pv,*/ e;  /* setpoint, current value, error */
	float min, max;
	boolean isLimit, isLast;
	float integral, eLast;

	//public Controller(final /*N*/float p, final /*N*/float i, final /*N*/float d) {
	//	kp = p;
	//	ki = i;
	//	kd = d;
	//}

	public Controller(final float p, final float min, final float max) {
		if(min > max || p <= 0) throw new IllegalArgumentException();
		/* just give convenient values */
		kp = p;
		ki = p * 0.4f;
		kd = p * 0.2f;
		this.min = min;
		this.max = max;
		isLimit = true;
	}

	/** returns the next step fixme id; fixme:  */
	public /*N*/float next(final /*N*/float error/*presentValue*/) {

		/* this is a remenant from where the setpoint could not equal zero
		 pv = presentValue;
		 e = sp - pv;*/
		/* this is much easier */
		e = error;

		/* p */
		final float p = kp * e;

		/* i */
		final float i = ki * integral;
		
		/* d */
		float derivative = 0;
		if(isLast) {
			derivative = e - eLast;
		}
		final float d = kd * derivative;

		if(isLimit) {
			if(r > max)      r = max;
			else if(r < min) r = min;
		}

		/* update to values for the next time */
		integral += e;
		last      = e;
		isLast    = true;
		
		/* kp * e + ki * (int e) + kd * (d/dt e) */
		return p + i + d;
	}

	public boolean isWithin(final /*N*/float tolerance) {
		return (e > -tolerance) && (e < tolerance);
	}

	//public void setSetpoint(final /*N*/float setpoint) {
	//	sp = setpoint;
	//}
	
	public void reset() {
		integral = 0;
		isLast = false;
		e = 0f;
	}

	public String toString() {
		return "("+(int)e/*pv+":"+(int)sp*/+")";
		//return "Controller"+this.hashCode()+" with pid "+kp+", "+ki+", "+kd+" at setpoint "+sp+" is at "+pv+"";
	}
}

/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* This class is the product of redesigning the lab code to be more intuitive. It was not used in the code that we ran in the demo
and is not instantiated in Lab5 or any of the other lab files, and not required for the code to compile. It is awesome, but we were
not able to get it working in time to use in this lab. We intend to use it in the final project.

THIS IS NOT USED IN THE LAB but could be -Neil */

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

	/** returns the next step; fixme: add time (more complicated!)? */
	public /*N*/float next(final /*N*/float error/*presentValue*/) {

		/* this is a remenant from where the setpoint could NOT equal zero
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

		float pid = p + i + d;

		/* limit output */
		if(isLimit) {
			if(pid > max)      pid = max;
			else if(pid < min) pid = min;
		}

		/* update to values for the next time */
		integral += e;
		last      = e;
		isLast    = true;
		
		/* kp * e + ki * (int e) + kd * (d/dt e) */
		return pid;
	}

	/** this method is a convineience; it's always checking if it's w/i epsilon */
	public boolean isWithin(final /*N*/float tolerance) {
		return (e > -tolerance) && (e < tolerance);
	}

	/** this fn is not needed now; the setpoint is always zero */
	/*public void setSetpoint(final N setpoint) {
		sp = setpoint;
	}*/
	
	/** reset the intergral and the derivative; this allows the controller to
	 be used more then once */
	public void reset() {
		isLast = false;
		integral = 0;
		e = 0f;
	}

	/** print the last error */
	public String toString() {
		return "("+(int)e/*pv+":"+(int)sp*/+")";
		//return "Controller"+this.hashCode()+" with pid "+kp+", "+ki+", "+kd+" at setpoint "+sp+" is at "+pv+"";
	}
}

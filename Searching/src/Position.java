/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import java.lang.Integer;

/* Position fixme */

class Position {

	public float x, y; /* lazy */
	/*        ---|--
	         /   |  \
	INT_MAX v    |   |
	---------^----------- 0
	INT_MIN  |   |   |
	          \  |  /
	           --|--
	 */
	public float/*int*/ theta;  /* signed 0:32 fixed point */

	public Position() {
	}

	public void copy(Position p) {
		x = p.x;
		y = p.y;
		theta = p.theta;
	}

	public void addLocation(final float x, final float y) {
		this.x += x;
		this.y += y;
	}

	public void addTheta(final float t) {
		theta += t;
		/* fixme: so much fail */
		if(0 < theta) {
			while(t < 180f) theta += 360f;
		} else {
			while(180f <= t) theta -= 360f;
		}
	}

	public String toString() {
		return "("+(int)x+","+(int)y+":"+(int)theta+")";
	}

	public static float/*int*/ fromDegrees(float degree) {
		if(degree < 0f) {
			while(degree < 180f) degree += 360f;
		} else {
			while(degree >= 180f) degree -= 360f;
		}
		return degree;
		//(int)(degree * (-(float)Integer.MIN_VALUE / 180f));
	}
}

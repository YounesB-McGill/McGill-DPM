/* Lab 5, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import java.lang.Integer;

/* Position */

class Position {
	public int /* float? */ x, y;
	public int theta; /* signed 0:32 fixed point */

	public Position() {
	}

	public String toString() {
		return "Position("+x+","+y+":"+theta+")";
	}

	public static int fromDegrees(float degree) {
		if(0 < degree) {
			while(degree < 180f) degree += 360f;
		} else {
			while(180f <= degree) degree -= 360f;
		}
		return (int)(degree * (-(float)Integer.MIN_VALUE / 180f));
	}
}

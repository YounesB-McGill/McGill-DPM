/* Lab 5 A, Group 51 -- Alex Bhandari-Young and Neil Edelman */

/* This method is O(2^{n-1}), but n is 2, so it's okay. We normalise our
 colours to make it lighting-independent. Compare with experimetal value for
 the different substances, and pick the closest (Cartesan distance to
 normalised colour values.) */

/* import javax.vecmath.Vector3f; <- does not have this, write our own :[ */
/* import java.lang.Comparable; */

import lejos.nxt.LCD;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

/* this is a driver that instantaties a Robot and makes it do stuff */

public class Lab5 {

	/* experimenatly determined over multiple orinatations */
	static Vector3f styrofoam = new Vector3f(0.569778f, 0.546741f, 0.611144f);
	static Vector3f woodblock = new Vector3f(0.795851f, 0.438809f, 0.414174f);

	public static void main(String args[]) {
		ColorSensor       cd = new ColorSensor(SensorPort.S3);
		ColorSensor.Color c;
		Vector3f          colour, colourDiff = new Vector3f();
		float             s, w;
		int               percent;

		/* the normalisation projects the HSL values onto L = 0.5 so it isn't
		 affected by natural light (3d -> 2d;) barycentric coordinates are the
		 square of the normalised values */
		styrofoam.normalize();
		woodblock.normalize();

		for( ; ; ) {
			/* get input */
			LCD.drawString("hit me", 0,0);
			if((Button.waitForAnyPress() & Button.ID_ESCAPE) != 0) break;
			LCD.clear();

			/* store it in useless "Color" then transfer it to useful class */
			c      = cd.getColor(); /* 0 - 255 */
			colour = new Vector3f(c.getRed() / 255f,
								  c.getGreen() / 255f,
								  c.getBlue() / 255f);
			colour.normalize();

			/* compare with styrofoam and wood; I think technically, we should
			 convert to a quatnion to get the great circle distance, but that's
			 kind of like measureing a distance between two points taking into
			 account the curvature of the Earth; it's monotonanic and close */
			colourDiff.sub(colour, styrofoam);
			s = colourDiff.lengthSquared();
			colourDiff.sub(colour, woodblock);
			w = colourDiff.lengthSquared();

			/* display */
			LCD.drawString("R "+c.getRed()+"/"+colour.r, 0,1);
			LCD.drawString("G "+c.getGreen()+"/"+colour.g, 0,2);
			LCD.drawString("B "+c.getBlue()+"/"+colour.b, 0,3);
			if(s > w) {
				percent = (int)(s / (s + w) * 100f);
				LCD.drawString("I'm " + percent + "% sure it's\na wooden block", 0,4);
			} else if(w > s) {
				percent = (int)(w / (s + w) * 100f);
				LCD.drawString("I'm " + percent + "% sure it's\na styrofoam block", 0,4);
			} else {
				LCD.drawString("I'm conflicted", 0,4);
			}
			LCD.drawString("s "+s, 0,6);
			LCD.drawString("w "+w, 0,7);
		}
	}
}

/* package javax.vecmath does not exist; aaaaauuuuuuggghht wtf */
/* this is a normalised colour on 19:13 bit fixed point
 3*255^2 = 195 075 (18 bit) */
/* no, that's complicated, just use floats */
class Vector3f /*implements Comparable<ColourNorm> <- only int */ {
	public float r, g, b;

	/** empty constructor */
	public Vector3f() {
	}

	/** fill constructor */
	public Vector3f(final float r, final float g, final float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/** returns the length squared */
	public float lengthSquared() {
		return r*r + g*g + b*b;
	}

	/** returns the length */
	public float length() {
		return (float)Math.sqrt(this.lengthSquared());
	}
	
	/** normalises this vector in place */
	public void normalize() {
		float d = length();

		/* if it's black as satan, just leave it alone */
		if(d == 0f) return;
		/* invert */
		float e = 1f / d;
		r *= e;
		g *= e;
		b *= e;
	}

	/** subtract */
	public void sub(final Vector3f x) {
		r -= x.r;
		g -= x.g;
		b -= x.g;
	}
	
	/** subtract */
	public void sub(final Vector3f x, final Vector3f y) {
		r = x.r - y.r;
		g = x.g - y.g;
		b = x.g - y.b;
	}
}

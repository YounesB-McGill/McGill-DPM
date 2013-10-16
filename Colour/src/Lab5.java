/* Lab 5 A, Group 51 -- Alex Bhandari-Young and Neil Edelman */

import lejos.nxt.LCD;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.ColorSensor;
/*import javax.vecmath.Tuple3i;*/ /* for storing the colour -- complians! */
/*import java.lang.Comparable;*/

/* this is a driver that instantaties a Robot and makes it do stuff */
public class Lab5 {
	/* experimenatly determined */
	static final ColourNorm styrofoam = new ColourNorm(32,  38,  26);
	static final ColourNorm woodblock = new ColourNorm(212, 167, 151);

	public static void main(String args[]) {
		ColorSensor cd = new ColorSensor(SensorPort.S3);
		ColorSensor.Color c;
		ColourNorm colour;
		float compStyr, compWood;

		for( ; ; ) {
			LCD.drawString("hit me", 0,0);
			if((Button.waitForAnyPress() & Button.ID_ESCAPE) != 0) break;
			LCD.clear();
			/* store it in useless "Color" then transfer it to useful class */
			c      = cd.getColor(); /* 0 - 255 */
			colour = new ColourNorm(c.getRed(), c.getGreen(), c.getBlue());
			/* compare with styrofoam and wood */
			compStyr = colour.compare(styrofoam);
			compWood = colour.compare(woodblock);
			/* display */
			LCD.drawString("R "+c.getRed(), 0,1);
			//  +":"+colour.r+" "+styrofoam.r+"/"+woodblock.r, 0,1);
			LCD.drawString("G "+c.getGreen(), 0,2);
			//+":"+colour.g+" "+styrofoam.g+"/"+woodblock.g, 0,2);
			LCD.drawString("B "+c.getBlue(), 0,3);
			// +":"+colour.b+" "+styrofoam.b+"/"+woodblock.b, 0,3);
			LCD.drawString(""+(int)((1 - Math.min(compStyr, compWood) / Math.max(compStyr, compWood))*100)+"% " + ((compStyr < compWood) ? "styrofoam" : "wood"), 0,4);
			/*LCD.drawString("sf "+styr, 0,5);
			LCD.drawString("wb "+wood, 0,6);*/
		}
	}
}

/* package javax.vecmath does not exist; aaaaauuuuuuggghht wtf I just want a
 stupid Vec3i */
/* this is a normalised colour on 19:13 bit fixed point
 3*255^2 = 195 075 (18 bit) */
class ColourNorm /*implements Comparable<ColourNorm>*/ {
	public float r, g, b;

	/** store 19:13 fixed point normalised value; forget it, store floats */
	public ColourNorm(final int r, final int g, final int b) {
		this.r = range(0, r, 255) / 255f;
		this.g = range(0, g, 255) / 255f;
		this.b = range(0, b, 255) / 255f;
		/* if it's black as satan, just leave it alone */
		if(this.r == 0 && this.g == 0 && this.b == 0) return;
		/* normalise */
		float oneOverd = 1f / (float)Math.sqrt(this.r*this.r + this.g*this.g + this.b*this.b);
		this.r *= oneOverd;
		this.g *= oneOverd;
		this.b *= oneOverd;
		/*this.r = (this.r << 13) / d2;
		this.g = (this.g << 13) / d2;
		this.b = (this.b << 13) / d2;*/
	}

	static int range(final int l, final int x, final int h) {
		if(l > x) {
			return l;
		} else if(x > h) {
			return h;
		}
		return x;
	}

	/** compare the values, this results in 31 max bits
	 no, floating point lazy */
	float/*int*/ compare(final ColourNorm x) {
		float/*int*/ red = r - x.r, green = g - x.g, blue = b - x.b;
		return red*red + green*green + blue*blue;
	}

	/* I don't really see how this would work since we compare it to a known
	 sample . . .
	 public int compareTo(final ColourNorm x) {
		return 0;
	}*/
}

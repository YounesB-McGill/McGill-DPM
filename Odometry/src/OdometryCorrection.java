/* 
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {

	private static final int CORRECTION_PERIOD = 10;

	private Odometer odometer;

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		int correctionStart, correctionEnd;

		while (true) {
			correctionStart = (int)System.currentTimeMillis();

			// put your correction code here

			// this ensure the odometry correction occurs only once every period
			correctionEnd = (int)System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}
/*
This class pings the ultrasonic sensor after 10ms and accumulates values. getDistance simply gets the last pinged distance.
The getFilteredDistance() method implements the median standard deviation method to return a median filtered distance.
The set of the data sets used can be adjusted.
Additionally, the get smallestping method allows the listener to accumulate a data set of pings while a blocking navigation
or odometer method is running. Get smallest ping is used in this lab to scan from scan points and find the blocks using the
ultrasonic sensor. The method sets the smallestping variables to the closest distance recored and corresponding theta from
the odometer to targetTheta.
*/
import lejos.nxt.UltrasonicSensor;
import lejos.util.TimerListener;
import java.util.Arrays;
import java.lang.Math;

public class UltrasonicListener implements TimerListener {

   //   public static final int OUTLIER_THESHOLD = 40;
   public static final float b = 1.4826f; //normality data constant
   public UltrasonicSensor uSensor;
   public Odometer odometer;
   public int[] dist = new int[5];
   public int count = 0;
   public int mad;
   public boolean scanning = false;
   public float targetTheta = -1;
   public int smallestPing = 254;

   public UltrasonicListener(UltrasonicSensor uSensor, Odometer odometer) {

      this.uSensor = uSensor;
      this.odometer = odometer;
      this.uSensor.setMode(UltrasonicSensor.MODE_PING);

   }

   public void timedOut() {

      uSensor.ping();
      count++;
      if (count == 5) {
         uSensor.getDistances(dist,0,5);
         count = 0;
         //recompute filtered distance using mad
         mad=mad(dist);
      }

      if(scanning) {
         //while scanning get smallest ping value and corresponding theta
         int ping = getDistance();
         if(smallestPing > ping) {
            smallestPing = ping;
            targetTheta = odometer.getTheta();
         }
	   }

   }

   public int getDistance() {
      return uSensor.getDistance();
   }
   
   
   public int getFilteredDistance() {
      //median filter
      return mad;
   }

   int mad(int[] dist) {

      Arrays.sort(dist);
      int medianIndex = dist.length/2; //array length assumed > 1
      int median = dist[medianIndex]; //eighth element is the median
      return median;
   }

   public void scan() {
      this.smallestPing = 254;
      this.targetTheta = 45;
      this.scanning = true;
   }
   public int getSmallestPing() {
      this.scanning = false;
      return this.smallestPing;
   }
   public float getTargetTheta() {
      return targetTheta;
   }

}

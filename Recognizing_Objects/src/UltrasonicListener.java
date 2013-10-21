import lejos.nxt.UltrasonicSensor;
import lejos.util.TimerListener;
import java.util.Arrays;
import java.lang.Math;

public class UltrasonicListener implements TimerListener {

   //   public static final int OUTLIER_THESHOLD = 40;
   public static final float b = 1.4826f; //normality data constant
   public UltrasonicSensor uSensor;
   public int[] dist = new int[15];
   public int count = 0;
   public boolean notInitial = false;
   public int mad;

   public UltrasonicListener(UltrasonicSensor uSensor) {

      this.uSensor = uSensor;
      this.uSensor.setMode(UltrasonicSensor.MODE_PING);

   }

   public void timedOut() {

      uSensor.ping();
      count++;
      if (count == 8) {
         uSensor.getDistances(dist,0,8);
         //recompute filtered distance using mad
         if(notInitial)
            mad=mad(dist);
      }
      else if (count == 15) {
         uSensor.getDistances(dist,8,7);
         count = 0;
         //recompute filtered distance using mad
         mad=mad(dist);
         notInitial = true;
      }
   }

   int getDistance() {
      //median absolute deviation
      return mad;
   }


   //possible filters

   //   int standardDeviation(int [] dist)
   //   int absoluteDeviation(int [] dist)
   int mad(int[] dist) {

      Arrays.sort(dist);
      int medianIndex = dist.length/2; //array length assumed > 1
      int median = dist[medianIndex]; //eighth element is the median
      return median;
//      for(int j=0;j<dist.length;j++) {
//         dist[j] = Math.abs(dist[j]-median); //compute deviations from median
//      }
//      Arrays.sort(dist);
//      int mad = (int)(b*dist[medianIndex]); //median of the deviations times normality constant
//      return mad; //filtered distance!!

   }
   //   int mean(int[] dist) {
   //      int sum = 0;
   //      for(int i=0;i<8;i++) {
   //         sum += dist[i]
   //      }
   //      int mean = sum/8;
   //      return mean;
   //   }

}

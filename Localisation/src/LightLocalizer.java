import lejos.nxt.LightSensor;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightSensor ls;
   private Navigation nav;
	
	public LightLocalizer(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
	   this.nav  = odo.getNavigation();	
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
      nav.travelTo(-5,-5);
		// start rotating and clock all 4 gridlines
      int blip = 0; //light sensor detection (should only be triggered by lines)
      double[] position = new double[3];
      double[] theta = new double[4]; //stores the four heading values for blips
      robot.setRotationSpeed(-50); //rotate left
		while(blip<4) { //rotate for 4 "blips"
         if(ls.whatever())
            theta[blip] = Math.toDegrees((odo.getPosition())[2]); //get theta and change to degrees before setting to theta array
            blip++;
      }
      robot.setRotationSpeed(0); //stop
      // do trig to compute (0,0) and 0 degrees
      //compute delta theta x and y
      double thetaY = Math.abs(theta[0]-theta[2]);//theta over y-axis (FOR X CORRECTION)
      double thetaX = Math.abs(theta[1]-theta[3]);//theta over x-axis (FOR Y CORRECTION)
      double thetaC = 270-theta[0]+theta[2]/2;//error in theta
      //apply fomula
      position[0] = -d*Math.sin(thetaY/2);//x computed
      position[1] = -d*Math.cos(thetaX/2);//y computed
      position[2] = position[2] + thetaC;//theta computed
		// when done travel to (0,0) and turn to 0 degrees
      nav.travelTo(0,0);
      nav.turnTo(0);
	}

}

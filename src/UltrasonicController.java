
public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();

   public int leftMotorSpeed();

   public int rightMotorSpeed();
}

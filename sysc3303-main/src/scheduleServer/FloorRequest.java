package scheduleServer;

public class FloorRequest {

	private int floorNum;
	private boolean isUp;
	
	public FloorRequest(int floorNum, boolean isUp) {
		this.floorNum = floorNum;
		this.isUp = isUp;
	}
	
	public int getFloorNum() { return floorNum; }
	
	public boolean isUp() { return isUp; }
}

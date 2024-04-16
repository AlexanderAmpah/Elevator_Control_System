package floorSim;

/**
 * Represents a floor
 * @author Philip Wanczycki
 * @version 0.1
 */
public class Floor {
	
	private int level;
	private boolean upLight;
	private boolean downLight;
	
	/**
	 * Class constructor
	 * @param level Floor level starting from 1 (ground floor)
	 */
	public Floor(int level) {
		this.level = level;
		upLight = false;
		downLight = false;
	}
	
	/** 
	 * enables the up or down light
	 * @param up (true for up, false for down)
	 */
	public void enableLight(boolean up) {
		if (up) {
			upLight = true;
			System.out.println("[FLOOR] Enabling UP light on floor " + level);
		} else {
			downLight = true;
			System.out.println("[FLOOR] Enabling DOWN light on floor " + level);
		}
	}

	/**
	 * disables the up or down light
	 * @param up (true for up, false for down)
	 */
	public void disableLight(boolean up) {
		
		if (up) {
			upLight = false;
			System.out.println("[FLOOR] Disabling UP light on floor " + level);
		} else {
			downLight = false;
			System.out.println("[FLOOR] Disabling DOWN light on floor " + level);
		}
	}
	
	public boolean isUpLightOn() { return upLight; }
	
	public boolean isDownLightOn() { return downLight; }

}
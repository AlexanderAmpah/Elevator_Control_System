package elevatorSim;

/**
 * Represents an elevator, used as context in elevator state pattern implementation
 * @author 
 *
 */
public class Elevator {
	private int level;
	private ElevatorSubsystem subsystem;
	private boolean stop;
	private int[] directionLamps; // indexed by floor, -1 down, 0 off, 1 up
	private boolean[] buttonLamps; // indexed by floor, indicates which floors will be visited
	
	/**
	 * Constructor
	 * @param subsystem
	 */
	public Elevator(ElevatorSubsystem subsystem) {
		this.level = 0;
		this.subsystem = subsystem;
		stop = false;
		
		directionLamps = new int[subsystem.getNumFloors()];
		buttonLamps = new boolean[subsystem.getNumFloors()];
		
		for (int i = 0; i < subsystem.getNumFloors(); ++i) {
			directionLamps[i] = 0;
			buttonLamps[i] = false;
		}
	}

	/**
	 * moves the elevator to the position given
	 * simulates  arrival sensors and the time moving between floors
	 * @param position
	 */
	public void move(int position) {
		// TODO: check position validity
		
		stop = false;
		
		if (position == level) {
			subsystem.handleArrivalSensor();
			return;
		}
		
		int increment = position - level > 0 ? 1 : -1;
		
		while (level != position && !stop) {
			try {	// sleep to simulate movement
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			level += increment;
			subsystem.printState();
			subsystem.handleArrivalSensor();
		}
//		subsystem.changeState(new ArrivalState(subsystem));
	}
	
	public void enableStop() {
		stop = true;
	}
	
	public void disableStop() {
		stop = false;
	}
	
	public int getLevel() {
		return level;
	}

	public boolean isStopped() {
		return stop;
	}
	
	public void enableDirectionLamp(int floor, boolean isUp) {
		if (floor < 0 || floor >= subsystem.getNumFloors()) {
			return;
		}
		
		// Only one direction lamp should be enabled (non-zero)
		disableDirectionLamps();
		
		directionLamps[floor] = (isUp ? 1 : -1);
	}
	
	public void disableDirectionLamps() {
		for (int i = 0; i < subsystem.getNumFloors(); ++i) {
			directionLamps[i] = 0;
		}
	}
	
	public int getDirectionLamp(int floor) {
		if (floor < 0 || floor >= subsystem.getNumFloors()) {
			return 0;
		}
		
		return directionLamps[floor];
	}
	
	public void enableButtonLamp(int floor) {
		if (floor < 0 || floor >= subsystem.getNumFloors()) {
			return;
		}
		
		buttonLamps[floor] = true;
	}
	
	public void disableButtonLamp(int floor) {
		if (floor < 0 || floor >= subsystem.getNumFloors()) {
			return;
		}
		
		buttonLamps[floor] = false;
	}
	
	public boolean getButtonLamp(int floor) {
		if (floor < 0 || floor >= subsystem.getNumFloors()) {
			return false;
		}
		
		return buttonLamps[floor];
	}
}

package elevatorSim;

public abstract class ElevatorState {
	
	// TODO: replace toString methods with enum representation
	public enum States {
		SYNC,
		IDLE,
		SERVING_UP,
		SERVING_DOWN,
		ARRIVED,
	}
	
	protected ElevatorSubsystem context;
	
	// Elevator Subsystem should continually call handleNextRequest(), 
	// proper action will be taken based on current state
	/**
	 * constructor
	 * @param context
	 */
	public ElevatorState(ElevatorSubsystem context) {
		this.context = context;
	}
	
	public static ElevatorState start(ElevatorSubsystem context) {
		return new SyncState(context);
	}
	
	public abstract States getState();
	
	/**
	 * executes action on entry of the state
	 */
	public abstract void onEntry();
	
	/**
	 * executes action on exit of the state
	 */
	public abstract void onExit();
	
	/**
	 * decides the next state based on new task given
	 * @param serveUp
	 */
	public abstract void handleNewTask(boolean serveUp);	// Called by listener thread upon receiving new task
	
	public abstract void handleTimeout();	// Called by timer thread upon completion
	
	/**
	 * checks if a stop is needed
	 * if so, change state to ArrivalState
	 */
	public abstract void handleArrivalSensor();	// Called by Elevator
}

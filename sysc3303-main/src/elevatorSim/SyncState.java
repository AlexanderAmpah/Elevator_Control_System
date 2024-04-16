package elevatorSim;

public class SyncState extends ElevatorState {

	public SyncState(ElevatorSubsystem context) {
		super(context);
	}

	@Override
	public States getState() {
		return ElevatorState.States.SYNC;
	}

	@Override
	public void onEntry() {
		// TODO sync, then change to idle state on success
		boolean success = context.sync();
		
		// if sync was successful, transition to idle state to start the state loop
		if (success) {
			System.out.println("Synced");
			context.changeState(new IdleState(context));
		}
		// if sync failed, let onEntry() return without changing state, 
		// which will let elevatorSubsystem.run() terminate
	}

	@Override
	public void onExit() {
		context.listener();
	}

	@Override
	public void handleNewTask(boolean serveUp) {
		// no change
	}

	@Override
	public void handleTimeout() {
		// no change
	}

	@Override
	public void handleArrivalSensor() {
		// no change
	}

}

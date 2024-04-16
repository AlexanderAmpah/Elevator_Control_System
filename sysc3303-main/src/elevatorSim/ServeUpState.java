package elevatorSim;

import java.net.UnknownHostException;

public class ServeUpState extends ElevatorState {

	public ServeUpState(ElevatorSubsystem context) {
		super(context);
	}

	@Override
	public States getState() {
		return States.SERVING_UP;
	}

	@Override
	public void onEntry() {
		
		context.notifyScheduler(getState());
		context.printState();
		
		int nextFloor = context.getNextDestination(true);
		System.out.println("Moving to " + nextFloor);
		context.moveElevator(nextFloor);
		// elevatorSubsystem.handleArrivalSensor called in elevator's move function
	}

	@Override
	public void onExit() {
		// TODO Auto-generated method stub
		context.requestElevatorButtons(); // maybe this should be after changeState call in handleArrivalSensor?
	}

	@Override
	public void handleNewTask(boolean serveUp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleTimeout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleArrivalSensor() {
		// check elevatorSubsystem if stop is needed (implicitly checks scheduler)
		// if stop is needed, change to arrival state
		context.notifyScheduler(getState());
		if (context.checkFloor()) {
			context.changeState(new ArrivalState(context));
		}
		// otherwise, no change
	}

}


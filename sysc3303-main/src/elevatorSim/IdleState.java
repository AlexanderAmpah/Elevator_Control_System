package elevatorSim;

import java.net.UnknownHostException;

public class IdleState extends ElevatorState {
	
	public IdleState(ElevatorSubsystem context) {
		super(context);
	}
	
	@Override
	public States getState() {
		return ElevatorState.States.IDLE;
	}

	@Override
	public void onEntry() {
		context.notifyScheduler(getState());
		while(!context.hasMoreRequests()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		handleNewTask(true);
	}

	@Override
	public void onExit() {
	}

	@Override
	public void handleNewTask(boolean serveUp) {
		
		
//		int nextFloor = context.getLocalTask();
//		serveUp = context.getCurrentFloor() < nextFloor;
		
		ElevatorState newState = context.getServingDirection() ? 
				new ServeUpState(context) : new ServeDownState(context);
//		if (nextFloor == context.getCurrentFloor()) {
//			newState = new ArrivalState(context);
//		} else {
//			newState = serveUp ? new ServeUpState(context) : new ServeDownState(context);
//		}

		context.changeState(newState);
	}

	@Override
	public void handleTimeout() {
		// no change
		
	}
	
	@Override
	public void handleArrivalSensor() {
		// no change in state
	}

}

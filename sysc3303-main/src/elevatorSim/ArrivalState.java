package elevatorSim;

import java.net.UnknownHostException;

public class ArrivalState extends ElevatorState {
	
	public ArrivalState(ElevatorSubsystem context) {
		super(context);
	}
	
	@Override
	public States getState() {
		return States.ARRIVED;
	}

	@Override
	public void onEntry() {
		
		context.notifyScheduler(getState());
		
		context.enableStop();
		
		context.printState();
		System.out.println("Opening Doors");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		context.printState();
		System.out.println("Unloading passengers");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ElevatorState newState;
		boolean morePass = context.hasMoreRequests();
		// move to Idle or Working depending on work queue
		if (morePass) {
			context.printState();
			System.out.println("Loading passengers");
			newState = context.getServingDirection() ?
					new ServeUpState(context) : new ServeDownState(context);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			context.printState();
			System.out.println("No more passengers");
			newState = new IdleState(context);
		}

		context.printState();
		System.out.println("Closing Doors");
		context.changeState(newState);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onExit() {
		// TODO Auto-generated method stub
		
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
		// no change needed
	}
	
}

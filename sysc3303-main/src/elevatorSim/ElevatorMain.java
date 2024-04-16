package elevatorSim;

import scheduleServer.Scheduler;

public class ElevatorMain {

	public static void main(String args[]){
		Thread elevatorThread1 = new Thread(new ElevatorSubsystem(5));
		Thread elevatorThread2 = new Thread(new ElevatorSubsystem(5));
		Thread elevatorThread3 = new Thread(new ElevatorSubsystem(5));
		Thread elevatorThread4 = new Thread(new ElevatorSubsystem(5));
		
		//have to track request while subsystem is sleeping
		
		elevatorThread1.start();
		elevatorThread2.start();
		elevatorThread3.start();
		elevatorThread4.start();
		
	}
}

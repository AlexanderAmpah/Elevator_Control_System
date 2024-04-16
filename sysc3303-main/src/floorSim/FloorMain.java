package floorSim;

import scheduleServer.Scheduler;

public class FloorMain {
	
	public static void main(String args[]){
		
		
	}
	
	public static void startFloor(int num, Elevator_GUI gui){
		Thread floorSysThread = new Thread(new FloorSubsystem(num, "resources/testinput1.txt", gui));
		floorSysThread.start();
	}
}

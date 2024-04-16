package elevatorSim;

import static org.junit.Assert.*;

import org.junit.Test;

import scheduleServer.Scheduler;

public class ElevatorStateTest {
//	//tests IdleState
//	@Test
//	public void handleNextRequest() {
//		int numFloors = 3;
//		Scheduler scheduler = new Scheduler(numFloors);
//		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
//		ElevatorState state = new IdleState(elevatorSubsystem);
//		
//		assertEquals(state.toString(), "IDLE");
//		
//		scheduler.addFloorRequest(1, false);
//		state.handleNextRequest();
//		
//		assertEquals(elevatorSubsystem.getState(), "WORKING");
//	}
//	
//	//tests WorkingState
//	@Test
//	public void handleNextRequest2() {
//		int numFloors = 3;
//		Scheduler scheduler = new Scheduler(numFloors);
//		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
//		scheduler.addFloorRequest(1, false);
//		elevatorSubsystem.handleNextRequest(); //changes state to WORKING
//		
//		ElevatorState state = new WorkingState(elevatorSubsystem);
//		
//		assertEquals(state.toString(), "WORKING");
//		
//		state.handleNextRequest(); //moves the elevator to the next floor
//				
//		assertEquals(elevatorSubsystem.getState(), "ARRIVING");
//	}
//	
//	//tests ArrivingState transitioning into IdleState
//	@Test
//	public void handleNextRequest3() {
//		int numFloors = 3;
//		Scheduler scheduler = new Scheduler(numFloors);
//		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
//		scheduler.addFloorRequest(1, false);
//		elevatorSubsystem.handleNextRequest(); //changes state to WORKING
//		elevatorSubsystem.handleNextRequest(); //moves the elevator to the next floor
//		
//		assertEquals(elevatorSubsystem.getState(), "ARRIVING");
//		
//		elevatorSubsystem.handleNextRequest();
//		
//		assertEquals(elevatorSubsystem.getState(), "IDLE");
//		
//	}
//	
//	//tests ArrivingState transitioning into WorkingState
//	@Test
//	public void handleNextRequest4() {
//		int numFloors = 3;
//		Scheduler scheduler = new Scheduler(numFloors);
//		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
//		scheduler.addFloorRequest(1, false);
//		scheduler.addFloorRequest(2, false);
//		elevatorSubsystem.handleNextRequest(); //changes state to WORKING
//		elevatorSubsystem.handleNextRequest(); //moves the elevator to the next floor
//		
//		assertEquals(elevatorSubsystem.getState(), "ARRIVING");
//		
//		elevatorSubsystem.handleNextRequest(); //changes state to WORKING	
//		
//		assertEquals(elevatorSubsystem.getState(), "WORKING");
//	}
//	
//	
//	@Test
//	public void handleArrivalSensor() {
//		int numFloors = 3;
//		Scheduler scheduler = new Scheduler(numFloors);
//		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
//		scheduler.addFloorRequest(1, false);
//		elevatorSubsystem.handleNextRequest(); //changes state to WORKING
//				
//		ElevatorState state = new WorkingState(elevatorSubsystem);
//		state.handleNextRequest(); //moves the elevator to the next floor
//						
//		assertEquals(elevatorSubsystem.getState(), "ARRIVING");
//	}
	


}

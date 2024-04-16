package elevatorSim;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.IOException;

import org.junit.Test;

import networking.TestListener;
import scheduleServer.Scheduler;

public class ElevatorSubsystemTest {
	
	/*
	 * Send sync request, receive reply indicating success
	 */
	@Test
	public void testSyncValid() {
		ElevatorSubsystem elev = new ElevatorSubsystem(5);
		
		Thread listenerThread = new Thread(() -> {
			TestListener listener = new TestListener(Scheduler.schedulerListenPort);
			byte[] expectedResponse = {0};
			DataInputStream dataIn = listener.listenAndRespond(expectedResponse);
			
			try {
				assertEquals((byte)0, dataIn.readByte());
				assertEquals((byte)1, dataIn.readByte());
				assertEquals((byte)elev.getID(), dataIn.readByte());
				assertEquals(elev.getListenerPort(), dataIn.readInt());
			} catch (Exception e) {
				e.printStackTrace();
			}
			elev.kill();
		});
		
		listenerThread.start();
		
		boolean success = elev.sync();
		
		assertTrue(success);
	}
	
	/*
	 * Send sync request, receive reply indicating failure
	 */
	@Test
	public void testSyncInvalid() {
		ElevatorSubsystem elev = new ElevatorSubsystem(5);
		
		Thread listenerThread = new Thread(() -> {
			TestListener listener = new TestListener(Scheduler.schedulerListenPort);
			byte[] expectedResponse = {1};
			DataInputStream dataIn = listener.listenAndRespond(expectedResponse);
			
			try {
				assertEquals((byte)0, dataIn.readByte());
				assertEquals((byte)1, dataIn.readByte());
				assertEquals((byte)elev.getID(), dataIn.readByte());
				assertEquals(elev.getListenerPort(), dataIn.readInt());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			elev.kill();
		});
		
		listenerThread.start();
		
		boolean success = elev.sync();
		
		assertFalse(success);
	}
	
	/*
	 * Add a floor request to the queue
	 *
	@Test
	public void addFloorRequest() {
		int numFloors = 3;
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		int floorNum = 0;
		boolean response = elevatorSubsystem.addFloorRequest(floorNum);
		assertTrue(response);
	}
	
	/*
	 * Check approaching floor for requests
	 *
	@Test
	public void checkFloor() {
		int numFloors = 3;
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		int floorNum = 0;
		boolean response = elevatorSubsystem.addFloorRequest(floorNum);
		assertTrue(response);
		try {
			response = elevatorSubsystem.checkFloor();
			assertTrue(response);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void checkFloor2() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		int floorNum = 0;
		scheduler.addFloorRequest(floorNum, true);
		boolean response;
		try {
			response = elevatorSubsystem.checkFloor();
			assertTrue(response);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void checkFloor3() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		int floorNum = 0;
		scheduler.addFloorRequest(floorNum, false);
		elevatorSubsystem.moveElevator(1);
		boolean response;
		try {
			response = elevatorSubsystem.checkFloor();
			assertFalse(response);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void arrivedAtFloor() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		int floorNum = 0;
		elevatorSubsystem.addFloorRequest(floorNum);
		boolean response;
		try {
			response = elevatorSubsystem.arrivedAtFloor();
			assertTrue(response);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			response = elevatorSubsystem.checkFloor();
			assertFalse(response);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * The problem with this test is how we make sure we
	 * send the right response to scheduler with the dir
	 * our elevator is heading
	 * 
	 * case:
	 * elevator is going from floor 0 to floor 2
	 * the scheduler has a request to go up and down on 
	 * floor 1.
	 * the elevator should accept the up request but
	 * not the down request
	 * 
	 * write an extension to this function that determines
	 * the direction the elevator is heading and updates
	 * the scheduler respectively
	 *
	@Test
	public void arrivedAtFloor2() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		int floorNum = 0;
		scheduler.addFloorRequest(floorNum, true);
		boolean response;
		try {
			response = elevatorSubsystem.arrivedAtFloor();
			assertTrue(response);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			response = elevatorSubsystem.checkFloor();
			assertFalse(response);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void handleIdleState() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		
		assertEquals(elevatorSubsystem.getState(), "IDLE");
		elevatorSubsystem.handleNextRequest();
		assertEquals(elevatorSubsystem.getState(), "IDLE");
		
		scheduler.addFloorRequest(2, false);
		elevatorSubsystem.handleNextRequest();
		assertEquals(elevatorSubsystem.getState(), "WORKING");
	}
	
	@Test
	public void handleWorkingState() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		
		scheduler.addFloorRequest(2, false);
		elevatorSubsystem.handleNextRequest();
		assertEquals(elevatorSubsystem.getState(), "WORKING");
		
		elevatorSubsystem.handleNextRequest();
		// wait for elevator to get to requested floor, idk the timing
		assertEquals(elevatorSubsystem.getState(), "ARRIVING");
		
	}
	
	@Test
	public void handleArrivalState() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		int floorNum = 0;
		
		scheduler.addFloorRequest(2, false);
		scheduler.addFloorRequest(1, false);
		elevatorSubsystem.handleNextRequest(); // change to working
		elevatorSubsystem.handleNextRequest(); // change move to next stop
		
		assertEquals(elevatorSubsystem.getState(), "ARRIVING");
		
		elevatorSubsystem.handleNextRequest(); // "open doors", then change to working again
		
		assertEquals(elevatorSubsystem.getState(), "WORKING");
		
		elevatorSubsystem.handleNextRequest(); // move to next stop
		
		assertEquals(elevatorSubsystem.getState(), "ARRIVING");
		
		elevatorSubsystem.handleNextRequest(); // "open doors", then back to idle
		
		assertEquals(elevatorSubsystem.getState(), "IDLE");
	}
	
	@Test
	public void moveElevator() {
		ElevatorSubsystem eSubsystem = new ElevatorSubsystem(5, 1);
		Elevator e = new Elevator(eSubsystem);
		eSubsystem.setElevator(e);
		
		eSubsystem.moveElevator(1);
		
		assertEquals(e.getLevel(), 1);
	}
	
	@Test
	public void hasMoreRequests() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		
		assertFalse(elevatorSubsystem.hasMoreRequests());
		
		elevatorSubsystem.addFloorRequest(2);

		assertTrue(elevatorSubsystem.hasMoreRequests());
	}
	
	@Test
	public void hasMoreRequests2() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, 1);
		
		assertFalse(elevatorSubsystem.hasMoreRequests());
		
		elevatorSubsystem.addFloorRequest(1);
		
		assertTrue(elevatorSubsystem.hasMoreRequests());
		
		elevatorSubsystem.moveElevator(1);
		try {
			elevatorSubsystem.arrivedAtFloor();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertFalse(elevatorSubsystem.hasMoreRequests());
	}
	
	*/
	
	//TODO: test for WORKING to ARRIVING when already at floor
	
}

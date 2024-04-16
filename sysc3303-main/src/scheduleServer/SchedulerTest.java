package scheduleServer;

import static org.junit.Assert.*;
import org.junit.Test;

public class SchedulerTest {

	/* 
	 * add a single floor request and returns true
	 * if it was added correctly
	 * 
	 * floorNum = floor number
	 * up = true is up false is down
	 * 
	 *
	@Test
	public void addFloorRequest() {
		int numFloors = 2;
		int floorNum = 0;
		boolean up = true;
		Scheduler scheduler = new Scheduler(numFloors);
		boolean response = scheduler.addFloorRequest(floorNum, up);
		assertTrue(response);
	}
	
	/*
	 * add a single floor and check that floor to
	 * make sure it was added correctly
	 *
	@Test
	public void checkFloor() {
		int numFloors = 2;
		Scheduler scheduler = new Scheduler(numFloors);
		int floorNum = 0;
		boolean up = true;
		boolean response = scheduler.addFloorRequest(floorNum, up);
		assertTrue(response);
		response = scheduler.checkFloor(floorNum, up);
		assertTrue(response);
	}
	
	@Test
	public void checkFloorErr() {
		int numFloors = 2;
		Scheduler scheduler = new Scheduler(numFloors);
		int floorNum = 0;
		boolean up = true;
		boolean response = scheduler.addFloorRequest(floorNum, up);
		assertTrue(response);
		response = scheduler.checkFloor(floorNum, false);
		assertFalse(response);
	}
	
	@Test
	public void checkFloorDirectionless() {
		int numFloors = 2;
		Scheduler scheduler = new Scheduler(numFloors);
		boolean up = true;
		// check up
		boolean response = scheduler.addFloorRequest(0, up);
		assertTrue(response);
		response = scheduler.checkFloor(0);
		assertTrue(response);
		
		//check error
		response = scheduler.checkFloor(1);
		assertFalse(response);
		
		//check down		
		response = scheduler.addFloorRequest(1, !up);
		assertTrue(response);
		response = scheduler.checkFloor(1);
		assertTrue(response);
	}
	
	/*
	 * add multiple floor requests in different
	 * directions to see if they were added correctly
	 *
	@Test
	public void addFloorRequest2() {
		int numFloors = 4;
		Scheduler scheduler = new Scheduler(numFloors);
		boolean up = true;
		boolean down = false;
		boolean response = scheduler.addFloorRequest(0, up);
		assertTrue(response);
		response = scheduler.addFloorRequest(3, down);
		assertTrue(response);
		response = scheduler.addFloorRequest(3, up);
		assertFalse(response);
		response = scheduler.addFloorRequest(0, down);
		assertFalse(response);
	}
	
	/*
	 * Get no tasks
	 *
	@Test
	public void getNextTask4() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		int currFloorNum = 0;
		int response = scheduler.getNextTask(currFloorNum);
		assertEquals(response , -1);
	}
	
	/*
	 * Get the farthest down request
	 *
	@Test 
	public void getNextTask1() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		scheduler.addFloorRequest(2, false);
		int currFloorNum = 0;
		int response = scheduler.getNextTask(currFloorNum);
		assertEquals(response, 2);
	}
	
	/*
	 * Get the farthest up request
	 *
	@Test
	public void getNextTask2() {
		int numFloors = 3;
		Scheduler scheduler = new Scheduler(numFloors);
		scheduler.addFloorRequest(0, true);
		int currFloorNum = 2;
		int response = scheduler.getNextTask(currFloorNum);
		assertEquals(response, 0);
	}
	
	/*
	 * Get closest task when multiple tasks are available
	 *
	@Test
	public void getNextTask5() {
		int numFloors = 4;
		Scheduler scheduler = new Scheduler(numFloors);
		int currFloor = 2;
		scheduler.addFloorRequest(3, false);
		scheduler.addFloorRequest(0, true);
		int nextTask = scheduler.getNextTask(currFloor);
		assertEquals(nextTask, 3);
	}
	
	@Test
	public void getNextTask6() {
		int numFloors = 5;
		Scheduler scheduler = new Scheduler(numFloors);
		int currFloor = 2;
		scheduler.addFloorRequest(4, false);
		scheduler.addFloorRequest(0, true);
		int nextTask = scheduler.getNextTask(currFloor);
		assertEquals(nextTask, 4);
	}

	
	/*
	 * Equal distance up and down requests
	 * Get closest floor in the middle of a large system
	 *
	@Test
	public void getNextTask3() {
		int numFloors = 10;
		Scheduler scheduler = new Scheduler(numFloors);
		scheduler.addFloorRequest(6, true);
		scheduler.addFloorRequest(9, false);
		int currFloor = 7;
		int nextTask = scheduler.getNextTask(currFloor);
		assertEquals(nextTask, 6);
	}
	*/
	
	@Test
	public void getNumFloors() {
		int numFloors = 5;
		Scheduler scheduler = new Scheduler(numFloors);
		int response = scheduler.getNumFloors();
		assertEquals(numFloors, response);
	}
	
	/*
	@Test
	public void checkFulfilled() {
		int numFloors = 10;
		Scheduler scheduler = new Scheduler(numFloors);
		scheduler.addFloorRequest(6, true);
		scheduler.addFloorRequest(6, false);
		scheduler.addFloorRequest(9, false);
		
		boolean response = scheduler.checkFulfilled(6);
		assertTrue(response);
		assertFalse(scheduler.checkFloor(6));
		assertTrue(scheduler.checkFloor(9));
		
		response = scheduler.checkFulfilled(9);
		assertFalse(scheduler.checkFloor(9));
	}
	*/
	
	// === TASK DISPATCHER TESTS ===
	
	@Test
	public void testDispatcherEnqueueTask() {
		
		Scheduler scheduler = new Scheduler(5);
		TaskDispatcher dispatcher = new TaskDispatcher(scheduler);
		
		boolean result = dispatcher.enqueueTask(new FloorRequest(1, true));
		
		assertTrue(result);
	}
	
	@Test
	public void testDispatcherGetElevatorPort() {
		
		Scheduler scheduler = new Scheduler(5);
		TaskDispatcher dispatcher = new TaskDispatcher(scheduler);
		
		// add idle elevator to scheduler
		
		int port = -1;
		// Loops until elevator is found
//		port = dispatcher.getEligibleElevatorPort(floorNum, isUp);
		
		assertNotEquals(port, -1);
		
	}
	
	@Test
	public void testDispatcherRun() {
		fail("not yet implemented");
	}
	
	/*
	 *  assign a floor 3 going up task
	 *  only one elevator moving up
	 */
	@Test
	public void assignTaskToElevator1() {
		Scheduler s = new Scheduler(5);
		
		//add elevator (elevatorNum=1, port=5)
		s.handleElevatorSync((byte)0, 5);
		//move the elevator to floor 1 and change to ServeUpState
		s.handleElevatorUpdate((byte)0, (byte)1, (byte)3);
		
		//assign a task for floor 3 going up
		int port = s.assignTaskToElevator(3, true);
		assertEquals(5, port);

	}
	
	/*
	 *  assign a floor 2 going down task
	 *  only one elevator moving down
	 */
	@Test
	public void assignTaskToElevator2() {
		Scheduler s = new Scheduler(5);
		
		//add elevator (elevatorNum=1, port=5)
		s.handleElevatorSync((byte)0, 5);
		//move the elevator to floor 4 and change to ServeDownState
		s.handleElevatorUpdate((byte)0, (byte)4, (byte)2);
		
		//assign a task for floor 2 going down
		int port = s.assignTaskToElevator(2, false);
		assertEquals(5, port);

	}
	
	/*
	 *  assign a floor 2 going up task
	 *  only one elevator is idle
	 */
	@Test
	public void assignTaskToElevator3() {
		Scheduler s = new Scheduler(5);
		
		//add elevator (elevatorNum=1, port=5)
		s.handleElevatorSync((byte)0, 5);
		//move the elevator to floor 4 and change to Idle
		s.handleElevatorUpdate((byte)0, (byte)4, (byte)0);
		
		//assign a task for floor 2 going up
		int port = s.assignTaskToElevator(2, true);
		assertEquals(5, port);
	}
	
	/*
	 *  assign a floor 3 going up task
	 *  multiple elevators going up and an idle elevator
	 */
	@Test
	public void assignTaskToElevator4() {
		Scheduler s = new Scheduler(8);
		
		//add elevators 
		s.handleElevatorSync((byte)0, 5);
		s.handleElevatorSync((byte)1, 6);
		s.handleElevatorSync((byte)2, 7);
		s.handleElevatorSync((byte)3, 8);
		
		//move the elevators
		s.handleElevatorUpdate((byte)0, (byte)4, (byte)3); //floor 4 going up
		s.handleElevatorUpdate((byte)1, (byte)1, (byte)3); //floor 1 going up
		s.handleElevatorUpdate((byte)2, (byte)2, (byte)3); //floor 2 going up
		s.handleElevatorUpdate((byte)3, (byte)1, (byte)0); //floor 1 idle
		
		//assign a task for floor 3 going up
		int port = s.assignTaskToElevator(3, true);
		assertEquals(7, port);
	}
	
	/*
	 *  assign a floor 3 going down
	 *  multiple elevators going up, down, and idle 
	 */
	@Test
	public void assignTaskToElevator5() {
		Scheduler s = new Scheduler(8);
		
		//add elevators 
		s.handleElevatorSync((byte)0, 5);
		s.handleElevatorSync((byte)1, 6);
		s.handleElevatorSync((byte)2, 7);
		s.handleElevatorSync((byte)3, 8);
		s.handleElevatorSync((byte)4, 9);
		
		//move the elevators
		s.handleElevatorUpdate((byte)0, (byte)2, (byte)3); //floor 2 going up
		s.handleElevatorUpdate((byte)1, (byte)2, (byte)2); //floor 2 going down
		s.handleElevatorUpdate((byte)2, (byte)4, (byte)2); //floor 4 going down
		s.handleElevatorUpdate((byte)3, (byte)2, (byte)0); //floor 2 idle
		s.handleElevatorUpdate((byte)3, (byte)5, (byte)0); //floor 5 idle
		
		//assign a task for floor 3 going down
		int port = s.assignTaskToElevator(3, false);
		assertEquals(7, port);
	}
	
	/*
	 *  assign a floor 4 going down
	 *  multiple elevators going up, and down
	 *  none are available for the task 
	 */
	@Test
	public void assignTaskToElevator6() {
		Scheduler s = new Scheduler(8);
		
		//add elevators 
		s.handleElevatorSync((byte)0, 5);
		s.handleElevatorSync((byte)1, 6);
		s.handleElevatorSync((byte)2, 7);
		
		//move the elevators
		s.handleElevatorUpdate((byte)0, (byte)2, (byte)2); //floor 2 going down
		s.handleElevatorUpdate((byte)1, (byte)3, (byte)2); //floor 3 going down
		s.handleElevatorUpdate((byte)2, (byte)3, (byte)3); //floor 3 going up
		
		//assign a task for floor 5 going down
		int port = s.assignTaskToElevator(5, false);
		assertEquals(-1, port);
	}
	
	
	// TODO: test for stranded request far away from busy elevator
}

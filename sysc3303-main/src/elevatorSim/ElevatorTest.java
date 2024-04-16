package elevatorSim;

import static org.junit.Assert.*;

import org.junit.Test;

import scheduleServer.Scheduler;

public class ElevatorTest {

	
	@Test
	public void move() {
		ElevatorSubsystem controller = new ElevatorSubsystem(5);
		Elevator e = new Elevator(controller);
		controller.setElevator(e);
		
		e.move(1);	// move up
		assertEquals(1, e.getLevel());
		
		e.move(0);	// move down
		assertEquals(0, e.getLevel());
	}
	
	// TODO: test move for invalid floors
	
	@Test
	public void enableStop() {
		ElevatorSubsystem controller = new ElevatorSubsystem(5);
		Elevator e = new Elevator(controller);
		controller.setElevator(e);
		
		e.enableStop();
		assertTrue(e.isStopped());
		
		// test if enableStop() stops elevator movement
	}
	
	@Test
	public void directionLampsValidFloors() {
		ElevatorSubsystem controller = new ElevatorSubsystem(5);
		Elevator e = new Elevator(controller);
		
		assertEquals(0, e.getDirectionLamp(0));
		assertEquals(0, e.getDirectionLamp(4));
		
		// enable up lamp at 1st floor
		e.enableDirectionLamp(0, true);
		assertEquals(1, e.getDirectionLamp(0));
		
		// enable down lamp at 5th floor
		e.enableDirectionLamp(4, false);
		assertEquals(0, e.getDirectionLamp(0));	// 1st floor lamp should be disabled
		assertEquals(-1, e.getDirectionLamp(4));
		
		// disable all lamps
		e.disableDirectionLamps();
		assertEquals(0, e.getDirectionLamp(4));
	}
	
	@Test
	public void directionLampsInvalidFloors() {
		ElevatorSubsystem controller = new ElevatorSubsystem(5);
		Elevator e = new Elevator(controller);
		
		// getter for invalid floor should return default value of 0 (off)
		assertEquals(0, e.getDirectionLamp(-1));
		assertEquals(0, e.getDirectionLamp(5));
		
		// enabling up/down lamps at invalid floors shouldn't change anything
		e.enableDirectionLamp(-1, true);
		assertEquals(0, e.getDirectionLamp(-1));
		
		e.enableDirectionLamp(5, false);
		assertEquals(0, e.getDirectionLamp(5));
	}
	
	@Test
	public void buttonLampValidFloors() {
		ElevatorSubsystem controller = new ElevatorSubsystem(5);
		Elevator e = new Elevator(controller);
		
		assertFalse(e.getButtonLamp(0));
		assertFalse(e.getButtonLamp(4));
		
		// enable elevator button lamps for bottom and top floors
		e.enableButtonLamp(0);
		e.enableButtonLamp(4);
		
		assertTrue(e.getButtonLamp(0));
		assertTrue(e.getButtonLamp(4));
		
		// disable same elevator buttons
		e.disableButtonLamp(0);
		e.disableButtonLamp(4);
		
		assertFalse(e.getButtonLamp(0));
		assertFalse(e.getButtonLamp(4));
	}
	
	@Test
	public void buttonLampInvalidFloors() {
		ElevatorSubsystem controller = new ElevatorSubsystem(5);
		Elevator e = new Elevator(controller);
		
		// getter for invalid floors should return default value of false (off)
		assertFalse(e.getButtonLamp(-1));
		assertFalse(e.getButtonLamp(5));
		
		// enabling for invalid floors shouldn't change anything
		e.enableButtonLamp(-1);
		e.enableButtonLamp(5);
		
		assertFalse(e.getButtonLamp(-1));
		assertFalse(e.getButtonLamp(5));
		
		// disabling for invalid floors shouldn't change anything
		e.disableButtonLamp(-1);
		e.disableButtonLamp(5);
		
		assertFalse(e.getButtonLamp(-1));
		assertFalse(e.getButtonLamp(5));
	}
}

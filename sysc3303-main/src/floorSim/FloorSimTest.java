package floorSim;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

import networking.TestListener;
import scheduleServer.Scheduler;

public class FloorSimTest {
	Elevator_GUI gui = new Elevator_GUI();
	@Test
	public void testFloorSubsystemInput() {
		
		FloorSubsystem floorSys = new FloorSubsystem(5, "resources/testinput1.txt", gui);
		
		PriorityQueue<String[]> events = floorSys.getEvents("resources/testinput1.txt");
		int size = events.size();
		assertTrue(size != 0);
		for (int i = 0; i < size; ++i) {
			String[] inputLine = events.poll();
			
			assertTrue(inputLine[0].matches("^\\d\\d:\\d\\d:\\d\\d\\.\\d+"));
			assertTrue(inputLine[1].matches("^\\d+"));
			assertTrue(inputLine[2].matches("^((Up)|(Down))"));
			assertTrue(inputLine[3].matches("^\\d+"));
		}
	}
	
	@Test
	public void testEnableDisableFloorLights() {
		Elevator_GUI gui = new Elevator_GUI();
		FloorSubsystem floorSys = new FloorSubsystem(5, "resources/testinput1.txt", gui);
		
		Floor floor = floorSys.getFloor(3);
		floor.enableLight(false);
		floor.enableLight(true);
		
		assertTrue(floor.isDownLightOn());
		assertTrue(floor.isUpLightOn());
		
		floor.disableLight(false);
		floor.disableLight(true);
		
		assertFalse(floor.isDownLightOn());
		assertFalse(floor.isUpLightOn());
	}
	
	@Test
	public void testCorrectFloorNum() {
		FloorSubsystem floorSys = new FloorSubsystem(5, "resources/testinput1.txt", gui);

		assertEquals(floorSys.getNumFloors(), 5);
	}
	
	@Test
	public void testHandleElevatorArrivalValid() {
		FloorSubsystem floorSys = new FloorSubsystem(5, "resources/testinput1.txt", gui);
		
		Floor floor = floorSys.getFloor(3);
		floor.enableLight(false);
		
		assertTrue(floor.isDownLightOn());
		
		assertTrue(floorSys.handleElevatorArrival(3, false));
		
		assertFalse(floor.isDownLightOn());
	}
	
	@Test
	public void testHandleElevatorArrivalInvalidFloor() {
		FloorSubsystem floorSys = new FloorSubsystem(5, "resources/testinput1.txt", gui);		
		assertFalse(floorSys.handleElevatorArrival(-1, true));
		assertFalse(floorSys.handleElevatorArrival(5, true));
	}
	
	@Test
	public void testFloorSysRun() {
		FloorSubsystem floorSys = new FloorSubsystem(5, "resources/testinput1.txt", gui);		
		Thread listenerThread = new Thread(() -> {
			TestListener listener = new TestListener(Scheduler.schedulerListenPort);
			byte[] expectedResponse = {0,0};
			DataInputStream dataIn = listener.listenAndRespond(expectedResponse);
			
			try {
				assertEquals((byte)0, dataIn.readByte());
				assertEquals((byte)0, dataIn.readByte());
				assertEquals((byte)2, dataIn.readByte());
				assertEquals((byte)4, dataIn.readByte());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// {valid, msgType, arrivedAtFloor, servingDirection}
			byte[] msg2 = {0,0,2,1};
			listener.asyncSend(msg2, FloorSubsystem.floorAddrName, FloorSubsystem.floorListenerPort);
			
			floorSys.kill();
		});
		
		listenerThread.start();
		
		floorSys.run();
	}

}
package elevatorSim;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import elevatorSim.ElevatorState.States;
import networking.RPCSender;
import scheduleServer.Scheduler;

public class ElevatorSubsystem extends RPCSender implements Runnable{
	
	public final static String elevatorAddrName = "localhost";
	private final static int elevatorBasePort = 11002;
	private int elevatorListenerPort;
	private DatagramSocket listener;
	
	private static int staticID = 0;
	
	private Elevator elevator;
	private int numFloors;
	private byte[] floorRequests; 	// requests from floor up/down buttons
									// 0 - none at this floor, 1 - up, 2 - down, 3 - both directions
	private boolean[] elevatorRequests;	// requests from in-elevator buttons
	private ElevatorState currState;
	private boolean running;
	private int id;
	
	/**
	 * 
	 * @param numFloors
	 */
	public ElevatorSubsystem(int numFloors) {
		setElevator(new Elevator(this));
		this.numFloors = numFloors;
		this.floorRequests = new byte[numFloors];
		this.elevatorRequests = new boolean[numFloors];
		this.listener = null;
		for (int i = 0; i < numFloors; i++) {
			this.floorRequests[i] = 0;
			this.elevatorRequests[i] = false;
		}
		//currState = new IdleState(this);
		
		running = true;
		
		this.id = ElevatorSubsystem.getNextID();
		elevatorListenerPort = elevatorBasePort + id;
	}
	
	private static int getNextID() {
		int id = staticID;
		staticID++;
		return id; 
	}
	
	/**
	 * transitions state and carries out exit and entry actions
	 * @param state
	 */
	public void changeState(ElevatorState state) {
		if (currState != null) {
			currState.onExit();
		}
		currState = state;
		currState.onEntry();
	}
	
	/**
	 * adds a floor task to the up requests or down requests floor list
	 * @param floorNum
	 * @param isUp (true for up, false for down)
	 * @return success
	 */
	public boolean addFloorRequest(int floorNum, boolean isUp) {
		if (floorNum < 0 || floorNum >= getNumFloors()) {
			return false;
		}
		
		byte directionToAdd = isUp ? (byte)1 : (byte)2;
		
		if (floorRequests[floorNum] == (byte) 3 || floorRequests[floorNum] == directionToAdd) {
			return false;	// direction already present at this index, fail to add
		} else {
			this.floorRequests[floorNum] += directionToAdd;
			return true;
		}
	}
	
	/**
	 * adds an elevator request to the elevator requests list
	 * @param floorNum
	 * @return success
	 */
	public boolean addElevatorRequest(int floorNum) {
		if (floorNum < 0 || floorNum >= getNumFloors()) {
			return false;
		}
		
		if (!this.elevatorRequests[floorNum]) {
			this.elevatorRequests[floorNum] = true;
		}
		return true;
	}
	
	/**
	 * gets the direction the elevator should travel to get to its destination
	 * @return boolean value for the direction
	 */
	public boolean getServingDirection() {
		// if we have passengers to drop off, get direction from their destination
		for (int i = 0; i < numFloors; ++i) {
			if (elevatorRequests[i]) {
				return (i > elevator.getLevel());
			}
		}
		
		// if no passengers, check down floorRequests, then up floorRequests		
		for (int i = floorRequests.length-1; i >= 0; --i) {
			if (floorRequests[i] == (byte) 2 || floorRequests[i] == (byte) 3)
				return false;
		}
		
		for (int i = 0; i < floorRequests.length; ++i) {
			if (floorRequests[i] == (byte) 1 || floorRequests[i] == (byte) 3)
				return true;
		}
		
		// return down by default
		return false;
	}
	
	/**
	 * gets the floor of the closest destination of the elevator
	 * @param servingUp (true for up, false for down)
	 * @return int of the closest floor
	 */
	public int getNextDestination(boolean servingUp) {
		int closestFloor = -1;
		
		if (servingUp) {
			// check elevatorRequests above current level
			for (int i = elevator.getLevel(); i < numFloors; ++i) {
				if (elevatorRequests[i]) {
					closestFloor = i;
					break;
				}
			}
			
			// check up floorRequests closest to the bottom (or above current level if we have elevatorRequests)
			int startIndex = (closestFloor == -1 ? 0 : elevator.getLevel());
			int endIndex = (closestFloor == -1 ? numFloors : closestFloor);
			for (int i = startIndex; i < endIndex; ++i) {
				if (floorRequests[i] == (byte)1 || floorRequests[i] == (byte)3) {
					closestFloor = i;
					break;
				}
			}
		} else {
			// check elevatorRequests below current level
			for (int i = elevator.getLevel(); i >= 0; --i) {
				if (elevatorRequests[i]) {
					closestFloor = i;
					break;
				}
			}
			
			// check down floorRequests closest to the top (or below current level if we have elevatorRequests)
			int startIndex = (closestFloor == -1 ? numFloors-1 : elevator.getLevel());
			int endIndex = (closestFloor == -1 ? 0 : closestFloor);
			for (int i = startIndex; i >= endIndex; --i) {
				if (floorRequests[i] == (byte)2 || floorRequests[i] == (byte)3) {
					closestFloor = i;
					break;
				}
			}
		}
		
		return closestFloor;
	}

	/**
	 * checks whether the elevator has a floor request or elevator request at its current floor
	 * @return boolean of if it should serve the floor
	 */
	public boolean checkFloor(){
		byte direction = (currState.getState() == States.SERVING_UP ? (byte) 1 : (byte) 2);
		return (floorRequests[elevator.getLevel()] == (byte)3 ||
				floorRequests[elevator.getLevel()] == direction ||
				elevatorRequests[elevator.getLevel()]);
	}
	
	/**
	 * updates the floor request of the current floor to complete 
	 * requests new floor requests from the scheduler and adds those to the floor requests list
	 */
	public void requestElevatorButtons(){
		int floorNum = elevator.getLevel();
		byte direction;
		if (currState.getState() == States.SERVING_UP) {
			direction = (byte) 1;
		}
		else {
			direction = (byte) 0;
		}
		
		if (floorNum < 0 || floorNum >= getNumFloors()) {
			return;
		}
		
		// Remove up or down requests for this floor
		if (floorRequests[floorNum] != (byte) 0) {
			if (direction == (byte) 1) {
				this.floorRequests[floorNum] -= (byte)1;
			} else {
				floorRequests[floorNum] -= (byte)2;
			}
		}
		
		// Remove in-elevator requests for this floor
		if (this.elevatorRequests[floorNum]) {
			this.elevatorRequests[floorNum] = false;
		}
		
		// 0,3 for fulfillTask
		byte[] taskRequest = {0,3, (byte) this.id, (byte)floorNum, direction};
		byte[] response = new byte[100];
		InetAddress destination;
		try {
			destination = InetAddress.getByName(Scheduler.schedulerAddrName);
			rpcSend(taskRequest, response, Scheduler.schedulerListenPort, destination, "ELEVATOR"+id);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		if (response[0] == 0) {
			//TODO: revise this??? Had a print funciton but was redundnat here
		}
		
		//second byte of response is number of new requests to add, subsequent bytes are floornums
		if (response[1] != -1) { // check that there are requests to add
			// add new requests to elevatorRequests
			for(int i=2; i< response[1] + 2; i++) {
				System.out.println("Adding new elevator request at " + response[i]);
				addElevatorRequest(response[i]);
			}
		}
		
	}
	
	
	
	public void handleNewTask(boolean isUp){
		if (currState == null) return;
		currState.handleNewTask(isUp);
	}
	
	public void handleArrivalSensor() {
		if (currState == null) return;
		
		currState.handleArrivalSensor();
	}
	
	public void moveElevator(int floor) {
		if (floor >= 0 && floor < getNumFloors()) {
			elevator.move(floor);
		}
	}
	
	public int getCurrentFloor() {
		return elevator.getLevel();
	}
	
	public int getID() {
		return id;
	}
	
	public int getListenerPort() {
		return elevatorListenerPort;
	}
	
	public int getNumFloors() {
		return numFloors;
	}
	
	/**
	 * checks if the elevator has any more floor or elevator requests
	 * @return boolean value of if it has more requests
	 */
	public boolean hasMoreRequests() {
		for (byte floor: floorRequests) {
			if (floor != (byte)0)
				return true;
		}
		for (boolean floor: elevatorRequests) {
			if (floor)
				return true;
		}
		return false;
	}
	
	public void enableStop() {
		elevator.enableStop();
	}
	
	public void setElevator(Elevator newElevator) {
		elevator = newElevator;
	}
	
	public void printState() {
		if (currState == null) {
			System.out.println("ERROR: elevator " + id + " state is null");
			return;
		}
		
		System.out.println("[ELEV " + this.id + " ]" + " pos: " + this.elevator.getLevel());
	}

	public void kill() {
		this.running = false;
	}
	
	/**
	 * updates the scheduler with the elevators current status
	 * @param stateEnum
	 */
	public void notifyScheduler(States stateEnum) {
		int state = 0;
		if(currState.getState() == States.IDLE) {
			state = 0;
		}
		if(currState.getState() == States.ARRIVED) {
			state = 1;
		}
		if(currState.getState() == States.SERVING_DOWN) {
			state = 2;
		}
		if(currState.getState() == States.SERVING_UP) {
			state = 3;
		}
		byte[] taskRequest = {0,2, (byte)this.id,(byte)elevator.getLevel(), (byte) state};
		byte[] response = new byte[100];
		System.out.println("Elevator: "+this.id+" notifying scheduler at floor: "+(elevator.getLevel())+" state: "+currState.getState().toString());
		InetAddress destination;
		try {
			destination = InetAddress.getByName(Scheduler.schedulerAddrName);
			rpcSend(taskRequest, response, Scheduler.schedulerListenPort, destination, "ELEVATOR"+id);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		//TODO: handle response
		
	}
	

	/**
	 *  Registers the elevator with the scheduler, sending its id and task listening port 
	 * @return success
	 */
	public boolean sync() {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOut);
		
		try {
			dataOut.writeByte(0);	// message start
			dataOut.writeByte(1);	// message type (sync req)
			dataOut.writeByte(id);	// elevator id
			dataOut.writeInt(elevatorListenerPort);	// listener port
			dataOut.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		byte[] syncRequest = byteOut.toByteArray();
		byte[] response = new byte[100];
		System.out.println("Elevator: "+this.id+" syncing with scheduler");
		InetAddress destination;
		try {
			destination = InetAddress.getByName(Scheduler.schedulerAddrName);
			rpcSend(syncRequest, response, Scheduler.schedulerListenPort, destination, "ELEVATOR"+id);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return response[0] == (byte) 0;
	}
	
	/**
	 * runs a thread to receive floor requests from the dispatcher
	 */
	public void listener() {
		Thread thread = new Thread(){
		    public void run(){
		    	System.out.println("listener alive, binding to port " + elevatorListenerPort);
				try {
					listener = new DatagramSocket(elevatorListenerPort);
				} catch (SocketException e) {
					e.printStackTrace();
				}
				DatagramPacket p;
				byte[] buff = null;
				byte[] resp = null;
		    	while (running) {
		            buff = new byte[100];

		            p = new DatagramPacket(buff, buff.length);

		            try {
						listener.receive(p);
					} catch (IOException e) {
						System.out.print("[ERROR] ");
						e.printStackTrace();
						listener.close();
						System.exit(1);
					}

		            buff = p.getData();
		            resp = new byte[2];

		            if (buff[0] != 0) {
		                System.out.println("INVALID REQUEST");
		                resp[0] = -1;
		            }
		            


		            if (buff[0] == 0 && buff[1] == 1) {
		                System.out.println("VALID REQUEST");
		                addFloorRequest(buff[2], (buff[3] == (byte) 1));
		                resp[0] = 0;
		            }
		            
		            p = new DatagramPacket(resp, resp.length, p.getAddress(), p.getPort());
		            try {
						listener.send(p);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

		    	}
		    	System.out.println("listener is dead");
		    }
		};
		thread.start();
	}
	
	@Override
	public void run() {
		printState();
		System.out.println("System is alive");
		changeState(new SyncState(this));
		
	}

}

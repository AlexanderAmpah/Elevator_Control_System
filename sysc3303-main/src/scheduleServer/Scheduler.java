package scheduleServer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import java.util.*;

import floorSim.FloorSubsystem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Scheduler implements Runnable{
	
	public final static String schedulerAddrName = "localhost";
	public final static int schedulerListenPort = 11001;
	
	private int numFloors;
	private boolean[] upRequest, downRequest;	//TODO: check if this is still needed with dispatcher
	private boolean running;
	private ArrayList<ArrayList<Integer>> upElevatorRequests;
	private ArrayList<ArrayList<Integer>> downElevatorRequests;
	private ArrayList<int[]> elevatorList;
	private TaskDispatcher dispatcher;
	private Thread dispatchThread;
	
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket, receiveSocket;
	
	/**
	 * 
	 * @param numFloors
	 */
	public Scheduler(int numFloors) {
		
		this.numFloors = numFloors;
		this.upRequest = new boolean[numFloors];
		this.downRequest = new boolean[numFloors];
		this.upElevatorRequests = new ArrayList<ArrayList<Integer>>();
		this.downElevatorRequests = new ArrayList<ArrayList<Integer>>();
		this.elevatorList = new ArrayList<int[]>();
		
		for (int i =0; i < numFloors; i++) {
			this.upRequest[i] = false;
			this.downRequest[i] = false;
			this.upElevatorRequests.add(new ArrayList<Integer>());
			this.downElevatorRequests.add(new ArrayList<Integer>());
		}
		
		dispatcher = new TaskDispatcher(this);
		dispatchThread = new Thread(dispatcher);
		
		running = true;
	}
	
	/*
	public boolean addFloorRequest(int floorNum, boolean up) {
		
		// Don't fulfill Floor Requests outside of the systems capabilities
		if (up && floorNum >= this.numFloors - 1 || !up && floorNum < 1 || floorNum >= this.numFloors || floorNum < 0) {
			return false;
		}
		
		//return false
		
		// Set the request to true if it's not already set
		if (up && !this.upRequest[floorNum]) {
			this.upRequest[floorNum] = true;
			System.out.println("Scheduler adding up request for floor: "+(floorNum));
		} else if (!this.downRequest[floorNum]) {
			this.downRequest[floorNum] = true;
			System.out.println("Scheduler adding down request for floor: "+(floorNum));
		}
		
		return true;
	}
	*/

	/**
	 * checks to see if there is a floor request for a given direction at a certain floor
	 * @return if there is a floor request or not
	 */
	public boolean checkFloor(int floorNum, boolean up) {
		// Return false if their isn't a floor request
		if (up && !this.upRequest[floorNum] || !up && !this.downRequest[floorNum]) {
			return false;
		}
		
		return true;
	}

	/*
	public int getNextTask(int currFloorNum) {
		
		// get lowest up request
		int lowestUpRequest = -1;
		for (int i = 0; i < this.numFloors; i++) {
			if (this.upRequest[i]) {
				lowestUpRequest = i;
				break;
			}
		}
		// get highest down request
		int highestDownRequest = -1;
		for (int i = this.numFloors - 1; i >= 0; i--) {
			if (this.downRequest[i]) {
				highestDownRequest = i;
				break;
			}
		}
		
		if (highestDownRequest == -1 && lowestUpRequest == -1) {
			return -1;
		}
		
		int distUp = Math.abs(currFloorNum - lowestUpRequest);
		int distDown = Math.abs(currFloorNum - highestDownRequest);
		
		if (highestDownRequest == -1) {
			upRequest[lowestUpRequest] = false;
			return lowestUpRequest;
		}
		
		if (lowestUpRequest == -1) {
			downRequest[highestDownRequest] = false;
			return highestDownRequest;
		}
		
		if (distDown > distUp) {
			upRequest[lowestUpRequest] = false;
			return lowestUpRequest;
		}
		downRequest[highestDownRequest] = false;
		return highestDownRequest;
	}
	*/

	public int getNumFloors() {
		return this.numFloors;
	}
	
	public boolean isRunning() {
		return this.running;
	}

	public boolean checkFloor(int floorNum) {
		return checkFloor(floorNum, true) || checkFloor(floorNum, false);
	}
	
	public void printStuff() {
		for(int i = 0; i < this.upRequest.length; i++) {
			for (int j = 0; j < this.upElevatorRequests.get(i).size(); j++) {
				System.out.print(this.upElevatorRequests.get(i).get(j) + 1 + " ");
			}
			System.out.print(" - ");
			for (int j = 0; j < this.downElevatorRequests.get(i).size(); j++) {
				System.out.print(this.downElevatorRequests.get(i).get(j) + 1 + " ");
			}
			System.out.println();
		}

	}
	
	/**
	 * updates floor subsystem upon arrival of an elevator 
	 * sends back an array of new tasks for the elevator
	 * @param floor
	 * @param direction
	 * @return an ArrayList of new tasks for the elevator
	 */
	private ArrayList<Integer> handleElevatorArrival(byte floor, byte direction) {
		int onFloor = floor;
		boolean servingUp = (direction == (byte) 1 ? true : false);
		System.out.println("[Scheduler] handleElevatorArrival for " + onFloor + " towards " + (servingUp?"UP":"DOWN"));
		
		// Update the floor subsystem and notify that an elevator has arrived onFloor
		
		byte[] floorMsg = {0, 0, floor, direction};
		try {
			sendPacket = new DatagramPacket(floorMsg, floorMsg.length, 
					InetAddress.getByName(FloorSubsystem.floorAddrName), 
					FloorSubsystem.floorListenerPort);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		
		// Respond to the elevator with new tasks
		
		
		ArrayList<Integer> newTasks;
		
		if (servingUp) {
			newTasks = upElevatorRequests.get(onFloor);
			upElevatorRequests.set(onFloor, new ArrayList<>());
		} else {
			System.out.println("Wrong way");
			newTasks = downElevatorRequests.get(onFloor);
			downElevatorRequests.set(onFloor, new ArrayList<>());
		}
		
		System.out.println(newTasks.toString());
		return newTasks;
	}
	
	/**
	 * adds a floor requests to the array with the corresponding direction at the corresponding floor
	 * @param b
	 * @param c
	 * @return successful 
	 */
	public boolean handleFloorRequest(byte b, byte c) {
        int fromFloor = b-1;	// change from actual floor nums to 0-based indices
        int toFloor = c-1;
        boolean isUp = fromFloor < toFloor;

        if (fromFloor < 0 || fromFloor >= numFloors) {
            return false;
        } 

        if (toFloor < 0 || toFloor >= numFloors) {
            return false;
        }

        if (isUp) {
            if (!upRequest[fromFloor]) {
                upRequest[fromFloor] = true;
            }

            if (!upElevatorRequests.get(fromFloor).contains(toFloor)) {
                upElevatorRequests.get(fromFloor).add(toFloor);
            }
            
            System.out.println("upRequest at floor " + b + ": " + upRequest[fromFloor]);
            System.out.println("upElevatorRequests at floor " + b + ": " + upElevatorRequests.get(fromFloor));
            
        } else {
        	if (!downRequest[fromFloor]) {
                downRequest[fromFloor] = true;
            }

            if (!downElevatorRequests.get(fromFloor).contains(toFloor)) {
                downElevatorRequests.get(fromFloor).add(toFloor);
            }
            
            System.out.println("downRequest at floor " + b + ": " + downRequest[fromFloor]);
            System.out.println("downElevatorRequests at floor " + b + ": " + downElevatorRequests.get(fromFloor));
        }
        
        // sending floorNum index rather than actual floor number starting from 1
     	dispatcher.enqueueTask(new FloorRequest(fromFloor, isUp));

        return true;
    }

	/**
	 * syncs and validates the elevator with the scheduler
	 * @param b
	 * @param port
	 * @return boolean value of whether it is valid or not
	 */
    public boolean handleElevatorSync(byte b, int port) {
        int elevatorNum = b;
        //TODO: Validate if port is correct
        //TODO: Validate that the elevatorNum is set to it's position in the ArrayList
        
        for (int i = 0; i < elevatorList.size(); i++) {
            if (elevatorList.get(i)[0] == elevatorNum) {
                return false;
            }
        }

        int[] elevatorInfo = {elevatorNum, port, 0, 0};
        elevatorList.add(elevatorInfo);
        return true;
    }
    
    /**
     * updates the elevator's info in the array list
     * @param b
     * @param c
     * @param d
     * @return successful
     */
    public boolean handleElevatorUpdate(byte b, byte c, byte d) {
        int elevatorNum = b;
        int onFloor = c;
        int state = d;
        
        System.out.println("elevID " + b + " - onFloor " + c + " - state " + d);

        if (onFloor > numFloors || onFloor < 0) {
            return false;
        }
        
        if (elevatorNum >= elevatorList.size() || elevatorList.size() == 0) {
            return false;
        }

        int[] info = elevatorList.get(elevatorNum);
        int[] elevatorInfo = {info[0], info[1], onFloor, state};

        elevatorList.set(elevatorNum, elevatorInfo);
        
        //Update gui with elevator position
        byte[] floorMsg = {0, 1, c, (byte) elevatorNum};
		try {
			sendPacket = new DatagramPacket(floorMsg, floorMsg.length, 
					InetAddress.getByName(FloorSubsystem.floorAddrName), 
					FloorSubsystem.floorListenerPort);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			
		}

        return true;
    }
    
    /**
     * updates the elevator info in the array list given the elevator's port
     * @param port
     * @return successful
     */
    public boolean setElevatorStateUnknownFromPort(int port) {
    	for (int i = 0; i < elevatorList.size(); ++i) {
    		if (elevatorList.get(i)[1] == port) {
    			int[] info = elevatorList.get(i);
    			int[] updated_info = {info[0], info[1], info[2], -1};
    			
    			elevatorList.set(i, updated_info);
    			
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * finds closest idle elevator or elevator moving towards floor in correct direction
     * @param floorNum
     * @param isUp
     * @return the elevator's associated port
     */
    public int assignTaskToElevator(int floorNum, boolean isUp) {
    	
    	int distanceFrom = numFloors+1;
    	int[] closestElevator = null;
    	
    	//looks for the closest elevator going up or idle
    	if(isUp) {
    		for (int i = 0; i < elevatorList.size(); i++) {
    			//checks if the elevator is going up and is the closest
                if (elevatorList.get(i)[3] == 3 && elevatorList.get(i)[2] <= floorNum && floorNum - elevatorList.get(i)[2] < distanceFrom) {
                	closestElevator = elevatorList.get(i);
                	distanceFrom = floorNum - elevatorList.get(i)[2];
                }
                //checks if the elevator is idle and is the closest
                else if (elevatorList.get(i)[3] == 0 && Math.abs(floorNum - elevatorList.get(i)[2]) < distanceFrom ) {
                	closestElevator = elevatorList.get(i);
                	distanceFrom = Math.abs(floorNum - elevatorList.get(i)[2]);
                }
            }
    	}
    	//looks for the closest elevator going down or idle
    	else {
    		for (int i = 0; i < elevatorList.size(); i++) {
    			//checks if the elevator is going down and is the closest
                if (elevatorList.get(i)[3] == 2 && elevatorList.get(i)[2] >= floorNum && elevatorList.get(i)[2] - floorNum < distanceFrom) {
                	closestElevator = elevatorList.get(i);
                	distanceFrom = elevatorList.get(i)[2] - floorNum;
                }
                //checks if the elevator is idle and is the closest
                else if (elevatorList.get(i)[3] == 0 && Math.abs(floorNum - elevatorList.get(i)[2]) < distanceFrom ) {
                	closestElevator = elevatorList.get(i);
                	distanceFrom = Math.abs(floorNum - elevatorList.get(i)[2]);
                }
            }
    	}
    	
    	//no available elevator (return -1)
    	if(closestElevator == null) {
    		return -1;
    	}
		return closestElevator[1];
	}
    
    /**
     * receives messages from elevator and floor subsystems
     * carries out appropriate steps based on the request it received
     * sends a response back to the place of origin
     */
    public void serve() {
        byte buff[], data[], resp[];
        
        try {
			receiveSocket = new DatagramSocket(schedulerListenPort);
			sendReceiveSocket = new DatagramSocket();
			sendReceiveSocket.setSoTimeout(5000);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
        
        while(running) {
            buff = new byte[100];

            receivePacket = new DatagramPacket(buff, buff.length);

            try {
				receiveSocket.receive(receivePacket);
			} catch (IOException e) {
				System.out.print("[ERROR] ");
				e.printStackTrace();
				receiveSocket.close();
				System.exit(1);
			}

            data = receivePacket.getData();


            boolean valid = false;
            resp = new byte[100];
            
            if (data[0] != 0) {
                System.out.println("INVALID REQUEST");
            }

            if (data[0] == 0 && data[1] == 0) {
                System.out.println("Floor Request: ");
                valid = handleFloorRequest(data[2], data[3]); //TODO: Handle return
            }

            if (data[0] == 0 && data[1] == 1) {
            	ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
            	DataInputStream dataIn = new DataInputStream(byteIn);
            	
            	try {
					dataIn.skipBytes(2);	// skip first 2 bytes, known values of 0 & 1
					byte elevatorID = dataIn.readByte();
					int elevatorPort = dataIn.readInt();
					
					System.out.println("Elevator Sync Request at port " + elevatorPort);
	                valid = handleElevatorSync(elevatorID, elevatorPort);
				} catch (IOException e) {
					e.printStackTrace();
					valid = false;
				}
            }

            if (data[0] == 0 && data[1] == 2) {
                System.out.print("Elevator Update Request: ");
                valid = handleElevatorUpdate(data[2], data[3], data[4]);
            }
            
            if (data[0] == 0 && data[1] == 3) {
            	//TODO: probably need to synchronize up/downElevatorRequests
            	ArrayList<Integer> replyTasks = handleElevatorArrival(data[3], data[4]);
            	
            	valid = (replyTasks == null) ? false : true;
            	
            	if (valid) {
            		if (replyTasks.isEmpty()) {
            			// no passenger requests, respond [0 -1]
            			resp[1] = -1;
            		} else {
            			// passenger requests exit, respond [0 numRequests floorNums...]
            			int numTasks = replyTasks.size();
            			if (numTasks > 98) {
            				numTasks = 98;
            			}
            			resp[1] = (byte) numTasks;
            			for (int i = 0; i < numTasks; ++i) {
            				resp[i+2] = replyTasks.get(i).byteValue();
            			}
            		}
            	}
            }

            resp[0] = valid ? (byte) (0) : (byte) (1);

            sendPacket = new DatagramPacket(resp, resp.length, receivePacket.getAddress(), receivePacket.getPort());
            try {
				sendReceiveSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
        }
        
        sendReceiveSocket.close();
        receiveSocket.close();
    }


    @Override
    public void run() {
		dispatchThread.start();
        System.out.println("Launching Scheduler");
        serve();
        System.out.println("Shutting Down Scheduler");
    }
	
}






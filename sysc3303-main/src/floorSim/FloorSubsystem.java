package floorSim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import networking.RPCSender;

import java.time.*;
import java.time.format.DateTimeFormatter;

import scheduleServer.*;

/**
 * Represents the controller for a collection of floors
 * @author Oka Ampah, Christian Green, Gavin MacNabb, Philip Wanczycki
 * @version 0.1
 */
public class FloorSubsystem extends RPCSender implements Runnable {
	
	public final static String floorAddrName = "localhost";
	public final static int floorListenerPort = 11000;
	public Elevator_GUI gui;
	
	Comparator<String[]> eventComparator = Comparator.comparing(time -> LocalTime.parse(time[0], DateTimeFormatter.ofPattern("HH:mm:ss.S")));
	
	private PriorityQueue<String[]> inputTable;
	
	private ArrayList<Floor> floors;
	private boolean running;
	
	DatagramSocket receiveSocket;
	DatagramPacket receivePacket;
	
	/**
	 * Class constructor
	 * @param scheduler	Scheduler object used to pass messages to/from ElevatorSubsystem
	 * @param filepath	File path to a txt or csv file with each line containing the following data: 
	 * 					timestamp, fromFloor, direction(Up/Down), toFloor
	 * @param numFloors Number of floors in the system
	 */
	public FloorSubsystem(int numFloors, String filepath, Elevator_GUI gui) {
		// Read input file and store in input table
		inputTable = getEvents(filepath);
		floors = new ArrayList<>();
		running = true;
		this.gui = gui;
		
		// TODO check if numFloors matches inputfile
		for (int i = 0; i < numFloors; ++i)
			floors.add(new Floor(i+1));
		
	}
	
	/**
	 * reads events from a file and converts them into a Priority Queue
	 * @param filepath
	 * @return PriorityQueue of events
	 */
	public PriorityQueue<String[]> getEvents(String filepath) {
		System.out.println("[FLOOR] Reading Input File: ");
		
		File inputFile;
		Scanner inputReader;
		PriorityQueue<String[]> inputQueue = new PriorityQueue<>(Comparator.comparing(
				time -> LocalTime.parse(time[0], DateTimeFormatter.ofPattern("HH:mm:ss.S"))));
		try {
			inputFile = new File(filepath);
			inputReader = new Scanner(inputFile);
			
			while (inputReader.hasNextLine()) {
				 inputQueue.add(inputReader.nextLine().split("\\s+"));
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(String[] next : inputQueue) {
			System.out.println("[FLOOR] " + next[0] + " " + next[1] + " " + next[2] + " " + next[3]);
		}
		System.out.println("");
		System.out.println("");
		
		return inputQueue;
	}
	
	/**
	 * sends events to the scheduler
	 * receives the response and sets the light of the floor lights on success
	 * @param splitEntry
	 */
	public void sendEvent(String[] splitEntry) {
		if (splitEntry.length != 4) {
			return;
		}
		
		//first two bits are for telling the scheduler what type of request it is (0,0 in this case)
		byte[] msgData = {0,0,0,0};	// initialize msgData
		try {	// add fromFloor and toFloor to msgData
			msgData[2] = Byte.parseByte(splitEntry[1]);
			msgData[3] = Byte.parseByte(splitEntry[3]);
			printMsgData(msgData);
		} catch (NumberFormatException e) {
			// check input for correct argument type, if incorrect, return null;
			e.printStackTrace();
			return;
		}

		//TODO: What is the expected response length
		byte[] response = new byte[100];
		try {
			InetAddress address = InetAddress.getByName(Scheduler.schedulerAddrName);
			rpcSend(msgData, response, Scheduler.schedulerListenPort, address, "FLOOR");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Floor f = floors.get(Integer.parseInt(splitEntry[1])-1);
		if (response[0] == (byte)0) {
			// enable up/down floor light if event was sent successfully
			f.enableLight(splitEntry[2].equals("Up"));
			gui.setLamp(Integer.parseInt(splitEntry[1])-1, f.isUpLightOn(), f.isDownLightOn());
			
		}
		if (response[0] == (byte)-1) {
			// enable up/down floor light if event was sent successfully
			gui.print("IO Exception: likely socket timeout. \n");
		}
		
		
	}
	
	/**
	 * disables the appropriate floor light upon arrival of an elevator
	 * @param floorNum
	 * @param isUp
	 * @return
	 */
	public boolean handleElevatorArrival(int floorNum, boolean isUp) {
		if (floorNum >= floors.size() || floorNum < 0) return false;
		
		floors.get(floorNum).disableLight(isUp);
		gui.setLamp(floorNum, floors.get(floorNum).isUpLightOn(), floors.get(floorNum).isDownLightOn());
		
		return true;
	}
	
	public Floor getFloor(int i) {
		return floors.get(i);
	}
	
	public int getNumFloors() {
		return floors.size();
	}
	
	/**
	 * receives updates from a socket for elevator arrivals
	 * calls method to handle arrival of the appropriate floor
	 * @return
	 */
	public boolean listenForUpdates() {
		try {
			receiveSocket = new DatagramSocket(floorListenerPort);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		
		while(running) {
			
			// create the response
			byte buff[] = new byte[100];
			receivePacket = new DatagramPacket(buff, buff.length);

			
			try {
				receiveSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
			byte[] data = receivePacket.getData();
			System.out.print("[FLOOR] Received item: ");
			System.out.println(data[0] + " " + data[1] + " " + data[2] + " " + data[3]);
			
			if (data[0] != 0) {
				continue;
			}
			
			if (data[1] == 0) {
				// Scheduler notifies Floor that elevator has arrived
				int floorNum = data[2];
				boolean isUp;
				if (data[3] == 1) {
					isUp = true;
				} else {
					isUp = false;
				}
				handleElevatorArrival(floorNum, isUp);
				// TODO: send response
			}
			if (data[1] == 1) {
				// Scheduler notifies Floor that elevator has arrived
				int floorNum = data[2];
				int eNum = data[3];
				gui.setFloor(floorNum, eNum);
				// TODO: send response
			}
		}
		
		receiveSocket.close();
		
		return true;
	}

	@Override
	public void run() {

		System.out.println("[FLOOR] Starting up the inputTable Scheduler");
		
		Thread timerThread = new Thread(() -> {
			Timer timer = new Timer();

			int tick = 100;
			
			timer.scheduleAtFixedRate(new TimerTask() {
				LocalTime now = LocalTime.of(14, 5, 15, 0);
				@Override
				public void run() {
					String[] head = inputTable.peek();
					String[] next;
					if (head != null && head[0].equals(now.format(DateTimeFormatter.ofPattern("HH:mm:ss.S")))) {
						next = inputTable.poll();
						System.out.println("[FLOOR] Popped item: "  + next[0] + " " + next[1] + " " + next[2] + " " + next[3]);
						sendEvent(next);
					}
					now = now.plusNanos(100000000);
				}
			}, 0, tick);
		});
		
		timerThread.start();
		
		listenForUpdates();
	}
	
	/**
	 * prints requests to console 
	 * @param msgData
	 */
	public void printMsgData(byte[] msgData) {
		System.out.println("[FLOOR] Sending request to Scheduler containing:");
		System.out.print("[FLOOR] byte[] ");
		for (int j = 0; j < 4; j++) {
			System.out.print(msgData[j] + " ");
		} System.out.println();
	}
	
	public void kill() {
		running = false;
	}
	//Have to add the timestamps to requests and fix logic 
	
}
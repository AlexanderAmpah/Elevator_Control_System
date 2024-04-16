package scheduleServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import elevatorSim.ElevatorSubsystem;
import networking.RPCSender;

public class TaskDispatcher extends RPCSender implements Runnable {
	
	private Scheduler scheduler;
	private Queue<FloorRequest> tasks;

	public TaskDispatcher(Scheduler scheduler) {
		this.scheduler = scheduler;
		tasks = new LinkedList<>();
	}
	
	/**
	 * adds a task to the tasks list
	 * @param task
	 * @return success
	 */
	public boolean enqueueTask(FloorRequest task) {
		boolean success;
		synchronized (tasks) {
			success = tasks.add(task);
			tasks.notifyAll();
		}
		return success;
	}
	
	/**
	 * gets the next task from the tasks list
	 * @return FloorRequest
	 */
	private FloorRequest getNextTask() {
		synchronized (tasks) {
			while (tasks.isEmpty()) {
				try {
					System.out.println("DISPATCH: Waiting for task");
					tasks.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("DISPATCH: Got task");
			
			return tasks.poll();
		}
	}
	
	/**
	 * continually tries to gets an elevator port that can handle a new task
	 * @param floorNum
	 * @param isUp
	 * @return an elevator port
	 */
	public int getEligibleElevatorPort(int floorNum, boolean isUp) {
		int elevPort = scheduler.assignTaskToElevator(floorNum, isUp);
		
		while (elevPort == -1 && scheduler.isRunning()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			elevPort = scheduler.assignTaskToElevator(floorNum, isUp);
		}
		
		return elevPort;
	}
	/**
	 * dispatches a task to an elevator
	 * @param task
	 */
	private void dispatch(FloorRequest task) {
		int floorNum = task.getFloorNum();
		boolean isUp = task.isUp();
		System.out.println("DISPATCH: sending task at " + floorNum + " towards " + (isUp ? "UP" : "DOWN"));
		
		byte[] taskMsg = {0,1,(byte)floorNum,(byte)(isUp ? 1 : 0)};
		byte[] response = new byte[100];
		response[0] = (byte)-1;
		int port = -1;
		
		while (response[0] == (byte)-1) {
			port = getEligibleElevatorPort(floorNum, isUp);
			
			try {
				InetAddress destination = InetAddress.getByName(ElevatorSubsystem.elevatorAddrName);
				rpcSend(taskMsg, response, port, destination, "TASK DISPATCHER");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		
		// set state to unknown until next update to avoid sending multiple conflicting tasks
		scheduler.setElevatorStateUnknownFromPort(port); 
		System.out.println("DISPATCH: sent " + floorNum + (isUp ? "UP" : "DOWN") + " to elevator at " + port);
	}

	@Override
	public void run() {
		System.out.println("Launching Dispatcher");
		while(scheduler.isRunning()) {
			dispatch(getNextTask());
		}		
		System.out.println("Dispatcher exiting");
	}

}

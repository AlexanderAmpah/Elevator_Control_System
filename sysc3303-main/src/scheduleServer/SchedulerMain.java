package scheduleServer;

public class SchedulerMain {
		
	public static void main(String args[]){
		Thread schedulerThread = new Thread(new Scheduler(5));
		schedulerThread.start();
	}
}

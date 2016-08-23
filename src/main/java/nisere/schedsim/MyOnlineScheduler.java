package nisere.schedsim;

import java.util.Timer;
import java.util.TimerTask;

public class MyOnlineScheduler implements Runnable {

	private MyOnlineQueue queue;
	
	protected ScheduledTask task;
	protected Timer timer;

	public MyOnlineScheduler(MyOnlineQueue myOnlineQueue) {
		this.queue = myOnlineQueue;
		
		initializeTimer();
	}
	
	private void initializeTimer() {
		this.timer =  new Timer();
		this.task = new ScheduledTask(this.queue);
		this.timer.schedule(this.task, 1000, 1000);
	}

	protected class ScheduledTask extends TimerTask {
		protected MyOnlineQueue queue;
		
		public ScheduledTask(MyOnlineQueue myOnlineQueue) {
			this.queue = myOnlineQueue;
		}
		
		@Override
		public void run() {
			getCloudlets();
		}

		protected void getCloudlets() {
			System.out.format("Scheduler: %s%n", this.queue.getCloudletsNo());
			
		}		
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}

package nisere.schedsim;

public class OnlineMain {

	public static void main(String[] args) {
		MyOnlineQueue queue = new MyOnlineQueue();
		MyOnlineScheduler scheduler = new MyOnlineScheduler(queue);
		MyOnlineFeeder feeder = new MyOnlineFeeder(queue);
		
		queue.run();
		scheduler.run();
		feeder.run();

	}

}

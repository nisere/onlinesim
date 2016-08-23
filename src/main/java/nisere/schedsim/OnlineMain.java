package nisere.schedsim;

import java.util.Calendar;

import org.cloudbus.cloudsim.core.CloudSim;

public class OnlineMain {

	public static void main(String[] args) {
		try {
			// Initialize the CloudSim package before creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events
			CloudSim.init(num_user, calendar, trace_flag);
			
			OnlineQueue queue = new OnlineQueue();
			//MyDatacenterBroker broker = new MyDatacenterBroker("MyBroker");
			OnlineScheduler scheduler = new OnlineScheduler(queue,null,null,null);
			OnlineFeeder feeder = new OnlineFeeder(queue);
			
			queue.run();
			scheduler.run();
			feeder.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

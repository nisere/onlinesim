package nisere.schedsim;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.cloudbus.cloudsim.Datacenter;

public class OnlineScheduler implements Runnable {

	private OnlineQueue queue;
	
	public OnlineQueue getQueue() {
		return queue;
	}

	public void setQueue(OnlineQueue queue) {
		this.queue = queue;
	}

	private HashMap<String,Datacenter> datacenters;
	private MyDatacenterBroker broker;
	private SchedulingAlgorithm algorithm;
	
	public SchedulingAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SchedulingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public MyDatacenterBroker getBroker() {
		return broker;
	}

	public void setBroker(MyDatacenterBroker broker) {
		this.broker = broker;
	}

	public HashMap<String,Datacenter> getDatacenters() {
		return datacenters;
	}

	public void setDatacenters(HashMap<String,Datacenter> datacenters) {
		this.datacenters = datacenters;
	}
	
	public void addDatacenter(Datacenter datacenter) {
		if (!getDatacenters().containsKey(datacenter.getName())) {
			getDatacenters().put(datacenter.getName(), datacenter);
		}
	}

	protected ScheduledTask task;
	protected Timer timer;

	public OnlineScheduler(OnlineQueue queue, 
			SchedulingAlgorithm algorithm,
			HashMap<String,Datacenter> datacenters,
			MyDatacenterBroker broker) throws Exception {
		this.queue = (queue == null ? new OnlineQueue() : queue);
		this.algorithm = algorithm;
		this.datacenters = (datacenters == null ? new HashMap<String,Datacenter>() : datacenters);
		this.broker = (broker == null ? new MyDatacenterBroker("MyBroker") : broker);
		this.broker.setAlgorithm(algorithm);
		
		initializeTimer();
	}
	
	private void initializeTimer() {
		this.timer =  new Timer();
		this.task = new ScheduledTask(this.queue);
		this.timer.schedule(this.task, 1000, 1000);
	}

	protected class ScheduledTask extends TimerTask {
		protected OnlineQueue queue;
		
		public ScheduledTask(OnlineQueue queue) {
			this.queue = queue;
		}
		
		@Override
		public void run() {
			getCloudlets();
			//scheduleCloudlets();
		}

		protected void getCloudlets() {
			System.out.format("Scheduler: %s%n", this.queue.getCloudletsNo());
			
		}		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//while(true)
		//getCloudlets();
		//scheduleCloudlets();
	}

	
}

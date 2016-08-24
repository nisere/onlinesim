package nisere.schedsim;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import nisere.schedsim.algorithm.SchedulingAlgorithm;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;

public class OnlineScheduler extends TimerTask {

	private OnlineQueue queue;
	private HashMap<String,Datacenter> datacenters;
	private DatacenterBroker broker;
	private SchedulingAlgorithm algorithm;

	protected Timer timer;
	
	public OnlineQueue getQueue() {
		return queue;
	}

	public void setQueue(OnlineQueue queue) {
		this.queue = queue;
	}

	public SchedulingAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SchedulingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public DatacenterBroker getBroker() {
		return broker;
	}

	public void setBroker(DatacenterBroker broker) {
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

	public OnlineScheduler(HashMap<String,Datacenter> datacenters,
			OnlineQueue queue, 
			SchedulingAlgorithm algorithm,
			DatacenterBroker broker) throws Exception {
		this.datacenters = datacenters;
		this.queue = queue;
		this.algorithm = algorithm;
		this.broker = broker;
		
		initializeTimer();
	}
	
	protected void initializeTimer() {
		this.timer =  new Timer();
		this.timer.schedule(this, 0, 1000);
	}


	@Override
	public void run() {

		getCloudlets();//removes from queue
		scheduleCloudlets();
		submitCloudlets();
	}

	private void submitCloudlets() {
		// TODO Auto-generated method stub
		
	}

	private void scheduleCloudlets() {
		// TODO Auto-generated method stub
		
	}

	protected void getCloudlets() {
		System.out.format("Scheduler: %s%n", this.queue.getCloudletsNo());
		
	}	
}

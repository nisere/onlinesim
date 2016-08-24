package nisere.schedsim.algorithm;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public abstract class SchedulingAlgorithm {

	/** List of scheduled cloudlets. */
	private List<? extends Cloudlet> cloudletScheduledList;


	public <T extends Cloudlet> List<T> getCloudletScheduledList() {
		return (List<T>)cloudletScheduledList;
	}
	
	public void setCloudletScheduledList(List<? extends Cloudlet> cloudletScheduledList) {
		this.cloudletScheduledList = cloudletScheduledList;
	}

	protected abstract void initCloudletScheduledList();

	public SchedulingAlgorithm() {
		initCloudletScheduledList();
	}

//	public synchronized void addCloudletToScheduledList(Cloudlet cloudlet) {
//		
//	}
//	
	
	/** Creates the schedule. */
	public abstract void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList);

}

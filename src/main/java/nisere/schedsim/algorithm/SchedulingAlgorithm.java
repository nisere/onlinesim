package nisere.schedsim.algorithm;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * Abstract class used for scheduling algorithms
 * 
 * @author Alina Chera
 *
 */
public abstract class SchedulingAlgorithm {

	/** Cost of the execution of cloudlets */
	double cost;

	/** List of scheduled cloudlets. */
	private List<? extends Cloudlet> cloudletScheduledList;
	
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * Gets the list of scheduled cloudlets
	 * @return the list of scheduled cloudlets casted to the real type
	 */
	public <T extends Cloudlet> List<T> getCloudletScheduledList() {
		return (List<T>)cloudletScheduledList;
	}
	
	/**
	 * Sets the list of scheduled cloudlets
	 * @param cloudletScheduledList the list
	 */
	public void setCloudletScheduledList(List<? extends Cloudlet> cloudletScheduledList) {
		this.cloudletScheduledList = cloudletScheduledList;
	}

	/** Initialize CloudletScheduledList with your choice */
	protected abstract void initCloudletScheduledList();

	public SchedulingAlgorithm() {
		initCloudletScheduledList();
	}
	
	/** Creates the schedule. */
	public abstract void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList);
	
	/** To be overridden. Use this as you need */
	public void prepare(double time) {
		
	}

}

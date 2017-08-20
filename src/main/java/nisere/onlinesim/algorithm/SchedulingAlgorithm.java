package nisere.onlinesim.algorithm;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import nisere.onlinesim.VmType;

/**
 * Abstract class used for scheduling algorithms
 * 
 * @author Alina Chera
 *
 */
public abstract class SchedulingAlgorithm {

	/** List of scheduled cloudlets. */
	private List<? extends Cloudlet> cloudletScheduledList;
	
	/** Cost of the execution of cloudlets */
	double cost;

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

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	/** Initialize CloudletScheduledList with your choice */
	protected abstract void initCloudletScheduledList();

	public SchedulingAlgorithm() {
		initCloudletScheduledList();
	}
	
	/** 
	 * Creates the schedule. 
	 * @param cloudletList the list of cloudlets to be scheduled
	 * @param vmList the list of VM on which the cloudlets can run
	 * @param vmTypes the list of VM types from all the datacenters; 
	 * use the datacenter id associated to the VM to get information about count and price;
	 * use the vmTypes to create VM as needed and add them to vmList
	 */
	public abstract void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList, List<? extends VmType> vmTypes);
	
	/** To be overridden. Use this as you need */
	public void prepare(double time) {
		
	}

}

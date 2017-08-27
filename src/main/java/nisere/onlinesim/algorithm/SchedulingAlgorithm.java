package nisere.onlinesim.algorithm;

import java.util.LinkedList;
import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

/**
 * Abstract class used for scheduling algorithms
 * 
 * @author Alina Chera
 *
 */
public abstract class SchedulingAlgorithm {
	/** List of scheduled cloudlets. */
	private List<? extends OnlineCloudlet> cloudletScheduledList;
	
	/** Cost of the execution of cloudlets */
	private double cost;
	
	/** List of unscheduled cloudlets */
	private List<? extends OnlineCloudlet> cloudletUnscheduledList;

	/**
	 * Gets the list of scheduled cloudlets
	 * @return the list of scheduled cloudlets casted to the real type
	 */
	@SuppressWarnings("unchecked")
	public <T extends OnlineCloudlet> List<T> getCloudletScheduledList() {
		return (List<T>)cloudletScheduledList;
	}
	
	/**
	 * Sets the list of scheduled cloudlets
	 * @param cloudletScheduledList the list
	 */
	public void setCloudletScheduledList(List<? extends OnlineCloudlet> cloudletScheduledList) {
		this.cloudletScheduledList = cloudletScheduledList;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	@SuppressWarnings("unchecked")
	public  <T extends OnlineCloudlet> List<T>  getCloudletUnscheduledList() {
		return (List<T>)cloudletUnscheduledList;
	}

	public void setCloudletUnscheduledList(List<? extends OnlineCloudlet> cloudletUnscheduledList) {
		this.cloudletUnscheduledList = cloudletUnscheduledList;
	}

	/** Override this to initialize CloudletScheduledList with your choice */
	protected void initialize() {
		setCloudletScheduledList(new LinkedList<OnlineCloudlet>());	
		setCloudletUnscheduledList(new LinkedList<OnlineCloudlet>());
	}

	public SchedulingAlgorithm() {
		initialize();
	}
	
	/** 
	 * Creates the schedule. 
	 * @param cloudletList the list of cloudlets to be scheduled
	 * @param vmList the list of VM on which the cloudlets can run
	 * @param vmTypes the list of VM types from all the datacenters; 
	 * use the datacenter id associated to the VM to get information about count and price;
	 * use the vmTypes to create VM as needed and add them to vmList
	 * @param time TODO
	 */
	public abstract void computeSchedule(List<? extends OnlineCloudlet> cloudletList,
			List<? extends OnlineVm> vmList, List<? extends VmType> vmTypes, double time);

	public abstract void unscheduleCloudlet(OnlineCloudlet cloudlet, double delay);

}

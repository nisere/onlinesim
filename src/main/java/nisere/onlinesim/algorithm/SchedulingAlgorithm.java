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
	private List<? extends OnlineCloudlet> scheduledCloudletList;
	
	/** Cost of the execution of cloudlets */
	private double cost;
	
	/** List of unscheduled cloudlets */
	private List<? extends OnlineCloudlet> unscheduledCloudletList;

	/**
	 * Gets the list of scheduled cloudlets
	 * @return the list of scheduled cloudlets casted to the real type
	 */
	@SuppressWarnings("unchecked")
	public <T extends OnlineCloudlet> List<T> getScheduledCloudletList() {
		return (List<T>)scheduledCloudletList;
	}
	
	/**
	 * Sets the list of scheduled cloudlets
	 * @param scheduledCloudletList the list
	 */
	public void setScheduledCloudletList(List<? extends OnlineCloudlet> cloudletScheduledList) {
		this.scheduledCloudletList = cloudletScheduledList;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	@SuppressWarnings("unchecked")
	public  <T extends OnlineCloudlet> List<T>  getUnscheduledCloudletList() {
		return (List<T>)unscheduledCloudletList;
	}

	public void setUnscheduledCloudletList(List<? extends OnlineCloudlet> cloudletUnscheduledList) {
		this.unscheduledCloudletList = cloudletUnscheduledList;
	}

	/** Override this to initialize CloudletScheduledList with your choice */
	protected void initialize() {
		initScheduledCloudletList();	
		initUnscheduledCloudletList();
	}
	
	public void initScheduledCloudletList() {
		setScheduledCloudletList(new LinkedList<OnlineCloudlet>());	
	}
	public void initUnscheduledCloudletList() {
		setUnscheduledCloudletList(new LinkedList<OnlineCloudlet>());
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

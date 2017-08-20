package nisere.onlinesim;

import java.util.LinkedList;
import java.util.List;

import nisere.onlinesim.algorithm.SchedulingAlgorithm;

/**
 * Scheduler is a helper class that puts together datacenters, a datacenter broker, 
 * VM instances, cloudlets and a scheduling algorithm and performs the steps
 * required for a simulation.
 * It adds the possibility for the scheduling algorithm to be run
 * periodically, to simulate scheduling in batches of tasks that arrive online.
 * 
 * @author Alina Chera
 *
 */
public class Scheduler {

	/** The VM type list for all datacenters */
	private List<? extends VmType> vmTypes;
	
	/** The datacenter broker */
	private OnlineDatacenterBroker broker;
	
	/** The VM list for all datacenters */
	private List<? extends OnlineVm> vmList;
	
	/** The cloudlet list */
	private List<? extends OnlineCloudlet> cloudletList;
	
	/** The scheduling algorithm */
	private SchedulingAlgorithm algorithm;
	
	/** The scheduling interval (in seconds, positive) */
	private int schedulingInterval;


	/**
	 * Creates a Scheduler object.
	 * @param datacenters the mapping of datacenters
	 * @param broker the datacenter broker
	 * @param vmList the VM list
	 * @param cloudletList the cloudlet list
	 * @param algorithm the scheduling algorithm
	 * @param schedulingInterval the scheduling interval in seconds
	 * @throws Exception
	 */
	public Scheduler(List<? extends VmType> vmTypes,
			OnlineDatacenterBroker broker,
			List<? extends OnlineVm> vmList,
			List<? extends OnlineCloudlet> cloudletList,
			SchedulingAlgorithm algorithm,
			int schedulingInterval) throws Exception {
		this.vmTypes = vmTypes;
		this.broker = broker;
		this.vmList = vmList;
		this.cloudletList = cloudletList;		
		this.algorithm = algorithm;	
		this.schedulingInterval = Math.max(0, schedulingInterval);
	}

	/**
	 * This method takes the cloudlets, schedules them and sends them to the broker.
	 */
	public void prepareSimulation() {
		List<OnlineCloudlet> scheduledCloudlets = getScheduledCloudlets(getCloudletList(), getVmList(), getVmTypes());
		getBroker().submitVmList(getVmList());
		getBroker().submitCloudletList(scheduledCloudlets);
	}
	
	/**
	 * This method computes the schedule for all the cloudlets.
	 * The scheduling algorithm is run in batches. 
	 * 
	 * @param cloudlets cloudlet list
	 * @param vms VM list
	 * 
	 * @return a list with scheduled cloudlets
	 */
	protected <T extends OnlineCloudlet> List<T> getScheduledCloudlets(List<? extends OnlineCloudlet> cloudlets, 
			List<? extends OnlineVm> vms, List<? extends VmType> types) {
		
		long delay = getSchedulingInterval();
		List<OnlineCloudlet> list = new LinkedList<>();
		
		for (OnlineCloudlet cloudlet : cloudlets) {
			if (cloudlet.getArrivalTime() > delay) {
				// this is the first of the next batch;
				// schedule the batch then reset the list and add this cloudlet
				getAlgorithm().prepare(delay);
				getAlgorithm().computeSchedule(list, vms, types);
				list = new LinkedList<>();
				while (cloudlet.getArrivalTime() > delay) {
					delay += getSchedulingInterval();
				}
			}
			
			// update delay to take into account the scheduling interval
			cloudlet.setDelay(delay);
			
			list.add(cloudlet);
		}
		getAlgorithm().prepare(delay);
		getAlgorithm().computeSchedule(list, vms, types);
		return getAlgorithm().getCloudletScheduledList();
	}

	/**
	 * Gets finished cloudlets.
	 * @return a list with the finished cloudlets
	 */
	public  <T extends OnlineCloudlet> List<T> getFinishedCloudlets() {
		return getBroker().getCloudletReceivedList();
	}

	/**
	 * @return the vmTypes
	 */
	@SuppressWarnings("unchecked")
	public <T extends VmType> List<T> getVmTypes() {
		return (List<T>)vmTypes;
	}

	/**
	 * @param vmTypes the vmTypes to set
	 */
	public void setVmTypes(List<? extends VmType> vmTypes) {
		this.vmTypes = vmTypes;
	}


	/**
	 * Gets the datacenter broker.
	 * @return the datacenter broker
	 */
	public OnlineDatacenterBroker getBroker() {
		return broker;
	}
	
	/**
	 * Sets the datacenter broker.
	 * @param broker the datacenter broker
	 */
	public void setBroker(OnlineDatacenterBroker broker) {
		this.broker = broker;
	}
	
	/**
	 * Gets the VM list.
	 * @return the VM list casted to the real type
	 */
	@SuppressWarnings("unchecked")
	public <T extends OnlineVm> List<T> getVmList() {
		return (List<T>)vmList;
	}

	/**
	 * Sets the VM list.
	 * @param vmList the VM list
	 */
	public void setVmList(List<? extends OnlineVm> vmList) {
		this.vmList = vmList;
	}
	
	/**
	 * Gets the cloudlet list.
	 * @return the cloudlet list casted to the real type
	 */
	@SuppressWarnings("unchecked")
	public <T extends OnlineCloudlet> List<T> getCloudletList() {
		return (List<T>)cloudletList;
	}

	/**
	 * Sets the cloudlet list.
	 * @param cloudletList the cloudlet list
	 */
	public void setCloudletList(List<? extends OnlineCloudlet> cloudletList) {
		this.cloudletList = cloudletList;
	}

	/**
	 * Gets the scheduling algorithm.
	 * @return the scheduling algorithm
	 */
	public SchedulingAlgorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets the scheduling algorithm.
	 * @param algorithm the scheduling algorithm
	 */
	public void setAlgorithm(SchedulingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	/**
	 * Gets the scheduling interval.
	 * @return the scheduling interval
	 */
	public int getSchedulingInterval() {
		return schedulingInterval;
	}
	
	/**
	 * Sets the scheduling interval
	 * @param schedulingInterval (in seconds, must be positive)
	 * @return true if successful, false if not
	 */
	public boolean setSchedulingInterval(int schedulingInterval) {
		boolean ret = false;
		if (schedulingInterval >= 0) {
			this.schedulingInterval = schedulingInterval;
			ret = true;
		}
		return ret;
	}

}

package nisere.schedsim;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nisere.schedsim.algorithm.SchedulingAlgorithm;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

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
	
	///** A map between the names of datacenters and the datacenters */
	//private Map<String,? extends Datacenter> datacenters;
	private List<? extends Datacenter> datacenters;
	/** The datacenter broker */
	private DatacenterBroker broker;
	/** The VM list for all datacenters */
	private List<? extends Vm> vmList;
	/** The cloudlet list */
	private List<? extends Cloudlet> cloudletList;
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
	public Scheduler(List<? extends Datacenter> datacenters,
			DatacenterBroker broker,
			List<? extends Vm> vmList,
			List<? extends Cloudlet> cloudletList,
			SchedulingAlgorithm algorithm,
			int schedulingInterval) throws Exception {
		this.datacenters = datacenters;
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
		List<Cloudlet> scheduledCloudlets = getScheduledCloudlets(getCloudletList(), getVmList());
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
	protected List<Cloudlet> getScheduledCloudlets(List<? extends Cloudlet> cloudlets, 
			List<? extends Vm> vms) {
		long delay = getSchedulingInterval();
		List<Cloudlet> list = new LinkedList<>();
		for (Cloudlet cloudlet : cloudlets) {
			if ((cloudlet instanceof MyCloudlet) && ( ((MyCloudlet) cloudlet).getArrivalTime() > delay )) {
				// this is the first of the next batch;
				// schedule the batch then reset the list and add this cloudlet
				getAlgorithm().prepare(delay);
				getAlgorithm().computeSchedule(list, vms);
				list = new LinkedList<>();
				while (((MyCloudlet) cloudlet).getArrivalTime() > delay) {
					delay += getSchedulingInterval();
				}
			}
			// update delay to take into account the scheduling interval
			if (cloudlet instanceof MyCloudlet) {
				((MyCloudlet) cloudlet).setDelay(delay);
			}
			list.add(cloudlet);
		}
		getAlgorithm().prepare(delay);
		getAlgorithm().computeSchedule(list, vms);
		return getAlgorithm().getCloudletScheduledList();
	}

	/**
	 * Gets finished cloudlets.
	 * @return a list with the finished cloudlets
	 */
	public List<Cloudlet> getFinishedCloudlets() {
		return getBroker().getCloudletReceivedList();
	}
	
	/**
	 * Gets the scheduling algorithm.
	 * @return the scheduling algorithm
	 */
	public SchedulingAlgorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Gets the datacenter broker.
	 * @return the datacenter broker
	 */
	public DatacenterBroker getBroker() {
		return broker;
	}

	/**
	 * Gets the cloudlet list.
	 * @return the cloudlet list casted to the real type
	 */
	public <T extends Cloudlet> List<T> getCloudletList() {
		return (List<T>)cloudletList;
	}

//	/**
//	 * Gets the map of datcenters.
//	 * @return the map of datacenters casted to the real type
//	 */
//	public <T extends Datacenter> Map<String,T> getDatacenters() {
//		return (Map<String,T>)datacenters;
//	}

	/**
	 * Gets the VM list.
	 * @return the VM list casted to the real type
	 */
	public <T extends Vm> List<T> getVmList() {
		return (List<T>)vmList;
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
		return true;
	}
	
	/**
	 * Sets the scheduling algorithm.
	 * @param algorithm the scheduling algorithm
	 */
	public void setAlgorithm(SchedulingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Sets the datacenter broker.
	 * @param broker the datacenter broker
	 */
	public void setBroker(DatacenterBroker broker) {
		this.broker = broker;
	}

	/**
	 * Sets the cloudlet list.
	 * @param cloudletList the cloudlet list
	 */
	public void setCloudletList(List<? extends Cloudlet> cloudletList) {
		this.cloudletList = cloudletList;
	}

//	/**
//	 * Sets the datacenters map.
//	 * @param datacenters the datacenters map
//	 */
//	public void setDatacenters(Map<String,Datacenter> datacenters) {
//		this.datacenters = datacenters;
//	}

	/**
	 * Sets the VM list.
	 * @param vmList the VM list
	 */
	public void setVmList(List<? extends Vm> vmList) {
		this.vmList = vmList;
	}
}

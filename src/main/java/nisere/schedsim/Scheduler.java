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
	
	/** A map between the names of datacenters and the datacenters */
	private Map<String,? extends Datacenter> datacenters;
	/** The datacenter broker */
	private DatacenterBroker broker;
	/** The VM list for all datacenters */
	private List<? extends Vm> vmList;
	/** The cloudlet list */
	private List<? extends Cloudlet> cloudletList;
	/** The scheduling algorithm */
	private SchedulingAlgorithm algorithm;
	
	/**
	 * Creates a Scheduler object.
	 * @param datacenters the mapping of datacenters
	 * @param broker the datacenter broker
	 * @param vmList the VM list
	 * @param cloudletList the cloudlet list
	 * @param algorithm the scheduling algorithm
	 * @throws Exception
	 */
	public Scheduler(Map<String,Datacenter> datacenters,
			DatacenterBroker broker,
			List<? extends Vm> vmList,
			List<? extends Cloudlet> cloudletList,
			SchedulingAlgorithm algorithm) throws Exception {
		this.datacenters = datacenters;
		this.broker = broker;
		this.vmList = vmList;
		this.cloudletList = cloudletList;		
		this.algorithm = algorithm;	
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
		long delay = 0;
		List<Cloudlet> list = new LinkedList<>();
		for (Cloudlet cloudlet : cloudlets) {
			if ((cloudlet instanceof MyCloudlet) && ( ((MyCloudlet) cloudlet).getDelay() > delay )) {
				// this is the first of the next batch;
				// schedule the batch then reset the list and add this cloudlet
				getAlgorithm().prepare(delay);
				getAlgorithm().computeSchedule(list, vms);
				list = new LinkedList<>();
				list.add(cloudlet);
				delay = ((MyCloudlet) cloudlet).getDelay();
			} else {
				list.add(cloudlet);
			}
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

	/**
	 * Gets the map of datcenters.
	 * @return the map of datacenters casted to the real type
	 */
	public <T extends Datacenter> Map<String,T> getDatacenters() {
		return (Map<String,T>)datacenters;
	}

	/**
	 * Gets the VM list.
	 * @return the VM list casted to the real type
	 */
	public <T extends Vm> List<T> getVmList() {
		return (List<T>)vmList;
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

	/**
	 * Sets the datacenters map.
	 * @param datacenters the datacenters map
	 */
	public void setDatacenters(Map<String,Datacenter> datacenters) {
		this.datacenters = datacenters;
	}

	/**
	 * Sets the VM list.
	 * @param vmList the VM list
	 */
	public void setVmList(List<? extends Vm> vmList) {
		this.vmList = vmList;
	}
}

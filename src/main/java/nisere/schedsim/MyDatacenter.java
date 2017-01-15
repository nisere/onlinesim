package nisere.schedsim;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

/**
 * MyDatacenter class adds to the Cloudsim Datacenter a way to define the type of instances
 * that can be created and their number.
 * 
 * @author Alina Chera
 *
 */
public class MyDatacenter extends Datacenter {
	
	/** 
	 *  A list with the VM instances that can be created in this datacenter.
	 *  To be treated like a VM type.
	 */
	private List<? extends Vm> vmInstances;
	
	/** 
	 * A mapping between VM instances (id) and how many can be generated. 
	 * If a certain VM type is not present that means that
	 * an infinite amount of VM of that type can be created.
	 */
	private Map<Integer,Integer> vmCount;
	
	/**
	 * A mapping between VM instances (id) and the cost of using them
	 * applied per timeInterval.
	 * If a certain VM is missing that means there is no cost.
	 */
	private Map<Integer,Double> vmCost;
	
	/**
	 * The time interval for which the cost is applied, in seconds.
	 * Must be >= 1.
	 */
	private int timeInterval;

	/**
	 * Allocates a new MyDatacenter object.
	 * 
	 * @param name 
	 * 			the name to be associated with this datacenter
	 * @param characteristics 
	 * 			the characteristics of this datacenter
	 * @param storageList 
	 * 			a list of storage elements
	 * @param vmAllocationPolicy 
	 * 			the allocation policy of VMs to hosts
     * @param schedulingInterval 
     * 			the scheduling delay to process each datacenter received event
     * @param vmInstances
     * 			a list with the VM instances that can be created in this datacenter;
     * 			to be treated like types
     * @param vmCount
     * 			a mapping between VM instances (id) and how many can be generated;
     * 			if a VM is absent that means an infinite amount of that type
     * @param vmCost
     * 			a mapping between VM instances (id) and the usage cost per timeInterval;
     * 			if a certain VM is missing that means there is no cost
     * @param timeInterval
     * 			the time interval for which the cost is applied (seconds, >= 1)
	 * @throws Exception 
	 */
	public MyDatacenter(String name, DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval, List<? extends Vm> vmInstances,
			Map<Integer,Integer> vmCount, Map<Integer,Double> vmCost, 
			int timeInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);
		this.vmInstances = vmInstances;
		this.vmCount = vmCount;
		this.vmCost = vmCost;
		this.timeInterval = Math.min(timeInterval, 1);
	}


	/**
	 * Gets the list of VM instances that can be created in this datacenter.
	 * @return the list of VM instances
	 */
	public <T extends Vm> List<T> getVmInstances() {
		return (List<T>)vmInstances;
	}

	/**
	 * Sets the list of VM instances that can be created in this datacenter.
	 * @param vmInstances the list of VM instances
	 */
	public void setVmInstances(List<? extends Vm> vmInstances) {
		this.vmInstances = vmInstances;
	}
	
	/**
	 * Gets the mapping between VM instances (id) and how many can be generated.
	 * @return the map between the VM instance and its maximum number
	 */
	public Map<Integer, Integer> getVmCount() {
		return vmCount;
	}
	
	/**
	 * Sets the mapping between VM instances (id) and how many can be generated.
	 * @param vmCount the map between the VM instance and its maximum number
	 */
	public void setVmCount(Map<Integer, Integer> vmCount) {
		this.vmCount = vmCount;
	}

	/**
	 * Gets the mapping between VM instances (id) and the cost of VM.
	 * @return the map between the VM instance and the cost
	 */
	public Map<Integer,Double> getVmCost() {
		return vmCost;
	}

	/**
	 * Sets the mapping between VM instances (id) and the cost of VM.
	 * @param vmCost the map between the VM instance and the cost
	 */
	public void setVmCost(Map<Integer,Double> vmCost) {
		this.vmCost = vmCost;
	}


	/**
	 * Gets the time interval for which the cost is applied.
	 * @return time interval
	 */
	public int getTimeInterval() {
		return timeInterval;
	}


	/**
	 * Sets the time interval. Must be >= 1.
	 * @param timeInterval time interval
	 * @return true if successful, false if not
	 */
	public boolean setTimeInterval(int timeInterval) {
		boolean ret = false;
		if (timeInterval >= 1) {
			this.timeInterval = timeInterval;
			ret = true;
		}
		return ret;
	}
}

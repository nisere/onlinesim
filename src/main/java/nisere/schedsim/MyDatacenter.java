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
 * that can be created, their number and their price.
 * 
 * @author Alina Chera
 *
 */
public class MyDatacenter extends Datacenter {
	
	/** 
	 *  A list with the VM types that can be created in this datacenter.
	 */
	private List<? extends Vm> vmTypes;
	
	/** 
	 * A mapping between VM types (id) and how many can be generated. 
	 * If a certain VM type is not present that means that
	 * an infinite amount of VM of that type can be created.
	 */
	private Map<Integer,Integer> vmCount;
	
	/**
	 * A mapping between VM types (id) and the price of using them
	 * applied per timeInterval.
	 * If a certain VM is missing that means there is no price.
	 */
	private Map<Integer,Double> vmPrice;
	
	/**
	 * The time interval for which the price is applied, in seconds.
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
     * @param vmTypes
     * 			a list with the VM types that can be created in this datacenter
     * @param vmCount
     * 			a mapping between VM types (id) and how many can be generated;
     * 			if a VM is absent that means an infinite amount of that type
     * @param vmPrice
     * 			a mapping between VM types (id) and the usage price per timeInterval;
     * 			if a certain VM is missing that means there is no price
     * @param timeInterval
     * 			the time interval for which the price is applied (seconds, >= 1)
	 * @throws Exception 
	 */
	public MyDatacenter(String name, DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval, List<? extends Vm> vmTypes,
			Map<Integer,Integer> vmCount, Map<Integer,Double> vmPrice, 
			int timeInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);
		this.vmTypes = vmTypes;
		this.vmCount = vmCount;
		this.vmPrice = vmPrice;
		this.timeInterval = Math.min(timeInterval, 1);
	}


	/**
	 * Gets the list of VM types that can be created in this datacenter.
	 * @return the list of VM types
	 */
	public <T extends Vm> List<T> getVmTypes() {
		return (List<T>)vmTypes;
	}

	/**
	 * Sets the list of VM types that can be created in this datacenter.
	 * @param vmTypes the list of VM types
	 */
	public void setVmTypes(List<? extends Vm> vmTypes) {
		this.vmTypes = vmTypes;
	}
	
	/**
	 * Gets the mapping between VM types (id) and how many can be generated.
	 * @return the map between the VM type and its maximum number
	 */
	public Map<Integer, Integer> getVmCount() {
		return vmCount;
	}
	
	/**
	 * Sets the mapping between VM types (id) and how many can be generated.
	 * @param vmCount the map between the VM type and its maximum number
	 */
	public void setVmCount(Map<Integer, Integer> vmCount) {
		this.vmCount = vmCount;
	}

	/**
	 * Gets the mapping between VM types (id) and the price of VM.
	 * @return the map between the VM type and the price
	 */
	public Map<Integer,Double> getVmPrice() {
		return vmPrice;
	}

	/**
	 * Sets the mapping between VM types (id) and the price of VM.
	 * @param vmPrice the map between the VM type and the price
	 */
	public void setVmPrice(Map<Integer,Double> vmPrice) {
		this.vmPrice = vmPrice;
	}


	/**
	 * Gets the time interval for which the price is applied.
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

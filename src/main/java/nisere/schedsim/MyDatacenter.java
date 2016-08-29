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
	
	/** A list with the VM instances that can be created in this datacenter */
	private List<? extends Vm> vmInstances;
	
	/** A mapping between VM instances (id) and how many can be generated */
	private Map<Integer,Integer> vmCount;

	public <T extends Vm> List<T> getVmInstances() {
		return (List<T>)vmInstances;
	}

	public Map<Integer, Integer> getVMcount() {
		return vmCount;
	}

	public void setVMcount(Map<Integer, Integer> vmCount) {
		this.vmCount = vmCount;
	}

	public void setVmInstances(List<? extends Vm> vmInstances) {
		this.vmInstances = vmInstances;
	}

	public MyDatacenter(String name, DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval, List<? extends Vm> vmInstances,
			Map<Integer,Integer> vmCount) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);
		this.vmInstances = vmInstances;
		this.vmCount = vmCount;
	}

}

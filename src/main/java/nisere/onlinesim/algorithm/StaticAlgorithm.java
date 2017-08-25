package nisere.onlinesim.algorithm;

import java.util.HashMap;
import java.util.Map;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;

public abstract class StaticAlgorithm extends SchedulingAlgorithm {

	/** A map between VM (id) and workload. */
	private Map<Integer,Double> workloadMap;

	/**
	 * Gets the workload map.
	 * @return the map between VM (id) and workload
	 */
	public Map<Integer, Double> getWorkloadMap() {
		if (workloadMap == null) {
			workloadMap = new HashMap<>();
		}
		return workloadMap;
	}

	/**
	 * Sets the workload map.
	 * @param workloadMap a map between VM (id) and workload
	 */
	public void setWorkloadMap(Map<Integer, Double> workloadMap) {
		this.workloadMap = workloadMap;
	}
	
	/**
	 * Gets the workload of a VM
	 * @param vmId the id of the VM
	 * @return the workload of the VM
	 */
	public double getWorkload(int vmId) {
		if (!getWorkloadMap().containsKey(vmId)) {
			getWorkloadMap().put(vmId, 0.0d);
		}
		return getWorkloadMap().get(vmId);
	}
	
	/**
	 * Sets the workload of a VM
	 * @param vmId the id of the VM
	 * @param workload the workload of the VM
	 */
	public void setWorkload(int vmId, double workload) {
		getWorkloadMap().put(vmId, workload);
	}
	
	/**
	 * The workload is updated with the maximum of the moment in time and the previous workload.
	 * @param time the moment in time
	 */
	public void updateWorkload(double time) {
		for (Map.Entry<Integer, Double> entry : getWorkloadMap().entrySet()) {
			double value = Math.max(time, entry.getValue());
			getWorkloadMap().put(entry.getKey(), value);
		}
	}

	@Override
	public void removeScheduledCloudlet(OnlineCloudlet cloudlet, double delay) {
		//OnlineVm vm = cloudlet.getVm();
		//vm.setUptime(vm.getUptime() - cloudlet.getCloudletLength() / vm.getMips());
		double work = getWorkload(cloudlet.getVmId());
		work -= cloudlet.getCloudletLength() / cloudlet.getVm().getMips();
		setWorkload(cloudlet.getVmId(),work);
		
		cloudlet.setVmId(-1);
		cloudlet.setVm(null);
		cloudlet.setDelay(delay);
	}

	
}

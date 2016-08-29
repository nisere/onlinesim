package nisere.schedsim.algorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * Sufferage algorithm
 * 
 * @author Alina Chera
 *
 */
public class SufferageAlgorithm extends SchedulingAlgorithm {

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
	
	@Override
	protected void initCloudletScheduledList() {
		setCloudletScheduledList(new LinkedList<Cloudlet>());
	}

	/**
	 * Creates the schedule with Sufferage algorithm.
	 */
	public void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList) {
		boolean isNotScheduled = true;
		while (isNotScheduled) {
			Cloudlet maxCloudlet = null;
			int maxVmId = -1;
			double maxSuffer = -1;
			double maxC = -1;
			for (Cloudlet cloudlet : cloudletList) {
				// if this cloudlet was bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				Cloudlet minCloudlet = null;
				int minVmId = -1;
				double firstMin = -1;
				double secondMin = -1;
				for (Vm vm : vmList) {
					double oldFirstMin = -1;
					double cij = getWorkload(vm.getId())
							+ cloudlet.getCloudletLength() / vm.getMips();
					// find first and second min of Cij = Wi + Eij, Cxj and Ckj
					if (firstMin == -1 || firstMin > cij) {
						oldFirstMin = firstMin;
						firstMin = cij;
						minCloudlet = cloudlet;
						minVmId = vm.getId();
						if (secondMin == -1 || oldFirstMin < secondMin) {
							secondMin = oldFirstMin;
						}
					} else if (secondMin == -1 || secondMin > cij) {
						secondMin = cij;
					}
				}
				// find max of Sufferxj = Ckj - Cxj, where Cxj = first min of
				// Cij and Ckj = second min of Cij found above
				if (secondMin < 0) {
					secondMin = firstMin;
				}
				if (firstMin >= 0
						&& (maxSuffer == -1 || maxSuffer < secondMin - firstMin)) {
					maxSuffer = secondMin - firstMin;
					maxC = firstMin;
					maxCloudlet = minCloudlet;
					maxVmId = minVmId;
				}
			}
			if (maxSuffer >= 0) {
				maxCloudlet.setVmId(maxVmId);
				setWorkload(maxVmId,maxC);
				getCloudletScheduledList().add(maxCloudlet);
			} else {
				isNotScheduled = false;
			}
		}
	}
	
	/** 
	 * This method is used to reset the workload taking into account
	 * a moment in time and previous workload.
	 * 
	 * If there is outstanding work then the workload will be initialized
	 * with the difference between the workload and the moment in time.
	 * 
	 * @param time the moment in time
	 */
	@Override
	public void prepare(double time) {
		for (Map.Entry<Integer, Double> entry : getWorkloadMap().entrySet()) {
			double value = Math.max(0, entry.getValue() - time);
			getWorkloadMap().put(entry.getKey(), value);
		}
	}
}

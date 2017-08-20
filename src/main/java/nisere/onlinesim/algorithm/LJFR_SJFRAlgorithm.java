package nisere.onlinesim.algorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

/**
 * LJFR_SJFR algorithm
 * 
 * @author Alina Chera
 *
 */
public class LJFR_SJFRAlgorithm extends SchedulingAlgorithm {
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
		setCloudletScheduledList(new LinkedList<OnlineCloudlet>());
	}

	/**
	 * Creates the schedule with LJFR_SJFR algorithm.
	 */
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList,
			List<? extends OnlineVm> vmList, List<? extends VmType> vmTypes) {

		boolean isNotScheduled = true;
		int countVm = vmList.size();
		
		// first noVms cloudlets are scheduled with MaxMin		
		while (isNotScheduled && countVm > 0) {
			countVm--;

			OnlineCloudlet maxCloudlet = null;
			int maxVmId = -1;
			double max = -1;
			for (OnlineCloudlet cloudlet : cloudletList) {
				// if this cloudlet was bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				OnlineCloudlet minCloudlet = null;
				int minVmId = -1;
				double min = -1;
				for (OnlineVm vm : vmList) {
					// find min of Cij = Wi + Eij
					if (min == -1
							|| min > getWorkload(vm.getId())
									+ cloudlet.getCloudletLength()
									/ vm.getMips()) {
						min = getWorkload(vm.getId())
								+ cloudlet.getCloudletLength() / vm.getMips();
						minCloudlet = cloudlet;
						minVmId = vm.getId();
					}
				}
				// find max of Cxy, where Cxy = min of Cij found above
				if (min >= 0 && (max == -1 || max < min)) {
					max = min;
					maxCloudlet = minCloudlet;
					maxVmId = minVmId;
				}
			}
			if (max >= 0) {
				maxCloudlet.setVmId(maxVmId);
				setWorkload(maxVmId, max);
				getCloudletScheduledList().add(maxCloudlet);
			} else {
				isNotScheduled = false;
			}
		}

		// next use alternatively MinMin and MaxMin
		while (isNotScheduled) {
			OnlineCloudlet minminCloudlet = null;
			int minminVmId = -1;
			double minmin = -1;
			for (OnlineCloudlet cloudlet : cloudletList) {
				// if this cloudlet was bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				for (OnlineVm vm : vmList) {
					// fin min of Cij = Wi + Eij
					if (minmin == -1
							|| minmin > getWorkload(vm.getId())
									+ cloudlet.getCloudletLength()
									/ vm.getMips()) {
						minmin = getWorkload(vm.getId())
								+ cloudlet.getCloudletLength() / vm.getMips();
						minminCloudlet = cloudlet;
						minminVmId = vm.getId();
					}
				}
			}
			if (minmin >= 0) {
				minminCloudlet.setVmId(minminVmId);
				setWorkload(minminVmId, minmin);
				getCloudletScheduledList().add(minminCloudlet);
			} else {
				isNotScheduled = false;
			}

			if (!isNotScheduled) {
				break;
			}

			OnlineCloudlet maxCloudlet = null;
			int maxVmId = -1;
			double max = -1;
			
			for (OnlineCloudlet cloudlet : cloudletList) {
				// if this cloudlet was bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				OnlineCloudlet minCloudlet = null;
				int minVmId = -1;
				double min = -1;
				for (OnlineVm vm : vmList) {
					// find min of Cij = Wi + Eij
					if (min == -1
							|| min > getWorkload(vm.getId())
									+ cloudlet.getCloudletLength()
									/ vm.getMips()) {
						min = getWorkload(vm.getId())
								+ cloudlet.getCloudletLength() / vm.getMips();
						minCloudlet = cloudlet;
						minVmId = vm.getId();
					}
				}
				// find max of Cxy, where Cxy = min of Cij found above
				if (min >= 0 && (max == -1 || max < min)) {
					max = min;
					maxCloudlet = minCloudlet;
					maxVmId = minVmId;
				}
			}
			if (max >= 0) {
				maxCloudlet.setVmId(maxVmId);
				setWorkload(maxVmId, max);
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

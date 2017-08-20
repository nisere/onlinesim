package nisere.onlinesim.algorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

/**
 * WorkQueue algorithm
 * 
 * @author Alina Chera
 *
 */
public class WorkQueueAlgorithm2 extends SchedulingAlgorithm {
	
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
	 * Creates the schedule with WorkQueue algorithm.
	 */
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList,
			List<? extends OnlineVm> vmList, List<? extends VmType> vmTypes) {
		
		boolean isNotScheduled = true;
		int randomId = 0;
		
		while (isNotScheduled && randomId < cloudletList.size()) {
			// select cloudlet randomly - skipped; instead take in order
			OnlineCloudlet cloudlet = cloudletList.get(randomId++);

			// if this cloudlet was bound to a VM continue
			if (cloudlet.getVmId() >= 0) {
				continue;
			}

			double min = -1;
			OnlineVm minvm = null;
			
			for (VmType type : vmTypes) {
				// create a new VM if possible with workload 0
				if (type.getCount() > 0) {
					type.setCount(type.getCount()-1);
					OnlineVm vm = OnlineVm.copy(type.getVm());
					((List<OnlineVm>)vmList).add(vm);
					min = 0.0;
					minvm = vm;
					break;
				}
			}
			
			if (min < 0) {
				for (OnlineVm vm : vmList) {
					// find VM with min workload
					if (min == -1 || min > getWorkload(vm.getId())) {
						min = getWorkload(vm.getId());
						minvm = vm;
					}
				}
			}

			if (min >= 0) {
				// schedule cloudlet on VM with min workload
				cloudlet.setVmId(minvm.getId());
				getCloudletScheduledList().add(cloudlet);
				double newWorkload = getWorkload(minvm.getId()) + cloudlet.getCloudletLength() 
						/ minvm.getMips();
				setWorkload(minvm.getId(), newWorkload);
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

package nisere.onlinesim.algorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import nisere.onlinesim.VmType;

/**
 * MinMax algorithm
 * 
 * @author Alina Chera
 *
 */
public class MinMaxAlgorithm extends SchedulingAlgorithm {
	
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
	 * Creates the schedule with MinMax algorithm.
	 */
	public void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList, List<? extends VmType> vmTypes) {

		boolean isNotScheduled = true;
		
		while (isNotScheduled) {
			Cloudlet maxCloudlet = null;
			int maxVmId = -1;
			double max = -1;
			double maxC = -1;
			for (Cloudlet cloudlet : cloudletList) {
				// if this cloudlet was bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				Cloudlet minCloudlet = null;
				int minVmId = -1;
				double minC = -1;
				double minCE = -1;
				double minE = -1;
				for (Vm vm : vmList) {
					// find min of Cij = Wi + Eij; min of Eij
					if (minC == -1
							|| minC > getWorkload(vm.getId())
									+ cloudlet.getCloudletLength()
									/ vm.getMips()) {
						minC = getWorkload(vm.getId())
								+ cloudlet.getCloudletLength() / vm.getMips();
						minCE = cloudlet.getCloudletLength() / vm.getMips();
						minCloudlet = cloudlet;
						minVmId = vm.getId();
					}
					if (minE == -1
							|| minE > cloudlet.getCloudletLength()
									/ vm.getMips()) {
						minE = cloudlet.getCloudletLength() / vm.getMips();
					}
				}
				// //find max of Kxj = Cxj/Ehj, where Cxj = min of Cij and Ehj =
				// min of Eij found above
				// find max of Kxj = Exj/Ehj, where Cxj = min of Cij and Exj =
				// the corresponding exec time of Cxj and Ehj = min of Eij found
				// above
				if (minCE >= 0 && minE >= 0
				// && (max == -1 || max < minCE/minE)) {
						&& (max == -1 || max < minE / minCE)) {
					// max = minCE/minE;
					max = minE / minCE;
					maxC = minC;
					maxCloudlet = minCloudlet;
					maxVmId = minVmId;
				}
			}
			if (max >= 0) {
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

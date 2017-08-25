package nisere.onlinesim.algorithm;

import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

/**
 * LJFR_SJFR algorithm
 * 
 * @author Alina Chera
 *
 */
public class LJFR_SJFRAlgorithm extends StaticAlgorithm {

	/**
	 * Creates the schedule with LJFR_SJFR algorithm.
	 */
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList,
			List<? extends OnlineVm> vmList, List<? extends VmType> vmTypes, double time) {

		updateWorkload(time);
		
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
				assignCloudletToVm(maxCloudlet,maxVmId,max);
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
				assignCloudletToVm(minminCloudlet,minminVmId,minmin);
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
				assignCloudletToVm(maxCloudlet,maxVmId,max);
			} else {
				isNotScheduled = false;
			}
		}
	}
	
	protected void assignCloudletToVm(OnlineCloudlet cloudlet, int vmId, double workload) {
		cloudlet.setVmId(vmId);
		cloudlet.setDelay(getWorkload(vmId));
		setWorkload(vmId, workload);
		getCloudletScheduledList().add(cloudlet);
	}
}

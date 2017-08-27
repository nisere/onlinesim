package nisere.onlinesim.algorithm;

import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

/**
 * Sufferage algorithm
 * 
 * @author Alina Chera
 *
 */
public class SufferageAlgorithm extends StaticAlgorithm {

	/**
	 * Creates the schedule with Sufferage algorithm.
	 */
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList,
			List<? extends OnlineVm> vmList, List<? extends VmType> vmTypes, double time) {
		
		updateWorkload(time);
		
		boolean isNotScheduled = true;
		while (isNotScheduled) {
			OnlineCloudlet maxCloudlet = null;
			int maxVmId = -1;
			OnlineVm maxVm = null;
			double maxSuffer = -1;
			double maxC = -1;
			for (OnlineCloudlet cloudlet : cloudletList) {
				// if this cloudlet was bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				OnlineCloudlet minCloudlet = null;
				int minVmId = -1;
				OnlineVm minVm = null;
				double firstMin = -1;
				double secondMin = -1;
				for (OnlineVm vm : vmList) {
					double oldFirstMin = -1;
					double cij = getWorkload(vm.getId())
							+ cloudlet.getCloudletLength() / vm.getMips();
					// find first and second min of Cij = Wi + Eij, Cxj and Ckj
					if (firstMin == -1 || firstMin > cij) {
						oldFirstMin = firstMin;
						firstMin = cij;
						minCloudlet = cloudlet;
						minVmId = vm.getId();
						minVm = vm;
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
					maxVm = minVm;
				}
			}
			if (maxSuffer >= 0) {
				maxCloudlet.setVmId(maxVmId);
				maxCloudlet.setVm(maxVm);
				maxCloudlet.setDelay(getWorkload(maxVmId));
				setWorkload(maxVmId,maxC);
				getScheduledCloudletList().add(maxCloudlet);
			} else {
				isNotScheduled = false;
			}
		}
	}

}

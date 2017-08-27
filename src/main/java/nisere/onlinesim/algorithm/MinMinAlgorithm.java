package nisere.onlinesim.algorithm;

import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

/**
 * MinMin algorithm
 * 
 * @author Alina Chera
 *
 */
public class MinMinAlgorithm extends StaticAlgorithm {

	/**
	 * Creates the schedule with MinMin algorithm.
	 */
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList,
			List<? extends OnlineVm> vmList, List<? extends VmType> vmTypes, double time) {
		
		updateWorkload(time);
		
		boolean isNotScheduled = true;
		
		while (isNotScheduled) {
			OnlineCloudlet minCloudlet = null;
			int minVmId = -1;
			OnlineVm minVm = null;
			double min = -1;
			for (OnlineCloudlet cloudlet : cloudletList) {
				// if this cloudlet is bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
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
						minVm = vm;
					}
				}
			}
			if (min >= 0) {
				minCloudlet.setVmId(minVmId);
				minCloudlet.setVm(minVm);
				minCloudlet.setDelay(getWorkload(minVmId));
				setWorkload(minVmId, min);
				getScheduledCloudletList().add(minCloudlet);
			} else {
				isNotScheduled = false;
			}
		}
	}

}

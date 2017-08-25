package nisere.onlinesim.algorithm;

import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

/**
 * MaxMin algorithm
 * 
 * @author Alina Chera
 *
 */
public class MaxMinAlgorithm extends StaticAlgorithm {
	
	/**
	 * Creates the schedule with MaxMin algorithm.
	 */
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList,
			List<? extends OnlineVm> vmList, List<? extends VmType> vmTypes, double time) {

		updateWorkload(time);
		
		boolean isNotScheduled = true;
		while (isNotScheduled) {
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
				maxCloudlet.setDelay(getWorkload(maxVmId));
				setWorkload(maxVmId,max);
				getCloudletScheduledList().add(maxCloudlet);
			} else {
				isNotScheduled = false;
			}
		}
	}

}

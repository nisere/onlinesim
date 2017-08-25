package nisere.onlinesim.algorithm;

import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

/**
 * WorkQueue algorithm
 * 
 * @author Alina Chera
 *
 */
public class WorkQueueAlgorithm extends StaticAlgorithm {
	
	/**
	 * Creates the schedule with WorkQueue algorithm with a modification:
	 * the selection of cloudlets is in order and not random like in the original algorithm
	 */
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList,
			List<? extends OnlineVm> vmList, List<? extends VmType> vmTypes, double time) {
		
		updateWorkload(time);
		
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
			for (OnlineVm vm : vmList) {
				// find VM with min workload
				if (min == -1 || min > getWorkload(vm.getId())) {
					min = getWorkload(vm.getId());
					minvm = vm;
				}
			}

			if (min >= 0) {
				// schedule cloudlet on VM with min workload
				cloudlet.setVmId(minvm.getId());
				cloudlet.setDelay(getWorkload(minvm.getId()));
				getCloudletScheduledList().add(cloudlet);
				double newWorkload = getWorkload(minvm.getId()) + cloudlet.getCloudletLength() 
						/ minvm.getMips();
				setWorkload(minvm.getId(), newWorkload);
			} else {
				isNotScheduled = false;
			}
		}
	}
	
}

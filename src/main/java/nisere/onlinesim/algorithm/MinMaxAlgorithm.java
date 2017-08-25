package nisere.onlinesim.algorithm;

import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

/**
 * MinMax algorithm
 * 
 * @author Alina Chera
 *
 */
public class MinMaxAlgorithm extends StaticAlgorithm {

	/**
	 * Creates the schedule with MinMax algorithm.
	 */
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList,
			List<? extends OnlineVm> vmList, List<? extends VmType> vmTypes, double time) {

		updateWorkload(time);
		
		boolean isNotScheduled = true;
		
		while (isNotScheduled) {
			OnlineCloudlet maxCloudlet = null;
			int maxVmId = -1;
			double max = -1;
			double maxC = -1;
			for (OnlineCloudlet cloudlet : cloudletList) {
				// if this cloudlet was bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				OnlineCloudlet minCloudlet = null;
				int minVmId = -1;
				double minC = -1;
				double minCE = -1;
				double minE = -1;
				for (OnlineVm vm : vmList) {
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
				maxCloudlet.setDelay(getWorkload(maxVmId));
				setWorkload(maxVmId,maxC);
				getCloudletScheduledList().add(maxCloudlet);
			} else {
				isNotScheduled = false;
			}
		}
	}

}

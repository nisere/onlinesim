package nisere.schedsim.algorithm;

import java.util.ArrayList;
import java.util.List;

import nisere.schedsim.SchedulingAlgorithm;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class MinMaxAlgorithm implements SchedulingAlgorithm {
	/** Processor workload. */
	protected double[] workload;

	/** List of scheduled cloudlets. */
	protected List<Cloudlet> cloudletScheduledList;

	/**
	 * Creates the schedule with MinMax algorithm.
	 */
	public void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList) {
		workload = new double[vmList.size()];
		cloudletScheduledList = new ArrayList<Cloudlet>();
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
							|| minC > workload[vm.getId()]
									+ cloudlet.getCloudletLength()
									/ vm.getMips()) {
						minC = workload[vm.getId()]
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
				workload[maxVmId] = maxC;
				cloudletScheduledList.add(maxCloudlet);
			} else {
				isNotScheduled = false;
			}
		}
	}

	/**
	 * Gets the list of scheduled cloudlets.
	 * 
	 * @return the list of scheduled cloudlets
	 */
	public List<? extends Cloudlet> getCloudletScheduledList() {

		return cloudletScheduledList;
	}
}

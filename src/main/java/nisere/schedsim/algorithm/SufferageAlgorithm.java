package nisere.schedsim.algorithm;

import java.util.ArrayList;
import java.util.List;

import nisere.schedsim.SchedulingAlgorithm;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class SufferageAlgorithm implements SchedulingAlgorithm {
	/** Processor workload. */
	protected double[] workload;

	/** List of scheduled cloudlets. */
	protected List<Cloudlet> cloudletScheduledList;

	/**
	 * Creates the schedule with Sufferage algorithm.
	 */
	public void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList) {
		workload = new double[vmList.size()];
		cloudletScheduledList = new ArrayList<Cloudlet>();
		boolean isNotScheduled = true;
		while (isNotScheduled) {
			Cloudlet maxCloudlet = null;
			int maxVmId = -1;
			double maxSuffer = -1;
			double maxC = -1;
			for (Cloudlet cloudlet : cloudletList) {
				// if this cloudlet was bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				Cloudlet minCloudlet = null;
				int minVmId = -1;
				double firstMin = -1;
				double secondMin = -1;
				for (Vm vm : vmList) {
					double oldFirstMin = -1;
					double cij = workload[vm.getId()]
							+ cloudlet.getCloudletLength() / vm.getMips();
					// find first and second min of Cij = Wi + Eij, Cxj and Ckj
					if (firstMin == -1 || firstMin > cij) {
						oldFirstMin = firstMin;
						firstMin = cij;
						minCloudlet = cloudlet;
						minVmId = vm.getId();
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
				}
			}
			if (maxSuffer >= 0) {
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

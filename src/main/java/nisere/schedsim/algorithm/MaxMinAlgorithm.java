package nisere.schedsim.algorithm;

import java.util.ArrayList;
import java.util.List;

import nisere.schedsim.MySchedulingAlgorithm;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class MaxMinAlgorithm implements MySchedulingAlgorithm {
	/** Processor workload. */
	protected double[] workload;

	/** List of scheduled cloudlets. */
	protected List<Cloudlet> cloudletScheduledList;

	/**
	 * Creates the schedule with MaxMin algorithm.
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
			for (Cloudlet cloudlet : cloudletList) {
				// if this cloudlet was bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				Cloudlet minCloudlet = null;
				int minVmId = -1;
				double min = -1;
				for (Vm vm : vmList) {
					// find min of Cij = Wi + Eij
					if (min == -1
							|| min > workload[vm.getId()]
									+ cloudlet.getCloudletLength()
									/ vm.getMips()) {
						min = workload[vm.getId()]
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
				workload[maxVmId] = max;
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

package nisere.schedsim.algorithm;

import java.util.ArrayList;
import java.util.List;

import nisere.schedsim.SchedulingAlgorithm;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class WorkQueueAlgorithm implements SchedulingAlgorithm {
	/** Processor workload. */
	protected double[] workload;

	/** List of scheduled cloudlets. */
	protected List<Cloudlet> cloudletScheduledList;

	/**
	 * Creates the schedule with WorkQueue algorithm.
	 */
	public void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList) {
		workload = new double[vmList.size()];
		cloudletScheduledList = new ArrayList<Cloudlet>();
		boolean isNotScheduled = true;
		int randomId = 0;
		while (isNotScheduled && randomId < cloudletList.size()) {
			// select cloudlet randomly - skipped; instead take in order
			Cloudlet cloudlet = cloudletList.get(randomId++);

			// if this cloudlet was bound to a VM continue
			if (cloudlet.getVmId() >= 0) {
				continue;
			}

			double min = -1;
			int minVmId = -1;
			for (Vm vm : vmList) {
				// find VM with min workload
				if (min == -1 || min > workload[vm.getId()]) {
					min = workload[vm.getId()];
					minVmId = vm.getId();
				}
			}

			if (min >= 0) {
				// schedule cloudlet on VM with min workload
				cloudlet.setVmId(minVmId);
				cloudletScheduledList.add(cloudlet);
				workload[minVmId] += cloudlet.getCloudletLength()
						/ vmList.get(minVmId).getMips();
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

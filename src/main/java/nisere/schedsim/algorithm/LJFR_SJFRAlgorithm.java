package nisere.schedsim.algorithm;

import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class LJFR_SJFRAlgorithm extends SchedulingAlgorithm {
	/** Processor workload. */
	protected double[] workload;

	@Override
	protected void initCloudletScheduledList() {
		setCloudletScheduledList(new LinkedList<Cloudlet>());
	}

	/**
	 * Creates the schedule with LJFR_SJFR algorithm.
	 */
	public void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList) {
		workload = new double[vmList.size()];
		//cloudletScheduledList = new ArrayList<Cloudlet>();
		boolean isNotScheduled = true;

		// first noVms cloudlets are scheduled with MaxMin
		int countVm = vmList.size();
		while (isNotScheduled && countVm > 0) {
			countVm--;

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
				getCloudletScheduledList().add(maxCloudlet);
			} else {
				isNotScheduled = false;
			}
		}

		// next use alternatively MinMin and MaxMin
		while (isNotScheduled) {
			Cloudlet minminCloudlet = null;
			int minminVmId = -1;
			double minmin = -1;
			for (Cloudlet cloudlet : cloudletList) {
				// if this cloudlet was bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				for (Vm vm : vmList) {
					// fin min of Cij = Wi + Eij
					if (minmin == -1
							|| minmin > workload[vm.getId()]
									+ cloudlet.getCloudletLength()
									/ vm.getMips()) {
						minmin = workload[vm.getId()]
								+ cloudlet.getCloudletLength() / vm.getMips();
						minminCloudlet = cloudlet;
						minminVmId = vm.getId();
					}
				}
			}
			if (minmin >= 0) {
				minminCloudlet.setVmId(minminVmId);
				workload[minminVmId] = minmin;
				getCloudletScheduledList().add(minminCloudlet);
			} else {
				isNotScheduled = false;
			}

			if (!isNotScheduled) {
				break;
			}

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
				getCloudletScheduledList().add(maxCloudlet);
			} else {
				isNotScheduled = false;
			}
		}
	}
}

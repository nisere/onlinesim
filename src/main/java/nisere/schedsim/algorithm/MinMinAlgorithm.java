package nisere.schedsim.algorithm;

import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class MinMinAlgorithm extends SchedulingAlgorithm {
	/** Processor workload. */
	protected double[] workload;

	@Override
	protected void initCloudletScheduledList() {
		setCloudletScheduledList(new LinkedList<Cloudlet>());
	}
	/**
	 * Creates the schedule with MinMin algorithm.
	 */
	public void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList) {
		workload = new double[vmList.size()];
		boolean isNotScheduled = true;
		while (isNotScheduled) {
			Cloudlet minCloudlet = null;
			int minVmId = -1;
			double min = -1;
			for (Cloudlet cloudlet : cloudletList) {
				// if this cloudlet is bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				for (Vm vm : vmList) {
					// fin min of Cij = Wi + Eij
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
			}
			if (min >= 0) {
				minCloudlet.setVmId(minVmId);
				workload[minVmId] = min;
				getCloudletScheduledList().add(minCloudlet);
			} else {
				isNotScheduled = false;
			}
		}
	}
}

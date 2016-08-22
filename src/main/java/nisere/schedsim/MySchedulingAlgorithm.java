package nisere.schedsim;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public interface MySchedulingAlgorithm {

	/** Creates the schedule. */
	void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList);

	/** Get the scheduled cloudlet list. */
	List<? extends Cloudlet> getCloudletScheduledList();
}

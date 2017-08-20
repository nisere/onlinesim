package nisere.onlinesim.algorithm;

import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import nisere.onlinesim.VmType;

/**
 * No algorithm, it leaves the list of cloudlets unscheduled.
 * 
 * @author Alina Chera
 *
 */
public class NOAlgorithm extends SchedulingAlgorithm {

	@Override
	protected void initCloudletScheduledList() {
		setCloudletScheduledList(new LinkedList<Cloudlet>());	
	}

	@Override
	public void computeSchedule(List<? extends Cloudlet> cloudletList,
			List<? extends Vm> vmList, List<? extends VmType> vmTypes) {
		getCloudletScheduledList().addAll(cloudletList);
	}

}

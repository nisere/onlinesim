package nisere.onlinesim.algorithm;

import java.util.LinkedList;
import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
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
		setCloudletScheduledList(new LinkedList<OnlineCloudlet>());	
	}

	@Override
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList,
			List<? extends OnlineVm> vmList, List<? extends VmType> vmTypes) {
		getCloudletScheduledList().addAll(cloudletList);
	}

}

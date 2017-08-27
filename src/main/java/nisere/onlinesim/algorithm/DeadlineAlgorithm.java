package nisere.onlinesim.algorithm;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

public class DeadlineAlgorithm extends StaticAlgorithm {

	@Override
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList, List<? extends OnlineVm> vmList,
			List<? extends VmType> vmTypes, double time) {
		sortCloudlets(cloudletList);
		
		updateWorkload(time);
		
		for (OnlineCloudlet cloudlet : cloudletList) {
			double min = Double.MAX_VALUE;
			OnlineVm minVm = null;
			for (OnlineVm vm : vmList) {
				double execTime = getWorkload(vm.getId()) + cloudlet.getCloudletLength() / vm.getMips();
				if (min > execTime && execTime < cloudlet.getArrivalTime() + cloudlet.getDeadline()) {
					min = execTime;
					minVm = vm;
				}
			}
			if (minVm != null) {
				cloudlet.setVmId(minVm.getId());
				cloudlet.setVm(minVm);
				cloudlet.setDelay(getWorkload(minVm.getId()));
				setWorkload(minVm.getId(), min);
				getScheduledCloudletList().add(cloudlet);
			} else {
				getUnscheduledCloudletList().add(cloudlet);
			}
		}

	}

	protected void sortCloudlets(List<? extends OnlineCloudlet> cloudletList) {
		Collections.sort(cloudletList, (c1,c2) -> 
			(c1.getDeadline() < c2.getDeadline() ? -1 : (c1.getDeadline() > c2.getDeadline() ? 1 : 0)));
	}

}

package nisere.onlinesim.algorithm;

import java.util.Collections;
import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;
/**
 * Computes schedule taking deadline into account.
 * First it sorts the cloudlets by deadline ascending.
 * Then it chooses the VM that if used it will finish the execution the fastest
 * (it yields the minimum workload)
 * If no VM is found that would finish the cloudlet within deadline the cloudlet
 * will remain unscheduled.
 * 
 * @author Nisere
 *
 */
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
	
	/**
	 * Sorts a list of cloudlets
	 * @param cloudletList cloudlet list
	 */
	protected void sortCloudlets(List<? extends OnlineCloudlet> cloudletList) {
		Collections.sort(cloudletList, (c1,c2) -> compare(c1,c2));
	}
	
	/**
	 * Compares two cloudlets
	 * @param c1 first cloudlet
	 * @param c2 second cloudlet
	 * @return -1 if c1 < c2, 1 if c1 > c2, 0 if c1 = c2
	 */
	protected int compare(OnlineCloudlet c1, OnlineCloudlet c2) {
		double realDeadline1 = c1.getArrivalTime() + c1.getDeadline();
		double realDeadline2 = c2.getArrivalTime() + c2.getDeadline();
		return (realDeadline1 < realDeadline2 ? -1 : (realDeadline1 > realDeadline2 ? 1 : 0));
	}

}

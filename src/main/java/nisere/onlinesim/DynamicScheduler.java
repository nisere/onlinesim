package nisere.onlinesim;

import java.util.LinkedList;
import java.util.List;

import nisere.onlinesim.algorithm.SchedulingAlgorithm;

public class DynamicScheduler extends Scheduler {

	public DynamicScheduler(List<? extends VmType> vmTypes, OnlineDatacenterBroker broker,
			List<? extends OnlineVm> vmList, List<? extends OnlineCloudlet> cloudletList, SchedulingAlgorithm algorithm,
			int schedulingInterval) throws Exception {
		super(vmTypes, broker, vmList, cloudletList, algorithm, schedulingInterval);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void runSchedulingAlgorithm(List<? extends OnlineCloudlet> cloudlets, 
			List<? extends OnlineVm> vms, List<? extends VmType> types, double delay) {
		
		//update cloudlet queue: add to cloudletList scheduled cloudlets not executed yet to be rescheduled
		List<OnlineCloudlet> removedList = new LinkedList<>();
		for (OnlineCloudlet cloudlet : getAlgorithm().getCloudletScheduledList()) {
			if (delay < cloudlet.getDelay()) {
				getAlgorithm().removeScheduledCloudlet(cloudlet, delay);
				removedList.add(cloudlet);			
			}
		}
		getAlgorithm().getCloudletScheduledList().removeAll(removedList);
		
		getAlgorithm().computeSchedule(cloudlets, vms, types, delay);
	}

}

package nisere.onlinesim;

import java.util.LinkedList;
import java.util.List;

import nisere.onlinesim.algorithm.SchedulingAlgorithm;

public class DynamicScheduler extends Scheduler {
	/** The estimated time required for scheduling */
	private double computationTime ;

	public DynamicScheduler(List<? extends VmType> vmTypes, OnlineDatacenterBroker broker,
			List<? extends OnlineVm> vmList, List<? extends OnlineCloudlet> cloudletList, SchedulingAlgorithm algorithm,
			int schedulingInterval) throws Exception {
		super(vmTypes, broker, vmList, cloudletList, algorithm, schedulingInterval);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void runSchedulingAlgorithm(List<? extends OnlineCloudlet> cloudlets, double delay) {
		
		//update cloudlet queue: add to cloudletList scheduled cloudlets not executed yet to be rescheduled
		List<OnlineCloudlet> removedList = new LinkedList<>();
		for (OnlineCloudlet cloudlet : getAlgorithm().getScheduledCloudletList()) {
			if (delay + getComputationTime() < cloudlet.getDelay()) {
				getAlgorithm().unscheduleCloudlet(cloudlet, delay);
				removedList.add(cloudlet);			
			}
		}
		getAlgorithm().getScheduledCloudletList().removeAll(removedList);
		((List<OnlineCloudlet>)cloudlets).addAll(removedList);
		
		getAlgorithm().computeSchedule(cloudlets, getVmList(), getVmTypes(), delay);
	}

	public double getComputationTime() {
		return computationTime;
	}

	public void setComputationTime(double computationTime) {
		this.computationTime = computationTime;
	}

}

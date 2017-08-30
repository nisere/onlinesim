package nisere.onlinesim;

import java.util.LinkedList;
import java.util.List;

import nisere.onlinesim.algorithm.SchedulingAlgorithm;

public class DynamicHybridScheduler extends Scheduler{
	
	private SchedulingAlgorithm publicAlgorithm;
	private List<? extends OnlineVm> publicVmList;
	private List<? extends VmType> publicVmTypes;
	
	/** The estimated time required for scheduling */
	private double computationTime ;
	
	public DynamicHybridScheduler(List<? extends VmType> vmTypes, OnlineDatacenterBroker broker,
			List<? extends OnlineVm> vmList, List<? extends OnlineCloudlet> cloudletList, 
			SchedulingAlgorithm algorithm, int schedulingInterval,
			SchedulingAlgorithm publicAlgorithm,
			List<? extends OnlineVm> publicVmList, 
			List<? extends VmType> publicVmTypes) throws Exception {
		super(vmTypes, broker, vmList, cloudletList, algorithm, schedulingInterval);
		this.publicAlgorithm = publicAlgorithm;
		this.publicVmList = publicVmList;
		this.publicVmTypes = publicVmTypes;
	}
	
	public void prepareSimulation() {
		scheduleCloudlets();
		
		List<? extends OnlineCloudlet> scheduledCloudlets = getAlgorithm().getScheduledCloudletList();
		scheduledCloudlets.addAll(getPublicAlgorithm().getScheduledCloudletList());

		getVmList().addAll(getPublicVmList());
		
		getBroker().submitVmList(getVmList());
		getBroker().submitCloudletList(scheduledCloudlets);
		
		getAlgorithm().getUnscheduledCloudletList().addAll(getPublicAlgorithm().getUnscheduledCloudletList());
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

		//run private algorithm
		getAlgorithm().computeSchedule(cloudlets, getVmList(), getVmTypes(), delay);
		
		//run public algorithm
		getPublicAlgorithm().computeSchedule(getAlgorithm().getUnscheduledCloudletList(), getPublicVmList(), getPublicVmTypes(), delay);
		getAlgorithm().initUnscheduledCloudletList();
	}

	public SchedulingAlgorithm getPublicAlgorithm() {
		return publicAlgorithm;
	}

	public void setPublicAlgorithm(SchedulingAlgorithm publicAlgorithm) {
		this.publicAlgorithm = publicAlgorithm;
	}

	@SuppressWarnings("unchecked")
	public <T extends OnlineVm> List<T> getPublicVmList() {
		return (List<T>)publicVmList;
	}

	public void setPublicVmList(List<? extends OnlineVm> publicVmList) {
		this.publicVmList = publicVmList;
	}

	@SuppressWarnings("unchecked")
	public <T extends VmType> List<T> getPublicVmTypes() {
		return (List<T>)publicVmTypes;
	}

	public void setPublicVmTypes(List<? extends VmType> publicVmTypes) {
		this.publicVmTypes = publicVmTypes;
	}

	public double getComputationTime() {
		return computationTime;
	}

	public void setComputationTime(double computationTime) {
		this.computationTime = computationTime;
	}

}

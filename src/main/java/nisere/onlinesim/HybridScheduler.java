package nisere.onlinesim;

import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;

import nisere.onlinesim.algorithm.SchedulingAlgorithm;

public class HybridScheduler extends Scheduler{
	
	private SchedulingAlgorithm publicAlgorithm;
	private List<? extends OnlineVm> publicVmList;
	private List<? extends VmType> publicVmTypes;
	
	public HybridScheduler(List<? extends VmType> vmTypes, OnlineDatacenterBroker broker,
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
		
		List<? extends OnlineCloudlet> scheduledCloudlets = getAlgorithm().getCloudletScheduledList();
		scheduledCloudlets.addAll(getPublicAlgorithm().getCloudletScheduledList());

		getVmList().addAll(getPublicVmList());
		
		getBroker().submitVmList(getVmList());
		getBroker().submitCloudletList(scheduledCloudlets);
	}
	
	@Override
	protected void runSchedulingAlgorithm(List<? extends OnlineCloudlet> cloudlets, double delay) {
		
		//run private algorithm
		getAlgorithm().computeSchedule(cloudlets, getVmList(), getVmTypes(), delay);
		
		//run public algorithm
		getPublicAlgorithm().computeSchedule(getAlgorithm().getCloudletUnscheduledList(), getPublicVmList(), getPublicVmTypes(), delay);
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

}

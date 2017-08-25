package nisere.onlinesim;

import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;

import nisere.onlinesim.algorithm.SchedulingAlgorithm;

public class HybridScheduler extends Scheduler{
	
	private SchedulingAlgorithm publicAlgorithm;
	private List<? extends Datacenter> privateDatacenters;
	private List<? extends Datacenter> publicDatacenters;
	
	public HybridScheduler(List<? extends VmType> vmTypes, OnlineDatacenterBroker broker,
			List<? extends OnlineVm> vmList, List<? extends OnlineCloudlet> cloudletList, SchedulingAlgorithm algorithm,
			SchedulingAlgorithm publicAlgorithm, int schedulingInterval,
			List<? extends Datacenter> privateDatacenters,
			List<? extends Datacenter> publicDatacenters) throws Exception {
		super(vmTypes, broker, vmList, cloudletList, algorithm, schedulingInterval);
		this.publicAlgorithm = publicAlgorithm;
		this.privateDatacenters = privateDatacenters;
		this.publicDatacenters = publicDatacenters;
	}
	
	public void prepareSimulation() {
		List<OnlineCloudlet> scheduledCloudlets = getScheduledCloudlets(getCloudletList(), getVmList(), getVmTypes());
		scheduledCloudlets.addAll(getPublicAlgorithm().getCloudletScheduledList());
		getBroker().submitVmList(getVmList());
		getBroker().submitCloudletList(scheduledCloudlets);
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
		
		//run private algorithm
		getAlgorithm().computeSchedule(cloudlets, vms, types, delay);
		
		//run public algorithm
		getPublicAlgorithm().computeSchedule(getAlgorithm().getCloudletUnscheduledList(), vms, types, delay);
	}

	public SchedulingAlgorithm getPublicAlgorithm() {
		return publicAlgorithm;
	}

	public void setPublicAlgorithm(SchedulingAlgorithm publicAlgorithm) {
		this.publicAlgorithm = publicAlgorithm;
	}

	public List<? extends Datacenter> getPrivateDatacenters() {
		return privateDatacenters;
	}

	public void setPrivateDatacenters(List<? extends Datacenter> privateDatacenters) {
		this.privateDatacenters = privateDatacenters;
	}

	public List<? extends Datacenter> getPublicDatacenters() {
		return publicDatacenters;
	}

	public void setPublicDatacenters(List<? extends Datacenter> publicDatacenters) {
		this.publicDatacenters = publicDatacenters;
	}

}

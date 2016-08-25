package nisere.schedsim;

import java.util.HashMap;
import java.util.List;

import nisere.schedsim.algorithm.SchedulingAlgorithm;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

public class OnlineScheduler {
	
	private HashMap<String,? extends Datacenter> datacenters;
	private DatacenterBroker broker;
	private List<? extends Vm> vmList;
	private List<? extends Cloudlet> cloudletList;	
	private SchedulingAlgorithm algorithm;
	
	public OnlineScheduler(HashMap<String,Datacenter> datacenters,
			DatacenterBroker broker,
			List<? extends Vm> vmList,
			List<? extends Cloudlet> cloudletList,
			SchedulingAlgorithm algorithm) throws Exception {
		this.datacenters = datacenters;
		this.broker = broker;
		this.vmList = vmList;
		this.cloudletList = cloudletList;		
		this.algorithm = algorithm;	
	}

	public SchedulingAlgorithm getAlgorithm() {
		return algorithm;
	}

//	public <T extends DatacenterBroker> T getBroker() {
//		return (T)broker;
//	}
	public DatacenterBroker getBroker() {
		return broker;
	}

	public <T extends Cloudlet> List<T> getCloudletList() {
		return (List<T>)cloudletList;
	}

	public <T extends Datacenter> HashMap<String,T> getDatacenters() {
		return (HashMap<String,T>)datacenters;
	}

	public <T extends Vm> List<T> getVmList() {
		return (List<T>)vmList;
	}

	public List<Cloudlet> getFinishedCloudlets() {
		return getBroker().getCloudletReceivedList();
	}

	public void prepareSimulation() {
		List<Cloudlet> cloudlets = getCloudlets();
		List<Cloudlet> scheduledCloudlets = scheduleCloudlets(cloudlets, getVmList());
		submitCloudlets(scheduledCloudlets);
	}

	protected List<Cloudlet> getCloudlets() {
		return getCloudletList();
	}
	
	protected List<Cloudlet> scheduleCloudlets(List<Cloudlet> cloudlets, List<Vm> vms) {
		getAlgorithm().computeSchedule(cloudlets, vms);
		return getAlgorithm().getCloudletScheduledList();
	}
	
	protected void submitCloudlets(List<Cloudlet> cloudlets) {
		getBroker().submitVmList(getVmList());
		getBroker().submitCloudletList(cloudlets);
	}
	
	public void setAlgorithm(SchedulingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public void setBroker(DatacenterBroker broker) {
		this.broker = broker;
	}

	public void setCloudletList(List<? extends Cloudlet> cloudletList) {
		this.cloudletList = cloudletList;
	}

	public void setDatacenters(HashMap<String,Datacenter> datacenters) {
		this.datacenters = datacenters;
	}

	public void setVmList(List<? extends Vm> vmList) {
		this.vmList = vmList;
	}
}

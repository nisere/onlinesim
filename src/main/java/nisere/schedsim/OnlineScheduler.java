package nisere.schedsim;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nisere.schedsim.algorithm.SchedulingAlgorithm;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

public class OnlineScheduler {
	
	private HashMap<String,? extends Datacenter> datacenters;
	private DatacenterBroker broker;
	private List<? extends Vm> vmList;
	private List<? extends Cloudlet> cloudletList;	
	private SchedulingAlgorithm algorithm;
	
	public <T extends Vm> List<T> getVmList() {
		return (List<T>)vmList;
	}

	public void setVmList(List<? extends Vm> vmList) {
		this.vmList = vmList;
	}

	public <T extends Cloudlet> List<T> getCloudletList() {
		return (List<T>)cloudletList;
	}

	public void setCloudletList(List<? extends Cloudlet> cloudletList) {
		this.cloudletList = cloudletList;
	}

	public SchedulingAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SchedulingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

//	public <T extends DatacenterBroker> T getBroker() {
//		return (T)broker;
//	}
	public DatacenterBroker getBroker() {
		return broker;
	}

	public void setBroker(DatacenterBroker broker) {
		this.broker = broker;
	}

	public <T extends Datacenter> HashMap<String,T> getDatacenters() {
		return (HashMap<String,T>)datacenters;
	}

	public void setDatacenters(HashMap<String,Datacenter> datacenters) {
		this.datacenters = datacenters;
	}
	
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

	public void prepareSimulation() {
		List<Cloudlet> cloudlets = getCloudlets();
		List<Cloudlet> scheduledCloudlets = scheduleCloudlets(cloudlets, getVmList());
		submitCloudlets(scheduledCloudlets);
	}
	
	public void printResult() {
		printCloudletList(getBroker().getCloudletReceivedList());
	}

	protected void submitCloudlets(List<Cloudlet> cloudlets) {
		getBroker().submitVmList(getVmList());
		getBroker().submitCloudletList(cloudlets);
	}

	protected List<Cloudlet> scheduleCloudlets(List<Cloudlet> cloudlets, List<Vm> vms) {
		getAlgorithm().computeSchedule(cloudlets, vms);
		return algorithm.getCloudletScheduledList();
	}

	protected List<Cloudlet> getCloudlets() {
		
		//int no = getQueue().getCloudletsNo();
		//System.out.format("Scheduler: %s%n", no);
		//List<Cloudlet> cloudlets = getQueue().getNCloudlets(no);
		List<Cloudlet> cloudlets = getCloudletList();
		
		return cloudlets;
	}
	
	protected void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;
		double flowtime = 0;
		double cost = 0;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time");

		int[] counter = new int[13];
		int index = 0;
		int step = 1000;
		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));

				flowtime += cloudlet.getFinishTime();

				if (cloudlet.getFinishTime() <= step * (index + 1)) {
					counter[index]++;
				} else {
					index++;
					counter[index] = counter[index - 1] + 1;
				}
			}
		}

		Log.printLine();
		Log.printLine("Flowtime: " + dft.format(flowtime));
		Log.printLine();
		for (int i = 0; i < 13; i++) {
			Log.print(counter[i] + ",");
		}
		Log.printLine();
	}
}

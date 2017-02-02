package nisere.schedsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nisere.schedsim.MyCloudlet;
import nisere.schedsim.MyDatacenterBroker;
import nisere.schedsim.MyVm;
import nisere.schedsim.Scheduler;
import nisere.schedsim.algorithm.NOAlgorithm;
import nisere.schedsim.algorithm.SchedulingAlgorithm;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * This example is used to test scheduling on more datacenters.
 * It uses Scheduler class to simulate online arrival of tasks.
 * 
 * @author Alina Chera
 *
 */
public class OnlineMain4 {

	public static void main(String[] args) {
		int noCloudlets = 4;
		int noVms = 1;
		// generate [minMipsUnif;maxMipsUnif) and multiply with 1000 to get
		// mips
		int minMipsUnif = 1;
		int maxMipsUnif = 2;
		// generate length [minLengthUnif;maxLengthUnif)
		int minLengthUnif = 100000;
		int maxLengthUnif = 200000;
		int seed = 9;
		int schedulingInterval = 400;
		long delayInterval = 400;
		int intervals = 2;
		
		try {
			// Initialize the CloudSim package before creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events
			CloudSim.init(num_user, calendar, trace_flag);
			// Datacenters are the resource providers in CloudSim. We need at
			// list one of them to run a CloudSim simulation
			Datacenter datacenter2 = createDatacenter("Private", noVms);
			Datacenter datacenter3 = createDatacenter("Public", noVms);

//			
//			HashMap<String,Datacenter> datacenters = new HashMap<>();
//			datacenters.put("Private", datacenter2);
//			datacenters.put("Public", datacenter3);
			
			List<Datacenter> datacenters = new ArrayList<>();
			datacenters.add(datacenter2);
			datacenters.add(datacenter3);
			
			
			//-----------
			int[] datacenterIds = new int[datacenters.size()];
//			int i = 0;
//			for(Map.Entry<String, Datacenter> entry : datacenters.entrySet()){
//			    //System.out.printf("Key : %s and Value: %s %n", entry.getKey(), entry.getValue());
//				datacenterIds[i++] = entry.getValue().getId();
//			}
			//-----------
			
			MyDatacenterBroker broker = new MyDatacenterBroker("MyBroker");
			
			List<MyVm> vmList = createRandomMyVms(broker.getId(),noVms,minMipsUnif, maxMipsUnif, seed, datacenterIds);
			
			List<MyCloudlet> cloudletList = createRandomMyCloudlets(broker.getId(),noCloudlets,minLengthUnif, maxLengthUnif, seed, delayInterval, intervals);
			
			SchedulingAlgorithm algorithm = new NOAlgorithm();
			
			Scheduler scheduler = new Scheduler(datacenters,broker,vmList,cloudletList,algorithm,schedulingInterval);

			scheduler.prepareSimulation();

			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			printResult(scheduler.getFinishedCloudlets());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Datacenter createDatacenter(String name, int noHosts) {

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		// our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 10000;

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store
																// Pe id and
																// MIPS Rating

		// 4. Create Host with its id and list of PEs and add them to the list
		// of machines
		int hostId = 0;
		int ram = 2048; // host memory
		long storage = 1000000; // host storage
		int bw = 10000;

		// add noHosts machines
		for (int i = 0; i < noHosts; i++) {
			hostList.add(new Host(hostId++, new RamProvisionerSimple(ram),
					new BwProvisionerSimple(bw), storage, peList,
					new VmSchedulerSpaceShared(peList))); // This is our machine
		}

		// 5. Create a DatacenterCharacteristics object that stores the
		// properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are
																		// not
																		// adding
																		// SAN
																		// devices
																		// by
																		// now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics,
					new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}
	
	private static List<MyVm> createRandomMyVms(int brokerId, int noVms, int minMipsUnif, int maxMipsUnif,
			int seed, int[] datacenterIds) {
		List<MyVm> vmlist = new ArrayList<>();

		// VM description
		int vmid = 0;
		int mips = 1000;
		long size = 10000; // image size
		int ram = 512; // vm memory
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		int timeInterval = 0;
		double costPerTimeInterval = 0;
		//int datacenterId = -1;

		UniformDistr mipsUnif = new UniformDistr(minMipsUnif, maxMipsUnif,
				seed);

//		for (int datacenterId : datacenterIds) {
//		// add noVms VMs
//			for (int i = 0; i < noVms; i++) {
//				int mult = (int) mipsUnif.sample();
//				vmlist.add(new MyVm(vmid++, brokerId, mips * mult, pesNumber,
//						ram, bw, size, vmm, new CloudletSchedulerSpaceShared(), timeInterval, costPerTimeInterval,
//						datacenterId));
//			}
//		}
		
		for (int i = 0; i < noVms; i++) {
			int mult = (int) mipsUnif.sample();
			vmlist.add(new MyVm(vmid++, brokerId, mips * mult, pesNumber,
					ram, bw, size, vmm, new CloudletSchedulerSpaceShared(), 3, -1));
		}
		for (int i = 0; i < noVms; i++) {
			int mult = (int) mipsUnif.sample();
			vmlist.add(new MyVm(vmid++, brokerId, mips * mult, pesNumber,
					ram, bw, size, vmm, new CloudletSchedulerSpaceShared(), 2, -1));
		}
		
		return vmlist;
	}
	
	private static List<MyCloudlet> createRandomMyCloudlets(int brokerId, int noCloudlets, int minLengthUnif, int maxLengthUnif,
			int seed, long delayInterval, int intervals) {
		List<MyCloudlet> cloudletList = new ArrayList<>();

		// Cloudlet properties
		int id = 0;
		int pesNumber = 1;
		//long length = 250000;
		long fileSize = 0;
		long outputSize = 0;
		UtilizationModel utilizationModel = new UtilizationModelFull();
		int deadline = 0;

		UniformDistr lengthUnif = new UniformDistr(minLengthUnif,
				maxLengthUnif, seed);

		// add noCloudlets*intervals cloudlets
		for (int j = 0; j < intervals; j++) {
			for (int i = 0; i < noCloudlets; i++) {
				int randomLength = (int) lengthUnif.sample();
				long delay = j*delayInterval;
				MyCloudlet cloudlet = new MyCloudlet(id++, randomLength, pesNumber,
						fileSize, outputSize, utilizationModel,
						utilizationModel, utilizationModel, deadline, delay);
				cloudlet.setUserId(brokerId);
				cloudletList.add(cloudlet);
			}
		}
		return cloudletList;
	}

	private static void printResult(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;
		double flowtime = 0;
		//double cost = 0;

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

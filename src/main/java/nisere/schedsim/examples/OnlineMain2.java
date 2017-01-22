package nisere.schedsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import nisere.schedsim.MyDatacenter;
import nisere.schedsim.Scheduler;
import nisere.schedsim.algorithm.*;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * This example is used for testing different static scheduling algorithms.
 * It uses the Scheduler class.
 * It does the same thing as MyCloudSimExample2.
 * 
 * @author Alina Chera
 *
 */
public class OnlineMain2 {

	public static void main(String[] args) {
		int noCloudlets = 512;
		int noVms = 16;
		// generate [minMipsUnif;maxMipsUnif) and multiply with 1000 to get
		// mips
		int minMipsUnif = 1;
		int maxMipsUnif = 11;
		// generate length [minLengthUnif;maxLengthUnif)
		int minLengthUnif = 100000;
		int maxLengthUnif = 400000;
		int seed = 9;
		
		try {
			// Initialize the CloudSim package before creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events
			CloudSim.init(num_user, calendar, trace_flag);
			// Datacenters are the resource providers in CloudSim. We need at
			// list one of them to run a CloudSim simulation
			Datacenter datacenter0 = createDatacenter("Private", noVms);

			
			//HashMap<String,Datacenter> datacenters = new HashMap<>();
			//datacenters.put("Private", datacenter0);
			List<Datacenter> datacenters = new ArrayList<>();
			datacenters.add(datacenter0);
			
			DatacenterBroker broker = new DatacenterBroker("MyBroker");
			
			List<Vm> vmList = createRandomVms(broker.getId(),noVms,minMipsUnif, maxMipsUnif, seed);
			
			List<Cloudlet> cloudletList = createRandomCloudlets(broker.getId(),noCloudlets,minLengthUnif, maxLengthUnif, seed);
			
			//SchedulingAlgorithm algorithm = new NOAlgorithm();
			SchedulingAlgorithm algorithm = new WorkQueueAlgorithm();
			//SchedulingAlgorithm algorithm = new SufferageAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMinAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMaxAlgorithm();
			//SchedulingAlgorithm algorithm = new MaxMinAlgorithm();
			//SchedulingAlgorithm algorithm = new LJFR_SJFRAlgorithm();
			
			Scheduler scheduler = new Scheduler(datacenters,broker,vmList,cloudletList,algorithm);

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
		int ram = 2048; // host memory (MB)
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
	
	private static List<Vm> createRandomVms(int brokerId, int noVms, int minMipsUnif, int maxMipsUnif,
			int seed) {
		// Fourth step: Create virtual machines
		List<Vm> vmlist = new ArrayList<Vm>();

		// VM description
		int vmid = 0;
		int mips = 1000;
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		UniformDistr mipsUnif = new UniformDistr(minMipsUnif, maxMipsUnif,
				seed);

		// add noVms VMs
		for (int i = 0; i < noVms; i++) {
			int mult = (int) mipsUnif.sample();
			vmlist.add(new Vm(vmid++, brokerId, mips * mult, pesNumber,
					ram, bw, size, vmm, new CloudletSchedulerSpaceShared()));
		}
		return vmlist;
	}
	
	private static List<Cloudlet> createRandomCloudlets(int brokerId, int noCloudlets, int minLengthUnif, int maxLengthUnif,
			int seed) {
		// Fifth step: Create Cloudlets
		List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

		// Cloudlet properties
		int id = 0;
		int pesNumber = 1;
		//long length = 250000;
		long fileSize = 0;
		long outputSize = 0;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		UniformDistr lengthUnif = new UniformDistr(minLengthUnif,
				maxLengthUnif, seed);

		// add noCloudlets cloudlets
		for (int i = 0; i < noCloudlets; i++) {
			int randomLength = (int) lengthUnif.sample();
			Cloudlet cloudlet = new Cloudlet(id++, randomLength, pesNumber,
					fileSize, outputSize, utilizationModel,
					utilizationModel, utilizationModel);
			cloudlet.setUserId(brokerId);
			cloudletList.add(cloudlet);
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

package nisere.schedsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nisere.schedsim.MyCloudlet;
import nisere.schedsim.MyDatacenter;
import nisere.schedsim.MyDatacenterBroker;
import nisere.schedsim.MyVm;
import nisere.schedsim.Scheduler;
import nisere.schedsim.algorithm.NOAlgorithm;
import nisere.schedsim.algorithm.SchedulingAlgorithm;
import nisere.schedsim.algorithm.WorkQueueAlgorithm;

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
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * StaticAlgorithmsExample class is used to test static scheduling algorithms.
 * 
 * @author Alina Chera
 *
 */
public class StaticAlgorithmsExample {
	// hi-hi 1-10 1-1000000
	// lo-lo 1-2 100000-400000
	// hi-lo 1-10 100000-400000
	// lo-hi 1-2 1-1000000
	
	static int noCloudlets = 512; // used to create random Cloudlets
	static int noVms = 16; // used to create random VMs
	// generate [minMipsUnif;maxMipsUnif) and multiply with 1000 to get mips
	static int minMipsUnif = 1;
	static int maxMipsUnif = 11;
	// generate length [minLengthUnif;maxLengthUnif)
	static int minLengthUnif = 100000;
	static int maxLengthUnif = 400000;
	static int seed = 9;
	static int vmId = 0; // used to create VM types
	
	static int schedulingInterval = 400;
	static long delayInterval = 400;
	static int intervals = 1;
	
	public static void main(String[] args) {

		Log.printLine("Starting simulation...");
		try {
			/* Initialize the simulation. */
			CloudSim.init(1, Calendar.getInstance(), false);
			
			/* Create a broker object. */
			MyDatacenterBroker broker = new MyDatacenterBroker("Broker");

			/*------------------------------------------*/
			
			/* Create a private cloud. */
			
			/* Create random VM types. */
			List<MyVm> vmTypes0 = createRandomMyVms(broker.getId(), noVms, minMipsUnif, maxMipsUnif, seed);
			
			/* Set count and price */
			HashMap<String,Integer> vmCount0 = new HashMap<>();
			HashMap<String,Double> vmPrice0 = new HashMap<>();
			for (MyVm vm : vmTypes0) {
				vmCount0.put(vm.getIdentifier(), 1);
				vmPrice0.put(vm.getIdentifier(), 0.0);
			}

			/* Create the datacenter. */
			MyDatacenter datacenter0 = createMyDatacenter("Private", vmTypes0, vmCount0, vmPrice0, 1);
			
			/*------------------------------------------*/
			
			/* Create a public cloud. */
			
			/* Create custom VM types. */
			MyVm vm1 = new MyVm(-1, broker.getId(), 1000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared(), -1, Integer.toString(vmId++));
			MyVm vm2 = new MyVm(-1, broker.getId(), 2000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared(), -1, Integer.toString(vmId++));
			MyVm vm3 = new MyVm(-1, broker.getId(), 3000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared(), -1, Integer.toString(vmId++));
			
			ArrayList<MyVm> vmTypes1 = new ArrayList<>();
			vmTypes1.add(vm1);
			vmTypes1.add(vm2);
			vmTypes1.add(vm3);
			
			/* Simulate infinite instances for public cloud */
			HashMap<String,Integer> vmCount1 = new HashMap<>();
			for (MyVm vm : vmTypes1) {
				vmCount1.put(vm.getIdentifier(), noCloudlets);
			}
			
			/* Add price to VM types */
			HashMap<String,Double> vmPrice1 = new HashMap<>();
			vmPrice1.put(vm1.getIdentifier(), 1.0);
			vmPrice1.put(vm2.getIdentifier(), 1.5);
			vmPrice1.put(vm3.getIdentifier(), 2.5);
			
			/* Create the datacenter. */
			MyDatacenter datacenter1 = createMyDatacenter("Public1", vmTypes1, vmCount1, vmPrice1, 3600);
			
			/*------------------------------------------*/

			/* Create another public cloud. */
			
			/* Create custom VM types. */
			MyVm vm4 = new MyVm(-1, broker.getId(), 1500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared(), -1, Integer.toString(vmId++));
			MyVm vm5 = new MyVm(-1, broker.getId(), 2500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared(), -1, Integer.toString(vmId++));
		
			ArrayList<MyVm> vmTypes2 = new ArrayList<>();
			vmTypes2.add(vm1);
			vmTypes2.add(vm4);
			vmTypes2.add(vm5);
			
			/* Simulate infinite instances for public cloud */
			HashMap<String,Integer> vmCount2 = new HashMap<>();
			for (MyVm vm : vmTypes2) {
				vmCount2.put(vm.getIdentifier(), noCloudlets);
			}
			
			/* Add price to VM types */
			HashMap<String,Double> vmPrice2 = new HashMap<>();
			vmPrice2.put(vm1.getIdentifier(), 0.9);
			vmPrice2.put(vm4.getIdentifier(), 1.5);
			vmPrice2.put(vm5.getIdentifier(), 2.0);
			
			/* Create the datacenter. */
			MyDatacenter datacenter2 = createMyDatacenter("Public2", vmTypes2, vmCount2, vmPrice2, 3600);
			
			/*------------------------------------------*/
			
			/* Create the datacenter list. */
//			Map<String,Datacenter> datacenters = new HashMap<>();
//			datacenters.put("Private", datacenter0);
//			datacenters.put("Public1", datacenter1);
//			datacenters.put("Public2", datacenter2);
			List<MyDatacenter> datacenters = new ArrayList<>();
			datacenters.add(datacenter0);
			//datacenters.add(datacenter1);
			//datacenters.add(datacenter2);
			
			/* Create the VM list. */
			List<MyVm> vmList = populateVmList(datacenters);

			/* Create the Cloudlet list. */
			List<MyCloudlet> cloudletList = createRandomMyCloudlets(broker.getId(),noCloudlets,minLengthUnif, maxLengthUnif, seed, delayInterval, intervals);
			
			/* Choose the scheduling algorithm. */
			//SchedulingAlgorithm algorithm = new NOAlgorithm();
			SchedulingAlgorithm algorithm = new WorkQueueAlgorithm();
			//SchedulingAlgorithm algorithm = new SufferageAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMinAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMaxAlgorithm();
			//SchedulingAlgorithm algorithm = new MaxMinAlgorithm();
			//SchedulingAlgorithm algorithm = new LJFR_SJFRAlgorithm();
			
			/* Create a scheduler. */
			Scheduler scheduler = new Scheduler(datacenters,broker,vmList,cloudletList,algorithm,schedulingInterval);

			/* Make the necessary preparations before starting the simulation. 
			 * This is the step where the algorithm is run. 
			 */
			scheduler.prepareSimulation();

			/* Start simulation. */
			CloudSim.startSimulation();

			/* Stop simulation. */
			CloudSim.stopSimulation();

			/* Print the results. */
			printResult(scheduler.getFinishedCloudlets());
			
			Log.printLine("Simulation finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	private static MyDatacenter createMyDatacenter(String name, List<MyVm> vmTypes, 
			Map<String,Integer> vmCount, Map<String,Double> vmPrice, int timeInterval) throws Exception {
		MyDatacenter datacenter;
	
		// Create a DatacenterCharacteristics object
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN devices

		ArrayList<Host> hostList = new ArrayList<>();
		int hostId = 0;
		// for each vm type check how many must be created;
		// for each vm create a host
		for (MyVm vm : vmTypes ) {
			int n = vmCount.get(vm.getIdentifier());
			List<Pe> peList = new ArrayList<Pe>();
			peList.add(new Pe(0, new PeProvisionerSimple(vm.getMips())));
			for (int i = 0; i < n; i++) {
				hostList.add(new Host(hostId++, new RamProvisionerSimple(vm.getRam()),
						new BwProvisionerSimple(vm.getBw()), vm.getSize(), 
						peList,	new VmSchedulerSpaceShared(peList)));					
			}
		}
		
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);
		
		datacenter = new MyDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList), 
				storageList, 0, vmTypes, vmCount, vmPrice, timeInterval);
		
		return datacenter;
	}
	
	private static List<MyVm> populateVmList(List<MyDatacenter> datacenters) {
		List<MyVm> vmlist = new ArrayList<>();
		int vmid = 0;
		
		for (MyDatacenter dc : datacenters) {
			for (Vm vm : dc.getVmTypes()) {
				for (int i = 0; i < dc.getVmCount().get( ((MyVm)vm).getIdentifier() ); i++) {
					vmlist.add(new MyVm(vmId++, vm.getUserId(), vm.getMips(), vm.getNumberOfPes(),
							vm.getRam(), vm.getBw(), vm.getSize(), vm.getVmm(), 
							new CloudletSchedulerSpaceShared(), dc.getId(), ((MyVm)vm).getIdentifier()));
				}
			}
		}
		
		
		return vmlist;
	}
	
	private static List<MyVm> createRandomMyVms(int brokerId, int noVms, int minMipsUnif, int maxMipsUnif,
			int seed) {
		List<MyVm> vmlist = new ArrayList<>();

		// VM description
		int mips = 1000;
		long size = 10000; // image size (MB)
		int ram = 1024; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		int datacenterId = -1;

		UniformDistr mipsUnif = new UniformDistr(minMipsUnif, maxMipsUnif,
				seed);

		// add noVms VMs
		for (int i = 0; i < noVms; i++) {
			int mult = (int) mipsUnif.sample();
			vmlist.add(new MyVm(-1, brokerId, mips * mult, pesNumber,
					ram, bw, size, vmm, new CloudletSchedulerSpaceShared(), datacenterId, Integer.toString(vmId++)));
		}
		return vmlist;
	}
	
	private static List<MyCloudlet> createRandomMyCloudlets(int brokerId, int noCloudlets, int minLengthUnif, int maxLengthUnif,
			int seed, long delayInterval, int intervals) {
		List<MyCloudlet> cloudletList = new ArrayList<>();

		// Cloudlet properties
		int id = 0;
		int pesNumber = 1;
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
	
	/** Prints the results */
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

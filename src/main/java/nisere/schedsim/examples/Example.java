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
 * Example class shows how to use schedsim extension.
 * It creates a private cloud and a public cloud 
 * and sets their characteristics accordingly.
 * It shows how to create the cloudlets to simulate online arrival.
 * It sets the scheduler in order to use batch scheduling.
 * It shows how to chose from several scheduling algorithms.
 * 
 * @author Alina Chera
 *
 */
public class Example {
	static int noCloudlets = 4; // used to create random Cloudlets
	static int noVms = 2; // used to create random VMs
	// generate [minMipsUnif;maxMipsUnif) and multiply with 1000 to get mips
	static int minMipsUnif = 1;
	static int maxMipsUnif = 2;
	// generate length [minLengthUnif;maxLengthUnif)
	static int minLengthUnif = 100000;
	static int maxLengthUnif = 200000;
	static int seed = 1;
	static int vmId = 0; // used to create VM types
	static int schedulingInterval = 200; // in seconds
	// generate delay [minDelayUnif;maxDelayUnif)
	static int minDelayUnif = 0;
	static int maxDelayUnif = 500;
	
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
			HashMap<Integer,Integer> vmCount0 = new HashMap<>();
			HashMap<Integer,Double> vmPrice0 = new HashMap<>();
			for (MyVm vm : vmTypes0) {
				vmCount0.put(vm.getTypeId(), 1);
				vmPrice0.put(vm.getTypeId(), 0.0);
			}

			/* Create the datacenter. */
			MyDatacenter datacenter0 = createMyDatacenter("Private", vmTypes0, vmCount0, vmPrice0, 1);
			
			/*------------------------------------------*/
			
			/* Create a public cloud. */
			
			/* Create custom VM types. */
			MyVm vm1 = new MyVm(-1, broker.getId(), 1000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared(), -1, vmId++);
			MyVm vm2 = new MyVm(-1, broker.getId(), 2000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared(), -1, vmId++);
			MyVm vm3 = new MyVm(-1, broker.getId(), 3000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared(), -1, vmId++);
			
			ArrayList<MyVm> vmTypes1 = new ArrayList<>();
			vmTypes1.add(vm1);
			vmTypes1.add(vm2);
			vmTypes1.add(vm3);
			
			/* Simulate infinite instances for public cloud */
			HashMap<Integer,Integer> vmCount1 = new HashMap<>();
			for (MyVm vm : vmTypes1) {
				vmCount1.put(vm.getTypeId(), noCloudlets);
			}
			
			/* Add price to VM types */
			HashMap<Integer,Double> vmPrice1 = new HashMap<>();
			vmPrice1.put(vm1.getTypeId(), 1.0);
			vmPrice1.put(vm2.getTypeId(), 1.5);
			vmPrice1.put(vm3.getTypeId(), 2.5);
			
			/* Create the datacenter. */
			MyDatacenter datacenter1 = createMyDatacenter("Public1", vmTypes1, vmCount1, vmPrice1, 3600);
			
			/*------------------------------------------*/

			/* Create another public cloud. */
			
			/* Create custom VM types. */
			MyVm vm4 = new MyVm(-1, broker.getId(), 1500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared(), -1, vmId++);
			MyVm vm5 = new MyVm(-1, broker.getId(), 2500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared(), -1, vmId++);
		
			ArrayList<MyVm> vmTypes2 = new ArrayList<>();
			vmTypes2.add(vm1);
			vmTypes2.add(vm4);
			vmTypes2.add(vm5);
			
			/* Simulate infinite instances for public cloud */
			HashMap<Integer,Integer> vmCount2 = new HashMap<>();
			for (MyVm vm : vmTypes2) {
				vmCount2.put(vm.getTypeId(), noCloudlets);
			}
			
			/* Add price to VM types */
			HashMap<Integer,Double> vmPrice2 = new HashMap<>();
			vmPrice2.put(vm1.getTypeId(), 0.9);
			vmPrice2.put(vm4.getTypeId(), 1.5);
			vmPrice2.put(vm5.getTypeId(), 2.0);
			
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
			List<MyCloudlet> cloudletList = createRandomMyCloudlets(broker.getId(),noCloudlets,minLengthUnif, maxLengthUnif, seed, minDelayUnif, maxDelayUnif);
			
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
			Map<Integer,String> dcNames = new HashMap<>();
			for (Datacenter dc : datacenters) {
				dcNames.put(dc.getId(), dc.getName());
			}
			printResult(scheduler.getFinishedCloudlets(),dcNames);
			
			Log.printLine("Simulation finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	private static MyDatacenter createMyDatacenter(String name, List<MyVm> vmTypes, 
			Map<Integer,Integer> vmCount, Map<Integer,Double> vmPrice, int timeInterval) throws Exception {
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
			int n = vmCount.get(vm.getTypeId());
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
				for (int i = 0; i < dc.getVmCount().get( ((MyVm)vm).getTypeId() ); i++) {
					vmlist.add(new MyVm(vmid++, vm.getUserId(), vm.getMips(), vm.getNumberOfPes(),
							vm.getRam(), vm.getBw(), vm.getSize(), vm.getVmm(), 
							new CloudletSchedulerSpaceShared(), dc.getId(), ((MyVm)vm).getTypeId()));
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
					ram, bw, size, vmm, new CloudletSchedulerSpaceShared(), datacenterId, vmId++));
		}
		return vmlist;
	}
	
	private static List<MyCloudlet> createRandomMyCloudlets(int brokerId, int noCloudlets, int minLengthUnif, int maxLengthUnif,
			int seed, int minDelayUnif, int maxDelayUnif) {
		List<MyCloudlet> cloudletList = new ArrayList<>();

		// Cloudlet properties
		int id = 0;
		int pesNumber = 1;
		//long length = 250000;
		long fileSize = 0;
		long outputSize = 0;
		UtilizationModel utilizationModel = new UtilizationModelFull();
		int deadline = 0;
		long delay = 0; // in seconds

		UniformDistr lengthUnif = new UniformDistr(minLengthUnif,
				maxLengthUnif, seed);
		UniformDistr delayUnif = new UniformDistr(minDelayUnif,
				maxDelayUnif, seed);

		// add noCloudlets*intervals cloudlets
		for (int i = 0; i < noCloudlets; i++) {
			int randomLength = (int) lengthUnif.sample();
			delay += (long) delayUnif.sample();
			MyCloudlet cloudlet = new MyCloudlet(id++, randomLength, pesNumber,
					fileSize, outputSize, utilizationModel,
					utilizationModel, utilizationModel, deadline, delay);
			cloudlet.setUserId(brokerId);
			cloudletList.add(cloudlet);
		}
		
		return cloudletList;
	}
	
	/** Prints the results */
	private static void printResult(List<Cloudlet> list,Map<Integer,String> dcNames) {
		int size = list.size();
		Cloudlet cloudlet;
		double flowtime = 0;
		//double cost = 0;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "  Datacenter  " + indent + "  VM ID" + indent + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time" + indent + "Arrival" + indent + "Delay");

		int[] counter = new int[13];
		int index = 0;
		int step = 1000;
		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + dcNames.get(cloudlet.getResourceId())
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime())
						+ indent + indent + dft.format(((MyCloudlet)cloudlet).getArrivalTime())
						+ indent + indent + dft.format(((MyCloudlet)cloudlet).getDelay()));

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

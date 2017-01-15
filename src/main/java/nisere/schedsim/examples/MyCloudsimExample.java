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
 * MyCloudsimExample class shows how to use schedsim extension.
 * It creates a private cloud and two public clouds 
 * and sets their characteristics accordingly.
 * It shows how to create the cloudlets to simulate online arrival.
 * It sets the scheduler in order to use batch scheduling.
 * It shows how to chose from several scheduling algorithms.
 * 
 * @author Alina Chera
 *
 */
public class MyCloudsimExample {

	
	public static void main(String[] args) {
		int noCloudlets = 4;
		int noVms = 2;
		// generate [minMipsUnif;maxMipsUnif) and multiply with 1000 to get
		// mips
		int minMipsUnif = 1;
		int maxMipsUnif = 2;
		// generate length [minLengthUnif;maxLengthUnif)
		int minLengthUnif = 100000;
		int maxLengthUnif = 200000;
		int seed = 1;
		long delayInterval = 400;
		int intervals = 2;
		
		Log.printLine("Starting MyCloudsimExample...");
		try {
			/* Initialize the simulation. */
			CloudSim.init(1, Calendar.getInstance(), false);
			
			/* Create a broker object. */
			MyDatacenterBroker broker = new MyDatacenterBroker("Broker");

			
			
			
			List<MyVm> vmList = createRandomMyVms(broker.getId(),noVms,minMipsUnif, maxMipsUnif, seed);

			List<MyCloudlet> cloudletList = createRandomMyCloudlets(broker.getId(),noCloudlets,minLengthUnif, maxLengthUnif, seed, delayInterval, intervals);
			
			Map<String,Datacenter> datacenters = new HashMap<>();
			
			Map<Integer,Integer> vmCount = new HashMap<>();
			for (Vm vm : vmList) {
				vmCount.put(vm.getId(), 1);
			}
			MyDatacenter datacenter3 = createMyDatacenter("Private", vmList, vmCount);
			datacenters.put("Private", datacenter3);
			
			
			
//			/* Create the datacenters. */
//			Datacenter datacenter0 = createDatacenter("Private");
//			Datacenter datacenter1 = createDatacenter("Public1");
//			Datacenter datacenter2 = createDatacenter("Public2");
//
//			Map<String,Datacenter> datacenters = new HashMap<>();
//			datacenters.put("Private", datacenter0);
//			datacenters.put("Public1", datacenter1);
//			datacenters.put("Public2", datacenter2);
//
//			
//			/* Create the VM list. */
//			List<MyVm> vmList = createRandomMyVms(broker.getId(),noVms,minMipsUnif, maxMipsUnif, seed, datacenterIds);
//			
//			/* Create the cloudelt list. */
//			List<MyCloudlet> cloudletList = createRandomMyCloudlets(broker.getId(),noCloudlets,minLengthUnif, maxLengthUnif, seed, delayInterval, intervals);

			
			/* Choose the scheduling algorithm. */
			SchedulingAlgorithm algorithm = new NOAlgorithm();
			//SchedulingAlgorithm algorithm = new WorkQueueAlgorithm();
			//SchedulingAlgorithm algorithm = new SufferageAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMinAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMaxAlgorithm();
			//SchedulingAlgorithm algorithm = new MaxMinAlgorithm();
			//SchedulingAlgorithm algorithm = new LJFR_SJFRAlgorithm();
			
			/* Create a scheduler. */
			Scheduler scheduler = new Scheduler(datacenters,broker,vmList,cloudletList,algorithm);

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
			
			Log.printLine("MyCloudsimExample finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	private static MyDatacenter createMyDatacenter(String name, List<? extends Vm> vmList, 
			Map<Integer,Integer> vmCount) throws Exception {
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

		List<Host> hostList = new ArrayList<Host>();
		int hostId = 0;
		// for each vm type check how many must be created;
		// for each vm create a host
		for (Vm vm : vmList ) {
			if (vmCount.containsKey(vm.getId())) {
				int n = vmCount.get(vm.getId());
				List<Pe> peList = new ArrayList<Pe>();
				peList.add(new Pe(0, new PeProvisionerSimple(vm.getMips())));
				for (int i = 0; i < n; i++) {
					hostList.add(new Host(hostId++, new RamProvisionerSimple(vm.getRam()),
							new BwProvisionerSimple(vm.getBw()), vm.getSize(), 
							peList,	new VmSchedulerSpaceShared(peList)));					
				}
			}
		}
		
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);
		
		datacenter = new MyDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList), 
				storageList, 0, vmList, vmCount, new HashMap<Integer,Double>(),1);
		
		return datacenter;
	}
	
	
	private static List<MyVm> createRandomMyVms(int brokerId, int noVms, int minMipsUnif, int maxMipsUnif,
			int seed) {
		List<MyVm> vmlist = new ArrayList<>();

		// VM description
		int vmid = 0;
		int mips = 1000;
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		int datacenterId = -1;

		UniformDistr mipsUnif = new UniformDistr(minMipsUnif, maxMipsUnif,
				seed);

		// add noVms VMs
		for (int i = 0; i < noVms; i++) {
			int mult = (int) mipsUnif.sample();
			vmlist.add(new MyVm(vmid++, brokerId, mips * mult, pesNumber,
					ram, bw, size, vmm, new CloudletSchedulerSpaceShared(), datacenterId));
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

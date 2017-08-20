package nisere.onlinesim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineDatacenterBroker;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.Scheduler;
import nisere.onlinesim.VmType;
import nisere.onlinesim.algorithm.NOAlgorithm;
import nisere.onlinesim.algorithm.SchedulingAlgorithm;

/**
 * Example class shows how to use this extension.
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

	public static void main(String[] args) {
		int noCloudlets = 4; // used to create random Cloudlets
		int noVms = 2; // used to create random VMs
		// generate [minMipsUnif;maxMipsUnif) and multiply with 1000 to get mips
		int minMipsUnif = 1;
		int maxMipsUnif = 2;
		// generate length [minLengthUnif;maxLengthUnif)
		int minLengthUnif = 100000;
		int maxLengthUnif = 200000;
		int seed = 1;
		int vmId = 0; // used to create VM types
		int schedulingInterval = 200; // in seconds
		// generate delay [minDelayUnif;maxDelayUnif)
		int minDelayUnif = 0;
		int maxDelayUnif = 500;

		Log.printLine("Starting simulation...");
		try {
			/* Initialize the simulation. */
			CloudSim.init(1, Calendar.getInstance(), false);

			/* Create a broker object. */
			OnlineDatacenterBroker broker = new OnlineDatacenterBroker("Broker");

			/*------------------------------------------*/

			/* Create a private cloud. */

			/* Create random VM types. */
			List<OnlineVm> vms0 = createRandomVms(broker.getId(), noVms, minMipsUnif, maxMipsUnif, seed, -1);
			ArrayList<VmType> vmTypes0 = new ArrayList<>();
			for (OnlineVm vm : vms0) {
				vmTypes0.add(new VmType(vm, 1, 0.0, 1, "PRVrand"));
			}

			/* Create the datacenter. */
			Datacenter datacenter0 = createDatacenter("Private", vmTypes0);

			/*------------------------------------------*/

			/* Create a public cloud. */

			/* Create custom VM types. */
			OnlineVm vm1 = new OnlineVm(broker.getId(), 1000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());
			OnlineVm vm2 = new OnlineVm(broker.getId(), 2000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());
			OnlineVm vm3 = new OnlineVm(broker.getId(), 3000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());

			ArrayList<VmType> vmTypes1 = new ArrayList<>();
			vmTypes1.add(new VmType(vm1, noCloudlets, 1.0, 3600, "PB1_1.0"));
			vmTypes1.add(new VmType(vm2, noCloudlets, 1.5, 3600, "PB1_1.5"));
			vmTypes1.add(new VmType(vm3, noCloudlets, 2.5, 3600, "PB1_2.5"));

			/* Create the datacenter. */
			Datacenter datacenter1 = createDatacenter("Public1", vmTypes1);

			/*------------------------------------------*/

			/* Create another public cloud. */

			/* Create custom VM types. */
			OnlineVm vm4 = new OnlineVm(broker.getId(), 1500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared());
			OnlineVm vm5 = new OnlineVm(broker.getId(), 2500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared());

			ArrayList<VmType> vmTypes2 = new ArrayList<>();
			vmTypes2.add(new VmType(vm1, noCloudlets, 0.9, 3600, "PB2_0.9"));
			vmTypes2.add(new VmType(vm4, noCloudlets, 1.5, 3600, "PB2_1.5"));
			vmTypes2.add(new VmType(vm5, noCloudlets, 2.0, 3600, "PB2_2.0"));

			/* Create the datacenter. */
			Datacenter datacenter2 = createDatacenter("Public2", vmTypes2);

			/*------------------------------------------*/

			/* Create the VM list. */
			List<VmType> vmTypes = new ArrayList<>();
			vmTypes.addAll(vmTypes0);
			vmTypes.addAll(vmTypes1);
			vmTypes.addAll(vmTypes2);
			List<OnlineVm> vmList = populateVmList(vmTypes);

			/* Create the Cloudlet list. */
			List<OnlineCloudlet> cloudletList = createRandomCloudlets(broker.getId(), noCloudlets, minLengthUnif,
					maxLengthUnif, seed, minDelayUnif, maxDelayUnif);

			/* Choose the scheduling algorithm. */
			SchedulingAlgorithm algorithm = new NOAlgorithm();
			// SchedulingAlgorithm algorithm = new WorkQueueAlgorithm();
			// SchedulingAlgorithm algorithm = new SufferageAlgorithm();
			// SchedulingAlgorithm algorithm = new MinMinAlgorithm();
			// SchedulingAlgorithm algorithm = new MinMaxAlgorithm();
			// SchedulingAlgorithm algorithm = new MaxMinAlgorithm();
			// SchedulingAlgorithm algorithm = new LJFR_SJFRAlgorithm();

			/* Create a scheduler. */
			Scheduler scheduler = new Scheduler(vmTypes, broker, vmList, cloudletList, algorithm,
					schedulingInterval);

			/*
			 * Make the necessary preparations before starting the simulation.
			 * This is the step where the algorithm is run.
			 */
			scheduler.prepareSimulation();

			/* Start simulation. */
			CloudSim.startSimulation();

			/* Stop simulation. */
			CloudSim.stopSimulation();

			/* Print the results. */
			printResult(scheduler.getFinishedCloudlets(),vmList);

			Log.printLine("Simulation finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	public static Datacenter createDatacenter(String name, List<VmType> vmTypes) throws Exception {
		Datacenter datacenter;

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
		for (VmType type : vmTypes) {
			int n = type.getCount();
			OnlineVm vm = type.getVm();
			List<Pe> peList = new ArrayList<Pe>();
			peList.add(new Pe(0, new PeProvisionerSimple(vm.getMips())));
			for (int i = 0; i < n; i++) {
				hostList.add(new Host(hostId++, new RamProvisionerSimple(vm.getRam()),
						new BwProvisionerSimple(vm.getBw()), vm.getSize(), peList, new VmSchedulerSpaceShared(peList)));
			}
		}

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone,
				cost, costPerMem, costPerStorage, costPerBw);

		datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);

		// Update datacenter reference in vmTypes
		for (VmType type : vmTypes) {
			type.setDatacenter(datacenter);
		}

		return datacenter;
	}

	public static List<OnlineVm> populateVmList(List<? extends VmType> vmTypes) {
		List<OnlineVm> vmlist = new ArrayList<>();

		for (VmType type : vmTypes) {
			OnlineVm vm = type.getVm();
			for (int i = 0; i < type.getCount(); i++) {
				OnlineVm vm2 = new OnlineVm(vm.getUserId(), vm.getMips(), vm.getNumberOfPes(), vm.getRam(), vm.getBw(),
						vm.getSize(), vm.getVmm(), new CloudletSchedulerSpaceShared());
				vm2.setDatacenterId(type.getDatacenter().getId());
				vm2.setVmType(type);
				vmlist.add(vm2);
			}
		}

		return vmlist;
	}

	public static List<OnlineVm> createRandomVms(int brokerId, int noVms, int minMipsUnif, int maxMipsUnif, int seed,
			int datacenterId) {
		List<OnlineVm> vmlist = new ArrayList<>();

		// VM description
		int mips = 1000;
		long size = 10000; // image size (MB)
		int ram = 1024; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		UniformDistr mipsUnif = new UniformDistr(minMipsUnif, maxMipsUnif, seed);

		// add noVms VMs
		for (int i = 0; i < noVms; i++) {
			int mult = (int) mipsUnif.sample();
			vmlist.add(new OnlineVm(brokerId, mips * mult, pesNumber, ram, bw, size, vmm,
					new CloudletSchedulerSpaceShared(), datacenterId));
		}
		return vmlist;
	}

	public static List<OnlineCloudlet> createRandomCloudlets(int brokerId, int noCloudlets, int minLengthUnif,
			int maxLengthUnif, int seed, int minArrivalUnif, int maxArrivalUnif) {
		List<OnlineCloudlet> cloudletList = new ArrayList<>();

		// Cloudlet properties
		int pesNumber = 1;
		// long length = 250000;
		long fileSize = 0;
		long outputSize = 0;
		UtilizationModel utilizationModel = new UtilizationModelFull();
		int deadline = 0;
		long delay = 0; // in seconds

		UniformDistr lengthUnif = new UniformDistr(minLengthUnif, maxLengthUnif, seed);
		UniformDistr delayUnif = new UniformDistr(minArrivalUnif, maxArrivalUnif, seed);

		// add noCloudlets*intervals cloudlets
		for (int i = 0; i < noCloudlets; i++) {
			int randomLength = (int) lengthUnif.sample();
			delay += (long) delayUnif.sample();
			OnlineCloudlet cloudlet = new OnlineCloudlet(randomLength, pesNumber, fileSize, outputSize, utilizationModel,
					utilizationModel, utilizationModel, deadline, delay);
			cloudlet.setUserId(brokerId);
			cloudletList.add(cloudlet);
		}

		return cloudletList;
	}

	public static void printResult(List<OnlineCloudlet> list,List<OnlineVm> vmList) {
		int size = list.size();
		OnlineCloudlet cloudlet;
		double flowtime = 0;
		double cost = 0;
		HashSet<OnlineVm> vmUsed = new HashSet<>();

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Datacenter" + indent + "VM ID" + indent + "VM Type " + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time" + indent + "Arrival" + indent + "Delay" + indent + "Cost");

		int[] counter = new int[13];
		int index = 0;
		int step = 1000;
		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");
				
				OnlineVm vm = VmList.getById(vmList, cloudlet.getVmId());
				vm.setUptime(cloudlet.getFinishTime());


				flowtime += cloudlet.getFinishTime();

				if (cloudlet.getFinishTime() <= step * (index + 1)) {
					counter[index]++;
				} else {
					index++;
					counter[index] = counter[index - 1] + 1;
				}

				double intervals = Math.ceil(1.0*vm.getUptime()/vm.getVmType().getPriceInterval());
				vm.setCost(intervals * vm.getVmType().getPrice());
				
				vmUsed.add(vm);
				
				Log.printLine(indent + indent + vm.getDatacenterId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + vm.getVmType().getName() + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime())
						+ indent + indent + dft.format(((OnlineCloudlet)cloudlet).getArrivalTime())
						+ indent + indent + dft.format(((OnlineCloudlet)cloudlet).getDelay())
						+ indent + indent + dft.format(vm.getCost()));

			}
		}
		
		for (OnlineVm vm : vmUsed) {
			cost += vm.getCost();
		}

		Log.printLine();
		Log.printLine("Flowtime: " + dft.format(flowtime));
		Log.printLine("Finished after " + step + ",2x" + step + "...");
		for (int i = 0; i < 13; i++) {
			Log.print(counter[i] + ",");
		}
		Log.printLine();
		Log.printLine("Cost: " + cost);
	}

}

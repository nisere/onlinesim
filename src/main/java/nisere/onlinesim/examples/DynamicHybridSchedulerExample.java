package nisere.onlinesim.examples;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import nisere.onlinesim.*;
import nisere.onlinesim.algorithm.*;

/**
 * StaticAlgorithmsExample class is used to test static scheduling algorithms.
 * 
 * @author Alina Chera
 *
 */
public class DynamicHybridSchedulerExample extends Example {
	
	public static void main(String[] args) {

		// cloudlets x-2x to work (delta chosen is 1.7) 1000000-2000000 = lo (15% > 1.7)
		// lo-lo 1000 1000000-2000000 / all 430-473
		// hi-lo 1000-5000 1000000-2000000 / all 430-473
		// lo-hi 1000 500000-5650000 / all 1050-1155
		// hi-hi 1000-5000 500000-5650000 / all 1050-1155
		
		int noCloudlets = 1200; // used to create random Cloudlets
		int noVms = 16; // used to create random VMs
		int minMipsUnif = 1000;
		int maxMipsUnif = 5000;
		// generate length [minLengthUnif;maxLengthUnif)
		int minLengthUnif = 500000;
		int maxLengthUnif = 5650000;
		int seed = 10;
		
		int schedulingInterval = 100;
		// generate arrival time [minArrivalUnif;maxArrivalUnif)
		int minArrivalUnif = 0;//0;
		int maxArrivalUnif = 100;//500;
		
		// generate deadline [minDeadlineUnif;maxDeadlineUnif)
		int minDeadlineUnif = 1050;
		int maxDeadlineUnif = 1155;
		
		//price intervals for datacenters
		int priceInterval1 = 500;
		int priceInterval2 = 500;
		
		double computationTime = 10;
		
		Log.printLine("Starting simulation...");
		try {
			/* Initialize the simulation. */
			CloudSim.init(1, Calendar.getInstance(), false);
			
			/* Create a broker object. */
			OnlineDatacenterBroker broker = new OnlineDatacenterBroker("Broker");

			/* Choose the scheduling algorithm. */
			//SchedulingAlgorithm algorithm = new WorkQueueAlgorithm();
			//SchedulingAlgorithm algorithm = new SufferageAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMinAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMaxAlgorithm();
			//SchedulingAlgorithm algorithm = new MaxMinAlgorithm();
			//SchedulingAlgorithm algorithm = new LJFR_SJFRAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMinAlgorithm2();
			//SchedulingAlgorithm algorithm = new DeadlineAlgorithm();

			MixDeadlineAlgorithm algorithm = new MixDeadlineAlgorithm();
			algorithm.setDeadlineDelta(1.1);
			algorithm.setLengthDelta(1.7);
			
			SchedulingAlgorithm publicAlgorithm = new PublicAlgorithm();
			
			
			/* Create the Cloudlet list. */
			List<OnlineCloudlet> cloudletList = createRandomCloudlets(broker.getId(),noCloudlets,minLengthUnif, maxLengthUnif, seed,minArrivalUnif, maxArrivalUnif, minDeadlineUnif, maxDeadlineUnif);
			//cloudletList.get(0).setDeadline(500);
			//cloudletList.get(noCloudlets - 1).setDeadline(500);
			//cloudletList.get(noCloudlets - 1).setDeadline(900);
			
			/*------------------------------------------*/
			
			/* Create a private cloud. */
			
			/* Create random VM types. */
			List<OnlineVm> vms0 = createRandomVms(broker.getId(), noVms, minMipsUnif, maxMipsUnif, seed, -1);
			ArrayList<VmType> vmTypes0 = new ArrayList<>();
			for (OnlineVm vm : vms0) {
				vmTypes0.add(new VmType(vm, 1, 0.0, 1, "PRVrand"));
			}

			/* Create the datacenter. */
			createDatacenter("Private", vmTypes0,true);

			/*------------------------------------------*/
			
			/* Create a public cloud. */

			/* Create custom VM types. */
			OnlineVm vm1 = new OnlineVm(broker.getId(), 1000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());
			OnlineVm vm2 = new OnlineVm(broker.getId(), 3000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());
			OnlineVm vm3 = new OnlineVm(broker.getId(), 5000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());

			ArrayList<VmType> vmTypes1 = new ArrayList<>();
			vmTypes1.add(new VmType(vm1, noCloudlets, 1.0, priceInterval1, "PB1_1.0"));
			vmTypes1.add(new VmType(vm2, noCloudlets, 3.0, priceInterval1, "PB1_3.0"));
			vmTypes1.add(new VmType(vm3, noCloudlets, 5.0, priceInterval1, "PB1_5.0"));

			/* Create the datacenter. */
			createDatacenter("Public1", vmTypes1,false);

			/*------------------------------------------*/

			/* Create another public cloud. */

			/* Create custom VM types. */
			OnlineVm vm4 = new OnlineVm(broker.getId(), 1500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared());
			OnlineVm vm5 = new OnlineVm(broker.getId(), 2500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared());
			OnlineVm vm6 = new OnlineVm(broker.getId(), 4500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared());

			ArrayList<VmType> vmTypes2 = new ArrayList<>();
			vmTypes2.add(new VmType(vm4, noCloudlets, 0.9, priceInterval2, "PB2_1.4"));
			vmTypes2.add(new VmType(vm5, noCloudlets, 1.5, priceInterval2, "PB2_2.6"));
			vmTypes2.add(new VmType(vm6, noCloudlets, 2.0, priceInterval2, "PB2_4.4"));

			/* Create the datacenter. */
			createDatacenter("Public2", vmTypes2,false);
			
			/*------------------------------------------*/
			
			/* Create the VM list. */
			List<VmType> vmTypes = new ArrayList<>();
			vmTypes.addAll(vmTypes0);
			List<OnlineVm> vmList = populateVmList(vmTypes0);
			List<VmType> publicVmTypes = new ArrayList<>();
			publicVmTypes.addAll(vmTypes1);
			publicVmTypes.addAll(vmTypes2);
			List<OnlineVm> publicVmList = new ArrayList<>();
			

			
			/* Create a scheduler. */
			DynamicHybridScheduler scheduler = new DynamicHybridScheduler(vmTypes,broker,vmList,cloudletList,algorithm, schedulingInterval, publicAlgorithm, publicVmList, publicVmTypes);
			scheduler.setComputationTime(computationTime);
			
			/* Make the necessary preparations before starting the simulation. 
			 * This is the step where the algorithm is run. 
			 */
			scheduler.prepareSimulation();

			/* Start simulation. */
			CloudSim.startSimulation();

			/* Stop simulation. */
			CloudSim.stopSimulation();

			/* Print the results. */
			printResult(scheduler);
			
			Log.printLine("Simulation finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
}

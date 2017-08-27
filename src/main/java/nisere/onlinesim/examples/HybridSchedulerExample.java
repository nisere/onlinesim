package nisere.onlinesim.examples;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
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
public class HybridSchedulerExample extends Example {
	
	public static void main(String[] args) {

		// hi-hi 1-10 1-1000000
		// lo-lo 1-2 100000-400000
		// hi-lo 1-10 100000-400000
		// lo-hi 1-2 1-1000000
		
		int noCloudlets = 100; // used to create random Cloudlets
		int noVms = 5; // used to create random VMs
		// generate [minMipsUnif;maxMipsUnif) and multiply with 1000 to get mips
		int minMipsUnif = 1;
		int maxMipsUnif = 3;
		// generate length [minLengthUnif;maxLengthUnif)
		int minLengthUnif = 800000;
		int maxLengthUnif = 900000;
		int seed = 10;
		
		int schedulingInterval = 100;
		// generate arrival time [minArrivalUnif;maxArrivalUnif)
		int minArrivalUnif = 0;//0;
		int maxArrivalUnif = 100;//500;
		
		// generate deadline [minDeadlineUnif;maxDeadlineUnif)
		int minDeadlineUnif = 0;
		int maxDeadlineUnif = 1000;
		
		//price intervals for datacenters
		int priceInterval1 = 400;
		int priceInterval2 = 400;
		
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
			SchedulingAlgorithm algorithm = new DeadlineAlgorithm();
			
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
			OnlineVm vm2 = new OnlineVm(broker.getId(), 2000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());
			OnlineVm vm3 = new OnlineVm(broker.getId(), 3000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());

			ArrayList<VmType> vmTypes1 = new ArrayList<>();
			vmTypes1.add(new VmType(vm1, noCloudlets, 1.0, priceInterval1, "PB1_1.0"));
			vmTypes1.add(new VmType(vm2, noCloudlets, 1.5, priceInterval1, "PB1_1.5"));
			vmTypes1.add(new VmType(vm3, noCloudlets, 2.5, priceInterval1, "PB1_2.5"));

			/* Create the datacenter. */
			createDatacenter("Public1", vmTypes1,false);

			/*------------------------------------------*/

			/* Create another public cloud. */

			/* Create custom VM types. */
			OnlineVm vm4 = new OnlineVm(broker.getId(), 1500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared());
			OnlineVm vm5 = new OnlineVm(broker.getId(), 2500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared());

			ArrayList<VmType> vmTypes2 = new ArrayList<>();
			vmTypes2.add(new VmType(vm1, noCloudlets, 0.9, priceInterval2, "PB2_0.9"));
			vmTypes2.add(new VmType(vm4, noCloudlets, 1.5, priceInterval2, "PB2_1.5"));
			vmTypes2.add(new VmType(vm5, noCloudlets, 2.0, priceInterval2, "PB2_2.0"));

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
			Scheduler scheduler = new HybridScheduler(vmTypes,broker,vmList,cloudletList,algorithm, schedulingInterval, publicAlgorithm, publicVmList, publicVmTypes);

			/* Make the necessary preparations before starting the simulation. 
			 * This is the step where the algorithm is run. 
			 */
			scheduler.prepareSimulation();

			/* Start simulation. */
			CloudSim.startSimulation();

			/* Stop simulation. */
			CloudSim.stopSimulation();

			/* Print the results. */
			printResult(scheduler.getFinishedCloudlets(),scheduler.getVmList());
			
			Log.printLine("Simulation finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
}

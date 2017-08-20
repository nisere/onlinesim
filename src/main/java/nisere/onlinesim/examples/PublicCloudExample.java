package nisere.onlinesim.examples;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import nisere.onlinesim.MyCloudlet;
import nisere.onlinesim.MyDatacenterBroker;
import nisere.onlinesim.MyVm;
import nisere.onlinesim.Scheduler;
import nisere.onlinesim.VmType;
import nisere.onlinesim.algorithm.SchedulingAlgorithm;
import nisere.onlinesim.algorithm.WorkQueueAlgorithm;

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
public class PublicCloudExample extends Example {
	
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
		// generate arrival time [minArrivalUnif;maxArrivalUnif)
		int minArrivalUnif = 0;
		int maxArrivalUnif = 500;

		Log.printLine("Starting simulation...");
		try {
			/* Initialize the simulation. */
			CloudSim.init(1, Calendar.getInstance(), false);
			
			/* Create a broker object. */
			MyDatacenterBroker broker = new MyDatacenterBroker("Broker");

			/*------------------------------------------*/
			
			/* Create a public cloud. */

			/* Create custom VM types. */
			MyVm vm1 = new MyVm(broker.getId(), 1000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());
			MyVm vm2 = new MyVm(broker.getId(), 2000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());
			MyVm vm3 = new MyVm(broker.getId(), 3000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());

			ArrayList<VmType> vmTypes1 = new ArrayList<>();
			vmTypes1.add(new VmType(vm1, noCloudlets, 1.0, 3600, "PB1_1.0"));
			vmTypes1.add(new VmType(vm2, noCloudlets, 1.5, 3600, "PB1_1.5"));
			vmTypes1.add(new VmType(vm3, noCloudlets, 2.5, 3600, "PB1_2.5"));

			/* Create the datacenter. */
			Datacenter datacenter1 = createDatacenter("Public1", vmTypes1);

			/*------------------------------------------*/

			/* Create another public cloud. */

			/* Create custom VM types. */
			MyVm vm4 = new MyVm(broker.getId(), 1500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared());
			MyVm vm5 = new MyVm(broker.getId(), 2500, 1, 1024, 512, 5000, "Xen", new CloudletSchedulerSpaceShared());

			ArrayList<VmType> vmTypes2 = new ArrayList<>();
			vmTypes2.add(new VmType(vm1, noCloudlets, 0.9, 3600, "PB2_0.9"));
			vmTypes2.add(new VmType(vm4, noCloudlets, 1.5, 3600, "PB2_1.5"));
			vmTypes2.add(new VmType(vm5, noCloudlets, 2.0, 3600, "PB2_2.0"));

			/* Create the datacenter. */
			Datacenter datacenter2 = createDatacenter("Public2", vmTypes2);
			
			/*------------------------------------------*/
			
			/* Create the VM list. */
			List<VmType> vmTypes = new ArrayList<>();
			vmTypes.addAll(vmTypes1);
			//vmTypes.addAll(vmTypes2);
			List<MyVm> vmList = populateVmList(vmTypes);

			/* Create the Cloudlet list. */
			List<MyCloudlet> cloudletList = createRandomMyCloudlets(broker.getId(),noCloudlets,minLengthUnif, maxLengthUnif, seed, minArrivalUnif, maxArrivalUnif);
			
			/* Choose the scheduling algorithm. */
			//SchedulingAlgorithm algorithm = new NOAlgorithm();
			SchedulingAlgorithm algorithm = new WorkQueueAlgorithm();
			//SchedulingAlgorithm algorithm = new SufferageAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMinAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMaxAlgorithm();
			//SchedulingAlgorithm algorithm = new MaxMinAlgorithm();
			//SchedulingAlgorithm algorithm = new LJFR_SJFRAlgorithm();
			
			/* Create a scheduler. */
			Scheduler scheduler = new Scheduler(vmTypes,broker,vmList,cloudletList,algorithm,schedulingInterval);

			/* Make the necessary preparations before starting the simulation. 
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
		/** Prints the results */


}

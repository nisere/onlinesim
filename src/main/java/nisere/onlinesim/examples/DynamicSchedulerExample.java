package nisere.onlinesim.examples;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
public class DynamicSchedulerExample extends Example {
	
	public static void main(String[] args) {

		// hi-hi 1-10 1-1000000
		// lo-lo 1-2 100000-400000
		// hi-lo 1-10 100000-400000
		// lo-hi 1-2 1-1000000
		
		int noCloudlets = 10; // used to create random Cloudlets
		int noVms = 4; // used to create random VMs
		// generate [minMipsUnif;maxMipsUnif) and multiply with 1000 to get mips
		int minMipsUnif = 1;
		int maxMipsUnif = 3;
		// generate length [minLengthUnif;maxLengthUnif)
		int minLengthUnif = 800000;
		int maxLengthUnif = 900000;
		int seed = 8;
		
		int schedulingInterval = 100;
		// generate arrival time [minArrivalUnif;maxArrivalUnif)
		int minArrivalUnif = 0;//0;
		int maxArrivalUnif = 100;//500;
		
		// generate deadline [minDeadlineUnif;maxDeadlineUnif)
		int minDeadlineUnif = 0;
		int maxDeadlineUnif = 1000;
		
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
			createDatacenter("Private", vmTypes0,true);
			
			/*------------------------------------------*/
			
			/* Create the VM list. */
			List<VmType> vmTypes = new ArrayList<>();
			vmTypes.addAll(vmTypes0);
			List<OnlineVm> vmList = populateVmList(vmTypes);

			/* Create the Cloudlet list. */
			List<OnlineCloudlet> cloudletList = createRandomCloudlets(broker.getId(),noCloudlets,minLengthUnif, maxLengthUnif, seed,minArrivalUnif, maxArrivalUnif, minDeadlineUnif, maxDeadlineUnif);
			
			/* Choose the scheduling algorithm. */
			//SchedulingAlgorithm algorithm = new WorkQueueAlgorithm();
			//SchedulingAlgorithm algorithm = new SufferageAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMinAlgorithm();
			//SchedulingAlgorithm algorithm = new MinMaxAlgorithm();
			//SchedulingAlgorithm algorithm = new MaxMinAlgorithm();
			SchedulingAlgorithm algorithm = new LJFR_SJFRAlgorithm();
			
			/* Create a scheduler. */
			Scheduler scheduler = new DynamicScheduler(vmTypes,broker,vmList,cloudletList,algorithm,schedulingInterval);

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

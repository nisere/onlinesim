package nisere.onlinesim.examples;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import nisere.onlinesim.*;
import nisere.onlinesim.algorithm.*;

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
public class PublicCloudExample extends Example {
	
	public static void main(String[] args) {
		int noCloudlets = 8; // used to create random Cloudlets
		// generate length [minLengthUnif;maxLengthUnif)
		int minLengthUnif = 600000;
		int maxLengthUnif = 900000;
		int seed = 1;
		int schedulingInterval = 0; // in seconds; 0 is now
		// generate arrival time [minArrivalUnif;maxArrivalUnif); [0;1) means all tasks now
		int minArrivalUnif = 0;
		int maxArrivalUnif = 1;
		//price intervals for datacenters
		int priceInterval1 = 400;
		int priceInterval2 = 400;

		Log.printLine("Starting simulation...");
		try {
			/* Initialize the simulation. */
			CloudSim.init(1, Calendar.getInstance(), false);
			
			/* Create a broker object. */
			OnlineDatacenterBroker broker = new OnlineDatacenterBroker("Broker");

			/* Create the Cloudlet list. */
			List<OnlineCloudlet> cloudletList = createRandomCloudlets(broker.getId(),noCloudlets,minLengthUnif, maxLengthUnif, seed, minArrivalUnif, maxArrivalUnif);
			cloudletList.get(0).setDeadline(300);
			cloudletList.get(noCloudlets - 1).setDeadline(300);
			
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
			Datacenter datacenter1 = createDatacenter("Public1", vmTypes1,false);

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
			Datacenter datacenter2 = createDatacenter("Public2", vmTypes2,false);
			
			/*------------------------------------------*/
			
			/* Create the VM list. */
			List<VmType> vmTypes = new ArrayList<>();
			vmTypes.addAll(vmTypes1);
			vmTypes.addAll(vmTypes2);
			//List<OnlineVm> vmList = populateVmList(vmTypes);
			List<OnlineVm> vmList = new ArrayList<>();

			/* Choose the scheduling algorithm. */
			SchedulingAlgorithm algorithm = new PublicAlgorithm();
			
			/* Create a scheduler. */
			Scheduler scheduler = new Scheduler(vmTypes,broker,vmList,cloudletList,algorithm,schedulingInterval);

			/* Make the necessary preparations before starting the simulation. 
			 * This is the step where the algorithm is run. 
			 */
			scheduler.prepareSimulation();
			
			Log.printLine("dc1");
			for (Host host : datacenter1.getHostList()) {
				Log.printLine(host.getId());
			}
			Log.printLine("dc2");			
			for (Host host : datacenter2.getHostList()) {
				Log.printLine(host.getId());
			}

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

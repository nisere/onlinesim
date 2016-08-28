package nisere.schedsim;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nisere.schedsim.algorithm.SchedulingAlgorithm;
import nisere.schedsim.algorithm.*;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

public class OnlineScheduler extends Scheduler {
	
	public OnlineScheduler(Map<String,Datacenter> datacenters,
			DatacenterBroker broker,
			List<? extends MyVm> vmList,
			List<? extends MyCloudlet> cloudletList,
			SchedulingAlgorithm algorithm) throws Exception {
		super(datacenters, broker, vmList, cloudletList, algorithm);
	}
	
	/** The scheduling algorithm is run in batches */
	@Override
	protected List<Cloudlet> scheduleCloudlets(List<Cloudlet> cloudlets, List<Vm> vms) {
		long delay = 0;
		List<Cloudlet> list = new LinkedList<>();
		for (Cloudlet cloudlet : cloudlets) {
			if ((cloudlet instanceof MyCloudlet) && ( ((MyCloudlet) cloudlet).getDelay() > delay )) {
				// this is the first of the next batch;
				// schedule the batch then reset the list and add this cloudlet
				getAlgorithm().prepare(delay);
				getAlgorithm().computeSchedule(list, vms);
				list = new LinkedList<>();
				list.add(cloudlet);
				delay = ((MyCloudlet) cloudlet).getDelay();
			} else {
				list.add(cloudlet);
			}
		}
		getAlgorithm().prepare(delay);
		getAlgorithm().computeSchedule(list, vms);
		return getAlgorithm().getCloudletScheduledList();
	}
}

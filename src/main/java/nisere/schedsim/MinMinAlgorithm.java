package nisere.schedsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class MinMinAlgorithm implements SchedulingAlgorithm {
	/** Processor workload. */
	protected double[] workload;
	
	/** List of scheduled cloudlets. */
	protected List<Cloudlet> cloudletScheduledList;
	
	/**
	 * Creates the schedule with MinMin algorithm.
	 */
	public void computeSchedule(List<? extends Cloudlet> cloudletList, List<? extends Vm> vmList) {
    	workload = new double[vmList.size()];
    	cloudletScheduledList = new ArrayList<Cloudlet>();
    	boolean isNotScheduled = true;
    	while (isNotScheduled) {
    		Cloudlet minCloudlet = null;
	    	int minVmId = -1;
	    	double min = -1;
	    	for (Cloudlet cloudlet : cloudletList) {
				// if this cloudlet is bound to a VM continue
				if (cloudlet.getVmId() >= 0) {
					continue;
				}
				for (Vm vm : vmList) {
					//fin min of Cij = Wi + Eij
					if (min == -1 || min > workload[vm.getId()] + cloudlet.getCloudletLength()/vm.getMips()) {
						min = workload[vm.getId()] + cloudlet.getCloudletLength()/vm.getMips();
						minCloudlet = cloudlet;
						minVmId = vm.getId();
					}
				}
	    	}
	    	if (min >= 0) {
	    		minCloudlet.setVmId(minVmId);
	        	workload[minVmId] = min;
	        	cloudletScheduledList.add(minCloudlet);
	    	} else {
	    		isNotScheduled = false;
	    	}
    	}
	}

	/**
	 * Gets the list of scheduled cloudlets. 
	 * @return the list of scheduled cloudlets
	 */
	public List<? extends Cloudlet> getCloudletScheduledList() {
		
		return cloudletScheduledList;
	}
}

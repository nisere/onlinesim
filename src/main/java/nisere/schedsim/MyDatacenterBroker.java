/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package nisere.schedsim;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * MyDatacentreBroker modifies the Cloudsim DatacentreBroker to add scheduling
 * algorithms. These are static algorithms: MinMin, MinMax, MaxMin, LJFR_SJFR,
 * Sufferage, WorkQueue
 * 
 * @author Alina Chera
 */
public class MyDatacenterBroker extends DatacenterBroker {

	/** The algorithm used for scheduling. */
	private MySchedulingAlgorithm algorithm;

	/**
	 * Created a new MyDatacenterBroker object.
	 * 
	 * @param name
	 *            name of this entity
	 * @throws Exception
	 *             the exception
	 */
	public MyDatacenterBroker(String name) throws Exception {
		super(name);
	}

	/**
	 * Gets the scheduling algorithm.
	 * 
	 * @return the scheduling algorithm
	 */
	public MySchedulingAlgorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets the scheduling algorithm.
	 * 
	 * @param algorithm
	 *            the scheduling algorithm
	 */
	public void setAlgorithm(MySchedulingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Submit cloudlets to the created VMs taking into account the schedule. If
	 * the schedule was computed with one of the algorithms, the cloudlets will
	 * have a VM already assigned. If no algorithm was set, the default schedule
	 * will have the same order in which the cloudlets were added to the list.
	 */
	@Override
	protected void submitCloudlets() {
		if (getAlgorithm() == null) {
			super.submitCloudlets();
			return;
		}

		MySchedulingAlgorithm algorithm = getAlgorithm();
		algorithm.computeSchedule(getCloudletList(), getVmList());

		int vmIndex = 0;
		for (Cloudlet cloudlet : algorithm.getCloudletScheduledList()) {
			Vm vm;
			// if user didn't bind this cloudlet and it has not been executed
			// yet
			if (cloudlet.getVmId() == -1) {
				vm = getVmsCreatedList().get(vmIndex);
			} else { // submit to the specific VM
				vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				if (vm == null) { // VM was not created
					Log.printLine(CloudSim.clock() + ": " + getName()
							+ ": Postponing execution of cloudlet "
							+ cloudlet.getCloudletId()
							+ ": bount VM not available");
					continue;
				}
			}

			Log.printLine(CloudSim.clock() + ": " + getName()
					+ ": Sending cloudlet " + cloudlet.getCloudletId()
					+ " to VM #" + vm.getId());
			cloudlet.setVmId(vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()),
					CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
			cloudletsSubmitted++;
			vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
			getCloudletSubmittedList().add(cloudlet);
		}

		// remove submitted cloudlets from waiting list
		for (Cloudlet cloudlet : getCloudletSubmittedList()) {
			algorithm.getCloudletScheduledList().remove(cloudlet);
		}
	}

	// /**
	// * Create the virtual machines in a datacenter.
	// * It overrides the original method with an additional checking
	// * for the case when there are different datacenters with specific VM.
	// *
	// * @param datacenterId the id of the chosen datacenter
	// */
	// @Override
	// protected void createVmsInDatacenter(int datacenterId) {
	// // send as much vms as possible for this datacenter before trying the
	// next one
	// // except when a different datacenter is already assinged to the VM
	// int requestedVms = 0;
	// String datacenterName = CloudSim.getEntityName(datacenterId);
	// for (Vm vm : getVmList()) {
	// if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
	// if ( (vm instanceof MyVm) && ( ( ((MyVm)vm).getDatacenterId() == -1 ) ||
	// ( ((MyVm)vm).getDatacenterId() == datacenterId ) ) )
	// continue;
	//
	// Log.printLine(CloudSim.clock() + ": " + getName() +
	// ": Trying to Create VM #" + vm.getId()
	// + " in " + datacenterName);
	// sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
	// requestedVms++;
	// }
	// }
	//
	// getDatacenterRequestedIdsList().add(datacenterId);
	//
	// setVmsRequested(requestedVms);
	// setVmsAcks(0);
	// }
}

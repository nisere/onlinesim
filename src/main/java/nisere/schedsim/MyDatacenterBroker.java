/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package nisere.schedsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

public class MyDatacenterBroker extends DatacenterBroker {
	public static final int CLOUDLET_DELAY = 999;

	public MyDatacenterBroker(String name) throws Exception {
		super(name);
	}
	
	/**
	 * It processes CLOUDLET_DELAY event.
	 */
	@Override
	protected void processOtherEvent(SimEvent ev) {
		switch (ev.getTag()) {
		case CLOUDLET_DELAY:
	//		List<MyVm> list = new ArrayList<>();
	//		for (Vm vm : getVmList()) {
	//			list.add(((MyVm)vm).clone(vm.getId() + 100));
	//		}
	//		submitVmList(list);
			int datacenterId = getVmsToDatacentersMap().get((getVmList().get(0).getId()));
			createVmsInDatacenter(datacenterId);
			submitCloudlets();
			break;
		}
	}
	
	/**
	 * Submit cloudlets to the created VMs taking into account the delay of the cloudlet before submitting.
	 * 
	 * For the case when it is used to simulate online arrival of cloudlets,
	 * it expects the cloudlet list to be sorted by delay ascending to simulate a real queue.
	 */
	@Override
	protected void submitCloudlets() {
		int vmIndex = 0;
		List<Cloudlet> successfullySubmitted = new ArrayList<Cloudlet>();
		List<Cloudlet> cloudletList = getCloudletList();
		for (Cloudlet cloudlet : cloudletList) {
			if ((cloudlet instanceof MyCloudlet) && (CloudSim.clock() < ((MyCloudlet)cloudlet).getDelay())) {
				send(getName(), ((MyCloudlet)cloudlet).getDelay(), CLOUDLET_DELAY);
				break;
			}
			Vm vm;
			// if user didn't bind this cloudlet and it has not been executed yet
			if (cloudlet.getVmId() == -1) {
				vm = getVmsCreatedList().get(vmIndex);
			} else { // submit to the specific vm
				vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				if (vm == null) { // vm was not created
					if(!Log.isDisabled()) {				    
					    Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Postponing execution of cloudlet ",
							cloudlet.getCloudletId(), ": bount VM not available");
					}
					continue;
				}
			}

			if (!Log.isDisabled()) {
			    Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Sending cloudlet ",
					cloudlet.getCloudletId(), " to VM #", vm.getId());
			}
			
			cloudlet.setVmId(vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
			cloudletsSubmitted++;
			vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
			getCloudletSubmittedList().add(cloudlet);
			successfullySubmitted.add(cloudlet);
		}

		// remove submitted cloudlets from scheduled list
		getCloudletList().removeAll(successfullySubmitted);
	}

	/**
	 * Create the virtual machines in a datacenter.
	 * 
	 * It overrides the original method with an additional checking
	 * for the case when there are different datacenters with specific VM.
	 * 
	 * It also removes the check for the existing of the VM in the datacenter
	 * to allow recreation after it was destroyed.
	 * 
	 * @param datacenterId the id of the chosen datacenter
	 */
	@Override
	protected void createVmsInDatacenter(int datacenterId) {
		// send as much vms as possible for this datacenter before trying the next one
		// except when a different datacenter is already assigned to the VM
		int requestedVms = 0;
		String datacenterName = CloudSim.getEntityName(datacenterId);
		for (Vm vm : getVmList()) {
			if (!( (vm instanceof MyVm) && ( ( ((MyVm)vm).getDatacenterId() == -1 ) ||
					( ((MyVm)vm).getDatacenterId() == datacenterId ) ) )) {
				continue;
			}
			Log.printLine(CloudSim.clock() + ": " + getName() +
					": Trying to Create VM #" + vm.getId()
					+ " in " + datacenterName);
			sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
			requestedVms++;
		}
		
		getDatacenterRequestedIdsList().add(datacenterId);
		
		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}
	 
	/**
	 * Process a cloudlet return event.
	 * The change allow for the simulation to continue until all cloudlets are finished.
	 * 
	 * @param ev a SimEvent object
	 */
	@Override
	protected void processCloudletReturn(SimEvent ev) {
       Cloudlet cloudlet = (Cloudlet) ev.getData();
       getCloudletReceivedList().add(cloudlet);
       Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Cloudlet ", cloudlet.getCloudletId(),
                       " received");
       cloudletsSubmitted--;
       if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
           Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": All Cloudlets executed. Finishing...");
           clearDatacenters();
           finishExecution();
       } else { // some cloudlets haven't finished yet
           if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
                   // all the cloudlets sent finished. It means that some bound
                   // cloudlet is waiting its VM be created
                   clearDatacenters();
                   int datacenterId = getVmsToDatacentersMap().get((getVmList().get(0).getId()));
                   createVmsInDatacenter(datacenterId);
           }
       }
	}
	 
}

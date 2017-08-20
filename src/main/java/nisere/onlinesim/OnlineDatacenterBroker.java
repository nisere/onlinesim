package nisere.onlinesim;

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

/**
 * This class adds to the DatacenterBroker the possibility to process
 * delayed cloudlets in order to simulate online arrival of tasks.
 * 
 * @author Alina Chera
 *
 */
public class OnlineDatacenterBroker extends DatacenterBroker {
	/**
	 * A constant indicating the CLOUDLET_DELAY event.
	 * This event is used to delay submition of a specific cloudlet
	 * to a later moment.
	 */
	public static final int CLOUDLET_DELAY = 999;

	/**
	 * Creates a new OnlineDatacenterBroker object.
	 * 
	 * @param name the name of the broker
	 * @throws Exception 
	 */
	public OnlineDatacenterBroker(String name) throws Exception {
		super(name);
	}
	
	/**
	 * It processes CLOUDLET_DELAY event.
	 */
	@Override
	protected void processOtherEvent(SimEvent ev) {
		switch (ev.getTag()) {
		case CLOUDLET_DELAY:
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
			if ((cloudlet instanceof OnlineCloudlet) && (CloudSim.clock() < ((OnlineCloudlet)cloudlet).getDelay())) {
				send(getName(), ((OnlineCloudlet)cloudlet).getDelay(), CLOUDLET_DELAY);
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
	 * @param datacenterId the id of the chosen datacenter
	 */
	@Override
	protected void createVmsInDatacenter(int datacenterId) {
		// send as much VMs as possible to this datacenter before trying the next one
		// except when a different datacenter is already assigned to the VM;
		// in this case send the VM directly to the assigned datacenter
		int requestedVms = 0;
		for (Vm vm : getVmList()) {
			// if VM was previously assigned to a different datacenter ignore it
			if (getVmsToDatacentersMap().containsKey(vm.getId()) 
					&& (getVmsToDatacentersMap().get(vm.getId()) != datacenterId) )
				continue;
			
			int otherId = datacenterId;
			if ((vm instanceof OnlineVm) && ( ((OnlineVm)vm).getDatacenterId() >= 0 )) {
				otherId = ((OnlineVm)vm).getDatacenterId();
			}
			Log.printLine(CloudSim.clock() + ": " + getName() +
					": Trying to Create VM #" + vm.getId()
					+ " in " + CloudSim.getEntityName(otherId));
			sendNow(otherId, CloudSimTags.VM_CREATE_ACK, vm);
			requestedVms++;
		}
		
		getDatacenterRequestedIdsList().add(datacenterId);
		
		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}
	 
	/**
	 * Process a cloudlet return event.
	 * 
	 * The VMs aren't destroyed anymore until all cloudlets are finished.
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
       } // else some cloudlets haven't finished yet
	}
	

	/**
	 * Process the ack received due to a request for VM creation.
	 * Not changed, added for debugging purposes.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmCreate(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];

		if (result == CloudSimTags.TRUE) {
			getVmsToDatacentersMap().put(vmId, datacenterId);
			getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": VM #", vmId,
					" has been created in Datacenter #", datacenterId, ", Host #",
					VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
		} else {
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Creation of VM #", vmId,
					" failed in Datacenter #", datacenterId);
		}

		incrementVmsAcks();

		// all the requested VMs have been created
		if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
			submitCloudlets();
		} else {
			// all the acks received, but some VMs were not created
			if (getVmsRequested() == getVmsAcks()) {
				// find id of the next datacenter that has not been tried
				for (int nextDatacenterId : getDatacenterIdsList()) {
					if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
						createVmsInDatacenter(nextDatacenterId);
						return;
					}
				}

				// all datacenters already queried
				if (getVmsCreatedList().size() > 0) { // if some vm were created
					submitCloudlets();
				} else { // no vms created. abort
					Log.printLine(CloudSim.clock() + ": " + getName()
							+ ": none of the required VMs could be created. Aborting");
					finishExecution();
				}
			}
		}
	}
}

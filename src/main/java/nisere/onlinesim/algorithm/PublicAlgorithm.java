package nisere.onlinesim.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nisere.onlinesim.OnlineCloudlet;
import nisere.onlinesim.OnlineVm;
import nisere.onlinesim.VmType;

/**
 * Minimizes the cost
 * @author Alina Chera
 *
 */
public class PublicAlgorithm extends SchedulingAlgorithm {
	
	@Override
	protected void initialize() {
		setScheduledCloudletList(new LinkedList<OnlineCloudlet>());
		setUnscheduledCloudletList(new LinkedList<OnlineCloudlet>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList, List<? extends OnlineVm> vmList,
			List<? extends VmType> vmTypes, double time) {
		//start with a fresh list of VM
		List<? extends OnlineVm> vms = new ArrayList<>();
		
		for (OnlineCloudlet cloudlet : cloudletList) {
			OnlineVm optimVm = null;
			VmType optimType = null;
			double minVmCost = Double.MAX_VALUE;
			double minTypeCost = Double.MAX_VALUE;
			//global values, in case no optim is found, assign to the cheapest vm
			//for example, no vm or vm type is found to execute the cloudlet within deadline
			OnlineVm minVm = null;
			VmType minType = null;
			double minCost = Double.MAX_VALUE;
			
			for (OnlineVm vm : vms) {
				double cost  = computeCost(cloudlet,vm);
				if (minVmCost > cost && checkDeadline(cloudlet,vm)) {
					optimVm = vm;
					minVmCost = cost;
				}
				if (minCost > cost) {
					minCost = cost;
					minVm = vm;
				}
			}
			for (VmType type : vmTypes) {
				double cost  = computeCost(cloudlet,type);
				if (minTypeCost > cost && checkDeadline(cloudlet,type,time)) {
					optimType = type;
					minTypeCost = cost;
				}
				if (minCost > cost) {
					minCost = cost;
					minVm = null;
					minType = type;
				}
			}
			if (minVmCost < minTypeCost) { //chose optimVm if exists
				assignCloudletToVm(cloudlet,optimVm, time);
			} else if (optimType != null) { //choose optimType if exists
				optimVm = optimType.createVm();
				((List<OnlineVm>)vms).add(optimVm);
				assignCloudletToVm(cloudlet,optimVm, time);
			} else {//no optimVm nor optimType exist
				getUnscheduledCloudletList().add(cloudlet); //leave unscheduled
//				//assign to cheapest
//				if (minVm != null) {
//					assignCloudletToVm(cloudlet,minVm, time);
//				} else if (minType != null) {
//					minVm = minType.createVm();
//					((List<OnlineVm>)vms).add(minVm);
//					assignCloudletToVm(cloudlet,minVm, time);
//				} else {
//					getUnscheduledCloudletList().add(cloudlet); //leave unscheduled
//				}
			}
		}
		//add the new VM to the scheduler VM list
		((List<OnlineVm>)vmList).addAll(vms);
	}
	
	public double computeCost(OnlineCloudlet cloudlet, OnlineVm vm) {
		double execTime = cloudlet.getCloudletLength() / vm.getMips();
		double finishTime = vm.getUptime() + execTime;
		return Math.ceil(finishTime/vm.getVmType().getPriceInterval()) * vm.getVmType().getPrice() - vm.getCost();
	}
	
	public double computeCost(OnlineCloudlet cloudlet, VmType type) {
		double execTime = cloudlet.getCloudletLength() / type.getVm().getMips();
		return Math.ceil(execTime/type.getPriceInterval()) * type.getPrice();
	}
	
	public boolean checkDeadline(OnlineCloudlet cloudlet, OnlineVm vm) {
		double execTime = cloudlet.getCloudletLength() / vm.getMips();
		return vm.getUptime() + execTime <= cloudlet.getArrivalTime() + cloudlet.getDeadline();
	}
	
	public boolean checkDeadline(OnlineCloudlet cloudlet, VmType type, double delay) {
		double execTime = cloudlet.getCloudletLength() / type.getVm().getMips();
		return delay + execTime <= cloudlet.getArrivalTime() + cloudlet.getDeadline();
	}
	
	public void assignCloudletToVm(OnlineCloudlet cloudlet, OnlineVm vm, double delay) {
		cloudlet.setDelay(delay + vm.getUptime());
		cloudlet.setVmId(vm.getId());
		cloudlet.setVm(vm);
		
		double execTime = cloudlet.getCloudletLength() / vm.getMips();
		vm.setUptime(vm.getUptime() + execTime);
		vm.setCost(Math.ceil(vm.getUptime()/vm.getVmType().getPriceInterval()) * vm.getVmType().getPrice());
		
		getScheduledCloudletList().add(cloudlet);
	}

	@Override
	public void unscheduleCloudlet(OnlineCloudlet cloudlet, double delay) {
		// TODO Auto-generated method stub
		
	}
	
}

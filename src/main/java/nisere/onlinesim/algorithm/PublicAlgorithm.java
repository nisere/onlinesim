package nisere.onlinesim.algorithm;

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
	protected void initCloudletScheduledList() {
		setCloudletScheduledList(new LinkedList<OnlineCloudlet>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void computeSchedule(List<? extends OnlineCloudlet> cloudletList, List<? extends OnlineVm> vmList,
			List<? extends VmType> vmTypes) {

		for (OnlineCloudlet cloudlet : cloudletList) {
			OnlineVm optimVm = null;
			VmType optimType = null;
			double minVmCost = Double.MAX_VALUE;
			double minTypeCost = Double.MAX_VALUE;
			
			for (OnlineVm vm : vmList) {
				double cost  = computeCost(cloudlet,vm);
				if (minVmCost > cost) {
					optimVm = vm;
					minVmCost = cost;
				}
			}
			for (VmType type : vmTypes) {
				double cost  = computeCost(cloudlet,type);
				if (minTypeCost > cost) {
					optimType = type;
					minTypeCost = cost;
				}
			}
			if (minVmCost < minTypeCost) {
				assignCloudletToVm(cloudlet,optimVm);
			} else if (optimType != null) {
					optimVm = optimType.createVm();
					((List<OnlineVm>)vmList).add(optimVm);
					assignCloudletToVm(cloudlet,optimVm);		
			} //if there is no optimVm or optimType do nothing
		}
	}
	
	public double computeCost(OnlineCloudlet cloudlet, OnlineVm vm) {
		double execTime = cloudlet.getCloudletLength() / vm.getMips();
		double finishTime = vm.getUptime() + execTime;
		double cost = Double.MAX_VALUE;
		if (cloudlet.getDeadline() <= finishTime) {
			cost = Math.ceil((vm.getUptime() + execTime)/vm.getVmType().getPriceInterval()*vm.getVmType().getPrice());
		}
		return cost;
	}
	
	public double computeCost(OnlineCloudlet cloudlet, VmType type) {
		double execTime = cloudlet.getCloudletLength() / type.getVm().getMips();
		double cost = Double.MAX_VALUE;
		if (cloudlet.getDeadline() <= execTime) {
			cost = Math.ceil(execTime/type.getPriceInterval()*type.getPrice());
		}
		return cost;
	}
	
	public void assignCloudletToVm(OnlineCloudlet cloudlet, OnlineVm vm) {
		cloudlet.setDelay(vm.getUptime());
		cloudlet.setVmId(vm.getId());
		
		double execTime = cloudlet.getCloudletLength() / vm.getMips();
		vm.setUptime(vm.getUptime() + execTime);
		vm.setCost(Math.ceil(vm.getUptime()/vm.getVmType().getPriceInterval()*vm.getVmType().getPrice()));
		
		getCloudletScheduledList().add(cloudlet);
	}
}

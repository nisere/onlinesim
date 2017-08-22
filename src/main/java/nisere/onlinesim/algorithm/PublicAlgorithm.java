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
		OnlineVm optimVm = null;
		for (OnlineCloudlet cloudlet : cloudletList) {
			double minCost = -1.0;
			for (OnlineVm vm : vmList) {
				double cost  = computeCost(cloudlet,vm);
				if (minCost == -1.0 || minCost > cost) {
					optimVm = vm;
					minCost = cost;
				}
			}
			for (VmType type : vmTypes) {
				double cost  = computeCost(cloudlet,type);
				if (minCost == -1.0 || minCost > cost) {
					OnlineVm vm = type.createVm();//should not create here
					((List<OnlineVm>)vmList).add(vm);
					optimVm = vm;
					minCost = cost;
				}
			}
			if (optimVm != null) {
				cloudlet.setDelay(optimVm.getUptime());
				cloudlet.setVmId(optimVm.getId());
				
				double execTime = cloudlet.getCloudletLength() / optimVm.getMips();
				optimVm.setUptime(optimVm.getUptime() + execTime);
				optimVm.setCost(Math.ceil(optimVm.getUptime()/optimVm.getVmType().getPriceInterval()*optimVm.getVmType().getPrice()));
				
				getCloudletScheduledList().add(cloudlet);
			}
		}
	}
	
	protected double computeCost(OnlineCloudlet cloudlet, OnlineVm vm) {
		double execTime = cloudlet.getCloudletLength() / vm.getMips();
		double cost = Math.ceil((vm.getUptime() + execTime)/vm.getVmType().getPriceInterval()*vm.getVmType().getPrice());
		return cost;
	}
	
	protected double computeCost(OnlineCloudlet cloudlet, VmType type) {
		double execTime = cloudlet.getCloudletLength() / type.getVm().getMips();
		double cost = Math.ceil(execTime/type.getPriceInterval()*type.getPrice());
		return cost;
	}
}

package nisere.onlinesim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class VmType {
	/** The datacenter to which this VM type belongs */
	private Datacenter datacenter;
	
	/** A VM that can be used to create new VMs of this type */
	private OnlineVm vm;
	
	/** How many VMs of this type can be created; 0 means infinity */
	private int count;
	
	/** The price of using this type of VM applied per priceInterval */
	private double price;
	
	/** The time interval on which the price is computed */
	private int priceInterval;
	
	/** The name of this VM type */
	private String name;
	
	/**
	 * @param vm
	 * @param count
	 * @param price
	 * @param priceInterval
	 * @param name
	 */
	public VmType(OnlineVm vm, int count, double price, int priceInterval, String name) {
		this.vm = vm;
		this.count = count;
		this.price = price;
		this.priceInterval = priceInterval;
		this.name = name;
	}
	
	/**
	 * Creates an VM of this type and a host for this VM which is added to the datacenter
	 * @return the created VM
	 */
	public OnlineVm createVm() {
		OnlineVm vm = getVm();
		OnlineVm newVm = new OnlineVm(vm.getUserId(), vm.getMips(), vm.getNumberOfPes(), vm.getRam(), vm.getBw(),
				vm.getSize(), vm.getVmm(), new CloudletSchedulerSpaceShared(), datacenter.getId());
		newVm.setVmType(this);
		
		List<Pe> peList = new ArrayList<Pe>();
		peList.add(new Pe(0, new PeProvisionerSimple(vm.getMips())));
		OnlineHost host = new OnlineHost(new RamProvisionerSimple(vm.getRam()),
				new BwProvisionerSimple(vm.getBw()), vm.getSize(), peList, new VmSchedulerSpaceShared(peList));
		host.setDatacenter(datacenter);
		
		//datacenter.getHostList().add(host);
		//interface OnlineVmAllocationPolicy addHost method, simple like this+implements interface
		if (datacenter.getVmAllocationPolicy() instanceof OnlineVmAllocationPolicySimple)
			((OnlineVmAllocationPolicySimple)datacenter.getVmAllocationPolicy()).addHost(host);
		
		return newVm;
	}
	
	public Datacenter getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(Datacenter datacenter) {
		this.datacenter = datacenter;
	}

	public OnlineVm getVm() {
		return vm;
	}

	public void setVm(OnlineVm vm) {
		this.vm = vm;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getPriceInterval() {
		return priceInterval;
	}

	public void setPriceInterval(int priceInterval) {
		this.priceInterval = priceInterval;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

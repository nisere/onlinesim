package nisere.schedsim;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

/**
 * MyVm class adds to the Cloudsim Vm class a cost per instance and a connection to a datacenter.
 * 
 * @author Alina Chera
 *
 */
public class MyVm extends Vm {

	/**
	 * The datacenter id where this VM is assigned.
	 * A negative value means no assignment.
	 */
	private int datacenterId;
	
	/**
	 * The id of the instance type. It must be unique for a datacenter.
	 */
	private int typeId;
	
	/** Uptime of this VM */
	private double uptime;
	
	/** Cost of using this VM */
	private double cost;

	/**
	 * Creates a new MyVm object.
	 * 
	 * @param id
	 *            the id of this VM
	 * @param userId
	 *            the id of this VM's owner
	 * @param mips
	 *            the mips of this VM
	 * @param numberOfPes
	 *            the number of CPUs of this VM
	 * @param ram
	 *            the amount of RAM (in MB)
	 * @param bw
	 *            the amount of bandwidth (in Mbps)
	 * @param size
	 *            the size the VM image size (the amount of storage it will use,
	 *            at least initially) (in MB)
	 * @param vmm
	 *            the virtual machine monitor that manages this VM
	 * @param cloudletScheduler
	 *            the cloudlet scheduler policy for cloudlets scheduling
	 * @param datacenterId
	 *            the id of the datacenter assigned to the VM
	 * @param typeId
	 *            the id of the instance type, unique per datacenter
	 */
	public MyVm(final int id, final int userId, final double mips,
			final int numberOfPes, final int ram, final long bw,
			final long size, final String vmm,
			final CloudletScheduler cloudletScheduler,			 
			final int datacenterId, final int typeId) {
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm,
				cloudletScheduler);

		this.datacenterId = datacenterId;
		this.typeId = typeId;
		setUptime(0.0);
		setCost(0.0);
	}

	/**
	 * Gets the id of the datacenter to which this VM belongs.
	 * 
	 * @return the id of the datacenter to which this VM belongs
	 */
	public int getDatacenterId() {
		return datacenterId;
	}

	/**
	 * Sets the id of the datacenter to which this VM belongs.
	 * 
	 * @param id the id of the datacenter to which this VM belongs
	 */
	public void setDatacenterId(int id) {
		datacenterId = id;
	}

	/**
	 * Gets the id of the instance type
	 * 
	 * @return the id of the instance type
	 */
	public int getTypeId() {
		return typeId;
	}

	/**
	 * Sets the id of the instance type. It must be unique per datacenter.
	 * 
	 * @param id the id of the instance type
	 */
	public void setTypeId(int id) {
		typeId = id;
	}

	public double getUptime() {
		return uptime;
	}

	public void setUptime(double time) {
		this.uptime = time;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
}

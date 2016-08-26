package nisere.schedsim;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

public class MyVm extends Vm {

	/**
	 * The unit of time (in seconds) for which the cost is applied.
	 */
	private int timeInterval;

	/**
	 * The cost per unit of time for using this VM.
	 */
	private double costPerTimeInterval;

	/**
	 * The datacenter id where this VM is assigned.
	 * A value of -1 means no assignment.
	 */
	private int datacenterId;

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
	 * @param timeInterval
	 * 			  the time interval for which the cost is applied (in seconds)
	 * @param costPerTimeInterval
	 * 			  the cost per time interval
	 */
	public MyVm(final int id, final int userId, final double mips,
			final int numberOfPes, final int ram, final long bw,
			final long size, final String vmm,
			final CloudletScheduler cloudletScheduler,			 
			final int timeInterval, final double costPerTimeInterval, final int datacenterId) {
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm,
				cloudletScheduler);

		this.timeInterval = Math.max(0, timeInterval);
		this.costPerTimeInterval = Math.max(0, costPerTimeInterval);
		this.datacenterId = datacenterId;
	}
	
	/**
	 * Gets the cost of this VM applied per interval of time.
	 * 
	 * @return the cost of this VM applied per interval of time
	 */
	public double getCostPerTimeInterval() {
		return costPerTimeInterval;
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
	 * Gets the interval of time for which the cost of this VM is applied.
	 * 
	 * @return the interval of time for which the cost of this VM is applied (in
	 *         seconds)
	 */
	public int getTimeInterval() {
		return timeInterval;
	}

	/**
	 * Sets the cost of this VM applied per interval of time.
	 * 
	 * @param cost
	 *            the cost of this VM applied per interval of time; must be >= 0
	 * @return <code>true</code> if it is successful, <code>false</code>
	 *         otherwise
	 */
	public boolean setCostPerTimeInterval(double cost) {
		boolean success = false;
		if (cost >= 0) {
			costPerTimeInterval = cost;
			success = true;
		}
		return success;
	}

	/**
	 * Sets the id of the datacenter to which this VM belongs.
	 * 
	 * @param id
	 *            the id of the datacenter to which this VM belongs
	 */
	public void setDatacenterId(int id) {
		datacenterId = id;
	}

	/**
	 * Sets the interval of time for which the cost of this VM is applied.
	 * 
	 * @param seconds
	 *            the interval of time for which the cost of this VM is applied
	 *            (in seconds); must be >= 0
	 * @return <code>true</code> if it is successful, <code>false</code>
	 *         otherwise
	 */
	public boolean setTimeInterval(int seconds) {
		boolean success = false;
		if (seconds >= 0) {
			timeInterval = seconds;
			success = true;
		}
		return success;
	}
}

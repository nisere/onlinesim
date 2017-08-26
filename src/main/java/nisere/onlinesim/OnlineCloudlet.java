package nisere.onlinesim;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

/**
 * This class adds to the Cloudsim Cloudlet a deadline to simulate deadline constrained tasks
 * and a delay to simulate online arrival.
 * 
 * @author Alina Chera
 *
 */
public class OnlineCloudlet extends Cloudlet {
	/** Last id assigned to an object of this class */
	public static int lastId = 0;
	
	/**
	 * The cloudlet deadline. The amount of time in seconds before the cloudlet
	 * must be completed.
	 */
	private long deadline;

	/**
	 * The delay from the start of the simulation when this cloudlet was
	 * processed.
	 */
	private double delay;
	
	/**
	 * The arrival time of the cloudlet, in seconds.
	 */
	private long arrivalTime;
	
	/** The VM on which the cloudlet is scheduled to run */
	private OnlineVm vm;

	/**
	 * Allocates a OnlineCloudlet object.
	 * 
	 * @param cloudletLength
	 *            the size of this cloudlet (in MI) >= 1
	 * @param pesNumber
	 *            the number of CPUs required >= 1
	 * @param cloudletFileSize
	 *            the size of this cloudlet when submitted (in bytes) >= 1
	 * @param cloudletOutputSize
	 *            the size of this cloudlet when finished (in bytes) >= 1
	 * @param deadline
	 *            the deadline of this cloudlet >= 0
	 * @param arrivalTime
	 *            the arrival time of this cloudlet >= 0
	 * @param utilizationModelCpu
	 *            the utilization model of the CPU
	 * @param utilizationModelRam
	 *            the utilization model of the RAM
	 * @param utilizationModelBw
	 *            the utilization model of the bandwidth
	 */
	public OnlineCloudlet(final long cloudletLength,
			final int pesNumber, final long cloudletFileSize,
			final long cloudletOutputSize,
			final UtilizationModel utilizationModelCpu,
			final UtilizationModel utilizationModelRam,
			final UtilizationModel utilizationModelBw,
			final long deadline, final long arrivalTime) {
		super(++lastId, cloudletLength, Math.max(1, pesNumber),
				cloudletFileSize, cloudletOutputSize, utilizationModelCpu,
				utilizationModelRam, utilizationModelBw);

		setDeadline(deadline);
		setArrivalTime(arrivalTime);
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

	public long getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(long arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public OnlineVm getVm() {
		return vm;
	}

	public void setVm(OnlineVm vm) {
		this.vm = vm;
	}
}

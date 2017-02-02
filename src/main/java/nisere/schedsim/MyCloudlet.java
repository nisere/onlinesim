package nisere.schedsim;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

/**
 * MyCloudlet adds to the Cloudsim Cloudlet a deadline to simulate deadline constrained tasks
 * and a delay to simulate online arrival.
 * 
 * @author Alina Chera
 *
 */
public class MyCloudlet extends Cloudlet {
	/**
	 * The cloudlet deadline. The amount of time in hours before the cloudlet
	 * must be completed. A value of 0 means there is no deadline.
	 */
	private int deadline;

	/**
	 * The delay from the start of the simulation when this cloudlet was
	 * processed. The delay of the cloudlet is in seconds.
	 */
	private long delay;
	
	/**
	 * The arrival time of the cloudlet, in seconds.
	 */
	private long arrivalTime;

	/**
	 * Allocates a MyCloudlet object.
	 * 
	 * @param cloudletId
	 *            the ID of this cloudlet
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
	public MyCloudlet(final int cloudletId, final long cloudletLength,
			final int pesNumber, final long cloudletFileSize,
			final long cloudletOutputSize,
			final UtilizationModel utilizationModelCpu,
			final UtilizationModel utilizationModelRam,
			final UtilizationModel utilizationModelBw,
			final int deadline, final long arrivalTime) {
		super(cloudletId, cloudletLength, Math.max(1, pesNumber),
				cloudletFileSize, cloudletOutputSize, utilizationModelCpu,
				utilizationModelRam, utilizationModelBw);

		this.deadline = Math.max(0, deadline);
		this.arrivalTime = Math.max(0, arrivalTime);
	}

	/**
	 * Gets the deadline of this cloudlet.
	 * 
	 * @return the deadline of this cloudlet (in hours)
	 */
	public int getDeadline() {
		return deadline;
	}
	
	/**
	 * Gets the delay of this cloudlet.
	 * 
	 * @return the delay of this cloudlet (in seconds)
	 */
	public long getDelay() {
		return delay;
	}
	
	/**
	 * Gets the arrival time of this cloudlet.
	 * 
	 * @return the arrival time of this cloudlet (in seconds)
	 */
	public long getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * Sets the deadline of this cloudlet.
	 * 
	 * @param deadline
	 *            the deadline of this cloudlet (in hours)
	 * @return <code>true</code> if it is successful, <code>false</code>
	 *         otherwise
	 */
	public boolean setDeadline(final int deadline) {
		boolean success = false;
		if (deadline >= 0) {
			this.deadline = deadline;
			success = true;
		}
		return success;
	}

	/**
	 * Sets the delay of this cloudlet.
	 * 
	 * @param delay
	 *            the delay of this cloudlet (in seconds)
	 * @return <code>true</code> if it is successful, <code>false</code>
	 *         otherwise
	 */
	public boolean setDelay(final long delay) {
		boolean success = false;
		if (delay >= 0) {
			this.delay = delay;
			success = true;
		}
		return success;
	}

	/**
	 * Sets the arrival time of this cloudlet.
	 * 
	 * @param arrivalTime
	 *            the arrival time of this cloudlet (in seconds)
	 * @return <code>true</code> if it is successful, <code>false</code>
	 *         otherwise
	 */
	public boolean setArrivalTime(final long arrivalTime) {
		boolean success = false;
		if (delay >= 0) {
			this.arrivalTime = arrivalTime;
			success = true;
		}
		return success;
	}
}

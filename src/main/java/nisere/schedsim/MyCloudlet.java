package nisere.schedsim;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

public class MyCloudlet extends Cloudlet {
	/**
	 * The cloudlet deadline. The amount of time in hours before the cloudlet
	 * must be completed. A value of 0 means there is no deadline.
	 */
	private int deadline;

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
	 * @param utilizationModelCpu
	 *            the utilization model of the CPU
	 * @param utilizationModelRam
	 *            the utilization model of the RAM
	 * @param utilizationModelBw
	 *            the utilization model of the bandwidth
	 */
	public MyCloudlet(final int cloudletId, final long cloudletLength,
			final int pesNumber, final long cloudletFileSize,
			final long cloudletOutputSize, final int deadline,
			final UtilizationModel utilizationModelCpu,
			final UtilizationModel utilizationModelRam,
			final UtilizationModel utilizationModelBw) {
		super(cloudletId, cloudletLength, Math.max(1, pesNumber),
				cloudletFileSize, cloudletOutputSize, utilizationModelCpu,
				utilizationModelRam, utilizationModelBw);

		this.deadline = Math.max(0, deadline);
	}

	/**
	 * Sets the deadline of this cloudlet.
	 * 
	 * @param hours
	 *            the deadline of this cloudlet (in hours)
	 * @return <code>true</code> if it is successful, <code>false</code>
	 *         otherwise
	 */
	public boolean setDeadline(final int hours) {
		boolean success = false;
		if (hours >= 0) {
			deadline = hours;
			success = true;
		}
		return success;
	}

	/**
	 * Gets the deadline of this cloudlet.
	 * 
	 * @return the deadline of this cloudlet (in hours)
	 */
	public int getDeadline() {
		return deadline;
	}
}

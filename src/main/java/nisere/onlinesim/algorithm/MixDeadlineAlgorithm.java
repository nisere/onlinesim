package nisere.onlinesim.algorithm;

import nisere.onlinesim.OnlineCloudlet;

/**
 * Computes schedule taking deadline into account.
 * First it sorts the cloudlets. 
 * For any two cloudlets, first sorts them by deadline ascending.
 * If the difference in deadline between the two is small, 
 * but the one with the greater deadline has a much larger cloudlet length,
 * then the one with the larger size has priority.
 * Then it chooses the VM that if used it will finish the execution the fastest
 * (it yields the minimum workload)
 * If no VM is found that would finish the cloudlet within deadline the cloudlet
 * will remain unscheduled.
 * 
 * @author Nisere
 *
 */
public class MixDeadlineAlgorithm extends DeadlineAlgorithm {

	/** A number to be used to find out how many times the deadline of a cloudlet
	 *  is greater than the deadline of another cloudlet 
	 */
	private double deadlineDelta;
	/** A number to be used to find out how many times the length of a cloudlet
	 *  is greater than the length of another cloudlet 
	 */
	private double lengthDelta;
	
	/**
	 * Compares two cloudlets
	 * @param c1 first cloudlet
	 * @param c2 second cloudlet
	 * @return -1 if c1 < c2, 1 if c1 > c2, 0 if c1 = c2
	 */
	@Override
	protected int compare(OnlineCloudlet c1, OnlineCloudlet c2) {
		int ret = 0;
		//double deadlineDelta = 1.2;
		//double lengthDelta = 1.6;
		double realDeadline1 = c1.getArrivalTime() + c1.getDeadline();
		double realDeadline2 = c2.getArrivalTime() + c2.getDeadline();

		if (realDeadline1 < realDeadline2) {
			ret = -1;
			if (realDeadline2 / realDeadline1 < getDeadlineDelta()
					&& 1.0 * c2.getCloudletLength() / c1.getCloudletLength() > getLengthDelta()) {
				ret = 1;
			}
		} else if (realDeadline1 > realDeadline2) {
			ret = 1;
			if (realDeadline1 / realDeadline2 < getDeadlineDelta() 
					&& 1.0 * c1.getCloudletLength() / c2.getCloudletLength() > getLengthDelta()) {
				ret = -1;
			}
		} else {
			if (c2.getCloudletLength() > c1.getCloudletLength()) {
				ret = 1;
			}
		}
		
		return ret;
	}

	public double getDeadlineDelta() {
		return deadlineDelta;
	}

	public void setDeadlineDelta(double deadlineDelta) {
		this.deadlineDelta = deadlineDelta;
	}

	public double getLengthDelta() {
		return lengthDelta;
	}

	public void setLengthDelta(double lengthDelta) {
		this.lengthDelta = lengthDelta;
	}

}

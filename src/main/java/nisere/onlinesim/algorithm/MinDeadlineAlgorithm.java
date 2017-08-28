package nisere.onlinesim.algorithm;

import nisere.onlinesim.OnlineCloudlet;

/**
 * Computes schedule taking deadline into account.
 * First it sorts the cloudlets by cloudlet length ascending.
 * Then it chooses the VM that if used it will finish the execution the fastest
 * (it yields the minimum workload)
 * If no VM is found that would finish the cloudlet within deadline the cloudlet
 * will remain unscheduled.
 * 
 * @author Nisere
 *
 */
public class MinDeadlineAlgorithm extends DeadlineAlgorithm {

	/**
	 * Compares two cloudlets
	 * @param c1 first cloudlet
	 * @param c2 second cloudlet
	 * @return -1 if c1 < c2, 1 if c1 > c2, 0 if c1 = c2
	 */
	@Override
	protected int compare(OnlineCloudlet c1, OnlineCloudlet c2) {
		return (c1.getCloudletLength() < c2.getCloudletLength() ? -1 : (c1.getCloudletLength() > c2.getCloudletLength() ? 1 : 0));
	}
}

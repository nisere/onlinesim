package nisere.schedsim;

import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;

public class OnlineQueue implements Runnable {
	protected final Object lock = new Object();
	
	private LinkedList<Cloudlet> cloudlets;
	
	protected LinkedList<Cloudlet> getCloudlets() {
		if (cloudlets == null) {
			cloudlets = new LinkedList<>();
		}
		return cloudlets;
	}

	public void addCloudlet(Cloudlet cloudlet) {
		synchronized (lock) {
			getCloudlets().add(cloudlet);
		}
	}
	
	public int getCloudletsNo() {
		synchronized (lock) {
			return getCloudlets().size();
		}
	}
	
	public List<Cloudlet> getNCloudlets(int n) {
		List<Cloudlet> cloudlets = new LinkedList<>();
		
		for (int i = 0; i < n; i++) {
			synchronized (lock) {
				if (this.cloudlets.peek() != null) {
					cloudlets.add(getCloudlets().poll());
				} else {
					break;
				}
			}
		}
		
		return cloudlets;
	}

	public void run() {}
}

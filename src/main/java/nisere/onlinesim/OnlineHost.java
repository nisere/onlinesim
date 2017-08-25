package nisere.onlinesim;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

public class OnlineHost extends Host {
	
	/** Last id assigned to an object of this class */
	public static int lastId = 0;
	
	/**
	 * Instantiates a new host.
	 * 
	 * @param id the host id
	 * @param ramProvisioner the ram provisioner
	 * @param bwProvisioner the bw provisioner
	 * @param storage the storage capacity
	 * @param peList the host's PEs list
	 * @param vmScheduler the vm scheduler
	 */
	public OnlineHost(
			RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner,
			long storage,
			List<? extends Pe> peList,
			VmScheduler vmScheduler) {
		super(++lastId,ramProvisioner,bwProvisioner,storage,peList,vmScheduler);

	}
}

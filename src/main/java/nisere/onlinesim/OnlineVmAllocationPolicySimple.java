package nisere.onlinesim;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;

public class OnlineVmAllocationPolicySimple extends VmAllocationPolicySimple {

	public OnlineVmAllocationPolicySimple(List<? extends Host> hostList) {
		super(hostList);
	}
	
	public void addHost(Host host) {
		getHostList().add(host);
		getFreePes().add(host.getNumberOfPes());
	}

}

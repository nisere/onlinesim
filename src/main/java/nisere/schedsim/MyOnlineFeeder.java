package nisere.schedsim;

import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.distributions.UniformDistr;

public class MyOnlineFeeder implements Runnable {

	private MyOnlineQueue queue;

	public MyOnlineFeeder(MyOnlineQueue myOnlineQueue) {
		this.queue = myOnlineQueue;
	}
	
	public void run() {
		try {
			while(true) {
				Thread.sleep(1500);
			
				System.out.println("Feeder: adding a cloudlet");
				this.queue.addCloudlet(new Cloudlet(0, 0, 0, 0, 0, null, null, null));
			}
			
		} catch (InterruptedException e) {
			System.out.println("Feeder interrupted");
		}
	}
	
//	public Cloudlet createCloudlet() {
//		int noCloudlets = 512;
//		
//		// generate length [minLengthUnif;maxLengthUnif)
//		int minLengthUnif = 100000;
//		int maxLengthUnif = 400000;
//		int seed = 9;
//		
//		// Fifth step: Create Cloudlets
//		//cloudletList = new ArrayList<Cloudlet>();
//
//		// Cloudlet properties
//		int id = 0;
//		int pesNumber = 1;
//		long length = 250000;
//		long fileSize = 0;
//		long outputSize = 0;
//		UtilizationModel utilizationModel = new UtilizationModelFull();
//
//		UniformDistr lengthUnif = new UniformDistr(minLengthUnif,
//				maxLengthUnif, seed);
//
//		// add noCloudlets cloudlets
//		//for (int i = 0; i < noCloudlets; i++) {
//			int randomLength = (int) lengthUnif.sample();
//			Cloudlet cloudlet = new Cloudlet(id++, randomLength, pesNumber,
//					fileSize, outputSize, utilizationModel,
//					utilizationModel, utilizationModel);
//			//cloudlet.setUserId(brokerId);
//			//cloudletList.add(cloudlet);
//		//}
//			
//		return cloudlet;
//	}

}

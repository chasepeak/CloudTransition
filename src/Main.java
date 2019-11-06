import java.util.LinkedList;
import java.util.Random;

/*  
 *  assumption:
 *   - each deployment cycle lasts for one month
 */

public class Main {
	
	//Parameters:
	
	// Total data mass (in Terabytes) being transfer to the cloud
	private static int totalData = 1000;
	
	// The percentage of the total data to be deployed in the last cycle
	private static double limit = 0.25;
	
	
	private static Random x = new Random();

	public static void main(String[] args) {
		System.out.println(deployData(totalData));
	}
	
	/**
	 * This method simulates the deployment of a mass of data to the cloud
	 * @param data - the data mass to be deployed for a full cloud transition
	 * @return the total cost of deploying the data mass to the cloud
	 */
	public static long deployData(int data) {
		//initialize the cycle count
		int cycles = 0;
		
		//initialize the deployed data, remaining data for each cycle
		long deployedData = 0, remainingData = data;
		
		//initialize the totalCost of the full deployment
		long totalCost = 0;
		
		//initialize the list of incremental data deployments for each cycle
		LinkedList<Long> dataPerCycle = new LinkedList<Long>();
		
		//executes a deployment cycle when the remaining data is greater than the described limit
		while (remainingData > (double)(data * limit)) {
			cycles++;
			
			//determines the amount of data to be deployed based on a random variable
			deployedData = Math.round(remainingData * x.nextDouble() + 0.005);
			dataPerCycle.add(deployedData);
			remainingData = remainingData - deployedData;
			
			totalCost += calculateCycleCost(dataPerCycle, data - remainingData);
		}
		
		//this final iteration accounts for the remaining data being deployed
		cycles++;
		dataPerCycle.add(remainingData);
		totalCost += calculateCycleCost(dataPerCycle, data);

		System.out.println("total cycles: " + cycles);
		
		return totalCost;
	}
	
	/**
	 * This method calculates the incurred cost for a given cycle, including the costs from previous cycles
	 * @param dataPerCycle - the list of data deployments per cycle
	 * @param totalDeployed - the total data that has already been deployed
	 * @return the cost incurred at the end of a deployment cycle
	 */
	private static long calculateCycleCost(LinkedList<Long> dataPerCycle, long totalDeployed) {
		long cost = 0;
		for (int i = 0; i < dataPerCycle.size(); i++) {
			cost += (getPricing(totalDeployed) * dataPerCycle.get(i));
		}
		return cost;
	}
	/**
	 * This method establishes the pricing of the data per TB based on the AWS S3 pricing standards
	 * @param totalDeployed - the total data that has already been deployed
	 * @return the storage pricing per TB
	 */
	private static int getPricing(long totalDeployed) {
		int pricing;
		if (totalDeployed <= 50) {
			pricing = 23;
		}
		else {
			pricing = (totalDeployed <= 450) ? 22 : 21;
		}
		return pricing;
	}
	
	/*private static double getRandomVar() {
		return Math.round((0.5 - (x.nextDouble() * 0.5)) * 100) / 100.0;
	}*/
	
	
}

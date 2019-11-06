import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
	private static double limit;
	
	private static File statistics_limit = new File("statistics_limit.txt");
	private static File statistics_cycles = new File("statistics_cycles.txt");
	
	
	private static Random x = new Random();

	public static void main(String[] args) {
		if (!(Files.exists(Paths.get(statistics_limit.getPath())) || Files.exists(Paths.get(statistics_limit.getPath())))) {
			try {
				statistics_limit.createNewFile();
				statistics_cycles.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0; i < 1_000; i++)
			deployData(totalData);
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
		
		//initialize the limit to a random variable
		limit = getRandomVar();
		
		//executes a deployment cycle when the remaining data is greater than the described limit
		while (remainingData > (double)(data * limit)) {
			cycles++;
			
			//System.out.println("\ncycle #" + cycles);
			
			//determines the amount of data to be deployed based on a random variable
			deployedData = Math.round(remainingData * x.nextDouble() + 0.005);
			dataPerCycle.add(deployedData);
			remainingData = remainingData - deployedData;
			//System.out.println("data deployed: " + deployedData);
			//System.out.println("remaining data: " + remainingData);
			
			totalCost += calculateCycleCost(dataPerCycle, data - remainingData);
			
			//System.out.println("total cost of deployments so far: " + totalCost);
		}
		
		//this final iteration accounts for the remaining data being deployed
		cycles++;
		dataPerCycle.add(remainingData);
		totalCost += calculateCycleCost(dataPerCycle, data);
		//System.out.println();
		//System.out.println("total cycles: " + cycles);
		//System.out.println("total cost: " + totalCost);
		try {
			Files.write(Paths.get(statistics_limit.getPath()), (String.format("%.2f,%d\n", limit, totalCost)).getBytes(), StandardOpenOption.APPEND);
			Files.write(Paths.get(statistics_cycles.getPath()), (String.format("%d,%d\n", cycles, totalCost)).getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		//System.out.println("cost for this deployment cycle: " + cost);
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
	
	private static double getRandomVar() {
	return Math.round(x.nextDouble() * 100) / 100.0;
	}
	
	
}

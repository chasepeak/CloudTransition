import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	
	//private static double firstPercentage = 0;
	
	// Total data mass (in Terabytes) being transfer to the cloud
	private static int totalData = 1000;
	
	//Average monthly wages to pay a contractor per 100TB pushed
	private static int labor = 7000 / 100;
	
	// The percentage of the total data to be deployed in the last cycle
	private static double limit;
	
	private static LinkedList<Long> dataCycles = new LinkedList<Long>();
	
	private static File even_cycles_stats = new File("even_cycles.txt");
	
	
	private static Random x = new Random();

	public static void main(String[] args) throws IOException {
		
		if (!Files.exists(Paths.get(even_cycles_stats.getPath()))) {
			try {
				even_cycles_stats.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		deployEvenCycles();
		//for (int i = 0; i < 3_000; i++)
		//	deployData();
	}
	
	public static long deployEvenCycles() throws IOException {
		
		//assumption: a cloud deployment transition period will be at most 2 years (24 months)
		int j, cycleRange = 24;
		long dataPerCycle;
		double i;
		
		//this will simulate the possible even splits of data deployments between 1 and 24 cycles
		for (i = 1; i <= cycleRange; i++) {
			long totalCost = 0, deployedData = 0;
			dataPerCycle = Math.round(totalData / i);
			
			//this performs the individual cycles
			for (j = 0; j < i; j++) {
				deployedData += dataPerCycle;
				dataCycles.add(dataPerCycle);
				totalCost += calculateLaborCost(dataPerCycle);
				totalCost += calculateStorageCost(deployedData);
			}
			Files.write(Paths.get(even_cycles_stats.getPath()), (String.format("%d,%d\n", (int)i, totalCost)).getBytes(), StandardOpenOption.APPEND);
			totalCost = 0;
		}
		
		return 0;
	}
	
	/**
	 * This method simulates the deployment of a mass of data to the cloud
	 * @param data - the data mass to be deployed for a full cloud transition
	 * @return the total cost of deploying the data mass to the cloud
	 */
	public static long deployData() {
		//initialize the cycle count
		int cycles = 0;
		
		//initialize the deployed data, remaining data for each cycle
		long deployedData = 0, remainingData = totalData;
		
		//initialize the totalCost of the full deployment
		long totalCost = 0;

		cycles = x.nextInt(23) + 1;
		long remainingDataSplits = Math.round(totalData / (double)cycles);
		for (int i = 0; i < cycles; i++) {
			totalCost += calculateLaborCost(remainingDataSplits);
			dataCycles.add(remainingDataSplits);
			remainingData -= remainingDataSplits;
			totalCost += calculateStorageCost(totalData - remainingData);
		}

		//firstPercentage = 0;
		return totalCost;
	}
	
	/**
	 * This method calculates the incurred cost for a given cycle, including the costs from previous cycles
	 * @param dataPerCycle - the list of data deployments per cycle
	 * @param totalDeployed - the total data that has already been deployed
	 * @return the cost incurred at the end of a deployment cycle
	 */
	private static long calculateStorageCost(long totalDeployed) {
		long cost = 0, price = getPricing(totalDeployed);
		for (int i = 0; i < dataCycles.size(); i++) {
			cost += (price * dataCycles.get(i));
		}
		return cost;
	}
	
	private static long calculateLaborCost(long deployedData) {
		long maxedEmployees = deployedData / 100;
		if (deployedData >= 100) {
			return Math.round(100 * maxedEmployees * labor + labor * (deployedData % 100));
		}
		else if (deployedData > 50) {
			return deployedData * labor;
		}
		return Math.round(labor * 0.5);
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
	
	/*private static double getRandomVar() {
	return Math.round(x.nextDouble() * 100) / 100.0;
	}*/
	
	
}

import java.util.LinkedList;
import java.util.Random;

/*  
 *  percentage of data pushed per cycle depends on the total amount of data
 *  
 *  assumption:
 *   - each deployment cycle lasts for one month
 */

public class Main {
	
	private static Random x = new Random();
	private static int totalData = 1000;
	private static double limit = 0.25;
	private static long totalCost;
	private static LinkedList<Long> dataPerCycle;

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			deployData(totalData);
			System.out.println();
		}
	}
	
	public static void deployData(int data) {
		int cycles = 0;
		long deployedData = 0, remainingData = data;
		totalCost = 0;
		dataPerCycle = new LinkedList<Long>();
		System.out.println(String.format("the limit is %d%%", (int)(limit * 100)));
		while (remainingData > (double)(data * limit)) {
			cycles++;
			System.out.println("cycle #" + cycles + ":");
			deployedData = Math.round(remainingData * x.nextDouble() + 0.005);
			dataPerCycle.add(deployedData);
			
			System.out.println("deployedData: " + deployedData);
			remainingData = remainingData - deployedData;
			System.out.println("remaining data: " + remainingData);
			
			totalCost += calculateCycleCost(data - remainingData);
			
			System.out.println("current cost: " + totalCost);
		}
		totalCost += calculateCycleCost(data);
		System.out.println("total cycles: " + ++cycles);
		System.out.println("total cost: " + totalCost);
	}
	
	private static long calculateCycleCost(long totalDeployed) {
		long cost = 0;
		for (int i = 0; i < dataPerCycle.size(); i++) {
			cost += (getPricing(totalDeployed) * dataPerCycle.get(i));
		}
		return cost;
	}
	
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

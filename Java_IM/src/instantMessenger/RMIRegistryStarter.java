package instantMessenger;


import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class RMIRegistryStarter {
	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(1099);
			System.out.println("RMI Registry started.");
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

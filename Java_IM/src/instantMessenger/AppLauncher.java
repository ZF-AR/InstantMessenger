package instantMessenger;

public class AppLauncher {
	public static void main(String[] args) {
		Class[] classes = { 
				RMIRegistryStarter.class,
				SessionServer.class,
				RelayServer.class,
				ClientLauncher.class
		};
		MainClassListLauncher.launch(classes);
	}
}

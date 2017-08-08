import client.ClientConnector;

public class Main {
	
	public static void main(String... args){
		System.out.println("Running...");
		
		try {
			new ClientConnector("localhost",26002).run();
		} catch (Exception e) {
			
			System.out.println("Failed to connect to host");
			e.printStackTrace();
		}

	}

}

import server.ServerListener;

public class Main {
	
	public static void main(String... args){
		System.out.println("Running server...");
		
		try {
			new ServerListener(26002).runAsTest();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

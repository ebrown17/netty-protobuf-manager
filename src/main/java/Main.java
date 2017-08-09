import server.ServerListener;

public class Main {
	
	public static void main(String... args){
		System.out.println("Running server...");
		
		try {
			new ServerListener(26002).run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

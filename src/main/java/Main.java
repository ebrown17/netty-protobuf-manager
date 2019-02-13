import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import protocol.json.JsonClient;
import protocol.json.JsonClientFactory;
import protocol.json.JsonServer;

public class Main {

    public static void main(String... args){
        // How to use the server and client classes

        JsonServer server = new JsonServer();
        server.addChannel(6666);
        server.startServer();

        JsonClientFactory factory = new JsonClientFactory();
        JsonClient client = factory.createClient("localhost",6666,JsonClient.class);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode parameters = mapper.createObjectNode();
        ObjectNode main = mapper.createObjectNode();
        main.put("eventType","trainEvent");
        main.put("subtype","DOOR_OPEN");

        parameters.put("serial",300);
        parameters.put("length",10);
        parameters.put("platform","A20-1");
        parameters.put("dss_code", 55 );
        main.set("parameters",parameters);


        try {
            client.connect();
            while(true) {
                client.sendMessage(main);
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

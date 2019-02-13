package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.Transceiver;
import common.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class JsonServer  extends Server<JsonNode> {

    private final static Logger logger = LoggerFactory.getLogger(JsonServer.class);

    public JsonServer() {
        super();
    }

    @Override
    public boolean createChannel(int port) {
        Transceiver<JsonNode> transceiver = new Transceiver<JsonNode>(port);
        JsonServerChannel channel = new JsonServerChannel(transceiver);

        return  addChannel(port,transceiver,channel);

    }

    @Override
    public void readMessage(InetSocketAddress addr, JsonNode message) {


    }

    public static void main(String... args){

        JsonServer server = new JsonServer();
        server.createChannel(6666);
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.ReadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.json.JsonClient;
import protocol.json.JsonClientFactory;
import protocol.json.JsonServer;
import protocol.protomessage.MessageClient;
import protocol.protomessage.MessageClientFactory;
import protocol.protomessage.MessageServer;
import protocol.protomessage.ProtoMessages.ProtoMessage;


public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String... args){
        protoTest();
    }
    public static void protoTest(){
        MessageServer server = new MessageServer();
        server.addChannel(6666);
        server.startServer();
        server.registerReadListener((addr, message) -> logger.info("xxxx {} ", message.toString()));

        MessageClientFactory factory = new MessageClientFactory();
        MessageClient client = factory.createClient("localhost",6666,MessageClient.class);

        ProtoMessage.Status status = ProtoMessage.Status.newBuilder().setHealth("GOOD").setErrors(5).setUptime(100).build();

        ProtoMessage main= ProtoMessage.newBuilder().setMessageType(ProtoMessage.MessageType.STATUS).setStatus(status).build();

        try {
            client.connect();
            while(true) {
                client.sendMessage(main);
                Thread.sleep(5000);
                server.broadcastOnAllChannels(main);
                Thread.sleep(5000);


            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void jsonTest(){
        // How to use the server and client classes

        JsonServer server = new JsonServer();
        server.addChannel(6666);
        server.startServer();
        server.registerReadListener((addr, message) -> logger.info("xxxx {} ", message.toString()));

        JsonClientFactory factory = new JsonClientFactory();
        JsonClient client = factory.createClient("localhost",6666,JsonClient.class);
        client.registerReadListener((addr, message) -> logger.info("client 1 xxx {} ", message.toString()));

        JsonClient client2 = factory.createClient("localhost",6666,JsonClient.class);

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
            client2.connect();
            while(true) {
                client.sendMessage(main);
                client2.sendMessage(main);
                Thread.sleep(5000);
                server.broadcastOnAllChannels(main);
                Thread.sleep(5000);


            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

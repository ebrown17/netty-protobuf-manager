package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import common.ReadListener;
import common.Transceiver;
import common.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class JsonServer  extends Server<JsonNode> {

    private final static Logger logger = LoggerFactory.getLogger(JsonServer.class);
    private ArrayList<ReadListener<JsonNode>> readListeners = new ArrayList<>();
    public JsonServer() {
        super();
    }

    @Override
    public boolean addChannel(int port) {
        Transceiver<JsonNode> transceiver = new Transceiver<JsonNode>(port);
        JsonServerChannel channel = new JsonServerChannel(transceiver);
        return  addChannel(port,transceiver,channel);
    }

    @Override
    public void readMessage(InetSocketAddress addr, JsonNode message) {
        logger.debug("readMessage got message: {}", message.toString());
        for(ReadListener<JsonNode> listener: readListeners){
            listener.read(addr,message);
        }


    }

    public void registerReadListener(ReadListener reader){
        readListeners.add(reader);
    }

}

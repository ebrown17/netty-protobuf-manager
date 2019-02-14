package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import common.Transceiver;
import common.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonServer  extends Server<JsonNode> {

    private final static Logger logger = LoggerFactory.getLogger(JsonServer.class);
    public JsonServer() {
        super();
    }

    @Override
    public boolean addChannel(int port) {
        Transceiver<JsonNode> transceiver = new Transceiver<JsonNode>(port);
        JsonServerChannel channel = new JsonServerChannel(transceiver);
        return  addChannel(port,transceiver,channel);
    }


}

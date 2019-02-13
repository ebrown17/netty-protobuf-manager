package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import common.Client.Client;
import common.Client.ClientFactory;
import common.Transceiver;

import java.net.InetSocketAddress;

public class JsonClientFactory extends ClientFactory<JsonNode> {

    @Override
    public JsonClient createClient(InetSocketAddress address, Transceiver<JsonNode> transceiver) {
        return new JsonClient(address,workerGroup, transceiver,new JsonClientChannel(transceiver));
    }

}

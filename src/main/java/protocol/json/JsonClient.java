package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import common.Client.Client;
import common.Transceiver;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class JsonClient extends Client<JsonNode> {

    private static final Logger logger = LoggerFactory.getLogger(JsonClient.class);
    private final Transceiver<JsonNode> transceiver;
    private final InetSocketAddress serverAddress;


    public JsonClient(InetSocketAddress serverAddress, EventLoopGroup sharedWorkerGroup, Transceiver<JsonNode> transceiver, JsonClientChannel clientChannel) {
        super(serverAddress, sharedWorkerGroup, transceiver, clientChannel);
        this.transceiver = transceiver;
        this.serverAddress = serverAddress;

    }

    @Override
    public void readMessage(InetSocketAddress addr, JsonNode message) {
        logger.info("readMessage {} sent: {} ", addr.getHostName(), message.toString());
    }

    @Override
    public void sendMessage(JsonNode message) {
        if (isActive()) {
            logger.info("sendMessage {}", message.toString());
            transceiver.sendMessage(serverAddress, message);
        }
    }
}

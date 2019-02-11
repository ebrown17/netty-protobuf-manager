package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import common.Transceiver;
import common.TransceiverChannel;
import common.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

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

        JsonClient client = new JsonClient();

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}

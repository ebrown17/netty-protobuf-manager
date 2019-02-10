package protocol.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.protomessage.MessageTransceiver;
import protocol.protomessage.server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

public class JsonServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private static final int INITIAL_CHANNEL_LIMIT = 5;


    private final static Logger logger = LoggerFactory.getLogger(JsonServer.class);

    public JsonServer(){
        ThreadFactory threadFactory = new DefaultThreadFactory("server");
        // the bossGroup will handle all incoming connections and pass them off to the workerGroup
        // the workerGroup will be used for processing all channels
        // 0 forces netty to use default number of threads which is max number of processors * 2
        bossGroup = new NioEventLoopGroup(1, threadFactory);
        workerGroup = new NioEventLoopGroup(0, threadFactory);
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 25);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.childHandler(new ServerJsonChannel());

        int port = 6666;
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(port));

        try {
            channelFuture.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Interrupted waiting for bind");
        }
        if (!channelFuture.isSuccess()) {
            logger.error("startChannel failed to bind to port {} ", port);
        }
        else {
            logger.debug("startChannel now listening for connections on port {}", port);
            Channel channel = channelFuture.channel();

            ChannelFutureListener closeListener = future -> {
                logger.info("channel {} closed unexpectedly, closed with {}", port, future.cause());
                channel.close();
            };


            channel.closeFuture().addListener(closeListener);

        }
    }


    public static void main(String... args){

        JsonServer server = new JsonServer();

        JsonClient client = new JsonClient();

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

package protocol.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;

public class JsonClient {
    private InetSocketAddress serverAddress;
    private Bootstrap bootstrap;
    private Channel channel;
    JsonClient(){
        bootstrap = new Bootstrap();
        bootstrap.group( new NioEventLoopGroup(0, new DefaultThreadFactory("client", true)));
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ServerJsonChannel());
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("localhost",6666));
        try {
            channelFuture.await();
            if (channelFuture.isSuccess()) {
                JsonHandler tester=   channelFuture.channel().pipeline().get(JsonHandler.class);
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode main = mapper.createObjectNode();

                ObjectNode parameters = mapper.createObjectNode();
                main.put("eventType","trainEvent");
                main.put("subtype","DOOR_OPEN");

                parameters.put("serial",300);
                parameters.put("length",10);
                parameters.put("platform","A20-1");
                parameters.put("dss_code", 55 );
                main.set("parameters",parameters);



                while(true){
                    tester.sendMessage(main);
                    Thread.sleep(500);
                }

            }
            else {
                channelFuture.channel().close();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



}

package protocol.json;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.atomic.AtomicLong;

public class ServerJsonChannel extends ChannelInitializer<SocketChannel> {

    private static final int WRITE_IDLE_TIME = 5;
    private static final AtomicLong channelIds = new AtomicLong(0L);

    ServerJsonChannel(){
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast("jsonCodec", new JsonJacksonCodec());
        p.addLast("jsonHandler", new JsonHandler());

    }

}

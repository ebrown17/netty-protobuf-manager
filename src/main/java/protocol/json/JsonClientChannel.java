package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import common.Transceiver;
import common.TransceiverChannel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import protocol.protobuf.ExceptionHandler;

public class JsonClientChannel extends TransceiverChannel<JsonNode> {

    Transceiver<JsonNode> transceiver;

    public JsonClientChannel(Transceiver<JsonNode> transceiver){
        super(transceiver);
        this.transceiver = transceiver;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        System.out.println("cleint init");
        ChannelPipeline p = ch.pipeline();
        p.addLast("jsonCodec", new JsonJacksonCodec());
        p.addLast("jsonHandler", new JsonHandler(channelIds.incrementAndGet(),transceiver));
        p.addLast("heartbeatHandler",new ClientHeartbeatHandler());
        p.addLast(new ExceptionHandler());
    }
}

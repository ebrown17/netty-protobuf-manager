package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import common.Client.TransceiverClientChannel;
import common.ExceptionHandler;
import common.Transceiver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

public class JsonClientChannel extends TransceiverClientChannel<JsonNode> {

    private Transceiver<JsonNode> transceiver;

    public JsonClientChannel(Transceiver<JsonNode> transceiver) {
        super(transceiver);
        this.transceiver = transceiver;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        p.addLast("jsonCodec", new JsonJacksonCodec());
        p.addLast("idleStateHandler", new IdleStateHandler(READ_IDLE_TIME, 0, 0));
        p.addLast("jsonHandler", new JsonHandler(channelIds.incrementAndGet(), transceiver));
        p.addLast("heartbeatHandler", new JsonHeartbeatReceiver(READ_IDLE_TIME, HEARTBEAT_MISS_LIMIT));
        p.addLast(new ExceptionHandler());
    }
}

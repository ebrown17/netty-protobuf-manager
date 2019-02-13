package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import common.HeartbeatReceiverHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonHeartbeatReceiver extends HeartbeatReceiverHandler {
    private final Logger logger = LoggerFactory.getLogger(JsonHeartbeatReceiver.class);
    /**
     * HeartbeatRecieverHandler expects to be receiving heartbeat message.
     * <p>
     * If the heartbeat miss limit is reached the channel is closed and the client's reconnect logic is
     * started.
     * <p>
     * By default every channel read will reset the miss count. To only reset on a heartbeat, you must override the channelRead
     * method and add appropriate logic. The method
     * {@link HeartbeatReceiverHandler#resetMissCounter() resetMissCounter } can be called reset the miss count.
     *
     * @param expectedInterval The expected heartbeat interval in seconds. This will be used to determine if server
     *                         is no longer alive.
     * @param missedLimit      The max amount of heartbeats allowed until handler closes channel.
     */
    public JsonHeartbeatReceiver(int expectedInterval, int missedLimit) {
        super(expectedInterval, missedLimit);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.trace("channelRead received {} from {}", msg.toString(), ctx.channel().remoteAddress());
        JsonNode jmsg = (JsonNode) msg;
        String types = jmsg.get("eventType").asText();
        if(types.equals("heartbeat")) {
            resetMissCounter();
        }
        else{
            ctx.fireChannelRead(msg);
        }
    }

}

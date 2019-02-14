package protocol.protomessage;

import common.HeartbeatReceiverHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.protomessage.ProtoMessages.ProtoMessage;

public class MessageHeartbeatReceiver extends HeartbeatReceiverHandler<ProtoMessage> {

    private final Logger logger = LoggerFactory.getLogger(MessageHeartbeatReceiver.class);
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
    public MessageHeartbeatReceiver(int expectedInterval, int missedLimit) {
        super(expectedInterval, missedLimit);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtoMessage message = ((ProtoMessage) msg);
        logger.trace("channelRead received {} from {}", message.getMessageType(), ctx.channel().remoteAddress());
        if (ProtoMessage.MessageType.HEARTBEAT == message.getMessageType()) {
            resetMissCounter();
        }
        else {
            ctx.fireChannelRead(msg);
        }
    }

}

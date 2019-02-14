package protocol.protomessage;
import common.Handler;
import common.Transceiver;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.protomessage.ProtoMessages.ProtoMessage;

public class MessageHandler extends Handler<ProtoMessage> {

    private ChannelHandlerContext ctx;
    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private final Transceiver<ProtoMessage> transceiver;
    private final Long handlerId;

    public MessageHandler(Long id, Transceiver<ProtoMessage> transceiver) {
        super(id, transceiver);
        this.handlerId = id;
        this.transceiver = transceiver;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMessage msg) throws Exception {
        logger.trace("channelRead0 {} sent: {}", ctx.channel().remoteAddress(), msg);
        ProtoMessage.MessageType type = msg.getMessageType();
        if (ProtoMessage.MessageType.HEARTBEAT == type) {
            ctx.fireChannelRead(msg);
        }
        else if(ProtoMessage.MessageType.UNKNOWN == type) {
            ctx.fireChannelRead(msg);
        }
        else {
            transceiver.handleMessage(remoteAddress, msg);
        }
    }
}

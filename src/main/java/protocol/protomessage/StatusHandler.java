package protocol.protomessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtoMessages.ProtoMessage;
import protobuf.ProtoMessages.ProtoMessage.MessageType;

public class StatusHandler extends SimpleChannelInboundHandler<ProtoMessage> {

  private final Logger logger = LoggerFactory.getLogger(StatusHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ProtoMessage msg) throws Exception {
    logger.trace("channelRead0 {} sent: {}", ctx.channel().remoteAddress(), msg);
    MessageType type = msg.getMessageType();
    if (MessageType.STATUS == type) {
      logger.debug("channelRead0 {} STATUS: {}",ctx.channel().remoteAddress(),msg);
    }
    else {
      ctx.fireChannelRead(msg);
    }
  }
}

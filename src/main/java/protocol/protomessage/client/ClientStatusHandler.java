package protocol.protomessage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtoMessages.ProtoMessage;
import protobuf.ProtoMessages.ProtoMessage.MessageType;
import protobuf.ProtoMessages.ProtoMessage.Status;

public class ClientStatusHandler extends SimpleChannelInboundHandler<ProtoMessage> {

  private final Logger logger = LoggerFactory.getLogger(ClientStatusHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ProtoMessage msg) throws Exception {
    logger.trace("channelRead0 {} sent: {}", ctx.channel().remoteAddress(), msg);
    MessageType type = msg.getMessageType();
    if (MessageType.STATUS == type) {
      logger.debug("channelRead0 {} STATUS: {}",ctx.channel().remoteAddress(),msg);
      Status status =  msg.getStatus();

    }
    else {
      ctx.fireChannelRead(msg);
    }
  }
}

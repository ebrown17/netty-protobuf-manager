package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.ProtoMessages.ProtoMessage;
import protobuf.ProtoMessages.ProtoMessage.MessageType;

public class ClientMessageHandler extends SimpleChannelInboundHandler<ProtoMessage> {

  private final Logger logger = LoggerFactory.getLogger(ClientMessageHandler.class);

  private ChannelHandlerContext ctx;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ProtoMessage msg) throws Exception {
    logger.trace("channelRead0 recieved {} from {}", msg.getMessageType(), ctx.channel().remoteAddress());
    MessageType type = msg.getMessageType();
    if (MessageType.DEFAULT_MESSAGE == type) {
      logger.debug("channelRead0 {} ", msg);
    }
    else if (MessageType.STATUS == type) {
      logger.debug("channelRead0 {} ", msg);
    }
    else {
      ctx.fireChannelRead(msg);
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.debug("channelActive");
    this.ctx = ctx;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.debug("channelInactive ");
  }

  public void sendData(ProtoMessage message) {
    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
      ctx.writeAndFlush(message);
    }
  }

}

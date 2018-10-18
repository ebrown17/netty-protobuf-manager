package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtoMessages.ProtoMessage;


public class ServerMessageHandler extends SimpleChannelInboundHandler<ProtoMessage> {

  private ChannelHandlerContext ctx;
  private final Logger logger = LoggerFactory.getLogger(ServerMessageHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ProtoMessage msg) throws Exception {
    logger.info("channelRead0 {} sent: {}", ctx.channel().remoteAddress(), msg);

  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.info("channelActive remote peer: {} connected", ctx.channel().remoteAddress());
    this.ctx = ctx;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.info("channelInactive remote peer: {} disconnected", ctx.channel().remoteAddress());
  }

  public void sendMessage(ProtoMessage message){
    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
      ctx.writeAndFlush(message);
    }
    else{
      logger.trace("sendMessage called when channel not active or writable");
    }
  }


}

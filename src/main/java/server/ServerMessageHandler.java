package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
    this.ctx = ctx;
    logger.info("channelActive remote peer: {} connected", ctx.channel().remoteAddress());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.info("channelInactive remote peer: {} disconnected", ctx.channel().remoteAddress());
  }

}

package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ServerDataHandler extends SimpleChannelInboundHandler<Message> {

  private ChannelHandlerContext ctx;
  private final Logger logger = LoggerFactory.getLogger("server.ServerDataHandler");

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
    logger.info("channelRead0 {} sent: {}", ctx.channel().remoteAddress(), msg.toString());
    
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

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.warn("Exception in connection from {} cause {}", ctx.channel().remoteAddress(), cause.toString());
    ctx.close();
  }

}

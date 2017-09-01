package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.JdssAuditor;
import protobuf.ProtobufMessage;

public class ClientDataHandler extends SimpleChannelInboundHandler<JdssAuditor.DisplayData> {

  private final Logger logger = LoggerFactory.getLogger("client.ClientDataHandler");
 
  private ChannelHandlerContext ctx;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, JdssAuditor.DisplayData msg) throws Exception {

  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.ctx = ctx;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {

  }

  public void sendData(DisplayData displayData) {
    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
      ctx.writeAndFlush(displayData);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.warn("Exception in connection from {} cause {}", ctx.channel().remoteAddress(), cause.toString());
    ctx.close();
  }

}
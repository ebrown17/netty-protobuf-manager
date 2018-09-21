package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.JdssAuditor;
import protobuf.JdssAuditor.DisplayData;
import server.ServerDataHandler;

public class ClientDataHandler extends SimpleChannelInboundHandler<JdssAuditor.DisplayData> {

  private final Logger logger = LoggerFactory.getLogger("client.ClientDataHandler");

  private ChannelHandlerContext ctx;
  private ClientHeartBeatHandler heatBeatHandler;
  
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, JdssAuditor.DisplayData msg) throws Exception {
    logger.debug("channelRead0 {} sent {}",ctx.channel().remoteAddress(),msg.toString());
    heatBeatHandler.resetTimeoutCounter();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.debug("channelActive ");
    this.ctx = ctx;
    heatBeatHandler = ctx.channel().pipeline().get(ClientHeartBeatHandler.class);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.debug("channelInactive ");
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

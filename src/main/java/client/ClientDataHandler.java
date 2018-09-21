package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.JdssAuditor.DisplayData;

public class ClientDataHandler extends SimpleChannelInboundHandler<Message> {

  private final Logger logger = LoggerFactory.getLogger("client.ClientDataHandler");

  private ChannelHandlerContext ctx;
  private ClientHeartBeatHandler heatBeatHandler;
  
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
   
    DisplayData test = ((DisplayData.class.cast(msg)) );
    logger.debug("channelRead0 recieved {} from {}",test.getMessageType(),ctx.channel().remoteAddress());
    
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

  public void sendData(Message displayData) {
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

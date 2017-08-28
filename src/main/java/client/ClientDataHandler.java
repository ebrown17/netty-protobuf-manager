package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.ProtobufMessage;

public class ClientDataHandler extends SimpleChannelInboundHandler<ProtobufMessage.ProtobufData> {

  private Client client;
  private final Logger logger = LoggerFactory.getLogger("client.ClientDataHandler");
 
  public ClientDataHandler() {
   
  }
  
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ProtobufMessage.ProtobufData msg) throws Exception {
    logger.info("channelRead0  recieved: {} from: {}", msg.toString(),ctx.channel().remoteAddress());

  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {

  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {


  }
/*
 * Should only handle inbound traffic in ClientDataHandler
  */
  public void sendData(int count,Channel channel) {
    if (channel.isActive() && channel.isWritable()) {
      logger.debug("sendData > sending... {} ", count);
      ProtobufMessage.ProtobufData data =
          ProtobufMessage.ProtobufData.newBuilder().setDataString("Test").setDataNumber(count).build();
      channel.writeAndFlush(data);
    }

  }

/*  public void sendheartBeat() {
    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
      logger.debug("sendheartBeat sending... {} ", heartbeat);
      ctx.writeAndFlush(heartbeat);
    }

  }*/

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.warn("Exception in connection from {} cause {}", ctx.channel().remoteAddress(), cause.toString(),cause);
    ctx.channel().close();
  }


}

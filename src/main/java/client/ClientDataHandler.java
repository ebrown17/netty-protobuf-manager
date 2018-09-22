package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.ProtobufDefaultMessages.DefaultMessages;
import protobuf.ProtobufDefaultMessages.DefaultMessages.MessageType;

public class ClientDataHandler extends SimpleChannelInboundHandler<Message> {

  private final Logger logger = LoggerFactory.getLogger("client.ClientDataHandler");

  private ChannelHandlerContext ctx;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
    DefaultMessages message = ((DefaultMessages) msg);
    logger.debug("channelRead0 recieved {} from {}", message.getMessageType(), ctx.channel().remoteAddress());
    if (MessageType.DEFAULT_MESSAGE == message.getMessageType()) {
      
    }
   /* else if(MessageType.STATUS == message.getMessageType() ) {
      
    }*/
    else {
      ctx.fireChannelRead(msg);
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.debug("channelActive ");
    this.ctx = ctx;
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

package protocol.protomessage;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtoMessages.ProtoMessage;

public class ExceptionHandler extends ChannelDuplexHandler {
  private final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ProtoMessage message = ((ProtoMessage) msg);
    logger.warn("channelRead: end of pipeline reached without handling: {}", message.toString());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.warn("Exception in connection from {} cause {}", ctx.channel().remoteAddress(), cause.toString());
    ctx.close();
  }

}

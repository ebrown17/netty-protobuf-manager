package common_handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import protobuf.ProtobufDefaultMessages.DefaultMessages;
import protobuf.ProtobufDefaultMessages.DefaultMessages.MessageType;

public class ExceptionHandler extends ChannelDuplexHandler {
  private final Logger logger = LoggerFactory.getLogger("common_handlers.ExceptionHandler");

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    DefaultMessages message = ((DefaultMessages) msg);
    logger.warn("channelRead: end of pipeline reached without handling: {}", message.toString());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.warn("Exception in connection from {} cause {}", ctx.channel().remoteAddress(), cause.toString());
    ctx.close();
  }

}

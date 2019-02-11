package common;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler extends ChannelDuplexHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object message) throws Exception {
      logger.warn("channelRead: end of pipeline reached without handling: {}", message.toString());
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
    logger.warn("Exception in connection from {} cause {}", ctx.channel().remoteAddress(), cause.toString());
    ctx.close();
  }
}

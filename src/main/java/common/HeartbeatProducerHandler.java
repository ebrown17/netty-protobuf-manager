package common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public abstract class HeartbeatProducerHandler<I> extends IdleStateHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final Transceiver<I> transceiver;

  HeartbeatProducerHandler(int readerIdleTimeSeconds,
                           int writerIdleTimeSeconds,
                           int allIdleTimeSeconds,
                           Transceiver<I> transceiver) {

    super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    this.transceiver = transceiver;
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;
      if (e.state() == IdleState.WRITER_IDLE) {
        logger.trace("userEventTriggered sendheartBeat");
        transceiver.sendMessage((InetSocketAddress) ctx.channel().remoteAddress(),  generateHeartBeat());
      }
    }
  }

  abstract I generateHeartBeat();

}

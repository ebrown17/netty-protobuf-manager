package common;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatReceiverHandler extends IdleStateHandler {


  private final Logger logger = LoggerFactory.getLogger(getClass());
  private int missedLimit, missCount = 0, expectedInterval;

  /**
   * HeartbeatRecieverHandler expects to be receiving heartbeat message.
   * <p>
   * If the heartbeat miss limit is reached the channel is closed and the client's reconnect logic is
   * started.
   * <p>
   * By default every channel read will reset the miss count. To only reset on a heartbeat, you must override the channelRead
   * method and add appropriate logic. The method
   * {@link HeartbeatReceiverHandler#resetMissCounter() resetMissCounter } can be called reset the miss count.
   *
   * @param expectedInterval The expected heartbeat interval. This will be used to determine if server
   *                         is no longer alive.
   * @param missedLimit      The max amount of heartbeats allowed until handler closes channel.
   */
  public HeartbeatReceiverHandler(int readerIdleTimeSeconds,
                                  int writerIdleTimeSeconds,
                                  int allIdleTimeSeconds,
                                  int expectedInterval,
                                  int missedLimit) {
    super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    this.expectedInterval = expectedInterval;
    this.missedLimit = missedLimit;
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    logger.debug("userEventTriggered");
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;
      logger.debug("userEventTriggered {} miss count {}", e.state(), missCount);

      if (e.state() == IdleState.READER_IDLE) {
        if (missCount >= missedLimit) {
          logger.info("userEventTriggered no heartbeat read for {} seconds. Closing Connection.",
              missedLimit * expectedInterval);
          ctx.close();
        }
        else {
          missCount++;
        }
      }
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    logger.debug("channelRead received {} from {}", msg.toString(), ctx.channel().remoteAddress());
    resetMissCounter();
    ctx.fireChannelRead(msg);

  }

  /**
   * Sets the heartbeat miss counter to zero.
   */
  protected void resetMissCounter() {
    missCount = 0;
  }
}

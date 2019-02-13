package common;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HeartbeatReceiverHandler<I> extends ChannelDuplexHandler {

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
   * @param expectedInterval The expected heartbeat interval in seconds. This will be used to determine if server
   *                         is no longer alive.
   * @param missedLimit      The max amount of heartbeats allowed until handler closes channel.
   */
  public HeartbeatReceiverHandler(int expectedInterval,int missedLimit) {
    this.expectedInterval = expectedInterval;
    this.missedLimit = missedLimit;
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    logger.trace("userEventTriggered");
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;
      logger.trace("userEventTriggered {} miss count {}", e.state(), missCount);

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

  /**
   * Sets the heartbeat miss counter to zero.
   */
  protected void resetMissCounter() {
    missCount = 0;
  }
}

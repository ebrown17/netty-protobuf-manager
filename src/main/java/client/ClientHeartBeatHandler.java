package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientHeartBeatHandler extends ChannelDuplexHandler {

  private final Logger logger = LoggerFactory.getLogger("client.ClientHeartBeatHandler");
  private int missedLimit, timeoutCount = 0,expectedInterval;

/**
 * 
 * @param expectedInterval The expected heartbeat interval. This will be used to determine if server is no longer alive.
 * @param missedLimit The max amount of heartbeats allowed until handler closes channel.
 * @param channel The channel to monitor heartbeats on. Expecting server to send heartbeats; only checking reader.
 */
  public ClientHeartBeatHandler(int expectedInterval,int missedLimit, Channel channel) {
    this.expectedInterval = expectedInterval;
    this.missedLimit = missedLimit;
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    logger.debug("userEventTriggered");
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;
      logger.debug("userEventTriggered {} miss count {}",e.state(),timeoutCount);
      
      if (e.state() == IdleState.READER_IDLE) {
        if (timeoutCount >= missedLimit) {
          logger.info("userEventTriggered no heartbeat read for {} seconds. Closing Connection.", missedLimit * expectedInterval);
          ctx.close();
        }
        else {
          timeoutCount++;
        }
      }
    }
  }

  public void resetTimeoutCounter() {
    timeoutCount = 0;
  }
}

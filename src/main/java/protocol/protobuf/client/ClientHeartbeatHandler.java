package protocol.protobuf.client;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.protobuf.ProtoMessages.ProtoMessage;
import protocol.protobuf.ProtoMessages.ProtoMessage.MessageType;

public class ClientHeartbeatHandler extends ChannelDuplexHandler {

  private final static Logger logger = LoggerFactory.getLogger(ClientHeartbeatHandler.class);
  private int missedLimit, missCount = 0, expectedInterval;

  /**
   * ClientHeartBeatHandler expects the server to be be sending a heartbeat message.
   * <p>
   * If the heartbeat miss limit is reached the channel is closed and the client's reconnect logic is
   * started.
   * <p>
   * By default only a heartbeat read will reset the miss count. The method
   * {@link ClientHeartbeatHandler#resetMissCounter() resetMissCounter } can be called on any read.
   *
   * @param expectedInterval The expected heartbeat interval. This will be used to determine if server
   *                         is no longer alive.
   * @param missedLimit      The max amount of heartbeats allowed until handler closes channel.
   *
   */
  public ClientHeartbeatHandler(int expectedInterval, int missedLimit) {
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
    ProtoMessage message = ((ProtoMessage) msg);
    logger.debug("channelRead recieved {} from {}", message.getMessageType(), ctx.channel().remoteAddress());
   if (MessageType.HEARTBEAT == message.getMessageType()) {
      resetMissCounter();
    }
    else {
      ctx.fireChannelRead(msg);
    }
  }

  /**
   * Sets the heartbeat miss counter to zero.
   */
  protected void resetMissCounter() {
    missCount = 0;
  }
}

package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import protobuf.ProtoMessages.ProtoMessage;
import protobuf.ProtoMessages.ProtoMessage.MessageType;

public class ClientHeartBeatHandler extends ChannelDuplexHandler {

  private final Logger logger = LoggerFactory.getLogger("client.ClientHeartBeatHandler");
  private int missedLimit, timeoutCount = 0, expectedInterval;

  /**
   * ClientHeartBeatHandler expects the server to be be sending a heartbeat message.
   * <p>
   * {@link ClientChannelInitializer}
   * specifies the expected heartbeat interval and amount of times a client can miss a heartbeat
   * message.
   * <p>
   * If the heartbeat miss interval is missed the channel is closed and the client's reconnect logic is started.
   * 
   * @param expectedInterval The expected heartbeat interval. This will be used to determine if server
   *        is no longer alive.
   * @param missedLimit The max amount of heartbeats allowed until handler closes channel.
   * @param channel The channel to monitor heartbeats on. Expecting server to send heartbeats; only
   *        checking reader.
   */
  public ClientHeartBeatHandler(int expectedInterval, int missedLimit, Channel channel) {
    this.expectedInterval = expectedInterval;
    this.missedLimit = missedLimit;
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    logger.debug("userEventTriggered");
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;
      logger.debug("userEventTriggered {} miss count {}", e.state(), timeoutCount);

      if (e.state() == IdleState.READER_IDLE) {
        if (timeoutCount >= missedLimit) {
          logger.info("userEventTriggered no heartbeat read for {} seconds. Closing Connection.",
              missedLimit * expectedInterval);
          ctx.close();
        }
        else {
          timeoutCount++;
        }
      }
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ProtoMessage message = ((ProtoMessage) msg);
    logger.debug("channelRead recieved {} from {}", message.getMessageType(), ctx.channel().remoteAddress());
    if (MessageType.HEARTBEAT == message.getMessageType()) {
      resetTimeoutCounter();
    }
    else {
      ctx.fireChannelRead(msg);
    }
  }

  protected void resetTimeoutCounter() {
    timeoutCount = 0;
  }
}

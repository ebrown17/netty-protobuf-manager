package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import protobuf.JdssAuditor.DisplayData;
import protobuf.JdssAuditor.DisplayData.HeartBeat;

public class ServerHeartbeatHandler extends ChannelDuplexHandler {

  private final Logger logger = LoggerFactory.getLogger("server.ServerHeartbeatHandler");
  private final static DisplayData heartBeat =
      DisplayData.newBuilder().setMessageType(DisplayData.AuditorMessageType.HEARTBEAT).setHearBeat(HeartBeat.newBuilder().setTime("heartbeat")).build();
  
  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;
      if (e.state() == IdleState.WRITER_IDLE) {
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
          logger.debug("userEventTriggered sendheartBeat");
          ctx.writeAndFlush(heartBeat);
        }
      }
    }
  }

}

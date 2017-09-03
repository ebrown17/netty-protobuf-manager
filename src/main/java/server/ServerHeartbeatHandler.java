package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHeartbeatHandler extends ChannelDuplexHandler {

  private ServerDataHandler handler;
  private final Logger logger = LoggerFactory.getLogger("server.ServerHeartbeatHandler");

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;
      if (e.state() == IdleState.WRITER_IDLE) {
        handler = ctx.channel().pipeline().get(ServerDataHandler.class);
        handler.sendheartBeat();
      }
      else if (e.state() == IdleState.READER_IDLE) {

      }
    }
  }

}

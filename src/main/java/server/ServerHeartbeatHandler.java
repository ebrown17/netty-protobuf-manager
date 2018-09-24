package server;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Timestamp;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import protobuf.ProtoMessages.ProtoMessage;
import protobuf.ProtoMessages.ProtoMessage.HeartBeat;
import protobuf.ProtoMessages.ProtoMessage.MessageType;
import protobuf.ProtoMessages.ProtoMessage.Status;

public class ServerHeartbeatHandler extends ChannelDuplexHandler {

  private final Logger logger = LoggerFactory.getLogger(ServerHeartbeatHandler.class);
  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;
      if (e.state() == IdleState.WRITER_IDLE) {
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
          logger.debug("userEventTriggered sendheartBeat");
            ctx.writeAndFlush(generateHeartBeat());
        }
      }
    }
  }

/*  private ProtoMessage generateStatusMessage() {
    Status status = Status.newBuilder().setHealth("GOOD").setErrors(5).setUptime(100).build();
    return ProtoMessage.newBuilder().setMessageType(MessageType.STATUS).setStatus(status).build();
  }*/
  
  private ProtoMessage generateHeartBeat() {
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(new Date().getTime()).build();
    HeartBeat heartbeat = HeartBeat.newBuilder().setDate(timestamp).build();
    return ProtoMessage.newBuilder().setMessageType(MessageType.HEARTBEAT).setHeartbeat(heartbeat).build();
  }

}

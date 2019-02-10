package protocol.protobuf.server;

import com.google.protobuf.Timestamp;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.protobuf.ProtoMessages.ProtoMessage;
import protocol.protobuf.ProtoMessages.ProtoMessage.HeartBeat;
import protocol.protobuf.ProtoMessages.ProtoMessage.MessageType;
import protocol.protobuf.MessageTransceiver;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * ServerHeartbeatHandler is configured to send heartbeat messages. Currently configured to send a heartbeat whenever
 * the writer is idle for the configured time in {@link ServerMessageChannel#WRITE_IDLE_TIME WRITE_IDLE_TIME } .
 */
public class ServerHeartbeatHandler extends ChannelDuplexHandler {

  private final static Logger logger = LoggerFactory.getLogger(ServerHeartbeatHandler.class);
  private final MessageTransceiver transceiver;

  ServerHeartbeatHandler(MessageTransceiver transceiver) {
    this.transceiver = transceiver;
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;
      if (e.state() == IdleState.WRITER_IDLE) {
        logger.debug("userEventTriggered sendheartBeat");
        transceiver.sendMessage((InetSocketAddress) ctx.channel().remoteAddress(), generateHeartBeat());
      }
    }
  }

  private ProtoMessage generateHeartBeat() {
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(new Date().getTime()).build();
    HeartBeat heartbeat = HeartBeat.newBuilder().setDate(timestamp).build();
    return ProtoMessage.newBuilder().setMessageType(MessageType.HEARTBEAT).setHeartbeat(heartbeat).build();
  }

}

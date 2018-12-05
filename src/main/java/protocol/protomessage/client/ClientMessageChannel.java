package protocol.protomessage.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import protobuf.ProtoMessages.ProtoMessage;
import protocol.protomessage.ExceptionHandler;
import protocol.protomessage.MessageHandler;
import protocol.protomessage.MessageTransceiver;
import protocol.protomessage.StatusHandler;

import java.util.concurrent.atomic.AtomicLong;

public class ClientMessageChannel extends ChannelInitializer<SocketChannel> {

  private static final int READ_IDLE_TIME = 10;
  private static final int HEARTBEAT_MISS_LIMIT = 2;

  private static final AtomicLong channelIds = new AtomicLong(0L);
  private final MessageTransceiver transceiver;

  ClientMessageChannel(){
    transceiver = new MessageTransceiver();
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();

    p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
    p.addLast("protobufDecoder", new ProtobufDecoder(ProtoMessage.getDefaultInstance()));
    p.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
    p.addLast("protobufEncoder", new ProtobufEncoder());

    p.addLast(new MessageHandler(channelIds.incrementAndGet(),transceiver));
    p.addLast(new StatusHandler());
    p.addLast("idleStateHandler", new IdleStateHandler(READ_IDLE_TIME, 0, 0));
    p.addLast("heartBeatHandler", new ClientHeartbeatHandler(READ_IDLE_TIME, HEARTBEAT_MISS_LIMIT, p.channel()));

    p.addLast(new ExceptionHandler());
  }
}

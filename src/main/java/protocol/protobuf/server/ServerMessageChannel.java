package protocol.protobuf.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import protocol.protobuf.ProtoMessages.ProtoMessage;
import protocol.protobuf.ExceptionHandler;
import protocol.protobuf.MessageHandler;
import protocol.protobuf.MessageTransceiver;

import java.util.concurrent.atomic.AtomicLong;


public class ServerMessageChannel extends ChannelInitializer<SocketChannel> {

  private static final int WRITE_IDLE_TIME = 5;
  private static final AtomicLong channelIds = new AtomicLong(0L);
  private final MessageTransceiver transceiver;

  ServerMessageChannel(MessageTransceiver transceiver){
    this.transceiver =transceiver;
  }

  @Override
  protected void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();

    p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
    p.addLast("protobufDecoder", new ProtobufDecoder(ProtoMessage.getDefaultInstance()));
    p.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
    p.addLast("protobufEncoder", new ProtobufEncoder());
    p.addLast(new MessageHandler(channelIds.incrementAndGet(),transceiver));
    p.addLast("idleStateHandler", new IdleStateHandler(0, WRITE_IDLE_TIME, 0));
    p.addLast("heartBeatHandler", new ServerHeartbeatHandler(transceiver));
    p.addLast(new ExceptionHandler());
  }

}

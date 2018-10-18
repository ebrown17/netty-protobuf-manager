package server;

import common_handlers.ExceptionHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import protobuf.ProtoMessages.ProtoMessage;


public class ServerMessageChannel extends ChannelInitializer<SocketChannel> {

  private static final int WRITE_IDLE_TIME = 5;

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();

    p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
    p.addLast("protobufDecoder", new ProtobufDecoder(ProtoMessage.getDefaultInstance()));
    p.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
    p.addLast("protobufEncoder", new ProtobufEncoder());
    p.addLast(new ServerMessageHandler());
    p.addLast("idleStateHandler", new IdleStateHandler(0, WRITE_IDLE_TIME, 0));
    p.addLast("heartBeatHandler", new ServerHeartbeatHandler());
    p.addLast(new ExceptionHandler());
  }

}

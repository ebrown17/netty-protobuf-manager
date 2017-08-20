package client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import protobuf.ProtobufMessage;

public class ClientChannelHandler extends ChannelInitializer<SocketChannel> {

  private ClientConnector client;
  private static final int READ_IDLE_TIME = 10;
  private static final int HEARTBEAT_RETRY_LIMIT = 10;

  public ClientChannelHandler(ClientConnector client) {
    this.client = client;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();
    // TODO implement heartbeat protocol
    /*
     * p.addLast("idleStateHandler",new IdleStateHandler(READ_IDLE_TIME,0,0));
     * p.addLast("heartBeatHandler",new ClientHeartBeatHandler(HEARTBEAT_RETRY_LIMIT,p.channel()));
     */
    p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
    p.addLast("protobufDecoder", new ProtobufDecoder(ProtobufMessage.ProtobufData.getDefaultInstance()));
    p.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
    p.addLast("protobufEncoder", new ProtobufEncoder());
    p.addLast(new ClientDataHandler(client));

  }



}

package protocol.protomessage;

import common.ExceptionHandler;
import common.Transceiver;
import common.server.TransceiverServerChannel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import protocol.protomessage.ProtoMessages.ProtoMessage;

public class MessageServerChannel extends TransceiverServerChannel<ProtoMessage> {
    private final Transceiver<ProtoMessage> transceiver;

    public MessageServerChannel(Transceiver<ProtoMessage> transceiver) {
        super(transceiver);
        this.transceiver = transceiver;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        p.addLast("protobufDecoder", new ProtobufDecoder(ProtoMessage.getDefaultInstance()));
        p.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        p.addLast("protobufEncoder", new ProtobufEncoder());
        p.addLast(new MessageHandler(channelIds.incrementAndGet(),transceiver));
        p.addLast("idleStateHandler", new IdleStateHandler(0, WRITE_IDLE_TIME, 0));
        p.addLast("heartBeatHandler", new MessageHearbeatProducer(transceiver));
        p.addLast(new ExceptionHandler());
    }
}

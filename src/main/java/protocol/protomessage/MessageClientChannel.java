package protocol.protomessage;
import common.Client.TransceiverClientChannel;
import common.ExceptionHandler;
import common.Transceiver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import protocol.protomessage.ProtoMessages.ProtoMessage;

public class MessageClientChannel extends TransceiverClientChannel<ProtoMessage> {

    public MessageClientChannel(Transceiver<ProtoMessage> transceiver) {
        super(transceiver);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        p.addLast("protobufDecoder", new ProtobufDecoder(ProtoMessage.getDefaultInstance()));
        p.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        p.addLast("protobufEncoder", new ProtobufEncoder());
        p.addLast("idleStateHandler", new IdleStateHandler(READ_IDLE_TIME, 0, 0));
        p.addLast(new MessageHandler(channelIds.incrementAndGet(),transceiver));
        p.addLast("heartBeatHandler", new MessageHeartbeatReceiver(READ_IDLE_TIME, HEARTBEAT_MISS_LIMIT));
        p.addLast(new ExceptionHandler());
    }
}

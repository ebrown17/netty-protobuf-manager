package protocol.protomessage;

import com.google.protobuf.Timestamp;
import common.HeartbeatProducerHandler;
import common.Transceiver;
import protocol.protomessage.ProtoMessages.ProtoMessage;

import java.util.Date;

public class MessageHearbeatProducer extends HeartbeatProducerHandler<ProtoMessage> {

    public MessageHearbeatProducer(Transceiver<ProtoMessage> transceiver) {
        super(transceiver);
    }

    @Override
    public ProtoMessage generateHeartBeat() {
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(new Date().getTime()).build();
        ProtoMessage.HeartBeat heartbeat = ProtoMessage.HeartBeat.newBuilder().setDate(timestamp).build();
        return ProtoMessage.newBuilder().setMessageType(ProtoMessage.MessageType.HEARTBEAT).setHeartbeat(heartbeat).build();
    }
}

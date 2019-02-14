package protocol.protomessage;

import common.Client.Client;
import common.Client.TransceiverClientChannel;
import common.Transceiver;
import io.netty.channel.EventLoopGroup;
import protocol.protomessage.ProtoMessages.ProtoMessage;

import java.net.InetSocketAddress;

public class MessageClient extends Client<ProtoMessage> {
    public <T extends TransceiverClientChannel> MessageClient(InetSocketAddress serverAddress, EventLoopGroup sharedWorkerGroup, Transceiver<ProtoMessage> transceiver, T clientChannel) {
        super(serverAddress, sharedWorkerGroup, transceiver, clientChannel);
    }
}

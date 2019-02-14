package protocol.protomessage;

import common.Client.Client;
import common.Client.ClientFactory;
import common.Transceiver;
import protocol.protomessage.ProtoMessages.ProtoMessage;

import java.net.InetSocketAddress;

public class MessageClientFactory extends ClientFactory<ProtoMessage> {
    @Override
    protected Client<ProtoMessage> createClient(InetSocketAddress address, Transceiver<ProtoMessage> transceiver) {
        return new MessageClient(address,workerGroup, transceiver,new MessageClientChannel(transceiver));
    }
}

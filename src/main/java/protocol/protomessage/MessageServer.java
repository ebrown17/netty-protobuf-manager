package protocol.protomessage;

import common.Transceiver;
import common.server.Server;
import protocol.protomessage.ProtoMessages.ProtoMessage;

public class MessageServer extends Server<ProtoMessage> {

    @Override
    public boolean addChannel(int port) {
        Transceiver<ProtoMessage> transceiver = new Transceiver<ProtoMessage>(port);
        MessageServerChannel channel = new MessageServerChannel(transceiver);
        return  addChannel(port,transceiver,channel);
    }
}

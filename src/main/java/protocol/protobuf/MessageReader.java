package protocol.protobuf;

import protocol.protobuf.ProtoMessages.ProtoMessage;

import java.net.InetSocketAddress;

public interface MessageReader {
   void readMessage(InetSocketAddress addr, ProtoMessage message);
}

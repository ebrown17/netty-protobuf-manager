package protocol.protobuf;

import java.net.InetSocketAddress;

 public interface MessageHandlerListener {

  void registerActiveHandler(int channelPort, InetSocketAddress remoteConnection);

  void registerInActiveHandler(int channelPort, InetSocketAddress remoteConnection);

}

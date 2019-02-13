package common;

import java.net.InetSocketAddress;

public interface HandlerListener<I> {
  void registerActiveHandler(int channelPort, InetSocketAddress remoteConnection);
  void registerInActiveHandler(int channelPort, InetSocketAddress remoteConnection);
}

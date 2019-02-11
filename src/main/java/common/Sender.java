package common;

import java.net.InetSocketAddress;

public interface Sender<I> {
  /**
   * Transceiver will send message to specified CONNECTED host. Will silently fail if host not connected.
   * @param addr  Remote address to send to
   * @param message Message to send
   */
  void sendMessage(InetSocketAddress addr,I message);
}

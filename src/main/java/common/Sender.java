package common;

import java.net.InetSocketAddress;

public interface Sender<I> {
  /**
   * Transceiver will send message to specified CONNECTED host. Will silently fail if host not connected.
   * @param message Message to send
   */
  void sendMessage(I message);
}

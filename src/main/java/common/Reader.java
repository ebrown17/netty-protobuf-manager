package common;

import java.net.InetSocketAddress;

public interface Reader<I> {
  /**
   * Transceiver calls this method when a read on this channel is detected
   * @param addr Address of remote machine who sent message
   * @param message Message received
   */
  void readMessage(InetSocketAddress addr, I message);

  void registerReadListener(ReadListener<I> reader);
}

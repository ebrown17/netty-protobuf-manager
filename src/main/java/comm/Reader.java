package comm;

import java.net.InetSocketAddress;

public interface Reader<I> {
  void readMessage(InetSocketAddress addr, I message);
}

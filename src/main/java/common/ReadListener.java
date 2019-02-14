package common;

import java.net.InetSocketAddress;

public interface ReadListener<I> {
    void read(InetSocketAddress addr, I message);
}

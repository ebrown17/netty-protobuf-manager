package protocol.protomessage.client;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ClientConnectionFactory {

  private final Logger logger = LoggerFactory.getLogger(ClientConnectionFactory.class);

  private EventLoopGroup workerGroup;
  private Class<? extends Channel> channelClass;
  private PooledByteBufAllocator allocator;

  public ClientConnectionFactory() {
    // 0 forces netty to use default number of threads which is max number of processors * 2
    // this workerGroup will be shared with all clients
    this.workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("client", true));
    this.channelClass = NioSocketChannel.class;
    this.allocator = PooledByteBufAllocator.DEFAULT;

  }

  public Client createClient(String host, int port) {
    InetSocketAddress address = new InetSocketAddress(host, port);
    return createClient(address);
  }

  private Client createClient(InetSocketAddress address) {
    return new Client(address, workerGroup);
  }

}

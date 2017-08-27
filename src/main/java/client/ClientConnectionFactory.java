package client;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

public class ClientConnectionFactory {

  private final Logger logger = LoggerFactory.getLogger("client.ClientConnectionFactory");

  private EventLoopGroup workerGroup;
  private Class<? extends Channel> channelClass;
  private PooledByteBufAllocator allocator;
  
  public ClientConnectionFactory() {
    // 0 forces netty to use default number of threads which is max number of processors * 2
    // this workerGroup will be shared with all clients
    this.workerGroup = new NioEventLoopGroup(0,new DefaultThreadFactory("-client", true));
    this.channelClass = NioSocketChannel.class;
    this.allocator = PooledByteBufAllocator.DEFAULT;
    
  }
  
  public void createClient(String host,int port) {
    InetSocketAddress address = new  InetSocketAddress(host, port);
    createClient(address);
  }
  
  private void createClient(InetSocketAddress address) {
    
  }

}

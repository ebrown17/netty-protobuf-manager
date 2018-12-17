package protocol.protomessage.client;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.protomessage.MessageTransceiver;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ClientConnectionFactory {

  private final static Logger logger = LoggerFactory.getLogger(ClientConnectionFactory.class);

  private final EventLoopGroup workerGroup;
  private final Class<? extends Channel> channelClass;
  private final PooledByteBufAllocator allocator;
  private final HashMap<Integer, MessageTransceiver> transceiverMap;

  public ClientConnectionFactory() {
    // 0 forces netty to use default number of threads which is max number of processors * 2
    // this workerGroup will be shared with all clients
    this.workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("client", true));
    this.channelClass = NioSocketChannel.class;
    this.allocator = PooledByteBufAllocator.DEFAULT;
    transceiverMap = new HashMap<Integer, MessageTransceiver>();

  }

  public Client createClient(String host, int port) {
    InetSocketAddress address = new InetSocketAddress(host, port);
    MessageTransceiver transceiver = transceiverMap.get(port);
    if(transceiver == null){
      transceiver = new MessageTransceiver(port);
    }
    return createClient(address,transceiver);
  }

  private Client createClient(InetSocketAddress address,MessageTransceiver transceiver) {
    return new Client(address, workerGroup,transceiver);
  }

}

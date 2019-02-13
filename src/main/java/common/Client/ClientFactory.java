package common.Client;

import common.Transceiver;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;

public abstract class ClientFactory<I> {
    private final static Logger logger = LoggerFactory.getLogger(ClientFactory.class);

    public final EventLoopGroup workerGroup;
    private final Class<? extends Channel> channelClass;
    private final PooledByteBufAllocator allocator;


    public ClientFactory() {
        // 0 forces netty to use default number of threads which is max number of processors * 2
        // this workerGroup will be shared with all clients
        this.workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("client", true));
        this.channelClass = NioSocketChannel.class;
        this.allocator = PooledByteBufAllocator.DEFAULT;

    }

    public <T extends Client<I>> T createClient(String host, int port, Class<T> clientType) {
        InetSocketAddress address = new InetSocketAddress(host, port);
        Transceiver<I> transceiver = new Transceiver<I>(port);
        return clientType.cast(createClient(address, transceiver));
    }

    protected abstract Client<I> createClient(InetSocketAddress address, Transceiver<I> transceiver);
}

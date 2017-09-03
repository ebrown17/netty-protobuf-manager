package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import protobuf.JdssAuditor.DisplayData;

public class Client {
    private final Logger logger = LoggerFactory.getLogger("client.Client");

    private InetSocketAddress serverAddress;
    private Bootstrap bootstrap;
    private Channel channel;
    private ClientDataHandler handler;

    private static final long RETRY_TIME = 10L;
    private static final long MAX_RETRY_TIME = 60L;
    private static final int MAX_RETRY_UNTIL_INCR = 30;
    private static final int TOTAL_MAX_RETRY_COUNT = 360;
    private int retryCount = 0;
    private boolean disconnectIntiated = true;

    public Client(InetSocketAddress serverAddress, EventLoopGroup sharedWorkerGroup) {
        this.serverAddress = serverAddress;
        bootstrap = new Bootstrap();
        bootstrap.group(sharedWorkerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ClientChannelInitializer());
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    public void connect() throws InterruptedException {

        if (isActive()) {
            logger.warn("connect already active don't create new connection ");
            return; 
        }
        ChannelFuture channelFuture = bootstrap.connect(serverAddress);
        try {
            channelFuture.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted trying to connect");
        }
        if (!channelFuture.isSuccess()) {
            channelFuture.channel().eventLoop().schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        connect();
                    } catch (InterruptedException e) {
                        // TODO test to see if this breaks it
                        throw new RuntimeException("Interrupted trying to connect");
                    }
                }
            }, calculateRetryTime(), TimeUnit.SECONDS);
        } else {
            logger.info("connect Client connected to {} on port {}", serverAddress.getHostString(),serverAddress.getPort());
            retryCount = 0;
            disconnectIntiated = false;
            channel = channelFuture.channel();
            handler = channel.pipeline().get(ClientDataHandler.class);
            // future to handle when client connection is lost
            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (!disconnectIntiated) {
                        logger.warn("connect.closeFuture Client connection lost, initiating reconnect logic... ");
                        connect();
                    } else {
                        channel.close();
                        logger.info("connect.closeFuture > Client fully diconnected");
                    }
                }
            });

        }

    }

    public void disconnect() throws IOException {
        if (channel == null || !isActive()) {
            logger.info("disconnect disconnect called when connection already closed");
            return;
        }
        disconnectIntiated = true;
        logger.info("disconnect disconnect explicitly called");
        channel.close().awaitUninterruptibly(1, TimeUnit.SECONDS);

    }

    /**
     * @return Will return the time in milliseconds. Returns the
     *         {@code RETRY_TIME} for the specified {@code retryCount}. After
     *         this limit is reached it will then only return the time specified
     *         with {@code MAX_RETRY_TIME}
     */
    private long calculateRetryTime() {
        if (retryCount >= MAX_RETRY_UNTIL_INCR) {
            logger.debug("calculateRetryTime {}>={} setting {} as retry interval: total time retrying {} seconds",
                    retryCount, MAX_RETRY_UNTIL_INCR, MAX_RETRY_TIME,
                    ((retryCount - MAX_RETRY_UNTIL_INCR) * MAX_RETRY_TIME) + (MAX_RETRY_UNTIL_INCR * RETRY_TIME));
            retryCount++;
            return MAX_RETRY_TIME;
        } else {
            logger.debug(
                    "calculateRetryTime {}<{} setting {} seconds as retry interval: total time retrying {} seconds",
                    retryCount, MAX_RETRY_UNTIL_INCR, RETRY_TIME, RETRY_TIME * retryCount);
            retryCount++;
            return RETRY_TIME;
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isActive() {
        return (channel != null && (channel.isOpen() || channel.isActive()));
    }

    public void sendData(DisplayData displayData) {

        if (!isActive()) {
            logger.warn("sendData tried to send data on null or closed channel");
            return;
        }
        logger.trace("sendData {} to remote host",displayData.toString(),channel.remoteAddress());
        handler.sendData(displayData);
    }}

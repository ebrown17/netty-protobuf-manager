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
  //TODO probably never want to stop retry; so this could be removed
  private static final int TOTAL_MAX_RETRY_COUNT = 360;
  private ClientConnectionListener retryistener;
  private ClientClosedListener closedListener;
  private int retryCount = 0;
  private long initialRetryTime = 0;
  private boolean disconnectInitiated = true;

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
      logger.warn("connect called while connection already active");
      return;
    }
    if(retryistener != null && retryistener.isAttemptingConnection()) {
      logger.warn("connect called while connection attempt already in progress");
      return;
    }
    if(retryistener == null){
      logger.info("connect creating new connection listener");
      retryistener = new ClientConnectionListener(this);
    }

    ChannelFuture channelFuture = bootstrap.connect(serverAddress);
    retryistener.setAttemptingConnection();
    channelFuture.addListener(retryistener);
  }

  public void disconnect() throws IOException {
    if (channel == null || !isActive()) {
      logger.info("disconnect called when connection not active or channel null");
      return;
    }
    channel.closeFuture().removeListener(closedListener);
    disconnectInitiated = true;
    logger.info("disconnect disconnect explicitly called");
    channel.close().awaitUninterruptibly(1, TimeUnit.SECONDS);

  }

  protected void connectionEstablished(ChannelFuture future) {
    logger.info("connectionEstablished Client connected to {} ", serverAddress.getHostString());
    retryCount = 0;
    initialRetryTime = 0;
    disconnectInitiated = false;
    channel = future.channel();
    handler = channel.pipeline().get(ClientDataHandler.class);
    // future to handle when client connection is lost or closed
    closedListener = new ClientClosedListener(this);
    channel.closeFuture().addListener(closedListener);
  }

  /**
   * @return Will return the time in milliseconds. Returns the
   *         {@code RETRY_TIME} for the specified {@code retryCount}. After
   *         this limit is reached it will then only return the time specified
   *         with {@code MAX_RETRY_TIME}. 
   */
  protected long calculateRetryTime() {
    long retryTime = System.currentTimeMillis() - initialRetryTime;
    
    if(retryTime>= 10000L) {
      retryCount++;
    }
    if(initialRetryTime==0) {
      initialRetryTime = retryTime;
      retryTime = 0;
    }
    
    if (retryCount >= MAX_RETRY_UNTIL_INCR) {
      logger.debug("calculateRetryTime {} >= {} setting {} as retry interval: total time retrying {} seconds", retryCount,
          MAX_RETRY_UNTIL_INCR, MAX_RETRY_TIME,
          retryTime/1000L);
      return MAX_RETRY_TIME;
    }
    else {
      logger.debug("calculateRetryTime {} < {} setting {} seconds as retry interval: total time retrying {} seconds",
          retryCount, MAX_RETRY_UNTIL_INCR, RETRY_TIME,  retryTime/1000L);
      return RETRY_TIME;
    }
  }

  public Channel getChannel() {
    return channel;
  }

  protected boolean isDisconnectInitiated() {
    return disconnectInitiated;
  }

  public boolean isActive() {
    return (channel != null && (channel.isOpen() || channel.isActive()));
  }

  public void sendData(DisplayData displayData) {
    if (!isActive()) {
      logger.warn("sendData can't send data on null or closed channel");
      return;
    }
    logger.trace("sendData {} to remote host", displayData.toString(), channel.remoteAddress());
    handler.sendData(displayData);
  }
}

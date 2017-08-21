package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientConnector {

  private String host;
  private int port;
  private EventLoopGroup workerGroup;
  private Bootstrap bootstrap;
  private ChannelFuture channelFuture;
  private Channel channel;
  private ClientDataHandler handler;
  private boolean disconnectIntiated;
  private  InetSocketAddress serverAddress;
  
  private static final long RETRY_TIME = 10L;
  private static final long MAX_RETRY_TIME = 60L;
  private static final int MAX_RETRY_UNTIL_INCR = 30;
  private static final int TOTAL_MAX_RETRY_COUNT = 360;

  private int retryCount = 0;

  private final Logger logger = LoggerFactory.getLogger("client.ClientConnector");

  public ClientConnector(InetSocketAddress serverAddress) {
    this.serverAddress= serverAddress;
    this.host = serverAddress.getHostString();
    this.port = serverAddress.getPort();
  }

  public void configureConnection() {
    workerGroup = new NioEventLoopGroup();
    bootstrap = new Bootstrap();
    bootstrap.group(workerGroup);
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.handler(new ClientChannelHandler(this));
    bootstrap.option(ChannelOption.TCP_NODELAY, true);
    bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
  }

  public void connect() {

    channelFuture = bootstrap.connect(serverAddress);
    try {
      channelFuture.await();
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted trying to connect");
    }
    if (!channelFuture.isSuccess()) {
      channelFuture.channel().eventLoop().schedule(new Runnable() {
        @Override
        public void run() {
          connect();
        }
      }, calculateRetryTime(), TimeUnit.SECONDS);
    } else {
      logger.info("connect >  Client connected to {} on port {}", host, port);
      retryCount = 0;
      disconnectIntiated = false;
      channel = channelFuture.channel();
      channel.closeFuture().addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {

          if (!disconnectIntiated) {
            logger.warn("connect.closeFuture > Client connection lost, initiating reconnect logic... ");
            connect();
          } else {
            workerGroup.shutdownGracefully();
            logger.info("connect.closeFuture > Client fully diconnected");
          }
        }
      });
      handler = channel.pipeline().get(ClientDataHandler.class);

    }

  }

  public void disconnect() throws IOException {
    if (channel == null || !channel.isOpen()) {
      return;
    }
    disconnectIntiated = true;
    logger.info("disconnect > disconnect explicitly called");
    channel.close().awaitUninterruptibly(1, TimeUnit.SECONDS);

  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public boolean isActive() {
    return (channel.isOpen() || channel.isActive());
  }

  public Channel getChannel() {
    return channel;
  }

  public void sendData(int count) {

    if (null == channel || !channel.isOpen()) {
      logger.warn("sendData > tried to send data on null or closed channel");
      return;
    }

    handler.sendData(count);

  }

  /**
   * @return Will return the time in milliseconds. Returns the {@code RETRY_TIME} for the specified
   *         {@code retryCount}. After this limit is reached it will then only return the time
   *         specified with {@code MAX_RETRY_TIME}
   * 
   */
  private long calculateRetryTime() {
    if (retryCount >= MAX_RETRY_UNTIL_INCR) {
      logger.debug("calculateRetryTime > {}>={} setting {} as retry interval: total time retrying {} seconds",
          retryCount, MAX_RETRY_UNTIL_INCR, MAX_RETRY_TIME,
          ((retryCount - MAX_RETRY_UNTIL_INCR) * MAX_RETRY_TIME) + (MAX_RETRY_UNTIL_INCR * RETRY_TIME));
      retryCount++;
      return MAX_RETRY_TIME;
    } else {
      logger.debug("calculateRetryTime > {}<{} setting {} seconds as retry interval: total time retrying {} seconds",
          retryCount, MAX_RETRY_UNTIL_INCR, RETRY_TIME, RETRY_TIME * retryCount);
      retryCount++;
      return RETRY_TIME;
    }
  }

  public static void main(String... args) {

    try {
      
      InetSocketAddress serverAddress = new InetSocketAddress("localhost",26002);
      
      ClientConnector test = new ClientConnector(serverAddress);
      test.configureConnection();
      test.connect();
      int count = 0;
      while (true) {
        try {
          count++;
          test.sendData(count);
          Thread.sleep(1000);

         /* if (count == 100) {

            break;
          }*/
        } catch (Exception es) {

        }

      }

     // test.disconnect();

    } catch (Exception e) {

      e.printStackTrace();
    }

  }

}

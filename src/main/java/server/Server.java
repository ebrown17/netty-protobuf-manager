package server;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

public class Server {

  private int port;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ServerBootstrap bootstrap;
  private Channel channel;
  private InetSocketAddress socketAddress;
  private ChannelFutureListener notNormalShutdown;
  private final Logger logger = LoggerFactory.getLogger(Server.class);

  public Server(InetSocketAddress socketAddress) {
    this.socketAddress = socketAddress;
  }

  public void configure() {
    ThreadFactory threadFactory = new DefaultThreadFactory("server");
    // the bossGroup will handle all incoming connections and pass them off to the workerGroup
    // the workerGroup will be used for processing all channels
    // 0 forces netty to use default number of threads which is max number of processors * 2
    bossGroup = new NioEventLoopGroup(1, threadFactory);
    workerGroup = new NioEventLoopGroup(0, threadFactory);

    bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup);
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.childHandler(new ServerChannelInitializer());
    bootstrap.option(ChannelOption.SO_BACKLOG, 25);
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
  }

  public void startServer() {

    if (isActive()) {
      logger.warn("startServer already active don't try to bind to port again ");
      return;
    }

    ChannelFuture channelFuture = bootstrap.bind(socketAddress);

    try {
      channelFuture.await();
    }
    catch (InterruptedException e) {
      throw new RuntimeException("Interrupted waiting for bind");
    }
    if (!channelFuture.isSuccess()) {
      logger.error("startServer Server failed to bind to port {} ", port);
    }
    else {
      logger.debug("startServer Server listening for connections... ");
      channel = channelFuture.channel();

      channel.closeFuture().addListener(notNormalShutdown = future -> {
        logger.info("startServer.closeFuture shutdownServer Not explicitly called {}", future.cause());
        channel.close();
      });
    }
  }

  public void shutdownServer() {
    logger.info("shutdownServer explicitly called Shutting down server ");

    if (!isActive()) {
      logger.info("shutdownServer server already shutdown ");
      return;
    }

    channel.closeFuture().removeListener(notNormalShutdown);

    channel.close().addListener((ChannelFutureListener) future -> {
      if (!future.isSuccess()) {
        logger.warn("shutdownServer Server shutdown error {}", future.cause());
      }
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
      logger.info("shutdownServer server fully shutdown");
    });

  }

  public boolean isActive() {
    return (channel != null && (channel.isOpen() || channel.isActive()));
  }

  public String getServerName() {
    return socketAddress.getHostString();
  }
}

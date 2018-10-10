package server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

public class Server {

  private int port;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ServerBootstrap bootstrap;
  private ChannelFutureListener closeListener;
  private HashMap<Integer, Channel> channelMap = new HashMap<Integer, Channel>();
  private HashMap<Integer, InetSocketAddress> sockMap;

  private final Logger logger = LoggerFactory.getLogger(Server.class);

  public Server() {
    configure();
  }

  private void configure() {

    ThreadFactory threadFactory = new DefaultThreadFactory("server");
    // the bossGroup will handle all incoming connections and pass them off to the workerGroup
    // the workerGroup will be used for processing all channels
    // 0 forces netty to use default number of threads which is max number of processors * 2
    bossGroup = new NioEventLoopGroup(1, threadFactory);
    workerGroup = new NioEventLoopGroup(0, threadFactory);
    bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup);
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.option(ChannelOption.SO_BACKLOG, 25);
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
  }

  public <T extends ChannelInitializer<SocketChannel>> void addChannel(int port, Class<T> channelInitializer) {
    if (sockMap == null) {
      sockMap = new HashMap<Integer, InetSocketAddress>(5);
    }
    try {
      if (sockMap.get(port) == null) {
        ChannelInitializer<SocketChannel> channelInit = channelInitializer.newInstance();
        InetSocketAddress sockAddr = new InetSocketAddress(port);
        sockMap.put(port, sockAddr);
        bootstrap.childHandler(channelInit);
      }
      else {
        logger.warn("addChannel port {} already added to server bootstrap; not adding.", port);
      }
    }
    catch (Exception e) {
      logger.error("addChannel {}", e.getMessage());
    }


  }

  public void startServer() {

    sockMap.forEach((port, socketAddress) -> {

      if (isActive(port)) {
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
        Channel channel = channelFuture.channel();

        channel.closeFuture().addListener(future -> {
          logger.info("channel {} shutdown not explicitly called, channel closed with {}", port, future.cause());
          channel.close();
        });

        channelMap.put(port, channel);
      }

    });

  }

  //TODO
  public void startChannel() {
  }

  //TODO
  public void stopChannel() {
  }

  public void shutdownServer() {
    logger.info("shutdownServer explicitly called Shutting down server ");

    channelMap.forEach((port,channel)->{
      if (!isActive(port)) {
        logger.info("shutdownServer channel {} already shutdown ",port);
        // return in lamba only stops current iteration
        return;
      }
      //TODO
      channel.closeFuture().removeListener(ChannelFutureListener.class);

      channel.close().addListener(future -> {
        if (!future.isSuccess()) {
          logger.warn("shutdownServer channel {} shutdown error {}",port, future.cause());
        }
        logger.info("shutdownServer channel {} fully shutdown",port);
      });

    });

    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
    logger.info("shutdownServer server fully shutdown");





  }

  public boolean isActive(int port) {
    Channel channel = channelMap.get(port);
    return (channel != null && (channel.isOpen() || channel.isActive()));
  }

  public boolean allActive() {

    boolean allActive = true;

    for (Map.Entry<Integer, Channel> entry : channelMap.entrySet()) {
      Integer key = entry.getKey();
      Channel channel = entry.getValue();
      if (channel == null && (!channel.isOpen() || !channel.isActive())) {
        allActive = false;
      }
    }

    return allActive;
  }

  /*public String getServerName() {
    return socketAddress.getHostString();
  }*/

  public static void main(String... args) {

    Server server = new Server();
    server.addChannel(6000, ServerChannelMessageInitializer.class);
    server.startServer();

  }


}


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

  private boolean allActive;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ServerBootstrap bootstrap;
  private HashMap<Integer, Channel> channelMap = new HashMap<Integer, Channel>();
  private HashMap<Integer, ArrayList<ChannelFutureListener>> channelListenerMap = new HashMap<Integer, ArrayList<ChannelFutureListener>>(5);
  private HashMap<Integer, InetSocketAddress> portAddressMap = new HashMap<Integer, InetSocketAddress>(5);

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

    try {
      if (portAddressMap.get(port) == null) {
        ChannelInitializer<SocketChannel> channelInit = channelInitializer.newInstance();
        InetSocketAddress sockAddr = new InetSocketAddress(port);
        portAddressMap.put(port, sockAddr);
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

  public void startChannel(int port) {
    InetSocketAddress socketAddress = portAddressMap.get(port);
    if (socketAddress != null) {

      if (isActive(port)) {
        logger.warn("startChannel called or an already active channel: {}", port);
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
        logger.error("startChannel failed to bind to port {} ", port);
      }
      else {
        logger.debug("startChannel now listening for connections on port {}", port);
        Channel channel = channelFuture.channel();

        ChannelFutureListener closeListener = future -> {
          logger.info("channel {} closed unexpectedly, closed with {}", port, future.cause());
          channel.close();
        };

        ArrayList<ChannelFutureListener> listenerList = channelListenerMap.get(port);
        if (listenerList == null) {
          listenerList = new ArrayList<ChannelFutureListener>(5);
        }
        listenerList.add(closeListener);

        channel.closeFuture().addListener(closeListener);

        channelMap.put(port, channel);
        channelListenerMap.put(port, listenerList);
      }
    }
    else {
      logger.error("startChannel called with unconfigured port: {}", port);
    }
  }

  //TODO
  public void closeChannel(int port) {
    Channel channel = channelMap.get(port);
    if (channel != null) {
      if (!isActive(channel)) {
        logger.info("closeChannel called on already closed channel {}", port);
        return;
      }

      channelListenerMap.get(port).forEach(listener -> channel.closeFuture().removeListener(listener));

      channel.close().addListener(future -> {
        if (!future.isSuccess()) {
          logger.warn("closeChannel channel {} error {}", port, future.cause());
        }
        logger.info("closeChannel channel {} now closed", port);
      });
    }
    else {
      logger.warn("closeChannel called with null channel {}", port);
    }

  }

  public void startServer() {
    portAddressMap.keySet().stream().forEach((port) -> startChannel(port));
  }

  public void shutdownServer() {
    logger.info("shutdownServer explicitly called Shutting down server ");

    channelMap.keySet().stream().forEach((port) -> closeChannel(port));

    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
    logger.info("shutdownServer server fully shutdown");
  }

  public boolean isActive(int port) {
    Channel channel = channelMap.get(port);
    return (channel != null && (channel.isOpen() || channel.isActive()));
  }

  public boolean isActive(Channel channel) {
    return (channel != null && (channel.isOpen() || channel.isActive()));
  }

  public boolean allActive() {

    allActive = true;

    channelMap.values().stream().forEach(channel -> {
      if (channel == null || (!channel.isOpen() || !channel.isActive())) {
        allActive = false;
      }
    });

    return allActive;
  }

  /*public String getServerName() {
    return socketAddress.getHostString();
  }*/

  public static void main(String... args) {

    Server server = new Server();
    server.addChannel(6000, ServerChannelMessageInitializer.class);
    server.addChannel(6001, ServerChannelMessageInitializer.class);
    server.startServer();

    try {
      Thread.sleep(5000);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }

    server.closeChannel(6001);
    LoggerFactory.getLogger("main").info("all active {}",server.allActive());

  }


}


package server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerListener {

  private int port;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ServerBootstrap bootstrap;
  private ChannelFuture channelFuture;
  private Channel channel;
  private final Logger logger = LoggerFactory.getLogger("server.ServerListener");

  public ServerListener(int port) {
    this.port = port;
  }

  public void configureServer() {
    bossGroup = new NioEventLoopGroup();
    workerGroup = new NioEventLoopGroup();
    bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup);
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.childHandler(new ServerChannelInitializer(this));
    bootstrap.option(ChannelOption.SO_BACKLOG, 25);
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
  }

  public void startServer() {

    channelFuture = bootstrap.bind(port);

    try {
      channelFuture.await();
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted waiting for bind");
    }
    if (!channelFuture.isSuccess()) {
      logger.debug("startServer > Server failed to bind to port {} ", port);
    } else {
      channel = channelFuture.channel();
    }

    logger.debug("startServer > Server listening for connections... ");

  }

  public void shutdownServer() {
    logger.debug("shutdownServer > Shutting down server ");

    if (channel == null || !channel.isOpen()) {
      return;
    }

    channel.close().addListener(new ChannelFutureListener() {

      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
          logger.warn("shutdownServer > Server shutdown error {}", future.cause());
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

      }

    });

  }

  public void runAsTest() throws InterruptedException {
    logger.debug("runAsTest > Server Starting... ");
    configureServer();
    startServer();
  }

  public static void main(String... args) {

    try {
      ServerListener test = new ServerListener(26002);
      test.runAsTest();
      Thread.sleep(60000);
      test.shutdownServer();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}

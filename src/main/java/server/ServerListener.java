package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerListener {

	private int port;
	private EventLoopGroup serverBossGroup;
	private EventLoopGroup serveWorkGroup;
	private ServerBootstrap bootstrap;
	private ChannelFuture channelFuture;
	private Channel channel;
	private final Logger logger = LoggerFactory.getLogger("server.ServerListener");
	
	public ServerListener(int port) {
		this.port = port;
	}

	public void configureServer() {
		serverBossGroup = new NioEventLoopGroup();
		serveWorkGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		
		bootstrap.group(serverBossGroup, serveWorkGroup)
		.channel(NioServerSocketChannel.class)
		.childHandler(new ServerChannelHandler(this))
		.option(ChannelOption.SO_BACKLOG, 25)
		.childOption(ChannelOption.SO_KEEPALIVE, true);
	}
	
	public void startServer() throws InterruptedException{
		try{
			logger.debug("startServer > Server listening for connections... ");
			channel = bootstrap.bind(port).sync().channel();
			channel.closeFuture().sync();
		}
		finally {
			serverBossGroup.shutdownGracefully();
			serverBossGroup.shutdownGracefully();
		}
	}
	
	public void shutdownServer(){
		logger.debug("shutdownServer > Server listening for connections... ");
		channel.close();
		
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

package server;

import client.ClientConnector;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerListener {

	private int port;
	
	public ServerListener(int port){
		this.port=port;
	}
	
	public void runAsTest() throws InterruptedException {
	    EventLoopGroup bossGroup = new NioEventLoopGroup(); 
	    EventLoopGroup workerGroup = new NioEventLoopGroup();
	    try {
	        ServerBootstrap b = new ServerBootstrap(); 
	        b.group(bossGroup, workerGroup)
	        	.channel(NioServerSocketChannel.class) 
	        	.childHandler(new ServerInitializer())
	        	.option(ChannelOption.SO_BACKLOG, 1)          
	        	.childOption(ChannelOption.SO_KEEPALIVE, true); 
	
	        ChannelFuture f = b.bind(port).sync();	       
	        f.channel().closeFuture().sync();
	        
	    } finally {
	        workerGroup.shutdownGracefully();
	        bossGroup.shutdownGracefully();
	    }
	}
	
	public static void main(String... args){
		System.out.println("Running server...");
		
		try {
			new ServerListener(26002).runAsTest();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

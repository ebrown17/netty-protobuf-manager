package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import retry_logic.ConnectionListener;

public class ClientConnector {

	private String host;
	private int port;
	private EventLoopGroup clientEventLoop;
	private Bootstrap clientBootstrap;
	private ChannelFuture channelFuture;
	private Channel channel;
	public boolean connected = false;
	
	public ClientConnector(String host, int port){
		this.host=host;
		this.port=port;
	}
	
	public void configureConnection(){
		clientEventLoop = new NioEventLoopGroup();
		clientBootstrap = new Bootstrap();
		
		clientBootstrap.group(clientEventLoop)
			.channel(NioSocketChannel.class)
			.handler(new ClientChannelHandler(this))
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_KEEPALIVE, true);
		
	}
	
	public void connect(){
		channelFuture = clientBootstrap.connect(host,port);
		channelFuture.addListener(new ConnectionListener(this));
		channel = channelFuture.channel();
	}
	
	
	
	public void runAsTest() throws InterruptedException {
		 
		try {
			configureConnection();
			connect(); 	
			while(true){
				if(connected){
					ClientDataHandler handler =  channel.pipeline().get(ClientDataHandler.class);
					
					int count = 1;
					while(connected){
						Thread.sleep(1000);
						handler.sendData(count);
						count++;
					}
				}
				else {
					System.out.println("Did not connect");	
				}
				Thread.sleep(4000);
			}	
	         	
		 }
		 finally {
			 clientEventLoop.shutdownGracefully();
		 }
	}
	
	public static void main(String... args){
		System.out.println("Running client...");
		
		try {
			new ClientConnector("localhost",26002).runAsTest();
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}
	
}

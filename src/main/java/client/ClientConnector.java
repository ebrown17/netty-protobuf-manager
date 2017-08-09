package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientConnector {

	private String host;
	private int port;
	
	public ClientConnector(String host, int port){
		this.host=host;
		this.port=port;
	}
	
	public void run() throws InterruptedException {
		 EventLoopGroup workerGroup = new NioEventLoopGroup();
		 try {
			 Bootstrap bootstrap = new Bootstrap();
	         	bootstrap.group(workerGroup)
	         	.channel(NioSocketChannel.class)
	         	.handler(new ClientInitializer());
	         	
	            // Start the client.
	            ChannelFuture f = bootstrap.connect(host, port).sync(); // (5)
	            
	            ClientDataHandler sendText =  f.channel().pipeline().get(ClientDataHandler.class);	           
	            System.out.println("sending data");
	            int count = 1;
	            while(count < 100){
	            	Thread.sleep(1000);
	            	sendText.sendData(count);
	            	count++;
	            }
	            
	            
	            // Wait until the connection is closed.
	            f.channel().closeFuture().sync();
	         	
		 }
		 finally {
			 workerGroup.shutdownGracefully();
		 }
	}
	
}

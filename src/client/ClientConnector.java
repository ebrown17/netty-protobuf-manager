package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ClientConnector {

	private String host;
	private int port;
	
	ClientConnector(String host, int port){
		this.host=host;
		this.port=port;
	}
	
	public void run() {
		 EventLoopGroup workerGroup = new NioEventLoopGroup();
		 try {
			 Bootstrap bootstrap = new Bootstrap();
	         bootstrap.group(workerGroup)
	         .channel(NioServerSocketChannel.class)
	         .handler(new ClientInitializer());
		 }
		 finally {
			 
		 }
	}
	
}

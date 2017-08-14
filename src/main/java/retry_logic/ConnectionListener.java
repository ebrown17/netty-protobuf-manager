package retry_logic;

import java.util.concurrent.TimeUnit;

import client.ClientConnector;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;


public class ConnectionListener implements ChannelFutureListener{

	private ClientConnector client;
	public ConnectionListener(ClientConnector client){
		this.client = client;
	}
	
	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if(future.isSuccess()){
			client.connected = true;
		}
		else if(!future.isSuccess()){
			future.channel().eventLoop().schedule(new Runnable(){
				@Override
				public void run() {
					client.connect();
				}	
			}, 5L,TimeUnit.SECONDS);
		}
		
	}

}

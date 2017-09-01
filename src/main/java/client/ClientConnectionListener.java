package client;

import java.util.concurrent.TimeUnit;

import client.ClientConnector;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;


public class ClientConnectionListener implements ChannelFutureListener {

  private Client client;
  private long reconnectInterval;

  public ClientConnectionListener(Client client, long reconnectInterval) {
    this.client = client;
    this.reconnectInterval = reconnectInterval;
  }

  @Override
  public void operationComplete(ChannelFuture future) throws Exception {
    if (future.isSuccess()) {
      // client.setConnection(true);
    } else if (!future.isSuccess()) {
      future.channel().eventLoop().schedule(new Runnable() {
        @Override
        public void run() {
          try {
			client.connect();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
      }, reconnectInterval, TimeUnit.SECONDS);
    }

  }

}

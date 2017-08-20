package client;

import java.util.concurrent.TimeUnit;

import client.ClientConnector;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;


public class ClientConnectionListener implements ChannelFutureListener {

  private ClientConnector client;
  private long reconnectInterval;

  public ClientConnectionListener(ClientConnector client, long reconnectInterval) {
    this.client = client;
    this.reconnectInterval = reconnectInterval;
  }

  // TODO throw proper exception and handle correctly
  @Override
  public void operationComplete(ChannelFuture future) throws Exception {
    if (future.isSuccess()) {
      // client.setConnection(true);
    } else if (!future.isSuccess()) {
      future.channel().eventLoop().schedule(new Runnable() {
        @Override
        public void run() {
          client.connect();
        }
      }, reconnectInterval, TimeUnit.SECONDS);
    }

  }

}

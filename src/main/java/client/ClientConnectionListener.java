package client;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;


public class ClientConnectionListener implements ChannelFutureListener {

  private Client client;
  private boolean attemptingConnection = false;
  
  public ClientConnectionListener(Client client) {
    this.client = client;
  }

  @Override
  public void operationComplete(ChannelFuture future) throws Exception {
    if (future.isSuccess()) {
      attemptingConnection = false;
      client.connectionEstablished(future);
    }
    else {
      future.channel().close();
      future.channel().eventLoop().schedule(() -> {
        try {
          client.connect();
        }
        catch (InterruptedException e) {
          // TODO test to see what happens if this is reached
          throw new RuntimeException("Interrupted trying to connect");
        }

      }, client.calculateRetryTime(), TimeUnit.SECONDS);
    }

  }

  protected void setAttemptingConnection(boolean attemptingConnection) {
    this.attemptingConnection = attemptingConnection;
  }

  protected boolean isAttemptingConnection() {
    return attemptingConnection;
  }

}

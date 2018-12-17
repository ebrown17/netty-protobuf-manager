package protocol.protomessage.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.TimeUnit;


public class ClientConnectionListener implements ChannelFutureListener {

  private final Client client;
  private boolean attemptingConnection = true;

  public ClientConnectionListener(Client client) {
    this.client = client;
  }

  @Override
  public void operationComplete(ChannelFuture future) throws Exception {
    if (future.isSuccess()) {
      clearAttemptingConnection();
      client.connectionEstablished(future);
    }
    else {
      future.channel().close();
      future.channel().eventLoop().schedule(() -> {
        try {
          clearAttemptingConnection();
          client.connect();
        }
        catch (InterruptedException e) {
          // TODO test to see what happens if this is reached
          throw new RuntimeException("ClientConnectionListener interrupted while trying to connect");
        }
      }, client.calculateRetryTime(), TimeUnit.SECONDS);
    }

  }

  protected void setAttemptingConnection() {
    attemptingConnection = true;
  }

  private void clearAttemptingConnection() {
    attemptingConnection = false;
  }

  protected boolean isAttemptingConnection() {
    return attemptingConnection;
  }

}

package common.Client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ClientClosedConnectionListener implements ChannelFutureListener {
  private final static Logger logger = LoggerFactory.getLogger(ClientClosedConnectionListener.class);
  private Client client;

  public ClientClosedConnectionListener(Client client) {
    this.client = client;
  }

  @Override
  public void operationComplete(ChannelFuture future) throws Exception {
    if (client.isDisconnectInitiated()) {
      future.channel().close().awaitUninterruptibly(1, TimeUnit.SECONDS);
      logger.info("connect.closeFuture > Client fully disconnected");
    }
    else {
      future.channel().eventLoop().schedule(() -> {
        try {
          client.connect();
        }
        catch (InterruptedException e) {
          logger.error("operationComplete {}",e.getMessage(),e);
          throw new RuntimeException("Interrupted trying to connect");
        }
      }, client.calculateRetryTime(), TimeUnit.SECONDS);
    }
  }

}

package common;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.atomic.AtomicLong;

public abstract class TransceiverChannel<I> extends ChannelInitializer<SocketChannel> {
  public static final int READ_IDLE_TIME = 10;
  public static final int HEARTBEAT_MISS_LIMIT = 2;
  public static final int WRITE_IDLE_TIME = 5;

  public static final AtomicLong channelIds = new AtomicLong(0L);
  public final Transceiver<I> transceiver;

   public TransceiverChannel(Transceiver<I> transceiver){
    this.transceiver = transceiver;
   }


}

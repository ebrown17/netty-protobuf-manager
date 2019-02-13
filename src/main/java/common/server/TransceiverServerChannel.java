package common.server;

import common.Transceiver;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.atomic.AtomicLong;

public abstract class TransceiverServerChannel<I> extends ChannelInitializer<SocketChannel> {
    protected static final int WRITE_IDLE_TIME = 5;

    protected static final AtomicLong channelIds = new AtomicLong(0L);
    protected final Transceiver<I> transceiver;

    public TransceiverServerChannel(Transceiver<I> transceiver) {
        this.transceiver = transceiver;
    }


}

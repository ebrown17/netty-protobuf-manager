package common.Client;

import common.Transceiver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

import java.util.concurrent.atomic.AtomicLong;

public abstract class TransceiverClientChannel<I> extends ChannelInitializer<Channel> {
    protected static final int READ_IDLE_TIME = 10;
    protected static final int HEARTBEAT_MISS_LIMIT = 2;
    protected static final AtomicLong channelIds = new AtomicLong(0L);
    protected final Transceiver<I> transceiver;

    public TransceiverClientChannel(Transceiver<I> transceiver){
        this.transceiver = transceiver;
    }


}
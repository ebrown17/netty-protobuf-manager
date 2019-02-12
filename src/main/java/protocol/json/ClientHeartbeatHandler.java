package protocol.json;

import common.HeartbeatReceiverHandler;

public class ClientHeartbeatHandler extends HeartbeatReceiverHandler {
    private static final int READ_IDLE_TIME = 10;
    private static final int HEARTBEAT_MISS_LIMIT = 2;
    /**
     * HeartbeatRecieverHandler expects to be receiving heartbeat message.
     * <p>
     * If the heartbeat miss limit is reached the channel is closed and the client's reconnect logic is
     * started.
     * <p>
     * By default every channel read will reset the miss count. To only reset on a heartbeat, you must override the channelRead
     * method and add appropriate logic. The method
     * {@link HeartbeatReceiverHandler#resetMissCounter() resetMissCounter } can be called reset the miss count.
     */
    public ClientHeartbeatHandler() {
        super(READ_IDLE_TIME, 0, 0, READ_IDLE_TIME, HEARTBEAT_MISS_LIMIT);
    }
}

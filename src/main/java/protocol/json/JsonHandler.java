package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import common.Handler;
import common.Transceiver;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonHandler extends Handler<JsonNode> {

    private ChannelHandlerContext ctx;
    private final static Logger logger = LoggerFactory.getLogger(JsonHandler.class);
    private final Transceiver<JsonNode> transceiver;
    private final Long handlerId;

    JsonHandler(Long id, Transceiver<JsonNode> transceiver) {
        super(id,transceiver);
        this.handlerId = id;
        this.transceiver = transceiver;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonNode msg) throws Exception {
        logger.trace("channelRead0 from: {} received: {}", ctx.channel().remoteAddress(), msg);
        String types = msg.get("eventType").asText();
        if(types.equals("heartbeat")){
            ctx.fireChannelRead(msg);
        }
        else{
            transceiver.handleMessage(remoteAddress, msg);
        }
    }
}

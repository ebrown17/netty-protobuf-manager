package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import common.Handler;
import common.Transceiver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

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
        logger.info("channelRead0 {} sent: {}", ctx.channel().remoteAddress(), msg);
        transceiver.handleMessage(remoteAddress, msg);
    }


}

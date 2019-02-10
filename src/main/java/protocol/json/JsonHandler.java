package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class JsonHandler extends SimpleChannelInboundHandler<JsonNode> {

    private ChannelHandlerContext ctx;
    private final static Logger logger = LoggerFactory.getLogger(JsonHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonNode msg) throws Exception {
        logger.info("channelRead0 {} sent: {}", ctx.channel().remoteAddress(), msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive remote peer: {} connected", ctx.channel().remoteAddress());
        this.ctx = ctx;
    }

    public void sendMessage(JsonNode message) {
        if (ctx != null && ctx.channel().isActive() && ctx.channel().isWritable()) {
            logger.info("sendMessage {} to {} written to wire",message,ctx.channel().remoteAddress());
            ctx.writeAndFlush(message);
        }
        else {
            logger.warn("sendMessage called when channel not active or writable");
        }
    }
}

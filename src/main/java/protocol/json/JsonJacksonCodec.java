package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.CharBuffer;
import java.util.List;

public class JsonJacksonCodec extends ByteToMessageCodec<JsonNode> {

    private final static Logger logger = LoggerFactory.getLogger(JsonJacksonCodec.class);

    private ObjectMapper mapper;

    public JsonJacksonCodec() {
        mapper = new ObjectMapper();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, JsonNode msg, ByteBuf out) throws Exception {
        out.writeBytes(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg.toString()), CharsetUtil.UTF_8));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);
        out.add(mapper.readValue((InputStream) byteBufInputStream, JsonNode.class));
    }

}
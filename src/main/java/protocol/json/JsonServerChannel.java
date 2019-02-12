package protocol.json;

import common.Transceiver;
import common.TransceiverChannel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import com.fasterxml.jackson.databind.JsonNode;
import protocol.protobuf.ExceptionHandler;

public class JsonServerChannel extends TransceiverChannel<JsonNode> {

  Transceiver<JsonNode> transceiver;

  public JsonServerChannel(Transceiver<JsonNode> transceiver){
    super(transceiver);
    this.transceiver = transceiver;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();
    p.addLast("jsonCodec", new JsonJacksonCodec());
    p.addLast("jsonHandler", new JsonHandler(channelIds.incrementAndGet(),transceiver));
    p.addLast("heartbeatHandler",new ServerHeartbeatHandler(transceiver));
    p.addLast(new ExceptionHandler());
  }
}

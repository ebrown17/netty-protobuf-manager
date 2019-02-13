package protocol.json;

import common.ExceptionHandler;
import common.Transceiver;
import common.server.TransceiverServerChannel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.timeout.IdleStateHandler;

public class JsonServerChannel extends TransceiverServerChannel<JsonNode> {
  private final Transceiver<JsonNode> transceiver;

  public JsonServerChannel(Transceiver<JsonNode> transceiver){
    super(transceiver);
    this.transceiver = transceiver;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();
    p.addLast("jsonCodec", new JsonJacksonCodec());
    p.addLast("jsonHandler", new JsonHandler(channelIds.incrementAndGet(),transceiver));
    p.addLast("idleStateHandler",new IdleStateHandler(0,WRITE_IDLE_TIME,0));
    p.addLast("heartbeatHandler",new JsonHeartbeatProducer(transceiver));
    p.addLast(new ExceptionHandler());
  }
}

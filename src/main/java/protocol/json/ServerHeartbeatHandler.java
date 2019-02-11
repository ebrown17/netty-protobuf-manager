package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.HeartbeatProducerHandler;
import common.Transceiver;

import java.util.Date;

public class ServerHeartbeatHandler extends HeartbeatProducerHandler<JsonNode> {
  private static final int WRITE_IDLE_TIME = 5;
  private final ObjectMapper mapper;
  private final ObjectNode heartbeat;

  public ServerHeartbeatHandler(Transceiver<JsonNode> transceiver){
    super(0,WRITE_IDLE_TIME,0,transceiver);
    mapper = new ObjectMapper();
    heartbeat = mapper.createObjectNode();
  }

  @Override
  public JsonNode generateHeartBeat() {
    heartbeat.put("heartbeat",new Date().getTime());
    return heartbeat;
  }
}

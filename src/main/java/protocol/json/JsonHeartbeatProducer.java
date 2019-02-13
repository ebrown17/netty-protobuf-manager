package protocol.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.HeartbeatProducerHandler;
import common.Transceiver;

import java.util.Date;

public class JsonHeartbeatProducer extends HeartbeatProducerHandler<JsonNode> {

  private final ObjectMapper mapper;
  private final ObjectNode heartbeat;
  private final ObjectNode parameters;

  public JsonHeartbeatProducer(Transceiver<JsonNode> transceiver){
    super(transceiver);
    mapper = new ObjectMapper();
    heartbeat = mapper.createObjectNode();
    parameters = mapper.createObjectNode();
  }

  @Override
  public JsonNode generateHeartBeat() {
    heartbeat.put("eventType","heartbeat");
    parameters.put("sent",new Date().getTime());
    heartbeat.set("parameters",parameters);
    return heartbeat;
  }
}

package protocol.protomessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transceiver.MessageTransceiver;

public class ServerMessageHandler extends MessageHandler {
  private final Logger logger = LoggerFactory.getLogger(ServerMessageHandler.class);

  ServerMessageHandler(Long id,MessageTransceiver transceiver){
    super(id,transceiver);
  }

}

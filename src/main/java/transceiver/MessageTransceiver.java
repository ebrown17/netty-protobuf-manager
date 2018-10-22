package transceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtoMessages.ProtoMessage;
import server.ServerMessageHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class MessageTransceiver {
  private final Logger logger = LoggerFactory.getLogger(MessageTransceiver.class);
  private final ConcurrentHashMap<Long, ServerMessageHandler> registeredHandlers;
  private final ConcurrentHashMap<InetSocketAddress, ServerMessageHandler> activeHandlers;
  private final ConcurrentHashMap<InetSocketAddress, ServerMessageHandler> inactiveHandlers;

  public MessageTransceiver() {
    registeredHandlers = new ConcurrentHashMap<Long, ServerMessageHandler>();
    activeHandlers = new ConcurrentHashMap<InetSocketAddress, ServerMessageHandler>();
    inactiveHandlers = new ConcurrentHashMap<InetSocketAddress, ServerMessageHandler>();
  }

  public void registerHandler(Long handlerId, ServerMessageHandler handler) {
    logger.info("registerHandler handler registered with Id: {}", handlerId);
    registeredHandlers.put(handlerId, handler);

  }

  public void registerHandlerActive(InetSocketAddress addr, ServerMessageHandler handler) {
    logger.info("registerHandlerActive handler active with addr: {}", addr);
    inactiveHandlers.remove(addr);
    activeHandlers.put(addr, handler);
  }

  public void registerHandlerInActive(InetSocketAddress addr, ServerMessageHandler handler) {
    logger.info("registerHandlerInActive handler inactive with addr: {}", addr);
    activeHandlers.remove(addr);
    inactiveHandlers.put(addr, handler);
  }

  public void handleMessage(InetSocketAddress addr,ProtoMessage msg) {
    logger.debug("handleMessage from {} with {}",addr, msg);
    sendMessage(addr,msg);
  }

  public void sendMessage(InetSocketAddress addr,ProtoMessage msg) {
    logger.debug("sendMessage to addr: {} with {}", addr,msg);
    activeHandlers.get(addr).sendMessage(msg);
  }

  public void broadCastMessage(ProtoMessage msg){
      for(ServerMessageHandler handler: activeHandlers.values()){
        handler.sendMessage(msg);
      }

  }

}

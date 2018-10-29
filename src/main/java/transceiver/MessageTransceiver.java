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
  private final Object activeLock = new Object();

  public MessageTransceiver() {
    registeredHandlers = new ConcurrentHashMap<Long, ServerMessageHandler>();
    activeHandlers = new ConcurrentHashMap<InetSocketAddress, ServerMessageHandler>();
  }

  public void registerHandler(Long handlerId, ServerMessageHandler handler) {
    logger.info("registerHandler handler registered with Id: {}", handlerId);
    registeredHandlers.putIfAbsent(handlerId, handler);

  }

  public void registerHandlerActive(InetSocketAddress addr, ServerMessageHandler handler) {
    logger.info("registerHandlerActive handler active with addr: {}", addr);
    synchronized (activeLock){
      activeHandlers.putIfAbsent(addr, handler);
    }
  }

  public void registerHandlerInActive(InetSocketAddress addr, Long handlerId) {
    logger.info("registerHandlerInActive handler inactive with addr: {}", addr);
    synchronized (activeLock){
      activeHandlers.remove(addr);
    }
    registeredHandlers.remove(handlerId);
  }

  public void handleMessage(InetSocketAddress addr,ProtoMessage msg) {
    logger.debug("handleMessage from {} with {}",addr, msg);

  }

  public void sendMessage(InetSocketAddress addr,ProtoMessage msg) {
    logger.debug("sendMessage to addr: {} with {}", addr,msg);
    ServerMessageHandler handler = activeHandlers.get(addr);
    if(handler != null){
      handler.sendMessage(msg);
    }
  }

  public void broadCastMessage(ProtoMessage msg){
    synchronized (activeLock){
      for(ServerMessageHandler handler: activeHandlers.values()){
        handler.sendMessage(msg);
      }
    }

  }

}

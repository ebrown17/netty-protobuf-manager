package protocol.protomessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtoMessages.ProtoMessage;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class MessageTransceiver {
  private final Logger logger = LoggerFactory.getLogger(MessageTransceiver.class);
  private final ConcurrentHashMap<InetSocketAddress, MessageHandler> activeHandlers;
  private final Object activeLock = new Object();

  public MessageTransceiver() {
    activeHandlers = new ConcurrentHashMap<InetSocketAddress, MessageHandler>();
  }

  public void handlerActive(InetSocketAddress addr, MessageHandler handler) {
    logger.info("registerHandlerActive handler active with addr: {}", addr);
    synchronized (activeLock){
      activeHandlers.putIfAbsent(addr, handler);
    }
  }

  public void handlerInActive(InetSocketAddress addr) {
    logger.info("registerHandlerInActive handler inactive with addr: {}", addr);
    synchronized (activeLock){
      activeHandlers.remove(addr);
    }
  }

  public void handleMessage(InetSocketAddress addr,ProtoMessage msg) {
    logger.debug("handleMessage from {} with {}",addr, msg);

  }

  public void sendMessage(InetSocketAddress addr,ProtoMessage msg) {
    logger.trace("sendMessage to addr: {} with {}", addr,msg);
    MessageHandler handler = activeHandlers.get(addr);
    if(handler != null){
      handler.sendMessage(msg);
    }
  }

  public void broadCastMessage(ProtoMessage msg){
    synchronized (activeLock){
      for(MessageHandler handler: activeHandlers.values()){
        handler.sendMessage(msg);
      }
    }

  }

}

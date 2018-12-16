package protocol.protomessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtoMessages.ProtoMessage;
import protocol.protomessage.client.Client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MessageTransceiver {
  private final static Logger logger = LoggerFactory.getLogger(MessageTransceiver.class);
  private final ConcurrentHashMap<InetSocketAddress, MessageHandler> activeHandlers;
  private final ConcurrentHashMap<InetSocketAddress, MessageReader> channelReaders;
  private final ArrayList<MessageHandlerListener> handlerListeners;
  private final Object activeLock = new Object();
  private final int channelPort;

  public MessageTransceiver(int channelPort) {
    activeHandlers = new ConcurrentHashMap<InetSocketAddress, MessageHandler>();
    channelReaders = new ConcurrentHashMap<InetSocketAddress, MessageReader>();
    handlerListeners = new ArrayList<MessageHandlerListener>();
    this.channelPort = channelPort;
  }

  protected void handlerActive(InetSocketAddress addr, MessageHandler handler) {
    logger.info("registerHandlerActive handler active with addr: {}", addr);
    synchronized (activeLock) {
      MessageHandler activeHandler = activeHandlers.get(addr);
      if(activeHandler == null){
        activeHandlers.putIfAbsent(addr,handler);
        handlerListeners.forEach(listener -> listener.registerActiveHandler(channelPort, addr));
      }
    }
  }

  protected void handlerInActive(InetSocketAddress addr) {
    logger.info("registerHandlerInActive handler inactive with addr: {}", addr);
    synchronized (activeLock) {
      activeHandlers.remove(addr);
      handlerListeners.forEach(listener -> listener.registerInActiveHandler(channelPort, addr));
    }
  }

  protected void handleMessage(InetSocketAddress addr, ProtoMessage msg) {
    logger.trace("handleMessage from {} with {}", addr, msg);
    MessageReader reader = channelReaders.get(addr);
    if(reader != null){
      reader.readMessage(addr,msg);
    }
  }

  public void registerChannelReader(InetSocketAddress addr, MessageReader reader){
      channelReaders.putIfAbsent(addr,reader);
  }

  public void registerHandlerActivityListener(MessageHandlerListener listener){
    if(!handlerListeners.contains(listener)){
      handlerListeners.add(listener);
    }
  }

  public void sendMessage(InetSocketAddress addr, ProtoMessage msg) {
    synchronized (activeLock) {
      logger.trace("sendMessage to addr: {} with {}", addr, msg);
      MessageHandler handler = activeHandlers.get(addr);
      if (handler != null) {
        handler.sendMessage(msg);
      }
    }
  }

  public void broadcastMessage(ProtoMessage msg) {
    synchronized (activeLock) {
      for (MessageHandler handler : activeHandlers.values()) {
        handler.sendMessage(msg);
      }
    }

  }

}

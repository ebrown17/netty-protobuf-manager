package comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Transceiver<I> {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final ConcurrentHashMap<InetSocketAddress, Handler<Class<? extends I>>> activeHandlers;
  private final ConcurrentHashMap<InetSocketAddress, Reader<Class<? extends I>>> channelReaders;
  private final ArrayList<HandlerListener<Class<? extends I>>> handlerListeners;
  private final Object activeLock = new Object();
  private final int channelPort;

  public Transceiver(int channelPort) {
    activeHandlers = new ConcurrentHashMap<InetSocketAddress, Handler<Class<? extends I>>>();
    channelReaders = new ConcurrentHashMap<InetSocketAddress, Reader<Class<? extends I>>>();
    handlerListeners = new ArrayList<HandlerListener<Class<? extends I>>>();
    this.channelPort = channelPort;
  }

  protected void handlerActive(InetSocketAddress addr, Handler<Class<? extends I>> handler) {
    synchronized (activeLock) {
      Handler<Class<? extends I>> activeHandler = activeHandlers.get(addr);
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

  protected void handleMessage(InetSocketAddress addr, Class<? extends I> message) {
    logger.trace("handleMessage from {} with {}", addr, message);
    Reader<Class<? extends I>> reader = channelReaders.get(addr);
    if(reader != null){
      reader.readMessage(addr,message);
    }
  }

  public void registerChannelReader(InetSocketAddress addr, Reader<Class<? extends I>> reader){
    channelReaders.putIfAbsent(addr,reader);
  }

  public void registerHandlerActivityListener(HandlerListener<Class<? extends I>> listener){
    if(!handlerListeners.contains(listener)){
      handlerListeners.add(listener);
    }
  }

  public void sendMessage(InetSocketAddress addr,Class<? extends I>message) {
    synchronized (activeLock) {
      logger.trace("sendMessage to addr: {} with {}", addr, message);
      Handler<Class<? extends I>> handler = activeHandlers.get(addr);
      if (handler != null) {
        handler.sendMessage(message);
      }
    }
  }

  public void broadcastMessage(Class<? extends I> message) {
    synchronized (activeLock) {
      for (Handler<Class<? extends I>> handler : activeHandlers.values()) {
        handler.sendMessage(message);
      }
    }
  }

}

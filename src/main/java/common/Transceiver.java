package common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Transceiver<I> {

  final Logger logger = LoggerFactory.getLogger(getClass());
  final ConcurrentHashMap<InetSocketAddress, Handler<I>> activeHandlers;
  final ConcurrentHashMap<InetSocketAddress, Reader<I>> channelReaders;
  final ArrayList<HandlerListener<I>> handlerListeners;
  final Object activeLock = new Object();
  final int channelPort;

  public Transceiver(int channelPort) {
    activeHandlers = new ConcurrentHashMap<InetSocketAddress, Handler<I>>();
    channelReaders = new ConcurrentHashMap<InetSocketAddress, Reader<I>>();
    handlerListeners = new ArrayList<HandlerListener<I>>();
    this.channelPort = channelPort;
  }

  protected void handlerActive(InetSocketAddress addr, Handler<I> handler) {
    synchronized (activeLock) {
      Handler<I> activeHandler = activeHandlers.get(addr);
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

  public void handleMessage(InetSocketAddress addr, I message) {
    logger.trace("handleMessage from {} with {}", addr, message);
    Reader<I> reader = channelReaders.get(addr);
    if(reader != null){
      reader.readMessage(addr,message);
    }
  }

  public void registerChannelReader(InetSocketAddress addr, Reader reader){
    channelReaders.putIfAbsent(addr,reader);
  }

  public void registerHandlerActivityListener(HandlerListener<I> listener){
    if(!handlerListeners.contains(listener)){
      handlerListeners.add(listener);
    }
  }

  /**
   * Sends a message to specified address if connected to this transceiver
   * @param addr
   * @param message
   */
  public void sendMessage(InetSocketAddress addr,I message) {
    synchronized (activeLock) {
      logger.trace("sendMessage to addr: {} with {}", addr, message);
      Handler<I> handler = activeHandlers.get(addr);
      if (handler != null) {
        handler.sendMessage(message);
      }
    }
  }

  /**
   * Broadcast a message on all channels connected to this port's transceiver
   * @param message
   */
  public void broadcastMessage(I message) {
    synchronized (activeLock) {
      for (Handler<I> handler : activeHandlers.values()) {
        handler.sendMessage(message);
      }
    }
  }

}

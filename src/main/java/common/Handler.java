package common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public abstract class Handler<I> extends SimpleChannelInboundHandler<I> {

  private ChannelHandlerContext ctx;
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private InetSocketAddress remoteAddress;
  private final Transceiver<? extends I>transceiver;
  private final Long handlerId;

  public Handler(Long id, Transceiver<? extends I> transceiver){
    handlerId=id;
    this.transceiver = transceiver;
  }

  public void sendMessage(I message) {
    if (ctx != null && ctx.channel().isActive() && ctx.channel().isWritable()) {
      logger.trace("sendMessage {} to {} written to wire",message.toString(),remoteAddress);
      ctx.writeAndFlush(message);
    }
    else {
      logger.warn("sendMessage called when channel not active or writable");
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.trace("channelActive remote peer: {} connected", ctx.channel().remoteAddress());
    this.ctx = ctx;
    remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    //transceiver.handlerActive(remoteAddress, this);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.trace("channelInactive remote peer: {} disconnected", ctx.channel().remoteAddress());
    transceiver.handlerInActive(remoteAddress);
  }

  public Long getHandlerId() {
    return handlerId;
  }

}
